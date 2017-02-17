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

import java.io.IOException;

/**
 * Mapper converting positions in generated source files to positions in original sources they were generated from.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface SourcePositionMapper
{

    /**
     * Sets source files character set name (encoding).
     * 
     * @param charsetName source files character set name
     */
    void setCharsetName( String charsetName );

    /**
     * Performs mapping from the position in generated source file to the position in original source file it was
     * generated from.<br>
     * <br>
     * Returns:
     * <ul>
     * <li>position in original source file</li>
     * <li>
     * {@code null} value if file of the input position is not recognized as generated file supported by this
     * mapper.</li>
     * </ul>
     *
     * @param p compilation error/warning position in generated file
     * @return position in original source file or {@code null} value if file of the input position is not recognized as
     *         generated file supported by this mapper
     * @throws IOException I/O exception during generated or original source file reading
     */
    SourcePosition map( SourcePosition p ) throws IOException;

}
