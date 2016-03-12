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
 * SBT compiler interface.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface Compiler
{
    /**
     * Returns default Scala version for the compiler.
     * 
     * This default value is used when none of the below options is true:
     * <ul>
     * <li>plugin's <code>scalaVersion</code> configuration parameter is defined,</li>
     * <li>project's <code>scala.version</code> property is defined,</li>
     * <li>project contains <code>org.scala-lang:scala-library</code> dependency.</li>
     * </ul>
     *
     * @return default Scala version for the compiler
     */
    String getDefaultScalaVersion();

    /**
     * Returns default SBT version for the compiler.
     * 
     * This default value is used when none of the below options is true:
     * <ul>
     * <li>plugin's <code>sbtVersion</code> configuration parameter is defined,</li>
     * <li>project's <code>sbt.version</code> property is defined,</li>
     * </ul>
     *
     * @return default SBT version for the compiler
     */
    String getDefaultSbtVersion();

    /**
     * Performs the compilation of the project.
     * 
     * @param configuration SBT compilation configuration
     * 
     * @return incremental compilation analysis object
     * 
     * @throws CompilerException if compilation fails
     */
    Analysis performCompile( CompilerConfiguration configuration )
        throws CompilerException;

}
