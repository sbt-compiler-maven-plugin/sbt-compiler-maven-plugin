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

package com.google.code.sbt;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.google.code.sbt.compiler.Compiler;
import com.google.code.sbt.compiler.CompilerConfiguration;
import com.google.code.sbt.compiler.CompilerException;

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
     * Scala Compiler version.
     * 
     * If not specified:
     * a) version of project's org.scala-lang:scala-library dependency is used
     * b) if org.scala-lang:scala-library dependency does not exist in the project DEFAULT_SCALA_VERSION is used
     * 
     * @since 1.0.0
     */
    @Parameter( property = "scala.version" )
    private String scalaVersion;

    /**
     * SBT version
     * 
     * @since 1.0.0
     */
    @Parameter( property = "sbt.version" )
    private String sbtVersion;

    /**
     * The -encoding argument for Scala and Java compilers.
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
     * <i>Maven Internal</i>: Project to interact with.
     */
    @Component
    protected MavenProject project;

    /**
     * Maven project builder used to resolve artifacts.
     */
    @Component
    protected MavenProjectBuilder mavenProjectBuilder;

    /**
     * All projects in the reactor.
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
     * List of Remote Repositories used by the resolver
     */
    @Parameter( property = "project.remoteArtifactRepositories", readonly = true, required = true )
    protected List<?> remoteRepos;

    /**
     * Map of compiler implementations. For now only zero or one allowed.
     */
    @Component( role = Compiler.class )
    private Map<String, Compiler> compilers;

    @Parameter( property = "plugin.groupId", readonly = true, required = true )
    private String pluginGroupId;

    @Parameter( property = "plugin.artifactId", readonly = true, required = true )
    private String pluginArtifactId;

    @Parameter( property = "plugin.version", readonly = true, required = true )
    private String pluginVersion;

    /**
     * Performs compilation.
     * 
     * @throws MojoExecutionException if an unexpected problem occurs.
     * Throwing this exception causes a "BUILD ERROR" message to be displayed.
     * @throws MojoFailureException if an expected problem (such as a compilation failure) occurs.
     * Throwing this exception causes a "BUILD FAILURE" message to be displayed.
     */
    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( "pom".equals( project.getPackaging() ) )
        {
            return;
        }

        try
        {
            long ts = System.currentTimeMillis();
            internalExecute();
            long te = System.currentTimeMillis();
            getLog().debug( String.format( "Mojo execution time: %d ms", te - ts ) );
        }
        catch ( IOException e )
        {
            throw new MojoExecutionException( "Scala compilation failed", e );
        }
    }

    /**
     * Actual compilation code, to be overridden.
     * 
     * @throws MojoExecutionException if an unexpected problem occurs.
     * @throws MojoFailureException if an expected problem (such as a compilation failure) occurs.
     * @throws IOException if an IO exception occurs.
     */
    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
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
                throw new MojoExecutionException(
                                                  String.format( "Required %s:%s:%s:jar artifact not found",
                                                                 SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID,
                                                                 resolvedScalaVersion ) );
            }
            
            Artifact scalaCompilerArtifact =
                getResolvedArtifact( SCALA_GROUPID, SCALA_COMPILER_ARTIFACTID, resolvedScalaVersion );
            if ( scalaCompilerArtifact == null )
            {
                throw new MojoExecutionException(
                                                  String.format( "Required %s:%s:%s:jar artifact not found",
                                                                 SCALA_GROUPID, SCALA_COMPILER_ARTIFACTID,
                                                                 resolvedScalaVersion ) );
            }

            List<File> scalaExtraJars = getCompilerDependencies( scalaCompilerArtifact );
            scalaExtraJars.remove( scalaLibraryArtifact.getFile() );

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
            configuration.setScalacOptions( scalacOptions );
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
            throw new MojoFailureException( "Scala compilation failed", e );
        }
        catch ( ArtifactResolutionException e )
        {
            throw new MojoFailureException( "Scala compilation failed", e );
        }
        catch ( InvalidDependencyVersionException e )
        {
            throw new MojoFailureException( "Scala compilation failed", e );
        }
        catch ( ProjectBuildingException e )
        {
            throw new MojoFailureException( "Scala compilation failed", e );
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
     * A list of inclusion filters for the compiler.
     * 
     * @return inclusion filters
     */
    protected abstract Set<String> getSourceIncludes();
    
    /**
     * A list of exclusion filters for the compiler.
     * 
     * @return exclusion filters
     */
    protected abstract Set<String> getSourceExcludes();
    
    /**
     * Returns output directory.
     * 
     * @return output directory
     */
    protected abstract File getOutputDirectory();

    /**
     * Returns incremental compilation analysis cache file.
     * 
     * @return analysis cache file
     */
    protected abstract File getAnalysisCacheFile();

    /**
     * Returns incremental compilation analyses map for reactor projects.
     * 
     * @return analysis cache map
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
            result = sbtCompiler.getDefaultScalaVersion();
            Artifact scalaLibraryArtifact =
                getDependencyArtifact( project.getArtifacts(), SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID, "jar" );
            if ( scalaLibraryArtifact != null )
            {
                result = scalaLibraryArtifact.getVersion();
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

    private File defaultAnalysisDirectory( MavenProject p )
    {
        return new File( p.getBuild().getDirectory(), "cache" );
    }

    /**
     * Returns incremental main compilation analysis cache file location for a project.
     * 
     * @param p Maven project
     * @return analysis cache file location
     */
    protected File defaultAnalysisCacheFile( MavenProject p )
    {
        return new File( defaultAnalysisDirectory( p ), "compile" );
    }

    /**
     * Returns incremental test compilation analysis cache file location for a project.
     * 
     * @param p Maven project
     * @return analysis cache file location
     */
    protected File defaultTestAnalysisCacheFile( MavenProject p )
    {
        return new File( defaultAnalysisDirectory( p ), "test-compile" );
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
            d.add( artifact.getFile() );
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

    private Compiler getSbtCompiler()
        throws MojoExecutionException
    {
        if ( compilers.isEmpty() )
        {
            StringBuilder sb = new StringBuilder( 1250 );
            sb.append( "No compiler defined.\n\n" );
            appendCompilerConfigurationHelpMessage( sb );
            throw new MojoExecutionException( sb.toString() );
        }

        if ( compilers.size() > 1 )
        {
            StringBuilder sb = new StringBuilder( 1250 );
            sb.append( "Too many compiles defined.\n\n" );
            appendCompilerConfigurationHelpMessage( sb );
            sb.append( "ONLY ONE!\n\n" );
            throw new MojoExecutionException( sb.toString() );
        }

        Map.Entry<String, Compiler> compilerEntry = compilers.entrySet().iterator().next();
        String compilerId = compilerEntry.getKey();
        Compiler sbtCompiler = compilerEntry.getValue();

        getLog().debug( String.format( "Using compiler \"%s\".", compilerId ) );

        return sbtCompiler;
    }

    // Proper compiler configuration info helper methods

    private void appendCompilerConfigurationHelpMessage( StringBuilder sb )
    {
        String suggestedSbtCompiler = getSuggestedSbtCompiler();

        sb.append( "Add plugin dependency:\n\n" );
        if ( suggestedSbtCompiler != null )
        {
            appendCompilerConfigurationHelpMessageForCompiler( sb, suggestedSbtCompiler );
            sb.append( "\nor dependency containing your custom SBT " ).append( sbtVersion );
            sb.append( " compiler implementation.\n" );
        }
        else
        {
            appendCompilerConfigurationHelpMessageForCompiler( sb, "sbt-compiler-sbt013" );
            sb.append( "\nfor SBT 0.13.x compatible compiler, or:\n\n" );
            appendCompilerConfigurationHelpMessageForCompiler( sb, "sbt-compiler-sbt012" );
            sb.append( "\nfor SBT 0.12.x compatible compiler, or dependency containing your custom SBT" );
            if ( sbtVersion != null )
            {
                sb.append( " " ).append( sbtVersion );
            }
            sb.append( " compiler implementation.\n" );
        }
        sb.append( "\n" );
    }
    
    private String getSuggestedSbtCompiler()
    {
        String result = null;
        if ( sbtVersion != null && !sbtVersion.isEmpty() )
        {
            if ( sbtVersion.startsWith( "0.13." ) )
            {
                result = "sbt-compiler-sbt013";
            }
            else if ( sbtVersion.startsWith( "0.12." ) )
            {
                result = "sbt-compiler-sbt012";
            }
        }
        if ( result == null && "play2".equals( project.getPackaging() ) )
        {
            String playVersion = project.getProperties().getProperty( "play2.version" );
            if ( playVersion != null && !playVersion.isEmpty() )
            {
                if ( playVersion.startsWith( "2.2." ) )
                {
                    result = "sbt-compiler-sbt013";
                }
                else if ( playVersion.startsWith( "2.1." ) )
                {
                    result = "sbt-compiler-sbt012";
                }
            }
        }
        return result;
    }

    private void appendCompilerConfigurationHelpMessageForCompiler( StringBuilder sb, String compilerId )
    {
        sb.append( "|    <plugin>\n" );
        sb.append( "|        <groupId>" ).append ( pluginGroupId ).append( "</groupId>\n" );
        sb.append( "|        <artifactId>" ).append ( pluginArtifactId ).append( "</artifactId>\n" );
        sb.append( "|        <version>" ).append ( pluginVersion ).append( "</version>\n" );
        sb.append( "|        <dependencies>\n" );
        sb.append( "|            <dependency>\n" );
        sb.append( "|                <groupId>" ).append ( pluginGroupId ).append( "</groupId>\n" );
        sb.append( "|                <artifactId>" ).append( compilerId ).append( "</artifactId>\n" );
        sb.append( "|                <version>" ).append ( pluginVersion ).append( "</version>\n" );
        sb.append( "|            </dependency>\n" );
        sb.append( "|        </dependencies>\n" );
        sb.append( "|    </plugin>\n" );
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
