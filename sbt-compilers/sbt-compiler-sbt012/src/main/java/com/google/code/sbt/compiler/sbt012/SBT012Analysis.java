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

package com.google.code.sbt.compiler.sbt012;

import java.io.File;
import java.util.Set;

import scala.collection.JavaConversions;

import sbt.inc.AnalysisStore;

import com.typesafe.zinc.Compiler;

import com.google.code.sbt.compiler.api.Analysis;

/**
 * {@link Analysis} wrapper around
 * SBT <a href="http://www.scala-sbt.org/0.12.4/api/index.html#sbt.inc.Analysis">sbt.inc.Analysis</a> delegate.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SBT012Analysis
    implements Analysis
{
    private sbt.inc.Analysis analysis;

    /**
     * Creates {@link Analysis} wrapper around
     * SBT <a href="http://www.scala-sbt.org/0.12.4/api/index.html#sbt.inc.Analysis">sbt.inc.Analysis</a> delegate.
     * 
     * @param analysis SBT native {@code sbt.inc.Analysis} delegate
     */
    public SBT012Analysis( sbt.inc.Analysis analysis )
    {
        this.analysis = analysis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToFile( File analysisCacheFile )
    {
        AnalysisStore analysisStore = Compiler.analysisStore( analysisCacheFile );
        analysisStore.set( analysis, analysisStore.get().get()._2/* compileSetup */ );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<File> getSourceFiles()
    {
        return JavaConversions.setAsJavaSet( analysis.apis().internal().keySet() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCompilationTime( File sourceFile )
    {
        return analysis.apis().internalAPI( sourceFile ).compilation().startTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<File> getProducts( File sourceFile )
    {
        return JavaConversions.setAsJavaSet( analysis.relations().products( sourceFile ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateClassFileTimestamp( File classFile )
    {
        // Class file time stamps not supported
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unwrap()
    {
        return analysis;
    }

}
