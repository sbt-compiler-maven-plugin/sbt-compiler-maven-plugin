/*
 * Copyright 2013-2014 Grzegorz Slowikowski (gslowikowski at gmail dot com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.google.code.sbt.compiler.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

import org.codehaus.plexus.util.DirectoryScanner;

import com.google.code.sbt.compiler.api.Compiler;
import com.google.code.sbt.compiler.api.CompilerConfiguration;
import com.google.code.sbt.compiler.api.CompilerException;
import com.google.code.sbt.compiler.api.Compilers;

/**
 * Abstract base class for SBT compilation mojos.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public abstract class AbstractSBTCompileMojo
    extends AbstractMojo
{
    /**
     * Scala artifacts "groupId".
     */
    private static final String SCALA_GROUPID = "org.scala-lang";

    /**
     * Scala library "artifactId".
     */
    private static final String SCALA_LIBRARY_ARTIFACTID = "scala-library";

    /**
     * Scala compiler "artifactId".
     */
    private static final String SCALA_COMPILER_ARTIFACTID = "scala-compiler";

    /**
     * SBT artifacts "groupId".
     */
    private static final String SBT_GROUP_ID = "com.typesafe.sbt";

    /**
     * SBT compile interface "artifactId".
     */
    private static final String COMPILER_INTERFACE_ARTIFACT_ID = "compiler-interface";

    /**
     * SBT compile interface sources "classifier".
     */
    private static final String COMPILER_INTERFACE_CLASSIFIER = "sources";

    /**
     * SBT interface "artifactId".
     */
    private static final String XSBTI_ARTIFACT_ID = "sbt-interface";

    /**
     * Forced Scala version.<br>
     * <br>
     * If specified, this version of Scala compiler is used for compilation.<br>
     * If not specified, version of project's <b>{@code org.scala-lang:scala-library}</b> dependency is used.<br>
     * If there is no <b>{@code org.scala-lang:scala-library}</b> dependency in the project (in Java only projects),
     * selected compiler's {@link Compiler#getDefaultScalaVersion()} is used. 
     * 
     * @since 1.0.0
     */
    @Parameter( property = "scala.version" )
    protected String scalaVersion;

    /**
     * Forced SBT version.<br>
     * <br>
     * Used to automatically select one of the "well known" SBT compilers if no compiler added explicitly as plugin's dependency.
     * There are three cases possible:
     * <ul>
     * <li>
     * If {@link #sbtVersion} is specified, compatible {@link Compiler} implementation
     * is selected and configured to use {@link #sbtVersion} SBT version for compilation.
     * </li>
     * <li>
     * If {@link #sbtVersion} is not specified, and {@link #playVersion} is specified
     * {@link #playVersion} parameter value is used to indirectly select compatible {@link Compiler} implementation
     * and it's {@link Compiler#getDefaultSbtVersion()} SBT version used for compilation.
     * </li>
     * <li>
     * If both {@link #sbtVersion} and {@link #playVersion} are not specified
     * the most recent {@link Compiler} implementation is selected
     * and it's {@link Compiler#getDefaultSbtVersion()} SBT version used for compilation.
     * </li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @Parameter( property = "sbt.version" )
    protected String sbtVersion;

    /**
     * Forced Play&#33 Framework version.
     * <br>
     * <br>
     * Used to select SBT version in Play&#33 Framework projects based on framework version used.
     * See {@link #sbtVersion} for more details.
     * 
     * @since 1.0.0
     */
    @Parameter( property = "play2.version" )
    protected String playVersion;

    /**
     * Scala and Java source files encoding.
     * 
     * @since 1.0.0
     */
    @Parameter( property = "project.build.sourceEncoding" )
    protected String sourceEncoding;

    /**
     * Additional parameters for Java compiler.
     * 
     * @since 1.0.0
     */
    @Parameter( property = "sbt.javacOptions", defaultValue = "-g" )
    protected String javacOptions;

    /**
     * Additional parameters for Scala compiler.
     * 
     * @since 1.0.0
     */
    @Parameter( property = "sbt.scalacOptions", defaultValue = "-deprecation -unchecked" )
    protected String scalacOptions;

    /**
     * Additional parameters for Scala compiler.
     * <break>
     * For internal use only.
     */
    @Parameter( property = "sbt._scalacOptions", defaultValue = "", readonly = true )
    protected String _scalacOptions;

    /**
     * Scala compiler plugins.
     * 
     * @since 1.0.0
     */
    @Parameter( property = "sbt.scalacPlugins", defaultValue = "" )
    protected String scalacPlugins;

    /**
     * Scala compiler plugins.
     * <break>
     * For internal use only.
     */
    @Parameter( property = "sbt._scalacPlugins", defaultValue = "", readonly = true )
    protected String _scalacPlugins;

    /**
     * Maven project to interact with.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    protected MavenProject project;

    /**
     * Maven project builder used to resolve artifacts.
     */
    @Component
    protected MavenProjectBuilder mavenProjectBuilder;

    /**
     * All Maven projects in the reactor.
     */
    @Parameter( defaultValue = "${reactorProjects}", required = true, readonly = true )
    protected List<MavenProject> reactorProjects;

    /**
     * Artifact factory used to look up artifacts in the remote repository.
     */
    @Component
    protected ArtifactFactory factory;

    /**
     * Artifact resolver used to resolve artifacts.
     */
    @Component
    protected ArtifactResolver resolver;

    /**
     * Location of the local repository.
     */
    @Parameter( property = "localRepository", readonly = true, required = true )
    protected ArtifactRepository localRepo;

    /**
     * Remote repositories used by the resolver
     */
    @Parameter( property = "project.remoteArtifactRepositories", readonly = true, required = true )
    protected List<ArtifactRepository> remoteRepos;

    /**
     * Plugin's groupId used for well known providers resolution
     */
    @Parameter( property = "plugin.groupId", readonly = true, required = true )
    private String pluginGroupId;

    /**
     * Plugin's version used for well known providers resolution
     */
    @Parameter( property = "plugin.version", readonly = true, required = true )
    private String pluginVersion;

    /**
     * Map of compiler implementations. For now only zero or one allowed.
     */
    @Component( role = Compiler.class )
    private Map<String, Compiler> compilers;

    /**
     * Performs compilation.
     * 
     * @throws MojoExecutionException if unexpected problem occurs
     * @throws MojoFailureException if expected problem (such as compilation failure) occurs
     */
    @Override
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( !"pom".equals( project.getPackaging() ) )
        {
            long ts = System.currentTimeMillis();
            internalExecute();
            long te = System.currentTimeMillis();
            getLog().debug( String.format( "Mojo execution time: %d ms", te - ts ) );
        }
    }

    /**
     * Performs compilation.
     * 
     * @throws MojoExecutionException if unexpected problem occurs
     * @throws MojoFailureException if expected problem (such as compilation failure) occurs
     */
    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException
    {
        List<String> compileSourceRoots = getCompileSourceRoots();

        if ( compileSourceRoots.isEmpty() )// ?
        {
            getLog().info( "No sources to compile" );

            return;
        }

        List<File> sourceRootDirs = new ArrayList<File>( compileSourceRoots.size() );
        for ( String compileSourceRoot : compileSourceRoots )
        {
            sourceRootDirs.add( new File( compileSourceRoot ) );
        }

        List<File> sourceFiles = getSourceFiles( sourceRootDirs );
        if ( sourceFiles.isEmpty() )
        {
            getLog().info( "No sources to compile" );

            return;
        }

        try
        {
            Compiler sbtCompiler = getSbtCompiler();
            
            String resolvedScalaVersion = getScalaVersion( sbtCompiler );

            Artifact scalaLibraryArtifact =
                getResolvedArtifact( SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID, resolvedScalaVersion );
            if ( scalaLibraryArtifact == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:%s:jar artifact not found",
                                                                 SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID,
                                                                 resolvedScalaVersion ) );
            }

            Artifact scalaCompilerArtifact =
                getResolvedArtifact( SCALA_GROUPID, SCALA_COMPILER_ARTIFACTID, resolvedScalaVersion );
            if ( scalaCompilerArtifact == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:%s:jar artifact not found",
                                                                 SCALA_GROUPID, SCALA_COMPILER_ARTIFACTID,
                                                                 resolvedScalaVersion ) );
            }

            List<File> scalaExtraJars = getCompilerDependencies( scalaCompilerArtifact );

            String resolvedSbtVersion = getSbtVersion( sbtCompiler );

            Artifact xsbtiArtifact = getResolvedArtifact( SBT_GROUP_ID, XSBTI_ARTIFACT_ID, resolvedSbtVersion );
            if ( xsbtiArtifact == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:%s:jar dependency not found",
                                                                 SBT_GROUP_ID, XSBTI_ARTIFACT_ID, resolvedSbtVersion ) );
            }

            Artifact compilerInterfaceSrc =
                getResolvedArtifact( SBT_GROUP_ID, COMPILER_INTERFACE_ARTIFACT_ID, resolvedSbtVersion,
                                     COMPILER_INTERFACE_CLASSIFIER );
            if ( compilerInterfaceSrc == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:%s:%s:jar dependency not found",
                                                                 SBT_GROUP_ID, COMPILER_INTERFACE_ARTIFACT_ID,
                                                                 resolvedSbtVersion, COMPILER_INTERFACE_CLASSIFIER ) );
            }

            List<String> classpathElements = getClasspathElements();
            classpathElements.remove( getOutputDirectory().getAbsolutePath() );
            List<File> classpathFiles = new ArrayList<File>( classpathElements.size() );
            for ( String path : classpathElements )
            {
                classpathFiles.add( new File( path ) );
            }

            String resolvedScalacOptions = scalacOptions.trim();
            if ( _scalacOptions != null && _scalacOptions.length() > 0 )
            {
                resolvedScalacOptions = resolvedScalacOptions + ' ' + _scalacOptions;
            }

            if ( ( scalacPlugins != null && scalacPlugins.trim().length() > 0 )
                || ( _scalacPlugins != null && _scalacPlugins.trim().length() > 0 ) )
            {
                List<Artifact> resolvedScalaCompilerPluginArtifacts = new ArrayList<Artifact>();
                resolveScalacPluginArtifacts(resolvedScalaCompilerPluginArtifacts, scalacPlugins.trim() );
                resolveScalacPluginArtifacts(resolvedScalaCompilerPluginArtifacts, _scalacPlugins.trim() );
                for ( Artifact artifact: resolvedScalaCompilerPluginArtifacts )
                {
                    String arg = "-Xplugin:" + artifact.getFile().getAbsolutePath();
                    if ( arg.indexOf( ' ' ) >= 0 )
                    {
                        arg = '\"' + arg + '\"';
                    }
                    resolvedScalacOptions = resolvedScalacOptions + ' ' + arg;
                }
            }

            CompilerConfiguration configuration = new CompilerConfiguration();
            configuration.setSourceFiles( sourceFiles );
            configuration.setScalaLibraryFile( scalaLibraryArtifact.getFile() );
            configuration.setScalaCompilerFile( scalaCompilerArtifact.getFile() );
            configuration.setScalaExtraJarFiles( scalaExtraJars );
            configuration.setXsbtiFile( xsbtiArtifact.getFile() );
            configuration.setCompilerInterfaceSrcFile( compilerInterfaceSrc.getFile() );
            configuration.setClasspathFiles( classpathFiles );
            configuration.setLogger( new MavenCompilerLogger( getLog() ) );
            configuration.setOutputDirectory( getOutputDirectory() );
            configuration.setSourceEncoding( sourceEncoding );
            configuration.setJavacOptions( javacOptions );
            configuration.setScalacOptions( resolvedScalacOptions );
            configuration.setAnalysisCacheFile( getAnalysisCacheFile() );
            configuration.setAnalysisCacheMap( getAnalysisCacheMap() );

            sbtCompiler.performCompile( configuration );
        }
        catch ( CompilerException e )
        {
            throw new MojoFailureException( "Scala compilation failed", e );
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Scala compilation failed", e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Scala compilation failed", e );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new MojoExecutionException( "Scala compilation failed", e );
        }
        catch ( ProjectBuildingException e )
        {
            throw new MojoExecutionException( "Scala compilation failed", e );
        }
    }

    /**
     * Returns compilation classpath elements.
     * 
     * @return classpath elements
     */
    protected abstract List<String> getClasspathElements();

    /**
     * Returns compilation source roots.
     * 
     * @return source roots
     */
    protected abstract List<String> getCompileSourceRoots();

    /**
     * Returns source inclusion filters for the compiler.
     * 
     * @return source inclusion filters
     */
    protected abstract Set<String> getSourceIncludes();
    
    /**
     * Returns source exclusion filters for the compiler.
     * 
     * @return source exclusion filters
     */
    protected abstract Set<String> getSourceExcludes();
    
    /**
     * Returns output directory.
     * 
     * @return output directory
     */
    protected abstract File getOutputDirectory();

    /**
     * Returns SBT incremental compilation analysis cache file location for this project.
     * 
     * @return analysis cache file
     */
    protected abstract File getAnalysisCacheFile();

    /**
     * Returns SBT incremental compilation analysis cache file locations map for all reactor projects.
     * 
     * @return analysis cache files map
     */
    protected abstract Map<File, File> getAnalysisCacheMap();

    private Artifact getDependencyArtifact( Collection<?> classPathArtifacts, String groupId, String artifactId,
                                              String type )
    {
        Artifact result = null;
        for ( Iterator<?> iter = classPathArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( groupId.equals( artifact.getGroupId() ) && artifactId.equals( artifact.getArtifactId() )
                && type.equals( artifact.getType() ) )
            {
                result = artifact;
                break;
            }
        }
        return result;
    }

    private List<File> getSourceFiles( List<File> sourceRootDirs )
    {
        List<File> sourceFiles = new ArrayList<File>();

        Set<String> sourceIncludes = getSourceIncludes();
        if ( sourceIncludes.isEmpty() )
        {
            sourceIncludes.add( "**/*.java" );
            sourceIncludes.add( "**/*.scala" );
        }
        Set<String> sourceExcludes = getSourceExcludes();

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( sourceIncludes.toArray( new String[sourceIncludes.size()] ) );
        if ( !sourceExcludes.isEmpty() )
        {
            scanner.setExcludes( sourceExcludes.toArray( new String[sourceExcludes.size()] ) );
        }
        scanner.addDefaultExcludes();

        for ( File dir : sourceRootDirs )
        {
            if ( dir.isDirectory() )
            {
                scanner.setBasedir( dir );
                scanner.scan();
                String[] includedFileNames = scanner.getIncludedFiles();
                for ( String includedFileName : includedFileNames )
                {
                    File tmpAbsFile = new File( dir, includedFileName ).getAbsoluteFile(); // ?
                    sourceFiles.add( tmpAbsFile );
                }
            }
        }
        // scalac is sensible to scala file order, file system can't guarantee file order => unreproductible build error
        // across platform
        // to guarantee reproductible command line we order file by path (os dependend).
        // Collections.sort( sourceFiles );
        return sourceFiles;
    }

    private String getScalaVersion( Compiler sbtCompiler )
    {
        String result = scalaVersion;
        if ( result == null || result.length() == 0 )
        {
            Artifact scalaLibraryArtifact =
                getDependencyArtifact( project.getArtifacts(), SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID, "jar" );
            if ( scalaLibraryArtifact != null )
            {
                result = scalaLibraryArtifact.getVersion();
            }
            else
            {
                result = sbtCompiler.getDefaultScalaVersion();
            }
        }
        return result;
    }

    private String getSbtVersion( Compiler sbtCompiler )
    {
        String result = sbtVersion;
        if ( result == null || result.length() == 0 )
        {
            result = sbtCompiler.getDefaultSbtVersion();
        }
        return result;
    }

    /**
     * Returns SBT incremental main compilation analysis cache file location for a project.
     * 
     * @param p Maven project
     * @return analysis cache file location
     */
    protected File defaultAnalysisCacheFile( MavenProject p )
    {
        File classesDirectory = new File( p.getBuild().getOutputDirectory() );
        return new File( Compilers.getCacheDirectory( classesDirectory ), "compile" );
    }

    /**
     * Returns SBT incremental test compilation analysis cache file location for a project.
     * 
     * @param p Maven project
     * @return analysis cache file location
     */
    protected File defaultTestAnalysisCacheFile( MavenProject p )
    {
        File testClassesDirectory = new File( p.getBuild().getTestOutputDirectory() );
        return new File( Compilers.getCacheDirectory( testClassesDirectory ), "test-compile" );
    }

    // Private utility methods

    private Artifact getResolvedArtifact( String groupId, String artifactId, String version )
        throws ArtifactNotFoundException, ArtifactResolutionException
    {
        Artifact artifact = factory.createArtifact( groupId, artifactId, version, Artifact.SCOPE_RUNTIME, "jar" );
        resolver.resolve( artifact, remoteRepos, localRepo );
        return artifact;
    }

    private Artifact getResolvedArtifact( String groupId, String artifactId, String version, String classifier )
        throws ArtifactNotFoundException, ArtifactResolutionException
    {
        Artifact artifact = factory.createArtifactWithClassifier( groupId, artifactId, version, "jar", classifier );
        resolver.resolve( artifact, remoteRepos, localRepo );
        return artifact;
    }

    private List<File> getCompilerDependencies( Artifact scalaCompilerArtifact )
        throws ArtifactNotFoundException, ArtifactResolutionException, InvalidDependencyVersionException,
        ProjectBuildingException
    {
        List<File> d = new ArrayList<File>();
        for ( Artifact artifact : getAllDependencies( scalaCompilerArtifact ) )
        {
            if ( !SCALA_GROUPID.equals( artifact.getGroupId() )
                || !SCALA_LIBRARY_ARTIFACTID.equals( artifact.getArtifactId() ) )
            {
                d.add( artifact.getFile() );
            }
        }
        return d;
    }

    private Set<Artifact> getAllDependencies( Artifact artifact )
        throws ArtifactNotFoundException, ArtifactResolutionException, InvalidDependencyVersionException,
        ProjectBuildingException
    {
        Set<Artifact> result = new HashSet<Artifact>();
        MavenProject p = mavenProjectBuilder.buildFromRepository( artifact, remoteRepos, localRepo );
        Set<Artifact> d = resolveDependencyArtifacts( p );
        result.addAll( d );
        for ( Artifact dependency : d )
        {
            Set<Artifact> transitive = getAllDependencies( dependency );
            result.addAll( transitive );
        }
        return result;
    }

    /**
     * This method resolves the dependency artifacts from the project.
     * 
     * @param theProject The POM.
     * @return resolved set of dependency artifacts.
     * @throws ArtifactResolutionException
     * @throws ArtifactNotFoundException
     * @throws InvalidDependencyVersionException
     */
    private Set<Artifact> resolveDependencyArtifacts( MavenProject theProject )
        throws ArtifactNotFoundException, ArtifactResolutionException, InvalidDependencyVersionException
    {
        AndArtifactFilter filter = new AndArtifactFilter();
        filter.add( new ScopeArtifactFilter( Artifact.SCOPE_TEST ) );
        filter.add( new NonOptionalArtifactFilter() );
        // TODO follow the dependenciesManagement and override rules
        Set<Artifact> artifacts = theProject.createArtifacts( factory, Artifact.SCOPE_RUNTIME, filter );
        for ( Artifact artifact : artifacts )
        {
            resolver.resolve( artifact, remoteRepos, localRepo );
        }
        return artifacts;
    }

    // Cached classloaders
    private static final ConcurrentHashMap<String, ClassLoader> cachedClassLoaders = new ConcurrentHashMap<String, ClassLoader>( 2 );

    private static ClassLoader getCachedClassLoader( String compilerId )
    {
        return cachedClassLoaders.get( compilerId );
    }

    private static void setCachedClassLoader( String compilerId, ClassLoader classLoader )
    {
        cachedClassLoaders.put( compilerId, classLoader );
    }

    private Compiler getSbtCompiler()
        throws MojoExecutionException
    {
        Compiler sbtCompiler = null;
        if ( !compilers.isEmpty() )
        {
            sbtCompiler = getDeclaredSbtCompiler();
        }
        else
        {
            sbtCompiler = getWellKnownSbtCompiler();
        }
        return sbtCompiler;
    }

    private Compiler getDeclaredSbtCompiler()
        throws MojoExecutionException
    {
        if ( compilers.size() > 1 )
        {
            throw new MojoExecutionException( "Too many compiles defined. A maximum of one allowed." );
        }

        Map.Entry<String, Compiler> compilerEntry = compilers.entrySet().iterator().next();
        String compilerId = compilerEntry.getKey();
        Compiler sbtCompiler = compilerEntry.getValue();

        getLog().debug( String.format( "Using declared compiler \"%s\".", compilerId ) );

        return sbtCompiler;
    }

    private Compiler getWellKnownSbtCompiler()
        throws MojoExecutionException
    {
        try
        {
            String compilerId = Compilers.getDefaultCompilerId( sbtVersion, playVersion );
            ClassLoader compilerClassLoader = getCachedClassLoader( compilerId );
            if ( compilerClassLoader == null )
            {
                getLog().debug( String.format( "Cached classloader for compiler \"%s\" not available.", compilerId ) );
            }
            else
            {
                if ( compilerClassLoader.getParent() == Thread.currentThread().getContextClassLoader() )
                {
                    getLog().debug( String.format( "Using cached classloader for compiler \"%s\".", compilerId ) );
                }
                else
                {
                    getLog().debug( String.format( "Invalidated cached classloader for compiler \"%s\". Parent classloader changed from %d to %d.",
                                                   compilerId,
                                                   Integer.valueOf( compilerClassLoader.getParent().hashCode() ),
                                                   Integer.valueOf( Thread.currentThread().getContextClassLoader().hashCode() ) ) );
                    compilerClassLoader = null;
                }
            }
            if ( compilerClassLoader == null )
            {
                Artifact compilerArtifact =
                    getResolvedArtifact( pluginGroupId, "sbt-compiler-" + compilerId, pluginVersion );

                Set<Artifact> compilerDependencies = getAllDependencies( compilerArtifact );
                List<File> classPathFiles = new ArrayList<File>( compilerDependencies.size() + 2 );
                classPathFiles.add( compilerArtifact.getFile() );
                for ( Artifact dependencyArtifact : compilerDependencies )
                {
                    classPathFiles.add( dependencyArtifact.getFile() );
                }
                String javaHome = System.getProperty( "java.home" );
                classPathFiles.add( new File( javaHome, "../lib/tools.jar" ) );

                List<URL> classPathUrls = new ArrayList<URL>( classPathFiles.size() );
                for ( File classPathFile : classPathFiles )
                {
                    classPathUrls.add( new URL( classPathFile.toURI().toASCIIString() ) );
                }

                compilerClassLoader =
                    new URLClassLoader( classPathUrls.toArray( new URL[classPathUrls.size()] ),
                                        Thread.currentThread().getContextClassLoader() );
                getLog().debug( String.format( "Setting cached classloader for compiler \"%s\" with parent classloader %d",
                                               compilerId, Integer.valueOf( compilerClassLoader.getParent().hashCode() ) ) );
                setCachedClassLoader( compilerId, compilerClassLoader );
            }

            String compilerClassName =
                String.format( "com.google.code.sbt.compiler.%s.%sCompiler", compilerId,
                               compilerId.toUpperCase( Locale.ROOT ) );
            Compiler sbtCompiler = (Compiler) compilerClassLoader.loadClass( compilerClassName ).newInstance();

            getLog().debug( String.format( "Using autodetected compiler \"%s\".", compilerId ) );

            return sbtCompiler;
        }
        catch ( ArtifactNotFoundException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( IllegalAccessException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( InstantiationException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( MalformedURLException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
        catch ( ProjectBuildingException e )
        {
            throw new MojoExecutionException( "Compiler autodetection failed", e );
        }
    }

    private void resolveScalacPluginArtifacts( List<Artifact> scalacPluginArtifacts, String scalacPlugins )
        throws ArtifactNotFoundException, ArtifactResolutionException
    {
        if ( scalacPlugins != null && scalacPlugins.trim().length() > 0 )
        {
            String[] scalacPluginsGAVs = scalacPlugins.trim().split( "," );
            for ( String scalacPluginGAV : scalacPluginsGAVs )
            {
                String[] gav = scalacPluginGAV.split( ":" );
                String groupId = gav[0];
                String artifactId = gav[1];
                String version = gav[2];
                Artifact scalacPluginArtifact = getResolvedArtifact( groupId, artifactId, version );
                if ( scalacPluginArtifact != null )
                {
                    scalacPluginArtifacts.add( scalacPluginArtifact );
                }
            }
        }
    }

    private static class NonOptionalArtifactFilter
        implements ArtifactFilter
    {
        public boolean include( Artifact artifact )
        {
            return !artifact.isOptional();
        }
    }

}
