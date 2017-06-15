/*
 * Copyright 2013-2017 Grzegorz Slowikowski (gslowikowski at gmail dot com)
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.code.sbt.compiler.api.AbstractCompiler;
import com.google.code.sbt.compiler.api.Analysis;
import com.google.code.sbt.compiler.api.CompilationProblem;
import com.google.code.sbt.compiler.api.CompilerConfiguration;
import com.google.code.sbt.compiler.api.CompilerException;
import com.google.code.sbt.compiler.api.CompilerLogger;
import com.google.code.sbt.compiler.api.DefaultCompilationProblem;
import com.google.code.sbt.compiler.api.DefaultSourcePosition;
import com.google.code.sbt.compiler.api.SourcePosition;

import org.codehaus.plexus.component.annotations.Component;

import xsbti.CompileFailed;
import xsbti.Maybe;
import xsbti.Position;
import xsbti.Problem;

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
    public boolean supportsSourcePositionMappers()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Analysis performCompile( CompilerConfiguration configuration )
        throws CompilerException
    {
        CompilerLogger logger = configuration.getLogger();
        SBT012Logger sbtLogger = new SBT012Logger( logger );
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
                           configuration.getAnalysisCacheMap(), configuration.getCompileOrder(),
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
            CompilationProblem[] problems = getScalacProblems( e.problems() );
            if ( problems.length == 0 )
            {
                String[] consoleErrorLines = sbtLogger.getConsoleErrorLines();
                problems = getJavacProblems( consoleErrorLines );
            }

            throw new CompilerException( "Scala compilation failed", e, problems );
        }
    }

    // scalac problems conversion

    private CompilationProblem[] getScalacProblems( Problem[] problems )
    {
        CompilationProblem[] result = new CompilationProblem[problems.length];
        for ( int i = 0; i < problems.length; i++ )
        {
            Problem problem = problems[i];
            Position position = problem.position();

            Maybe<Integer> line = position.line();
            String lineContent = position.lineContent();
            Maybe<Integer> offset = position.offset();
            Maybe<Integer> pointer = position.pointer();
            Maybe<File> sourceFile = position.sourceFile();
            SourcePosition sp =
                new DefaultSourcePosition( line.isDefined() ? line.get().intValue() : -1, lineContent,
                                           offset.isDefined() ? offset.get().intValue() : -1,
                                           pointer.isDefined() ? pointer.get().intValue() : -1,
                                           sourceFile.isDefined() ? sourceFile.get() : null );
            result[i] =
                new DefaultCompilationProblem( problem.category(), problem.message(), sp, problem.severity().name() );
        }
        return result;
    }

    // javac problems parsing from error logs

    private static final Pattern JAVAC_ERROR = Pattern.compile( "\\s*(.*[.]java):(\\d+):\\s*(.*)" );
    private static final Pattern JAVAC_ERROR_POSITION = Pattern.compile( "(\\s*)\\^\\s*" );
    private static final Pattern JAVAC_ERROR_INFO = Pattern.compile( "\\s+([a-z ]+):(.*)" );

    private CompilationProblem[] getJavacProblems( String[] consoleErrorLines )
    {
        List<CompilationProblem> problems = new ArrayList<CompilationProblem>();

        int i = 0;
        while ( i < consoleErrorLines.length )
        {
            String line = consoleErrorLines[i];
            Matcher matcher = JAVAC_ERROR.matcher( line );
            if ( matcher.find() )
            {
                File file = new File( matcher.group( 1 ) );
                int lineNo = Integer.parseInt( matcher.group( 2 ) );
                String message = matcher.group( 3 );
                if ( message.startsWith( "error: " ) )
                {
                    message = message.substring( "error: ".length() );
                }
                String lineContent = null;
                int pointer = -1;
                i++;
                if ( i < consoleErrorLines.length )
                {
                    lineContent = consoleErrorLines[i];
                    i++;
                    // Java6 has different error line order - additional message lines before content and pointer 
                    while ( i < consoleErrorLines.length )
                    {
                        line = consoleErrorLines[i];
                        i++;
                        matcher = JAVAC_ERROR_POSITION.matcher( line );
                        if ( matcher.find() )
                        {
                            break;
                        }
                        else
                        {
                            message = message + "\n  " + lineContent; // this wasn't lineContent yet
                            lineContent = line;
                        }
                    }
                    pointer = matcher.group( 1 ).length();
                    while ( i < consoleErrorLines.length )
                    {
                        line = consoleErrorLines[i];
                        matcher = JAVAC_ERROR_INFO.matcher( line );
                        if ( matcher.matches() )
                        {
                            message = message + '\n' + line;
                            i++;
                        }
                        else
                        {
                            break;
                        }
                    }
                }
                SourcePosition position = new DefaultSourcePosition( lineNo, lineContent, -1, pointer, file );
                CompilationProblem problem = new DefaultCompilationProblem( "", message, position, "Error" );
                problems.add( problem );
            }
            else
            {
                i++;
            }
        }

        return problems.toArray( new CompilationProblem[0] );
    }

}
