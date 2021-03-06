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

package com.google.code.sbt.compiler.api;

import java.io.Serializable;

/**
 * Compilation error/warning problem simple implementation.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class DefaultCompilationProblem implements CompilationProblem, Serializable
{
    private static final long serialVersionUID = 1L;

    private String category;

    private String message;

    private SourcePosition position;

    private String severity;

    /**
     * Creates instance of compilation error/warning problem.
     * 
     * @param category problem category
     * @param message problem message
     * @param position problem position
     * @param severity problem severity ("Error" or "Warning")
     */
    public DefaultCompilationProblem( String category, String message, SourcePosition position, String severity )
    {
        this.category = category;
        this.message = message;
        this.position = position;
        this.severity = severity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCategory()
    {
        return category;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage()
    {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourcePosition getPosition()
    {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSeverity()
    {
        return severity;
    }

}
