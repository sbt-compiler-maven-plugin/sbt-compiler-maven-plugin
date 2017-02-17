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

package com.google.code.sbt.compiler.plugin;

import java.io.IOException;
import java.util.Collection;

import com.google.code.sbt.compiler.api.SourcePosition;
import com.google.code.sbt.compiler.api.SourcePositionMapper;

/**
 * Wwrapper for a collection of source position mappers.<br>
 * <br>
 * When mapping the position it iterates over contained mappers until one of them returns
 * not {@code null} result.
 *
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SourcePositionMapperCollection
    implements SourcePositionMapper
{

    private Collection<SourcePositionMapper> mappers;

    /**
     * Creates source position mappers collection.
     * 
     * @param mappers source position mappers
     */
    public SourcePositionMapperCollection( Collection<SourcePositionMapper> mappers )
    {
        this.mappers = mappers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharsetName( String charsetName )
    {
        for ( SourcePositionMapper mapper: mappers )
        {
            mapper.setCharsetName( charsetName );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SourcePosition map( SourcePosition sp ) throws IOException
    {
        SourcePosition result = null;
        for ( SourcePositionMapper mapper: mappers )
        {
            result = mapper.map( sp );
            if ( result != null )
            {
                break;
            }
        }
        return result;
    }

}