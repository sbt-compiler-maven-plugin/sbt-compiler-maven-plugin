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

package com.google.code.sbt.compiler;

/**
 * This interface supplies the API for providing feedback to the user from the <code>Mojo</code>, using standard
 * <code>Maven</code> channels.
 * <br/>
 *
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface CompilerLogger
{
    /**
     * @return true if the <b>debug</b> error level is enabled
     */
    boolean isDebugEnabled();

    /**
     * Send a message to the user in the <b>debug</b> error level.
     *
     * @param content
     */
    void debug( CharSequence content );

    /**
     * Send an exception to the user in the <b>debug</b> error level.
     * <br/>
     * The stack trace for this exception will be output when this error level is enabled.
     *
     * @param error
     */
    void debug( Throwable error );

    /**
     * @return true if the <b>info</b> error level is enabled
     */
    boolean isInfoEnabled();

    /**
     * Send a message to the user in the <b>info</b> error level.
     *
     * @param content
     */
    void info( CharSequence content );

    /**
     * @return true if the <b>warn</b> error level is enabled
     */
    boolean isWarnEnabled();

    /**
     * Send a message to the user in the <b>warn</b> error level.
     *
     * @param content
     */
    void warn( CharSequence content );

    /**
     * @return true if the <b>error</b> error level is enabled
     */
    boolean isErrorEnabled();

    /**
     * Send a message to the user in the <b>error</b> error level.
     *
     * @param content
     */
    void error( CharSequence content );

}