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

package com.google.code.sbt.compiler.sbt013;

import java.io.File;
import java.util.Set;

import scala.collection.JavaConversions;

import sbt.inc.AnalysisStore;
import sbt.inc.LastModified;
import sbt.inc.Stamp;
import sbt.inc.Stamps;

import com.typesafe.zinc.Compiler;

import com.google.code.sbt.compiler.api.Analysis;

/**
 * {@link Analysis} wrapper around
 * SBT <a href="http://www.scala-sbt.org/0.13.15/api/index.html#sbt.inc.Analysis">sbt.inc.Analysis</a> delegate.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SBT013Analysis
    implements Analysis
{
    private sbt.inc.Analysis analysis;
    private Stamps stamps;

    /**
     * Creates {@link Analysis} wrapper around
     * SBT <a href="http://www.scala-sbt.org/0.13.15/api/index.html#sbt.inc.Analysis">sbt.inc.Analysis</a> delegate.
     * 
     * @param analysis SBT {@code sbt.inc.Analysis} delegate
     */
    public SBT013Analysis( sbt.inc.Analysis analysis )
    {
        this.analysis = analysis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeToFile( File analysisCacheFile )//use Zinc method (if there is any)?
    {
        if ( stamps != null ) // stamps were modified, merge now
        {
            analysis =
                analysis.copy( stamps, analysis.apis(), analysis.relations(), analysis.infos(), analysis.compilations() );
            stamps = null;
        }
        AnalysisStore analysisStore = Compiler.analysisStore( analysisCacheFile );
        analysisStore.set( analysis, analysisStore.get().get()._2 /* compileSetup */ );
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
        if ( stamps == null )
        {
            stamps = analysis.stamps();
        }
        Stamp existingStamp = stamps.product( classFile );
        if ( existingStamp != null && existingStamp instanceof LastModified )
        {
            Stamp newStamp = new LastModified( classFile.lastModified() );
            stamps = stamps.markProduct( classFile, newStamp );
        }
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
