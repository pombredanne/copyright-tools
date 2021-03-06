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

package com.coprtools.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.cli.MissingArgumentException;

import com.coprtools.cli.AbstractConsole;
import com.coprtools.commands.AbstractCommand;
import com.coprtools.commands.CommandFactory;
import com.coprtools.commands.CommandType;
import com.coprtools.constants.ConsoleCommandConstants;
import com.coprtools.constants.InserterConstants;
import com.coprtools.constants.OptionConstants;
import com.coprtools.constants.UserMessagesConstants;
import com.coprtools.exceptions.ArgumentParseException;
import com.coprtools.exceptions.InvalidCommandException;
import com.coprtools.util.FileManipulator;
import com.coprtools.writer.Writer;

/**
 * This is a core class responsible for parsing and all commands execution.
 *
 * @author Dimcho Nedev
 */
public class CopyrightToolsEngine implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(CopyrightToolsEngine.class.getName());

    private FileHandler fileHandler;

    private AbstractConsole cli;

    private FileManipulator manipulator;

    private Writer writer;

    private CommandFactory commandFactory;

    /**
     * Uses a dependency injection through the constructor.
     *
     * @param cli
     *            - this is a CLI implementation
     * @param manipulator
     *            - this is {@link FileManipulator manipulator} implementation
     * @param writer
     *            - a {@link Writer} object implementation
     * @param commandFactory
     *            - a {@link CommandFactory factory} that helps for a command
     *            creation
     */
    public CopyrightToolsEngine(AbstractConsole cli, FileManipulator manipulator, Writer writer,
            CommandFactory commandFactory) {
        this.cli = cli;
        this.manipulator = manipulator;
        this.writer = writer;
        this.commandFactory = commandFactory;
    }

    /*
     * TODO: The logic here smells like "spaghetti" code. ��� much "if - else"
     * statements witch make the code unclear. Consider to re-factor this
     * method.
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        LOGGER.setLevel(Level.SEVERE);
        try {
            this.cli.parse();

            if (cli.hasOption(OptionConstants.HELP_SHORT)) {
                cli.showUsage();
            } else if (cli.getArguments().length == 0) {
                throw new MissingArgumentException("Missing command!");
            } else {
                String textConsoleCommand = cli.getArguments()[0];

                String rootFolderPath = cli.getOptionValue(OptionConstants.ROOT_SHORT);
                String noticePath = cli.getOptionValue(OptionConstants.NOTICE_SHORT);
                String[] extensions = cli.getOptionValues(OptionConstants.EXTENSION_SHORT);
                String newNotice = null;

                if (cli.hasOption(OptionConstants.NEW_NOTICE_SHORT) && !cli.hasOption(OptionConstants.STRING_SHORT)) {
                    String newNoticePath = cli.getOptionValue(OptionConstants.NEW_NOTICE_SHORT);
                    File newNoticeFile = new File(newNoticePath);
                    newNotice = this.manipulator.readFromFile(newNoticeFile);
                }

                File rootDir = new File(rootFolderPath);
                File destinationFolder = null;

                if (cli.hasOption(OptionConstants.OUTPUT_SHORT)) {
                    String destinationPath = cli.getOptionValue(OptionConstants.OUTPUT_SHORT);
                    destinationFolder = new File(destinationPath);
                    this.manipulator.copyFolder(rootDir, destinationFolder);
                    rootDir = destinationFolder;
                }

                // Enables logging and creates a log file in the root path
                if (cli.hasOption(OptionConstants.INFO_LONG)) {
                    this.enableLogging(rootDir.getAbsolutePath());
                }

                String notice = null;
                if (cli.hasOption(OptionConstants.STRING_SHORT)) {
                    notice = cli.getOptionValue(OptionConstants.NOTICE_SHORT);
                    newNotice = cli.getOptionValue(OptionConstants.NEW_NOTICE_SHORT);
                } else {
                    File noticeFile = new File(noticePath);
                    notice = this.manipulator.readFromFile(noticeFile);
                    if (cli.hasOption(OptionConstants.BLANK_SHORT)) {
                        notice = insertBlankSpace(notice);
                    }
                }

                CommandType commandType = resolveCommandType(textConsoleCommand);

                AbstractCommand command = commandFactory.create(
                        commandType,
                        notice,
                        extensions,
                        this.manipulator,
                        newNotice);

                command.executeRecursively(rootDir);

                if (!command.isHasError()) {
                    writer.writeLine(UserMessagesConstants.SUCCESFULL_OPERATION_MESSAGE);
                } else {
                    writer.writeLine(UserMessagesConstants.FAILD_OPERTION_MESSAGE,
                            rootDir.getAbsolutePath() + File.separator + InserterConstants.LOG_FILENAME);
                }
                if (this.fileHandler != null) {
                    this.fileHandler.close();
                    LOGGER.removeHandler(this.fileHandler);
                }
            }

        } catch (MissingArgumentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (ArgumentParseException e) {
            LOGGER.log(Level.SEVERE, "Error while parsing arguments: " + e.getMessage());
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (SecurityException e) {
            LOGGER.log(Level.SEVERE, "Security violation has occurred: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unknown exception: " + e.getClass().getName() + " " + e.getMessage());
        }
    }

    /**
     * Enables the logging capabilities. Creates a log file in the roots path.
     *
     * @param rootPath
     *            - the projects's root directory
     * @throws SecurityException
     * @throws IOException
     */
    private void enableLogging(String rootPath) throws SecurityException, IOException {
        LOGGER.setLevel(Level.ALL);
        String logFilePath = rootPath + File.separator + InserterConstants.LOG_FILENAME;
        this.fileHandler = new FileHandler(logFilePath);
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.ALL);
        LOGGER.addHandler(fileHandler);
    }

    /**
     * Inserts a blank line after the notice(or before if a bottom option is
     * specified).
     *
     * @param notice
     *            - the notice
     * @return a notice with a blank line after(or before)
     */
    private String insertBlankSpace(String notice) {
        if (this.cli.hasOption(OptionConstants.BOOTOM_SHORT)) {
            notice = InserterConstants.LINE_SEPARATOR + notice;
        } else {
            notice += InserterConstants.LINE_SEPARATOR;
        }

        return notice;
    }

    private CommandType resolveCommandType(String consoleCommand) throws InvalidCommandException {

        CommandType resultCommand = null;

        switch (consoleCommand) {
        case ConsoleCommandConstants.INSERT:
            boolean bootomCondition = this.cli.hasOption(OptionConstants.BOOTOM_SHORT);
            resultCommand = bootomCondition ? CommandType.INSERT_AFTER : CommandType.INSERT_BEFORE;
            break;
        case ConsoleCommandConstants.REMOVE:
            resultCommand = CommandType.REMOVE;
            break;
        case ConsoleCommandConstants.REPLACE:
            resultCommand = CommandType.REPLACE;
            break;
        default:
            throw new InvalidCommandException("The comand cannot be resolved. Command: " + consoleCommand + " .");
        }

        return resultCommand;
    }
}
