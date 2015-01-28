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
 * Helper class.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class Compilers
{
    private static final String CLASSES = "classes";
    
    private static final String TEST_CLASSES = "test-classes";
    
    private static final String CACHE = "cache";
    
    private Compilers()
    {
    }

    /**
     * Returns compiler ID compatible with given SBT or Play&#33; Framework version. 
     * 
     * @param sbtVersion SBT version
     * @param playVersion Play&#33; Framework version
     * 
     * @return compiler ID
     */
    public static String getDefaultCompilerId( String sbtVersion, String playVersion )
    {
        String result = null;
        if ( sbtVersion != null && !sbtVersion.isEmpty() )
        {
            if ( sbtVersion.startsWith( "0.13." ) )
            {
                if ( sbtVersion.equals( "0.13.0" ) || sbtVersion.startsWith( "0.13.0-" ) )
                {
                    result = "sbt013";
                }
                else if ( sbtVersion.equals( "0.13.1" ) || sbtVersion.startsWith( "0.13.1-" ) )
                {
                    result = "sbt0131";
                }
                else if ( sbtVersion.equals( "0.13.2" ) || sbtVersion.startsWith( "0.13.2-" ) )
                {
                    result = "sbt0132";
                }
                else
                {
                    result = "sbt0135";
                }
            }
            else if ( sbtVersion.startsWith( "0.12." ) )
            {
                result = "sbt012";
            }
        }
        if ( result == null )
        {
            if ( playVersion != null && !playVersion.isEmpty() )
            {
                if ( playVersion.startsWith( "2.1." ) || playVersion.startsWith( "2.1-" ) )
                {
                    result = "sbt012";
                }
                else if ( playVersion.startsWith( "2.2." ) || playVersion.startsWith( "2.2-" ) )
                {
                    result = "sbt013";
                }
                else if ( playVersion.startsWith( "2.3." ) || playVersion.startsWith( "2.3-" ) )
                {
                    result = "sbt0135";
                }
            }
        }
        if ( result == null )
        {
            result = "sbt0135"; // use latest version
        }
        return result;
    }

    /**
     * Returns directory for incremental compilation cache files.
     * 
     * @param classesDirectory compilation output directory 
     * @return directory for incremental compilation cache files
     */
    public static File getCacheDirectory( File classesDirectory )
    {
        String classesDirectoryName = classesDirectory.getName();
        String cacheDirectoryName = classesDirectoryName.replace( TEST_CLASSES, CACHE ).replace( CLASSES, CACHE );
        return new File( classesDirectory.getParentFile(), cacheDirectoryName );
    }

}
