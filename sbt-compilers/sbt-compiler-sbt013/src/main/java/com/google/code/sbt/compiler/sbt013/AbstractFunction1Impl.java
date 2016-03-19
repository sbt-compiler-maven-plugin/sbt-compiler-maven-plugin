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

import scala.Function1;
import scala.Function1$class;
import scala.runtime.BoxedUnit;

/**
 * Base <a href="https://github.com/scala/scala/blob/v2.10.5/src/library/scala/Function1.scala">scala.Function1</a>
 * trait Java implementation
 *
 * @param <T1> function return value class
 * @param <R> function parameter class
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public abstract class AbstractFunction1Impl<T1, R>
    implements Function1<T1, R>
{

    @Override
    public final double apply$mcDD$sp( double value )
    {
        return Function1$class.apply$mcDD$sp( this, value );
    }

    @Override
    public final double apply$mcDF$sp( float value )
    {
        return Function1$class.apply$mcDF$sp( this, value );
    }

    @Override
    public final double apply$mcDI$sp( int value )
    {
        return Function1$class.apply$mcDI$sp( this, value );
    }

    @Override
    public final double apply$mcDJ$sp( long value )
    {
        return Function1$class.apply$mcDJ$sp( this, value );
    }

    @Override
    public final float apply$mcFD$sp( double value )
    {
        return Function1$class.apply$mcFD$sp( this, value );
    }

    @Override
    public final float apply$mcFF$sp( float value )
    {
        return Function1$class.apply$mcFF$sp( this, value );
    }

    @Override
    public final float apply$mcFI$sp( int value )
    {
        return Function1$class.apply$mcFI$sp( this, value );
    }

    @Override
    public final float apply$mcFJ$sp( long value )
    {
        return Function1$class.apply$mcFJ$sp( this, value );
    }

    @Override
    public final int apply$mcID$sp( double value )
    {
        return Function1$class.apply$mcID$sp( this, value );
    }

    @Override
    public final int apply$mcIF$sp( float value )
    {
        return Function1$class.apply$mcIF$sp( this, value );
    }

    @Override
    public final int apply$mcII$sp( int value )
    {
        return Function1$class.apply$mcII$sp( this, value );
    }

    @Override
    public final int apply$mcIJ$sp( long value )
    {
        return Function1$class.apply$mcIJ$sp( this, value );
    }

    @Override
    public final long apply$mcJD$sp( double value )
    {
        return Function1$class.apply$mcJD$sp( this, value );
    }

    @Override
    public final long apply$mcJF$sp( float value )
    {
        return Function1$class.apply$mcJF$sp( this, value );
    }

    @Override
    public final long apply$mcJI$sp( int value )
    {
        return Function1$class.apply$mcJI$sp( this, value );
    }

    @Override
    public final long apply$mcJJ$sp( long value )
    {
        return Function1$class.apply$mcJJ$sp( this, value );
    }

    @Override
    public final void apply$mcVD$sp( double value )
    {
        Function1$class.apply$mcVD$sp( this, value );
    }

    @Override
    public final void apply$mcVF$sp( float value )
    {
        Function1$class.apply$mcVF$sp( this, value );
    }

    @Override
    public final void apply$mcVI$sp( int value )
    {
        Function1$class.apply$mcVI$sp( this, value );
    }

    @Override
    public final void apply$mcVJ$sp( long value )
    {
        Function1$class.apply$mcVJ$sp( this, value );
    }

    @Override
    public final boolean apply$mcZD$sp( double value )
    {
        return Function1$class.apply$mcZD$sp( this, value );
    }

    @Override
    public final boolean apply$mcZF$sp( float value )
    {
        return Function1$class.apply$mcZF$sp( this, value );
    }

    @Override
    public final boolean apply$mcZI$sp( int value )
    {
        return Function1$class.apply$mcZI$sp( this, value );
    }

    @Override
    public final boolean apply$mcZJ$sp( long value )
    {
        return Function1$class.apply$mcZJ$sp( this, value );
    }

    @Override
    public final <A> Function1<T1, A> andThen( Function1<R, A> g )
    {
        return Function1$class.andThen( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcDD$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcDD$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcDF$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcDF$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcDI$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcDI$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcDJ$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcDJ$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcFD$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcFD$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcFF$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcFF$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcFI$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcFI$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcFJ$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcFJ$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcID$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcID$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcIF$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcIF$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcII$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcII$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcIJ$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcIJ$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcJD$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcJD$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcJF$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcJF$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcJI$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcJI$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcJJ$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcJJ$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcVD$sp( Function1<BoxedUnit, A> g )
    {
        return Function1$class.andThen$mcVD$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcVF$sp( Function1<BoxedUnit, A> g )
    {
        return Function1$class.andThen$mcVF$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcVI$sp( Function1<BoxedUnit, A> g )
    {
        return Function1$class.andThen$mcVI$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcVJ$sp( Function1<BoxedUnit, A> g )
    {
        return Function1$class.andThen$mcVJ$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcZD$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcZD$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcZF$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcZF$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcZI$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcZI$sp( this, g );
    }

    @Override
    public final <A> Function1<Object, A> andThen$mcZJ$sp( Function1<Object, A> g )
    {
        return Function1$class.andThen$mcZJ$sp( this, g );
    }

    @Override
    public final <A> Function1<A, R> compose( Function1<A, T1> g )
    {
        return Function1$class.compose( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcDD$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcDD$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcDF$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcDF$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcDI$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcDI$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcDJ$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcDJ$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcFD$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcFD$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcFF$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcFF$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcFI$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcFI$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcFJ$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcFJ$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcID$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcID$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcIF$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcIF$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcII$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcII$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcIJ$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcIJ$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcJD$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcJD$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcJF$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcJF$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcJI$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcJI$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcJJ$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcJJ$sp( this, g );
    }

    @Override
    public final <A> Function1<A, BoxedUnit> compose$mcVD$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcVD$sp( this, g );
    }

    @Override
    public final <A> Function1<A, BoxedUnit> compose$mcVF$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcVF$sp( this, g );
    }

    @Override
    public final <A> Function1<A, BoxedUnit> compose$mcVI$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcVI$sp( this, g );
    }

    @Override
    public final <A> Function1<A, BoxedUnit> compose$mcVJ$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcVJ$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcZD$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcZD$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcZF$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcZF$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcZI$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcZI$sp( this, g );
    }

    @Override
    public final <A> Function1<A, Object> compose$mcZJ$sp( Function1<A, Object> g )
    {
        return Function1$class.compose$mcZJ$sp( this, g );
    }

}