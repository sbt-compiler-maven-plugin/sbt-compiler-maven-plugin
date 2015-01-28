/*
 * Copyright 2013-2015 Grzegorz Slowikowski (gslowikowski at gmail dot com)
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
import java.util.Set;

/**
 * Incremental compilation analysis interface.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface Analysis
{
    /**
     * Returns source file's compilation time.
     *
     * @param sourceFile source file
     *
     * @return source file's compilation time
     */
    long getCompilationTime( File sourceFile );

    /**
     * Returns source file's compilation products (class files).
     *
     * @param sourceFile source file
     *
     * @return source file's compilation products (class files).
     */
    Set<File> getProducts( File sourceFile );

    /**
     * Updates class file time stamp written in incremental compilation analysis
     * to the value returned by {@link File#lastModified()}.
     * 
     * Not all compilers store file time stamps in the analysis,
     * but for the ones that do it, it's important that this information
     * is always up to date.
     * 
     * Class file is changed if it is processed after compilation.
     * If it's new time stamp will not be stored in the analysis cache file
     * the next time compilation runs, SBT compiler will unnecessarily recompile the file.
     * 
     * @param classFile class file
     */
    void updateClassFileTimestamp( File classFile );

    /**
     * Stores incremental compilation analysis in the file.
     * 
     * @param analysisCacheFile the file to store analysis in
     */
    void writeToFile( File analysisCacheFile );

    /**
     * Returns SBT native incremental compilation object.
     *  
     * @return SBT native incremental compilation
     */
    Object unwrap();

}
