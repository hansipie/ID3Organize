package net.chrysalide.ID3Organize;

import java.io.File;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
//import org.apache.log4j.Logger;

public class Start {

//	static Logger logger = Logger.getLogger(Start.class);
	static File _dst;
	static File _src;
//	static String _format;

	public static void main(String[] args) {
//		logger.info("ID3 Organize");
		if (commandeLine(args) == false)
			return;
		
		System.out.println("Source: " + _src);
		System.out.println("Destination: " + _dst);
//		System.out.println("Format: " + _format);
		
		Engine engine = new Engine(_src, _dst/*, _format*/);
		engine.directoryCrawler();
	}

	public static boolean commandeLine(String[] args) {

		
		Option source = new Option("s", "src", true, "MP3 source directory");
		source.setRequired(true);
		
		Option destination = new Option("d", "dst", true, "MP3 destination directory");
		destination.setRequired(true);

//		Option format = new Option("f", "format", true, "MP3 destination directory");
//		format.setRequired(true);

		Options options = new Options();
		options.addOption(source);
		options.addOption(destination);
//		options.addOption(format);

		CommandLineParser parser = new BasicParser();
		try {
			CommandLine line = parser.parse(options, args);
			_src = new File(line.getOptionValue("s"));
			_dst = new File(line.getOptionValue("d"));
//			_format = line.getOptionValue("f");
			return true;
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "ID3Organize", options, true);
			return false;
		}
	}
}
