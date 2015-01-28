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

/**
 * SBT incremental compilation analysis processor interface.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public interface AnalysisProcessor
{
    /**
     * Returns information if this compiler stores class file time stamps
     * in the incremental compilation analysis.
     * 
     *  @return information if class file time stamps are stored in the analysis
     */
    boolean areClassFileTimestampsSupported();

    /**
     * Reads incremental compilation analysis from cache file.
     * 
     * @param analysisCacheFile the file to read analysis from
     * 
     * @return incremental compilation analysis object
     */
    Analysis readFromFile( File analysisCacheFile );

}
