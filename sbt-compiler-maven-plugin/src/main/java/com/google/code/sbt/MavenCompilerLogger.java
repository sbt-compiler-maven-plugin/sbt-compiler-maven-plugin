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

package com.google.code.sbt;

import org.apache.maven.plugin.logging.Log;

import com.google.code.sbt.compiler.CompilerLogger;

/**
 * Maven org.apache.maven.plugin.logging.Log wrapper implementing CompilerLogger interface.
 *
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class MavenCompilerLogger
    implements CompilerLogger
{
    private Log delegate;

    public MavenCompilerLogger(Log delegate)
    {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDebugEnabled()
    {
        return delegate.isDebugEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void debug( CharSequence content )
    {
        delegate.debug( content );
    }

    /**
     * {@inheritDoc}
     */
    public void debug( Throwable throwable )
    {
        delegate.debug( throwable );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInfoEnabled()
    {
        return delegate.isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void info( CharSequence content )
    {
        delegate.info( content );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWarnEnabled()
    {
        return delegate.isWarnEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void warn( CharSequence content )
    {
        delegate.warn( content );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isErrorEnabled()
    {
        return delegate.isErrorEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void error( CharSequence content )
    {
        delegate.error( content );
    }

}