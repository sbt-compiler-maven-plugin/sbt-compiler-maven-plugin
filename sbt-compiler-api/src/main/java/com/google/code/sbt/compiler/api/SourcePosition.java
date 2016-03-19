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

/**
 * Compilation error/warning position.
 * <br>
 * <br>
 * Based on <a href="https://github.com/sbt/sbt/blob/v0.13.0/interface/src/main/java/xsbti/Position.java">SBT Position interface</a>.
 * <br>
 * Used by {@link SourcePositionMapper} implementations.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface SourcePosition
{

    /**
     * One-based line number or value less than one if line number not specified.
     *
     * @return line number
     */
    int getLine();

    /**
     * Line content or empty string if line number not specified.
     *
     * @return line content
     */
    String getLineContent();

    /**
     * Zero-based offset in characters from the beginning of the file or value less than zero if not specified.
     *
     * @return file offset
     */
    int getOffset();

    /**
     * One-based position in the line or value less than one if position not specified.
     *
     * @return position in the line
     */
    int getPointer();

    /**
     * File.
     *
     * @return file
     */
    File getFile();

}
