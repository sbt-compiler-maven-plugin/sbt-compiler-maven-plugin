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
 * Exception thrown if compilation fails.
 * 
 * @see Compiler#performCompile(CompilerConfiguration)
 */
public class CompilerException
    extends Exception
{
    private static final long serialVersionUID = 2L;

    private CompilationProblem[] problems;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message exception detail message
     */
    public CompilerException( String message )
    {
        super( message );
        this.problems = new CompilationProblem[0];
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message exception detail message
     * @param problems compilation problems
     */
    public CompilerException( String message, CompilationProblem[] problems )
    {
        super( message );
        this.problems = problems;
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message exception detail message
     * @param cause exception cause (nested throwable)
     */
    public CompilerException( String message, Throwable cause )
    {
        super( message, cause );
        this.problems = new CompilationProblem[0];
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param message exception detail message
     * @param cause exception cause (nested throwable)
     * @param problems compilation problems
     */
    public CompilerException( String message, Throwable cause, CompilationProblem[] problems )
    {
        super( message, cause );
        this.problems = problems;
    }

    /**
     * Returns compilation problems.
     *
     * @return compilation problems
     */
    public CompilationProblem[] getProblems()
    {
        return problems;
    }

}
