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
import java.io.IOException;
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
 * Compile Scala and Java sources
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @since 1.0.0
 */
@Mojo( name = "compile", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE )
public class SBTCompileMojo
    extends AbstractSBTCompileMojo
{
    /**
     * Source inclusion filters for the compiler.
     * 
     * @since 1.0.0
     */
    @Parameter
    protected Set<String> includes = new HashSet<String>();

    /**
     * Source exclusion filters for the compiler.
     * 
     * @since 1.0.0
     */
    @Parameter
    protected Set<String> excludes = new HashSet<String>();

    /**
     * Set this to <b>{@code true}</b> to bypass compilation of main sources.
     */
    @Parameter ( property = "maven.main.skip" )
    protected boolean skipMain;

    /**
     * The source directories containing Scala and Java main sources to be compiled.
     */
    @Parameter( defaultValue = "${project.compileSourceRoots}", readonly = true, required = true )
    private List<String> compileSourceRoots;

    /**
     * Project classpath.
     */
    @Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
    private List<String> classpathElements;

    /**
     * The directory for compiled classes.
     */
    @Parameter( defaultValue = "${project.build.outputDirectory}", required = true, readonly = true )
    private File outputDirectory;

    /**
     * Project's main artifact.
     */
    @Parameter( defaultValue = "${project.artifact}", readonly = true, required = true )
    private Artifact projectArtifact;

    @Override
    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if ( skipMain )
        {
            getLog().info( "Not compiling main sources" );
            return;
        }

        super.internalExecute();

        if ( outputDirectory.isDirectory() )
        {
            projectArtifact.setFile( outputDirectory );
        }
    }

    @Override
    protected List<String> getCompileSourceRoots()
    {
        return compileSourceRoots;
    }

    @Override
    protected Set<String> getSourceIncludes()
    {
        return includes;
    }
    
    @Override
    protected Set<String> getSourceExcludes()
    {
        return excludes;
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
        return defaultAnalysisCacheFile( project );
    }

    @Override
    protected Map<File, File> getAnalysisCacheMap()
    {
        HashMap<File, File> map = new HashMap<File, File>();
        for ( MavenProject reactorProject : reactorProjects )
        {
            if ( reactorProject != project )
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
            }
        }
        return map;
    }

}
