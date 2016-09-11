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

package com.google.code.sbt.compiler.api;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * SBT compilation configuration.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public class CompilerConfiguration implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * The {@code -encoding} option for Scala and Java compilers.
     */
    private String sourceEncoding;

    /**
     * Java compiler options.
     */
    private String javacOptions;

    /**
     * Scala compiler options.
     */
    private String scalacOptions;

    /**
     * Java and Scala source files.
     */
    private List<File> sourceFiles;

    /**
     * Scala library {@code scala-library-<version>.jar} file.
     */
    private File scalaLibraryFile;

    /**
     * Scala compiler {@code scala-compiler-<version>.jar} file.
     */
    private File scalaCompilerFile;

    /**
     * Additional jar files to be added to Scala compiler classloader.
     */
    private List<File> scalaExtraJars;

    /**
     * SBT interface {@code sbt-interface-<version>.jar} file.
     */
    private File xsbtiFile;

    /**
     * SBT compiler interface sources {@code compiler-interface-<version>-sources.jar} file.
     */
    private File compilerInterfaceSrcFile;

    /**
     * Compilation classpath files.
     */
    private List<File> classpathFiles;

    /**
     * Logger.
     */
    private transient CompilerLogger log;

    /**
     * Compilation output directory.
     */
    private File outputDirectory;

    /**
     * SBT incremental compilation analysis cache file.
     */
    private File analysisCacheFile;

    /**
     * All available SBT incremental compilation analysis cache files for reactor projects.
     */
    private Map<File, File> analysisCacheMap;

    /**
     * Compilation error/warning source position mapper.
     */
    private transient SourcePositionMapper sourcePositionMapper;


    /**
     * Returns Scala and Java source files encoding.
     * 
     * @return Scala and Java source files encoding
     */
    public String getSourceEncoding()
    {
        return sourceEncoding;
    }

    /**
     * Sets Scala and Java source files encoding.
     * 
     * @param sourceEncoding Scala and Java source files encoding
     */
    public void setSourceEncoding( String sourceEncoding )
    {
        this.sourceEncoding = sourceEncoding;
    }


    /**
     * Returns Java compiler options.
     * 
     * @return Java compiler options
     */
    public String getJavacOptions()
    {
        return javacOptions;
    }

    /**
     * Sets Java compiler options.
     * 
     * @param javacOptions Java compiler options
     */
    public void setJavacOptions( String javacOptions )
    {
        this.javacOptions = javacOptions;
    }


    /**
     * Returns Scala compiler options.
     * 
     * @return Scala compiler options
     */
    public String getScalacOptions()
    {
        return scalacOptions;
    }

    /**
     * Sets Scala compiler options.
     * 
     * @param scalacOptions Scala compiler options
     */
    public void setScalacOptions( String scalacOptions )
    {
        this.scalacOptions = scalacOptions;
    }


    /**
     * Returns Java and Scala source files.
     * 
     * @return Java and Scala source files
     */
    public List<File> getSourceFiles()
    {
        return sourceFiles;
    }

    /**
     * Sets Java and Scala source files.
     * 
     * @param sourceFiles Java and Scala source files
     */
    public void setSourceFiles( List<File> sourceFiles )
    {
        this.sourceFiles = sourceFiles;
    }


    /**
     * Returns Scala library {@code scala-library-<version>.jar} file.
     * 
     * @return Scala library file
     */
    public File getScalaLibraryFile()
    {
        return scalaLibraryFile;
    }

    /**
     * Sets Scala library {@code scala-library-<version>.jar} file.
     * 
     * @param scalaLibraryFile Scala library file
     */
    public void setScalaLibraryFile( File scalaLibraryFile )
    {
        this.scalaLibraryFile = scalaLibraryFile;
    }


    /**
     * Returns Scala compiler {@code scala-compiler-<version>.jar} file.
     * 
     * @return Scala compiler file
     */
    public File getScalaCompilerFile()
    {
        return scalaCompilerFile;
    }

    /**
     * Sets Scala compiler {@code scala-compiler-<version>.jar} file.
     * 
     * @param scalaCompilerFile Scala compiler file
     */
    public void setScalaCompilerFile( File scalaCompilerFile )
    {
        this.scalaCompilerFile = scalaCompilerFile;
    }


    /**
     * Returns additional jar files to be added to Scala compiler classloader.
     * 
     * @return additional jar files
     */
    public List<File> getScalaExtraJarFiles()
    {
        return scalaExtraJars;
    }

    /**
     * Sets additional jar files to be added to Scala compiler classloader.
     * 
     * @param scalaExtraJars additional jar files
     */
    public void setScalaExtraJarFiles( List<File> scalaExtraJars )
    {
        this.scalaExtraJars = scalaExtraJars;
    }


    /**
     * Returns SBT interface {@code sbt-interface-<version>.jar} file.
     * 
     * @return SBT interface file
     */
    public File getXsbtiFile()
    {
        return xsbtiFile;
    }

    /**
     * Sets SBT interface {@code sbt-interface-<version>.jar} file.
     * 
     * @param xsbtiFile SBT interface file
     */
    public void setXsbtiFile( File xsbtiFile )
    {
        this.xsbtiFile = xsbtiFile;
    }


    /**
     * Returns SBT compiler interface sources {@code compiler-interface-<version>-sources.jar} file.
     * 
     * @return SBT compiler interface sources file
     */
    public File getCompilerInterfaceSrcFile()
    {
        return compilerInterfaceSrcFile;
    }

    /**
     * Sets SBT compiler interface sources {@code compiler-interface-<version>-sources.jar} file.
     * 
     * @param compilerInterfaceSrcFile SBT compiler interface sources file
     */
    public void setCompilerInterfaceSrcFile( File compilerInterfaceSrcFile )
    {
        this.compilerInterfaceSrcFile = compilerInterfaceSrcFile;
    }


    /**
     * Returns compilation classpath files.
     * 
     * @return classpath files
     */
    public List<File> getClasspathFiles()
    {
        return classpathFiles;
    }

    /**
     * Sets compilation classpath files.
     * 
     * @param classpathFiles classpath files
     */
    public void setClasspathFiles( List<File> classpathFiles )
    {
        this.classpathFiles = classpathFiles;
    }


    /**
     * Returns logger providing compilation feedback.
     * 
     * @return logger providing compilation feedback
     */
    public CompilerLogger getLogger()
    {
        return log;
    }

    /**
     * Sets logger providing compilation feedback.
     * 
     * @param log logger providing compilation feedback
     */
    public void setLogger( CompilerLogger log )
    {
        this.log = log;
    }


    /**
     * Returns compilation output directory.
     * 
     * @return output directory
     */
    public File getOutputDirectory()
    {
        return outputDirectory;
    }

    /**
     * Sets compilation output directory.
     * 
     * @param outputDirectory output directory
     */
    public void setOutputDirectory( File outputDirectory )
    {
        this.outputDirectory = outputDirectory;
    }


    /**
     * Returns SBT incremental compilation analysis cache file location for a project.
     * 
     * @return analysis cache file
     */
    public File getAnalysisCacheFile()
    {
        return analysisCacheFile;
    }

    /**
     * Sets SBT incremental compilation analysis cache file location for a project.
     * 
     * @param analysisCacheFile analysis cache file
     */
    public void setAnalysisCacheFile( File analysisCacheFile )
    {
        this.analysisCacheFile = analysisCacheFile;
    }


    /**
     * Returns SBT incremental compilation analysis cache file locations map for all reactor projects.
     * 
     * @return analysis cache files map
     */
    public Map<File, File> getAnalysisCacheMap()
    {
        return analysisCacheMap;
    }

    /**
     * Sets SBT incremental compilation analysis cache file locations map for all reactor projects.
     * 
     * @param analysisCacheMap analysis cache files map
     */
    public void setAnalysisCacheMap( Map<File, File> analysisCacheMap )
    {
        this.analysisCacheMap = analysisCacheMap;
    }

    
    /**
     * Returns compilation error/warning source position mapper.
     * 
     * @return source position mapper
     */
    public SourcePositionMapper getSourcePositionMapper()
    {
        return sourcePositionMapper;
    }

    /**
     * Sets compilation error/warning source position mapper.
     * 
     * @param sourcePositionMapper source position mapper
     */
    public void setSourcePositionMapper( SourcePositionMapper sourcePositionMapper )
    {
        this.sourcePositionMapper = sourcePositionMapper;
    }

}
