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

import org.apache.maven.plugin.logging.Log;
import xsbti.F0;
import xsbti.Logger;

/**
 * Maven Logger wrapper implementing SBT Logger interface.
 */
public class SBT012Logger
    implements Logger
{

    Log mavenLogger;

    /**
     * Creates SBT Logger wrapper around Maven Log implementation.
     * @param l Maven logger delegate
     */
    public SBT012Logger( Log l )
    {
        this.mavenLogger = l;
    }

    /**
     * Send a message to the user in the <b>error</b> error level
     * if the <b>error</b> error level is enabled.
     *
     * @param msg message
     */
    public void error( F0<String> msg )
    {
        if ( mavenLogger.isErrorEnabled() )
        {
            mavenLogger.error( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>wanr</b> error level
     * if the <b>wanr</b> error level is enabled.
     *
     * @param msg message
     */
    public void warn( F0<String> msg )
    {
        if ( mavenLogger.isWarnEnabled() )
        {
            mavenLogger.warn( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>info</b> error level
     * if the <b>info</b> error level is enabled.
     *
     * @param msg message
     */
    public void info( F0<String> msg )
    {
        if ( mavenLogger.isInfoEnabled() )
        {
            mavenLogger.info( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>debug</b> error level
     * if the <b>debug</b> error level is enabled.
     *
     * @param msg message
     */
    public void debug( F0<String> msg )
    {
        if ( mavenLogger.isDebugEnabled() )
        {
            mavenLogger.debug( msg.apply() );
        }
    }

    /**
     * Send a message to the user in the <b>trace</b> error level
     * if the <b>trace</b> error level is enabled.
     *
     * @param exception exception thrown
     */
    public void trace( F0<Throwable> exception )
    {
        if ( mavenLogger.isDebugEnabled() )
        {
            mavenLogger.debug( "", exception.apply() );
        }
    }

}
