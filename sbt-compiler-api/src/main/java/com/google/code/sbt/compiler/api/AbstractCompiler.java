/*
 * Copyright 2013-2015 Grzegorz Slowikowski (gslowikowski at gmail dot com)
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

package com.google.code.sbt.compiler.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * An abstract base class for SBT compilers.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public abstract class AbstractCompiler
    implements Compiler
{
    /**
     * Returns merged <code>scalaOptions</code> and <code>sourceEncoding</code> configuration parameters.
     * 
     * @param configuration compiler configuration object
     * @return merged parameters
     */
    protected List<String> resolveScalacOptions( CompilerConfiguration configuration )
    {
        String scalacOptions = configuration.getScalacOptions();
        String sourceEncoding = configuration.getSourceEncoding();
        List<String> result = new ArrayList<String>( Arrays.asList( parseArgLine( scalacOptions ) ) );
        if ( !result.contains( "-encoding" ) && sourceEncoding != null && sourceEncoding.length() > 0 )
        {
            result.add( "-encoding" );
            result.add( sourceEncoding );
        }
        return result;
    }

    /**
     * Returns merged <code>javacOptions</code> and <code>sourceEncoding</code> configuration parameters.
     * 
     * @param configuration compiler configuration object
     * @return merged parameters
     */
    protected List<String> resolveJavacOptions( CompilerConfiguration configuration )
    {
        String javacOptions = configuration.getJavacOptions();
        String sourceEncoding = configuration.getSourceEncoding();
        List<String> result = new ArrayList<String>( Arrays.asList( parseArgLine( javacOptions ) ) );
        if ( !result.contains( "-encoding" ) && sourceEncoding != null && sourceEncoding.length() > 0 )
        {
            result.add( "-encoding" );
            result.add( sourceEncoding );
        }
        return result;
    }

    // Copied from commons-exec org.apache.commons.exec.CommandLine.translateCommandline(String) method
    // because it's private there and cannot be referenced from here.
    /**
     * Crack a command line.
     *
     * @param toProcess
     *            the command line to process
     * @return the command line broken into strings. An empty or null toProcess
     *         parameter results in a zero sized array
     */
    private String[] parseArgLine( final String toProcess )
    {
        if ( toProcess == null || toProcess.length() == 0 )
        {
            // no command? no string
            return new String[0];
        }

        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer( toProcess, "\"\' ", true );
        final ArrayList<String> list = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while ( tok.hasMoreTokens() )
        {
            final String nextTok = tok.nextToken();
            switch ( state )
            {
                case inQuote:
                    if ( "\'".equals( nextTok ) )
                    {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
                case inDoubleQuote:
                    if ( "\"".equals( nextTok ) )
                    {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    break;
                default:
                    if ( "\'".equals( nextTok ) )
                    {
                        state = inQuote;
                    }
                    else if ( "\"".equals( nextTok ) )
                    {
                        state = inDoubleQuote;
                    }
                    else if ( " ".equals( nextTok ) )
                    {
                        if ( lastTokenHasBeenQuoted || current.length() != 0 )
                        {
                            list.add( current.toString() );
                            current = new StringBuilder();
                        }
                    }
                    else
                    {
                        current.append( nextTok );
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }

        if ( lastTokenHasBeenQuoted || current.length() != 0 )
        {
            list.add( current.toString() );
        }

        if ( state == inQuote || state == inDoubleQuote )
        {
            throw new IllegalArgumentException( "Unbalanced quotes in " + toProcess );
        }

        final String[] args = new String[list.size()];
        return list.toArray( args );
    }
}
