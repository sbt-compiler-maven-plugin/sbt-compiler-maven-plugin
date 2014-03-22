/*
 * Copyright 2013-2014 Grzegorz Slowikowski (gslowikowski at gmail dot com)
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
import java.util.List;
import java.util.Map;

//import org.apache.maven.plugin.logging.Log;

/**
 * ...
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class CompilerConfiguration
{
    /**
     * The -encoding argument for Scala and Java compilers.
     * 
     * @since 1.0.0
     */
    private String sourceEncoding;

    /**
     * Additional parameters for Java compiler.
     * 
     * @since 1.0.0
     */
    private String javacOptions;

    /**
     * Additional parameters for Scala compiler.
     * 
     * @since 1.0.0
     */
    private String scalacOptions;

    
    private List<File> sourceFiles;

    private File scalaLibraryFile;

    private File scalaCompilerFile;

    private List<File> scalaExtraJars;

    private File xsbtiFile;

    private File compilerInterfaceSrcFile;

    private List<File> classpathFiles;

    private CompilerLogger log;

    private File outputDirectory;

    private File analysisCacheFile;

    private Map<File, File> analysisCacheMap;


    public String getSourceEncoding()
    {
        return sourceEncoding;
    }

    public void setSourceEncoding( String sourceEncoding )
    {
        this.sourceEncoding = sourceEncoding;
    }


    public String getJavacOptions()
    {
        return javacOptions;
    }

    public void setJavacOptions( String javacOptions )
    {
        this.javacOptions = javacOptions;
    }


    public String getScalacOptions()
    {
        return scalacOptions;
    }

    public void setScalacOptions( String scalacOptions )
    {
        this.scalacOptions = scalacOptions;
    }


    public List<File> getSourceFiles()
    {
        return sourceFiles;
    }

    public void setSourceFiles( List<File> sourceFiles )
    {
        this.sourceFiles = sourceFiles;
    }


    public File getScalaLibraryFile()
    {
        return scalaLibraryFile;
    }

    public void setScalaLibraryFile( File scalaLibraryFile )
    {
        this.scalaLibraryFile = scalaLibraryFile;
    }


    public File getScalaCompilerFile()
    {
        return scalaCompilerFile;
    }

    public void setScalaCompilerFile( File scalaCompilerFile )
    {
        this.scalaCompilerFile = scalaCompilerFile;
    }


    public List<File> getScalaExtraJarFiles()
    {
        return scalaExtraJars;
    }

    public void setScalaExtraJarFiles( List<File> scalaExtraJars )
    {
        this.scalaExtraJars = scalaExtraJars;
    }


    public File getXsbtiFile()
    {
        return xsbtiFile;
    }

    public void setXsbtiFile( File xsbtiFile )
    {
        this.xsbtiFile = xsbtiFile;
    }


    public File getCompilerInterfaceSrcFile()
    {
        return compilerInterfaceSrcFile;
    }

    public void setCompilerInterfaceSrcFile( File compilerInterfaceSrcFile )
    {
        this.compilerInterfaceSrcFile = compilerInterfaceSrcFile;
    }


    public List<File> getClasspathFiles()
    {
        return classpathFiles;
    }

    public void setClasspathFiles( List<File> classpathFiles )
    {
        this.classpathFiles = classpathFiles;
    }


    public CompilerLogger getLogger()
    {
        return log;
    }

    public void setLogger( CompilerLogger log )
    {
        this.log = log;
    }


    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }


    public File getAnalysisCacheFile()
    {
        return analysisCacheFile;
    }

    public void setAnalysisCacheFile( File analysisCacheFile )
    {
        this.analysisCacheFile = analysisCacheFile;
    }


    public Map<File, File> getAnalysisCacheMap()
    {
        return analysisCacheMap;
    }

    public void setAnalysisCacheMap( Map<File, File> analysisCacheMap )
    {
        this.analysisCacheMap = analysisCacheMap;
    }

}
