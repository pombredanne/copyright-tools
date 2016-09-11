package com.coprtools.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.coprtools.constants.OptionConstants;
import com.coprtools.constants.UsageConstants;
import com.coprtools.exceptions.ArgumentParseException;

public class ApacheCliConsole extends AbstractConsole {

    private Options options;

    private CommandLine cmd;

    public ApacheCliConsole(String arguments[]) {
        super(arguments);
        this.options = initalizeOptions();
    }

    private Options initalizeOptions() {
        Options options = new Options();

        // help option
        options.addOption(Option.builder(OptionConstants.HELP_SHORT)
                .longOpt(OptionConstants.HELP_LONG)
                .optionalArg(true)
                .desc(UsageConstants.HELP_OPTION_DESC).build());

        // file extensions list
        options.addOption(Option.builder(OptionConstants.EXTENSION_SHORT)
                .longOpt(OptionConstants.EXTENSION_LONG)
                .optionalArg(false)
                .required()
                .desc(UsageConstants.EXTENSIONS_OPTION_DESC)
                .hasArgs().build());

        // input folder location
        options.addOption(Option.builder(OptionConstants.ROOT_SHORT)
                .longOpt(OptionConstants.ROOT_LONG)
                .optionalArg(false)
                .required()
                .desc(UsageConstants.ROOT_OPTION_DESC)
                .hasArg().build());

        // notice file location
        options.addOption(Option.builder(OptionConstants.NOTICE_SHORT)
                .longOpt(OptionConstants.NOTICE_LONG)
                .optionalArg(false)
                .required()
                .desc(UsageConstants.NOTICE_OPTION_DESC)
                .hasArg().build());

        // new notice file location
        options.addOption(Option.builder(OptionConstants.NEW_NOTICE_SHORT)
                .longOpt(OptionConstants.NEW_NOTICE_LONG)
                .optionalArg(false)
                .desc(UsageConstants.NOTICE_OPTION_DESC)// TODO: change this
                .hasArg().build());

        // Insert on bottom of the source
        options.addOption(Option.builder(OptionConstants.BOOTOM_SHORT)
                .longOpt(OptionConstants.BOTTOM_LONG)
                .optionalArg(false)
                .desc(UsageConstants.BOTTOM_OPTION_DESCR).build());

        // Enable job info logging option
        options.addOption(Option.builder()
                .longOpt(OptionConstants.INFO_LONG)
                .optionalArg(true)
                .desc("Enables job info console logging").build());

        // Add blank line(s) after notice
        options.addOption(Option.builder(OptionConstants.BLANK_SHORT)
                .longOpt(OptionConstants.BLANK_LONG)
                .optionalArg(false)
                .desc(UsageConstants.BLANK_OPTION_DESC)
                .hasArg().build());

        return options;
    }

    @Override
    public void parse() throws ArgumentParseException {
        CommandLineParser parser = new DefaultParser();
        try {
            this.cmd = parser.parse(options, this.arguments);
        } catch (ParseException e) {
            throw new ArgumentParseException(e.getMessage());
        }
    }

    @Override
    public boolean hasOption(String option) {
        return this.cmd.hasOption(option);
    }

    @Override
    public void showUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
                UsageConstants.USAGE,
                UsageConstants.HEADER,
                this.options,
                UsageConstants.FOOTER,
                false);
    }

    @Override
    public String getOptionValue(String option) {
        return this.cmd.getOptionValue(option);
    }

    @Override
    public String[] getOptionValues(String option) {
        return this.cmd.getOptionValues(option);
    }

    @Override
    public String[] getArguments() {
        return this.cmd.getArgs();
    }
}