/*
 * Copyright (C) 2016 Dimcho Nedev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.coprtools.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;

import com.coprtools.constants.InserterConstants;

/**
 * Utility class that can manipulate with any kinds of source files.
 *
 * @author Dimcho Nedev
 */
public class SourceManipulator implements FileManipulator {
    /**
     * Reads the source from a file.
     *
     * @param file
     *            - the given source file
     * @return String, the source file content
     */
    @Override
    public String readFromFile(File file) throws FileNotFoundException, IOException {
        StringBuilder sourceBuilder = new StringBuilder();
        String line = InserterConstants.EMPTY_STRING;
        String source = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while ((line = reader.readLine()) != null) {
                sourceBuilder.append(line);
                sourceBuilder.append(InserterConstants.LINE_SEPARATOR);
            }

            source = sourceBuilder.toString().trim();
        }

        return source;
    }

    /**
     * Writes copyright notice on end of the given file.
     *
     * @param file
     *            - the given source file
     * @param notice
     *            - the copyright notice
     * @throws IOException
     *             - thrown when failed to write a notice to file
     */
    @Override
    public void writeToFile(File file, String notice) throws IOException {

        try (Writer writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(notice);
        }
    }

    /**
     * Overrides the original source file content with a new source (with
     * copyright notice).
     * <p>
     * Note: The original file will be deleted and a new one with the same source
     * will be created.
     * </p>
     *
     * @param file
     *            - the original source file with the old source
     * @param newSource
     *            - the new source with copyright notice
     * @throws IOException
     *             - thrown when failed to override a file
     */
    @Override
    public void overrideFile(File file, String newSource) throws IOException {
        file.delete();
        file = new File(file.getAbsolutePath());
        file.getParentFile().mkdirs();
        file.createNewFile();

        // TODO: consider whether this trim() is necessary
        this.writeToFile(file, newSource.trim());
    }

    /**
     * Copy a folder with all subfolders and files into another folder.
     *
     * @param sourceFolder
     *            - the source folder
     * @param destinationFolder
     *            - the destination folder
     * @throws IOException
     *            - when an I/O exception of some sort has occurred
     */
    @Override
    public void copyFolder(File sourceFolder, File destinationFolder) throws IOException {
        FileUtils.copyDirectory(sourceFolder, destinationFolder);
    }
}
