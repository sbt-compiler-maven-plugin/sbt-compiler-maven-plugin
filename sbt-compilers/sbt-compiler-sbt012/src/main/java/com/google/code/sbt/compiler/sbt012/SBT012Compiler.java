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

package com.google.code.sbt.compiler.sbt012;

import com.google.code.sbt.compiler.api.AbstractCompiler;
import com.google.code.sbt.compiler.api.CompilerConfiguration;
import com.google.code.sbt.compiler.api.CompilerException;

import org.codehaus.plexus.component.annotations.Component;

import com.typesafe.zinc.Compiler;
import com.typesafe.zinc.Inputs;
import com.typesafe.zinc.Setup;

/**
 * SBT 0.12.x compatible compiler (uses <a href="https://github.com/typesafehub/zinc">Zinc</a> 0.2.5)
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
@Component( role = com.google.code.sbt.compiler.api.Compiler.class, hint = "sbt012", description = "SBT 0.12.x compiler (uses Zinc 0.2.5)")
public class SBT012Compiler
    extends AbstractCompiler
{
    /**
     * SBT compilation order.
     */
    private static final String COMPILE_ORDER = "mixed";

    public String getDefaultScalaVersion()
    {
        return "2.10.0";
    }

    public String getDefaultSbtVersion()
    {
        return "0.12.2";
    }

    public void performCompile( CompilerConfiguration configuration )
        throws CompilerException
    {
        SBT012Logger sbtLogger = new SBT012Logger( configuration.getLogger() );
        Setup setup =
            Setup.create( configuration.getScalaCompilerFile(), configuration.getScalaLibraryFile(),
                          configuration.getScalaExtraJarFiles(), configuration.getXsbtiFile(),
                          configuration.getCompilerInterfaceSrcFile(), null/* , FORK_JAVA */ );
        if ( configuration.getLogger().isDebugEnabled() )
        {
            Setup.debug( setup, sbtLogger );
        }
        Compiler compiler = Compiler.create( setup, sbtLogger );

        Inputs inputs =
            Inputs.create( configuration.getClasspathFiles(), configuration.getSourceFiles(),
                           configuration.getOutputDirectory(), resolveScalacOptions( configuration ),
                           resolveJavacOptions( configuration ), configuration.getAnalysisCacheFile(),
                           configuration.getAnalysisCacheMap(), SBT012Compiler.COMPILE_ORDER,
                           /* getIncOptions(), */configuration.getLogger().isDebugEnabled() /* mirrorAnalysisCache */ );
        if ( configuration.getLogger().isDebugEnabled() )
        {
            Inputs.debug( inputs, sbtLogger );
        }

        try
        {
            compiler.compile( inputs, sbtLogger );
        }
        catch ( xsbti.CompileFailed e )
        {
            throw new CompilerException( "Scala compilation failed", e );
        }
    }

}
