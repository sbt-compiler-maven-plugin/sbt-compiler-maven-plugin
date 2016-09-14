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

package com.google.code.sbt.compiler.sbt013;

import java.io.File;

import scala.runtime.AbstractFunction1;

import xsbti.Maybe;
import xsbti.Position;

import com.google.code.sbt.compiler.api.CompilerLogger;
import com.google.code.sbt.compiler.api.DefaultSourcePosition;
import com.google.code.sbt.compiler.api.SourcePosition;
import com.google.code.sbt.compiler.api.SourcePositionMapper;

/**
 * SBT (<a href="http://www.scala-sbt.org/0.13.12/api/index.html#xsbti.Position">xsbti.Position</a>) =&gt;
 * <a href="http://www.scala-sbt.org/0.13.12/api/index.html#xsbti.Position">xsbti.Position</a>
 * wrapper around {@link SourcePositionMapper} delegate.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SBT013SourcePositionMapper extends AbstractFunction1<Position, Position>
{
    private SourcePositionMapper mapper;

    private CompilerLogger logger;

    /**
     * Creates SBT
     * (<a href="http://www.scala-sbt.org/0.13.12/api/index.html#xsbti.Position">xsbti.Position</a>) =&gt;
     * <a href="http://www.scala-sbt.org/0.13.12/api/index.html#xsbti.Position">xsbti.Position</a>
     * wrapper around {@link SourcePositionMapper} delegate.
     * 
     * @param mapper {@link SourcePositionMapper} delegate
     * @param logger {@link CompilerLogger} used to debug possible problems
     */
    public SBT013SourcePositionMapper( SourcePositionMapper mapper, CompilerLogger logger )
    {
        this.mapper = mapper;
        this.logger = logger;
    }

    /**
     * Performs mapping from the position in generated source file to the position in original source file it was
     * generated from.<br>
     * <br>
     * If no position in original source file can be found, input position is returned.
     * <br>
     * Position in source file cannot be found in two cases:
     * <ul>
     * <li>file was not generated,</li>
     * <li>no appropriate mapper was configured.</li>
     * </ul>
     *
     * @param position compilation error/warning position in generated file
     * @return position in source file or input position if no source position can be found
     * @return position in original source file or input position if position in original source file cannot be found
     */
    @Override
    public Position apply( Position position )
    {
        Position result = position;
        if ( mapper != null )
        {
            Maybe<Integer> line = position.line();
            String lineContent = position.lineContent();
            Maybe<Integer> offset = position.offset();
            Maybe<Integer> pointer = position.pointer();
            Maybe<File> sourceFile = position.sourceFile();
            SourcePosition sp =
                new DefaultSourcePosition( line.isDefined() ? line.get().intValue() : -1, lineContent,
                                           offset.isDefined() ? offset.get().intValue() : -1,
                                           pointer.isDefined() ? pointer.get().intValue() : -1,
                                           sourceFile.isDefined() ? sourceFile.get() : null );
            try
            {
                SourcePosition mappedPosition = mapper.map( sp );
                if ( mappedPosition != null )
                {
                    result = new SBT013Position( mappedPosition );
                }
            }
            catch ( Throwable t )
            {
                logger.debug( t );
            }
        }
        return result;
    }

}