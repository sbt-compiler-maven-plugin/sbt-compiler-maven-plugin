/*
 * Copyright 2013 Grzegorz Slowikowski (gslowikowski at gmail dot com)
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
import java.util.Arrays;
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

import scala.Option;

import com.typesafe.zinc.Compiler;
import com.typesafe.zinc.IncOptions;
import com.typesafe.zinc.Inputs;
import com.typesafe.zinc.Setup;

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
    public static final String SCALA_GROUPID = "org.scala-lang";

    /**
     * Scala library "artifactId".
     */
    public static final String SCALA_LIBRARY_ARTIFACTID = "scala-library";

    /**
     * Scala compiler "artifactId".
     */
    public static final String SCALA_COMPILER_ARTIFACTID = "scala-compiler";

    /**
     * SBT artifacts "groupId".
     */
    public static final String SBT_GROUP_ID = "com.typesafe.sbt";

    /**
     * SBT incremental compile "artifactId".
     */
    public static final String COMPILER_INTEGRATION_ARTIFACT_ID = "incremental-compiler";

    /**
     * SBT compile interface "artifactId".
     */
    public static final String COMPILER_INTERFACE_ARTIFACT_ID = "compiler-interface";

    /**
     * SBT compile interface sources "classifier".
     */
    public static final String COMPILER_INTERFACE_CLASSIFIER = "sources";

    /**
     * SBT interface "artifactId".
     */
    public static final String XSBTI_ARTIFACT_ID = "sbt-interface";

    /**
     * SBT compilation order.
     */
    private static final String COMPILE_ORDER = "mixed";

    /**
     * Run compilation in forked JVM.
     */
    private static final boolean FORK_JAVA = false;

    /**
     * SBT version
     * 
     * @since 1.0.0
     */
    @Parameter( property = "sbt.version", defaultValue = "0.13.0" )
    private String sbtVersion;

    /**
     * The -encoding argument for Scala and Java compilers.
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
     * Perform compilation.
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
            Artifact scalaLibraryArtifact =
                getDependencyArtifact( project.getArtifacts(), SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID, "jar" );
            if ( scalaLibraryArtifact == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:jar dependency not found",
                                                                 SCALA_GROUPID, SCALA_LIBRARY_ARTIFACTID ) );
            }

            String scalaVersion = scalaLibraryArtifact.getVersion();
            Artifact scalaCompilerArtifact =
                getResolvedArtifact( SCALA_GROUPID, SCALA_COMPILER_ARTIFACTID, scalaVersion );
            if ( scalaCompilerArtifact == null )
            {
                throw new MojoExecutionException(
                                                  String.format( "Required %s:%s:%s:jar dependency not found",
                                                                 SCALA_GROUPID, SCALA_COMPILER_ARTIFACTID,
                                                                 scalaVersion ) );
            }

            List<File> scalaExtraJars = getCompilerDependencies( scalaCompilerArtifact );
            scalaExtraJars.remove( scalaLibraryArtifact.getFile() );

            Artifact xsbtiArtifact = getResolvedArtifact( SBT_GROUP_ID, XSBTI_ARTIFACT_ID, sbtVersion );
            if ( xsbtiArtifact == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:%s:jar dependency not found",
                                                                 SBT_GROUP_ID, XSBTI_ARTIFACT_ID, sbtVersion ) );
            }

            Artifact compilerInterfaceSrc =
                getResolvedArtifact( SBT_GROUP_ID, COMPILER_INTERFACE_ARTIFACT_ID, sbtVersion,
                                     COMPILER_INTERFACE_CLASSIFIER );
            if ( compilerInterfaceSrc == null )
            {
                throw new MojoExecutionException( String.format( "Required %s:%s:%s:%s:jar dependency not found",
                                                                 SBT_GROUP_ID, COMPILER_INTERFACE_ARTIFACT_ID,
                                                                 sbtVersion, COMPILER_INTERFACE_CLASSIFIER ) );
            }

            List<String> classpathElements = getClasspathElements();
            classpathElements.remove( getOutputDirectory().getAbsolutePath() );
            List<File> classpathFiles = new ArrayList<File>( classpathElements.size() );
            for ( String path : classpathElements )
            {
                classpathFiles.add( new File( path ) );
            }

            SBTLogger sbtLogger = new SBTLogger( getLog() );
            Setup setup =
                Setup.create( scalaCompilerArtifact.getFile(), scalaLibraryArtifact.getFile(), scalaExtraJars,
                              xsbtiArtifact.getFile(), compilerInterfaceSrc.getFile(), null, FORK_JAVA );
            if ( getLog().isDebugEnabled() )
            {
                Setup.debug( setup, sbtLogger );
            }
            Compiler compiler = Compiler.create( setup, sbtLogger );

            Inputs inputs =
                Inputs.create( classpathFiles, sourceFiles, getOutputDirectory(), getScalacOptions(),
                               getJavacOptions(), getAnalysisCacheFile(), getAnalysisCacheMap(), COMPILE_ORDER,
                               getIncOptions(), getLog().isDebugEnabled() /* mirrorAnalysisCache */ );
            if ( getLog().isDebugEnabled() )
            {
                Inputs.debug( inputs, sbtLogger );
            }

            compiler.compile( inputs, sbtLogger );
        }
        catch ( xsbti.CompileFailed e )
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

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes( new String[] { "**/*.java", "**/*.scala" } );
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

    private List<String> getScalacOptions()
    {
        List<String> result = new ArrayList<String>( Arrays.asList( scalacOptions.split( " " ) ) );
        if ( !result.contains( "-encoding" ) && sourceEncoding != null && sourceEncoding.length() > 0 )
        {
            result.add( "-encoding" );
            result.add( sourceEncoding );
        }
        return result;
    }

    private List<String> getJavacOptions()
    {
        List<String> result = new ArrayList<String>( Arrays.asList( javacOptions.split( " " ) ) );
        if ( !result.contains( "-encoding" ) && sourceEncoding != null && sourceEncoding.length() > 0 )
        {
            result.add( "-encoding" );
            result.add( sourceEncoding );
        }
        return result;
    }

    private File defaultAnalysisDirectory( MavenProject p )
    {
        return new File( p.getBuild().getDirectory(), "analysis" );
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

    private IncOptions getIncOptions()
    {
        // comment from SBT (sbt.inc.IncOptions.scala):
        // After which step include whole transitive closure of invalidated source files.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Steps before transitive closure
        int transitiveStep = 3;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // What's the fraction of invalidated source files when we switch to recompiling
        // all files and giving up incremental compilation altogether. That's useful in
        // cases when probability that we end up recompiling most of source files but
        // in multiple steps is high. Multi-step incremental recompilation is slower
        // than recompiling everything in one step.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Limit before recompiling all sources
        double recompileAllFraction = 0.5d;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // Print very detailed information about relations, such as dependencies between source files.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Enable debug logging of analysis relations
        boolean relationsDebug = false;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // Enable tools for debugging API changes. At the moment this option is unused but in the
        // future it will enable for example:
        //   - disabling API hashing and API minimization (potentially very memory consuming)
        //   - diffing textual API representation which helps understanding what kind of changes
        //     to APIs are visible to the incremental compiler
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Enable analysis API debugging
        boolean apiDebug = false;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // Controls context size (in lines) displayed when diffs are produced for textual API
        // representation.
        //
        // This option is used only when `apiDebug == true`.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Diff context size (in lines) for API debug
        int apiDiffContextSize = 5;

        // comment from SBT (sbt.inc.IncOptions.scala):
        // The directory where we dump textual representation of APIs. This method might be called
        // only if apiDebug returns true. This is unused option at the moment as the needed functionality
        // is not implemented yet.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Destination for analysis API dump
        Option<File> apiDumpDirectory = Option.empty();

        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Restore previous class files on failure
        boolean transactional = false;

        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Backup location (if transactional)
        Option<File> backup = Option.empty();

        return new IncOptions( transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize, apiDumpDirectory, transactional, backup );
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
        filter.add( new ArtifactFilter()
        {
            public boolean include( Artifact artifact )
            {
                return !artifact.isOptional();
            }
        } );
        // TODO follow the dependenciesManagement and override rules
        Set<Artifact> artifacts = theProject.createArtifacts( factory, Artifact.SCOPE_RUNTIME, filter );
        for ( Artifact artifact : artifacts )
        {
            resolver.resolve( artifact, remoteRepos, localRepo );
        }
        return artifacts;
    }

}
