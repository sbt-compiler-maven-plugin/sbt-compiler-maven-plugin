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

import xsbti.Maybe;
import xsbti.Position;

import com.google.code.sbt.compiler.api.SourcePosition;

/**
 * SBT <a href="http://www.scala-sbt.org/0.13.12/api/index.html#xsbti.Position">xsbti.Position</a>
 * wrapper around {@link SourcePosition} delegate.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class SBT013Position
    implements Position
{
    private SourcePosition sourcePosition;

    /**
     * Creates SBT <a href="http://www.scala-sbt.org/0.13.12/api/index.html#xsbti.Position">xsbti.Position</a>
     * wrapper around {@link SourcePosition} delegate.
     * 
     * @param sourcePosition {@link SourcePosition} delegate
     */
    public SBT013Position( SourcePosition sourcePosition )
    {
        this.sourcePosition = sourcePosition;
    }

    @Override
    public Maybe<Integer> line()
    {
        int line = sourcePosition.getLine();
        return line > 0 ? Maybe.just( Integer.valueOf( line ) ) : Maybe.<Integer>nothing();
    }

    @Override
    public String lineContent()
    {
        String lineContent = sourcePosition.getLineContent();
        return lineContent != null ? lineContent : "";
    }

    @Override
    public Maybe<Integer> offset()
    {
        int offset = sourcePosition.getOffset();
        return offset >= 0 ? Maybe.just( Integer.valueOf( offset ) ) : Maybe.<Integer>nothing();
    }

    @Override
    public Maybe<Integer> pointer()
    {
        int column = sourcePosition.getPointer();
        return column > 0 ? Maybe.just( Integer.valueOf( column ) ) : Maybe.<Integer>nothing();
    }

    @Override
    public Maybe<String> pointerSpace()
    {
        String pointerSpace = null;
        int col = sourcePosition.getPointer();
        String content = sourcePosition.getLineContent();
        if ( col > 0 && content != null )
        {
            int pointerSpaceLength = Math.min( col, content.length() );
            StringBuilder sb = new StringBuilder( pointerSpaceLength );
            for ( int i = 0; i < pointerSpaceLength; i++ )
            {
                sb.append( content.charAt( i ) == '\t' ? '\t' : ' ' );
            }
            pointerSpace = sb.toString();
        }
        return pointerSpace != null ? Maybe.just( pointerSpace ) : Maybe.<String>nothing();
    }

    @Override
    public Maybe<String> sourcePath()
    {
        File sourceFile = sourcePosition.getFile();
        return sourceFile != null ? Maybe.just( sourceFile.getAbsolutePath() ) : Maybe.<String>nothing();
    }

    @Override
    public Maybe<File> sourceFile()
    {
        File sourceFile = sourcePosition.getFile();
        return sourceFile != null ? Maybe.just( sourceFile ) : Maybe.<File>nothing();
    }

}