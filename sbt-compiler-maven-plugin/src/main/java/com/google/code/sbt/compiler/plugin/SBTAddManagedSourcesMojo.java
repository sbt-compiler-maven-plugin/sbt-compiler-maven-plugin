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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

/**
 * Add managed source root, if it is not already added.<br/><br/>
 * Helper mojo.<br/>
 * Adds <code>${project.build.directory}/src_managed</code> is added to project's compile source roots
 * even if it does not exist yet (it may be created later by source generators).
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @since 1.0.0
 */
@Mojo( name = "addManagedSources", defaultPhase = LifecyclePhase.INITIALIZE )
public class SBTAddManagedSourcesMojo
    extends AbstractMojo
{

    /**
     * Maven project to interact with.
     */
    @Component
    protected MavenProject project;

    /**
     * Adds default SBT managed sources location to Maven project.
     * 
     * <code>${project.build.directory}/src_managed</code> is added to project's compile source roots
     */
    public void execute()
    {
        if ( "pom".equals( project.getPackaging() ) )
        {
            return;
        }

        File managedPath = new File( project.getBuild().getDirectory(), "src_managed" );
        String managedPathStr = managedPath.getAbsolutePath();
        if ( !project.getCompileSourceRoots().contains( managedPathStr ) )
        {
            project.addCompileSourceRoot( managedPathStr );
            getLog().debug( "Added source directory: " + managedPathStr );
        }
    }

}
