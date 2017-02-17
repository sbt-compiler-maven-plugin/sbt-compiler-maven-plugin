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

package com.google.code.sbt.compiler.plugin;

import org.apache.maven.plugin.logging.Log;

import com.google.code.sbt.compiler.api.CompilerLogger;

/**
 * Maven {@link org.apache.maven.plugin.logging.Log} wrapper implementing {@link CompilerLogger} interface.
 *
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class MavenCompilerLogger
    implements CompilerLogger
{
    private Log mavenLogger;

    /**
     * Creates {@link CompilerLogger} wrapper around Maven {@link Log} delegate.
     * 
     * @param mavenLogger Maven {@link Log} delegate
     */
    public MavenCompilerLogger( Log mavenLogger )
    {
        this.mavenLogger = mavenLogger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled()
    {
        return mavenLogger.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug( String content )
    {
        mavenLogger.debug( content );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug( Throwable throwable )
    {
        mavenLogger.debug( throwable );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled()
    {
        return mavenLogger.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info( String content )
    {
        mavenLogger.info( content );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled()
    {
        return mavenLogger.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn( String content )
    {
        mavenLogger.warn( content );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled()
    {
        return mavenLogger.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error( String content )
    {
        mavenLogger.error( content );
    }

}