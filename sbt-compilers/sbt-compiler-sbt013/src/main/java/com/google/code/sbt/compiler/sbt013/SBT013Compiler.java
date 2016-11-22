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

package com.google.code.sbt.compiler.sbt013;

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
import com.google.code.sbt.compiler.api.SourcePositionMapper;

import org.codehaus.plexus.component.annotations.Component;

import scala.Function1;
import scala.Option;

import xsbti.CompileFailed;
import xsbti.Maybe;
import xsbti.Position;
import xsbti.Problem;
import xsbti.Reporter;

import com.typesafe.zinc.Compiler;
import com.typesafe.zinc.IncOptions;
import com.typesafe.zinc.Inputs;
import com.typesafe.zinc.Setup;

/**
 * SBT 0.13.x compatible compiler (uses <a href="https://github.com/typesafehub/zinc">Zinc</a> 0.3.13)
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
@Component( role = com.google.code.sbt.compiler.api.Compiler.class, hint = "sbt013", description = "SBT 0.13.x compiler (uses Zinc 0.3.13)" )
public class SBT013Compiler
    extends AbstractCompiler
{
    /**
     * SBT compilation order.
     */
    private static final String COMPILE_ORDER = "mixed";

    /**
     * Run javac compilation in forked JVM.
     */
    private static final boolean FORK_JAVA = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultScalaVersion()
    {
        return "2.10.6";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefaultSbtVersion()
    {
        return "0.13.13";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsSourcePositionMappers()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Analysis performCompile( CompilerConfiguration configuration )
        throws CompilerException
    {
        CompilerLogger logger = configuration.getLogger();
        SBT013Logger sbtLogger = new SBT013Logger( logger );
        Setup setup =
            Setup.create( configuration.getScalaCompilerFile(), configuration.getScalaLibraryFile(),
                          configuration.getScalaExtraJarFiles(), configuration.getXsbtiFile(),
                          configuration.getCompilerInterfaceSrcFile(), null, SBT013Compiler.FORK_JAVA );
        if ( logger.isDebugEnabled() )
        {
            Setup.debug( setup, sbtLogger );
        }
        Compiler compiler = Compiler.create( setup, sbtLogger );

        Inputs inputs =
            Inputs.create( configuration.getClasspathFiles(), configuration.getSourceFiles(),
                           configuration.getOutputDirectory(), resolveScalacOptions( configuration ),
                           resolveJavacOptions( configuration ), configuration.getAnalysisCacheFile(),
                           configuration.getAnalysisCacheMap(), SBT013Compiler.COMPILE_ORDER,
                           getIncOptions( configuration ), false /* mirrorAnalysisCache */ );
        if ( logger.isDebugEnabled() )
        {
            Inputs.debug( inputs, sbtLogger );
        }

        try
        {
            SourcePositionMapper mapper = configuration.getSourcePositionMapper();
            Function1<Position, Position> sourcePositionMapper = new SBT013SourcePositionMapper( mapper, logger );
            Reporter reporter =
                new sbt.LoggerReporter( getMaximumErrors(), sbt.Logger$.MODULE$.xlog2Log( sbtLogger ),
                                        sourcePositionMapper );
            return new SBT013Analysis( compiler.compile( inputs, Option.<File> empty(), reporter, sbtLogger ) );
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

    private IncOptions getIncOptions( CompilerConfiguration configuration )
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
        boolean transactional = true;

        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Backup location (if transactional)
        Option<File> backup =
            Option.apply( new File( configuration.getOutputDirectory().getParentFile(),
                                    configuration.getOutputDirectory().getName() + ".bak" ) );

        // comment from SBT (sbt.inc.IncOptions scala):
        // Determines whether incremental compiler should recompile all dependencies of a file
        // that contains a macro definition.
        //
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Enable recompilation of all dependencies of a macro def
        boolean recompileOnMacroDef = true;

        // comment from SBT (sbt.inc.IncOptions scala):
        // Determines whether incremental compiler uses the new algorithm known as name hashing.
        //
        // IMPLEMENTATION NOTE:
        // Enabling this flag enables a few additional functionalities that are needed by the name hashing algorithm:
        //
        //   1. New dependency source tracking is used. See `sbt.inc.Relations` for details.
        //   2. Used names extraction and tracking is enabled. See `sbt.inc.Relations` for details as well.
        //   3. Hashing of public names is enabled. See `sbt.inc.AnalysisCallback` for details.
        // comment from Zinc (com.typesafe.zinc.Settings.scala):
        // Enable improved (experimental) incremental compilation algorithm
        boolean nameHashing = true;

        return new IncOptions( transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
                               apiDumpDirectory, transactional, backup, recompileOnMacroDef, nameHashing );
    }

    private int getMaximumErrors()
    {
        return 100;
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
