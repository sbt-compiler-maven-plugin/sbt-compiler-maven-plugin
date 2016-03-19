/*
 * Copyright 2013-2016 Grzegorz Slowikowski (gslowikowski at gmail dot com)
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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Add default Scala source roots, if they exist and are not already added.<br><br>
 * Helper mojo.<br>
 * Adds:
 * <ul>
 * <li>{@code src/main/scala} as compile source root</li>
 * <li>{@code src/test/scala} as test compile source root</li>
 * </ul>
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @since 1.0.0
 */
@Mojo( name = "addScalaSources", defaultPhase = LifecyclePhase.INITIALIZE )
public class SBTAddScalaSourcesMojo
    extends AbstractMojo
{

    /**
     * Maven project to interact with.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    protected MavenProject project;

    /**
     * Adds default Scala sources locations to Maven project.
     * 
     * <ul>
     * <li>{@code src/main/scala} is added to project's compile source roots</li>
     * <li>{@code src/test/scala} is added to project's test compile source roots</li>
     * </ul>
     */
    @Override
    public void execute()
    {
        if ( "pom".equals( project.getPackaging() ) )
        {
            return;
        }

        File baseDir = project.getBasedir();

        File mainScalaPath = new File( baseDir, "src/main/scala" );
        if ( mainScalaPath.isDirectory() )
        {
            String mainScalaPathStr = mainScalaPath.getAbsolutePath();
            if ( !project.getCompileSourceRoots().contains( mainScalaPathStr ) )
            {
                project.addCompileSourceRoot( mainScalaPathStr );
                getLog().debug( "Added source directory: " + mainScalaPathStr );
            }
        }

        File testScalaPath = new File( baseDir, "src/test/scala" );
        if ( testScalaPath.isDirectory() )
        {
            String testScalaPathStr = testScalaPath.getAbsolutePath();
            if ( !project.getTestCompileSourceRoots().contains( testScalaPathStr ) )
            {
                project.addTestCompileSourceRoot( testScalaPathStr );
                getLog().debug( "Added test source directory: " + testScalaPathStr );
            }
        }
    }

}
