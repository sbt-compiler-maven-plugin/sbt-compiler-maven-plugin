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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Compile Scala and Java test sources
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @since 1.0.0
 */
@Mojo( name = "testCompile", defaultPhase = LifecyclePhase.TEST_COMPILE, requiresDependencyResolution = ResolutionScope.TEST )
public class SBTTestCompileMojo
    extends AbstractSBTCompileMojo
{
    /**
     * Test source inclusion filters for the compiler.
     * 
     * @since 1.0.0
     */
    @Parameter
    protected Set<String> testIncludes = new HashSet<String>();

    /**
     * Test source exclusion filters for the compiler.
     * 
     * @since 1.0.0
     */
    @Parameter
    protected Set<String> testExcludes = new HashSet<String>();

    /**
     * Set this to <b>{@code true}</b> to bypass compilation of test sources.
     * 
     * @since 1.0.0
     */
    @Parameter( property = "maven.test.skip" )
    protected boolean skipTest;

    /**
     * The source directories containing Scala and Java test sources to be compiled.
     */
    @Parameter( defaultValue = "${project.testCompileSourceRoots}", readonly = true, required = true )
    private List<String> compileSourceRoots;

    /**
     * Project test classpath.
     */
    @Parameter( defaultValue = "${project.testClasspathElements}", required = true, readonly = true )
    private List<String> classpathElements;

    /**
     * The directory where compiled test classes go.
     */
    @Parameter( defaultValue = "${project.build.testOutputDirectory}", required = true, readonly = true )
    private File outputDirectory;

    @Override
    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException
    {
        if ( skipTest )
        {
            getLog().info( "Not compiling test sources" );
            return;
        }
        super.internalExecute();
    }

    @Override
    protected List<String> getCompileSourceRoots()
    {
        return compileSourceRoots;
    }

    @Override
    protected Set<String> getSourceIncludes()
    {
        return testIncludes;
    }
    
    @Override
    protected Set<String> getSourceExcludes()
    {
        return testExcludes;
    }

    @Override
    protected List<String> getClasspathElements()
    {
        return classpathElements;
    }

    @Override
    protected File getOutputDirectory()
    {
        return outputDirectory;
    }

    @Override
    protected File getAnalysisCacheFile()
    {
        return defaultTestAnalysisCacheFile( project );
    }

    @Override
    protected Map<File, File> getAnalysisCacheMap()
    {
        HashMap<File, File> map = new HashMap<File, File>();
        for ( MavenProject reactorProject : reactorProjects )
        {
            File analysisCacheFile = defaultAnalysisCacheFile( reactorProject );
            if ( analysisCacheFile.isFile() )
            {
                File reactorProjectArtifactFile = reactorProject.getArtifact().getFile();
                if ( reactorProjectArtifactFile != null )
                {
                    map.put( reactorProjectArtifactFile.getAbsoluteFile(), analysisCacheFile.getAbsoluteFile() );
                }
            }
            
            if ( reactorProject != project )
            {
                File testAnalysisCacheFile = defaultTestAnalysisCacheFile( reactorProject );
                if ( testAnalysisCacheFile.isFile() )
                {
                    List<Artifact> reactorProjectattachedArtifacts = reactorProject.getAttachedArtifacts();
                    for ( Artifact artifact: reactorProjectattachedArtifacts )
                    {
                        if ( "tests".equals( artifact.getClassifier() ) )
                        {
                            map.put( artifact.getFile().getAbsoluteFile(), testAnalysisCacheFile.getAbsoluteFile() );
                            break;
                        }
                    }
                }
            }
        }
        return map;
    }

}
