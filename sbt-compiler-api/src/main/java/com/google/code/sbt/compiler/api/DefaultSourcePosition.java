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

package com.google.code.sbt.compiler.api;

import java.io.File;
import java.io.Serializable;

/**
 * Compilation error/warning position simple implementation.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class DefaultSourcePosition implements SourcePosition, Serializable
{
    private static final long serialVersionUID = 1L;

    private int line;

    private String lineContent;

    private int offset;

    private int pointer;

    private File file;

    /**
     * Creates instance of error/warning position.
     * 
     * @param line one-based line number
     * @param lineContent line content
     * @param offset zero-based offset in characters from the beginning of the file
     * @param pointer one-based position in the line
     * @param file file
     */
    public DefaultSourcePosition( int line, String lineContent, int offset, int pointer, File file )
    {
        this.line = line;
        this.lineContent = lineContent;
        this.offset = offset;
        this.pointer = pointer;
        this.file = file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLine()
    {
        return line;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLineContent()
    {
        return lineContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOffset()
    {
        return offset;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPointer()
    {
        return pointer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile()
    {
        return file;
    }

}
