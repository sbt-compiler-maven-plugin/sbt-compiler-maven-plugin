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

package com.google.code.sbt.compiler.sbt0137;

import com.google.code.sbt.compiler.api.CompilerLogger;

import xsbti.F0;
import xsbti.Logger;

/**
 * {@link CompilerLogger} wrapper implementing SBT Logger interface.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SBT0137Logger
    implements Logger
{

    CompilerLogger compilerLogger;

    /**
     * Creates SBT Logger wrapper around given {@link CompilerLogger} implementation.
     * 
     * @param compilerLogger {@link CompilerLogger} delegate
     */
    public SBT0137Logger( CompilerLogger compilerLogger )
    {
        this.compilerLogger = compilerLogger;
    }

    /**
     * Send a message to the user in the <b>error</b> error level
     * if the <b>error</b> error level is enabled.
     *
     * @param msg message
     */
    @Override
    public void error( F0<String> msg )
    {
        if ( compilerLogger.isErrorEnabled() )
        {
            compilerLogger.error( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>warn</b> error level
     * if the <b>warn</b> error level is enabled.
     *
     * @param msg message
     */
    @Override
    public void warn( F0<String> msg )
    {
        if ( compilerLogger.isWarnEnabled() )
        {
            compilerLogger.warn( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>info</b> error level
     * if the <b>info</b> error level is enabled.
     *
     * @param msg message
     */
    @Override
    public void info( F0<String> msg )
    {
        if ( compilerLogger.isInfoEnabled() )
        {
            compilerLogger.info( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>debug</b> error level
     * if the <b>debug</b> error level is enabled.
     *
     * @param msg message
     */
    @Override
    public void debug( F0<String> msg )
    {
        if ( compilerLogger.isDebugEnabled() )
        {
            compilerLogger.debug( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>trace</b> error level
     * if the <b>trace</b> error level is enabled.
     *
     * @param exception exception thrown
     */
    @Override
    public void trace( F0<Throwable> exception )
    {
        if ( compilerLogger.isDebugEnabled() )
        {
            compilerLogger.debug( exception.apply() );
        }
    }

}
