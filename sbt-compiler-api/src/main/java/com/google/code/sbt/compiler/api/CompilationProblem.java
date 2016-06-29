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

/**
 * Compilation error/warning.
 * <br>
 * <br>
 * Based on <a href="https://github.com/sbt/sbt/blob/v0.13.0/interface/src/main/java/xsbti/Problem.java">SBT Problem interface</a>.
 * <br>
 * Contained by {@link CompilerException}.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface CompilationProblem
{

    /**
     * Problem category
     *
     * @return compilation problem category
     */
    String getCategory();

    /**
     * Problem message
     *
     * @return compilation problem message
     */
    String getMessage();

    /**
     * Problem position
     *
     * @return compilation problem position
     */
    SourcePosition getPosition();

    /**
     * Problem severity
     *
     * @return compilation problem severity
     */
    String getSeverity();

}
