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

package com.google.code.sbt.compiler.sbt012;

import com.google.code.sbt.compiler.api.AbstractCompiler;
import com.google.code.sbt.compiler.api.Analysis;
import com.google.code.sbt.compiler.api.CompilerConfiguration;
import com.google.code.sbt.compiler.api.CompilerException;
import com.google.code.sbt.compiler.api.CompilerLogger;

import org.codehaus.plexus.component.annotations.Component;

import xsbti.CompileFailed;
import xsbti.Logger;

import com.typesafe.zinc.Compiler;
import com.typesafe.zinc.Inputs;
import com.typesafe.zinc.Setup;

/**
 * SBT 0.12.x compatible compiler (uses <a href="https://github.com/typesafehub/zinc">Zinc</a> 0.2.5)
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
@Component( role = com.google.code.sbt.compiler.api.Compiler.class, hint = "sbt012", description = "SBT 0.12.x compiler (uses Zinc 0.2.5)" )
public class SBT012Compiler
    extends AbstractCompiler
{
    /**
     * SBT compilation order.
     */
    private static final String COMPILE_ORDER = "mixed";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultScalaVersion()
    {
        return "2.10.0";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultSbtVersion()
    {
        return "0.12.2";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Analysis performCompile( CompilerConfiguration configuration )
        throws CompilerException
    {
        CompilerLogger logger = configuration.getLogger();
        Logger sbtLogger = new SBT012Logger( logger );
        Setup setup =
            Setup.create( configuration.getScalaCompilerFile(), configuration.getScalaLibraryFile(),
                          configuration.getScalaExtraJarFiles(), configuration.getXsbtiFile(),
                          configuration.getCompilerInterfaceSrcFile(), null/* , FORK_JAVA */ );
        if ( logger.isDebugEnabled() )
        {
            Setup.debug( setup, sbtLogger );
        }
        Compiler compiler = Compiler.create( setup, sbtLogger );

        Inputs inputs =
            Inputs.create( configuration.getClasspathFiles(), configuration.getSourceFiles(),
                           configuration.getOutputDirectory(), resolveScalacOptions( configuration ),
                           resolveJavacOptions( configuration ), configuration.getAnalysisCacheFile(),
                           configuration.getAnalysisCacheMap(), SBT012Compiler.COMPILE_ORDER,
                           /* getIncOptions(), */logger.isDebugEnabled() /* mirrorAnalysisCache */ );
        if ( configuration.getSourcePositionMapper() != null )
        {
            logger.warn( "Source position mappers not supported" );
        }
        if ( logger.isDebugEnabled() )
        {
            Inputs.debug( inputs, sbtLogger );
        }

        try
        {
            return new SBT012Analysis( compiler.compile( inputs, sbtLogger ) );
        }
        catch ( CompileFailed e )
        {
            throw new CompilerException( "Scala compilation failed", e );
        }
    }

}
