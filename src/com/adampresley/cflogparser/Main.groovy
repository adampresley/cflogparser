package com.adampresley.cflogparser

import com.adampresley.cflogparser.args.*
import com.adampresley.cflogparser.writer.*
import com.adampresley.cflogparser.parser.*

import org.apache.log4j.*
import org.apache.log4j.helpers.*
import com.adampresley.groovy.metaclass.*

import net.sf.jasperreports.engine.*

/**
 * This application will scan and parse ColdFusion log files for errors
 * and compile them into a single Excel file output. This is useful for
 * troubleshooting server issues, crashes, or templates that are
 * misbehaving.
 * @author Adam Presley
 */
class Main 
{
	static main(args) {
		Bootstrap.loadMetaClassEnhancements()
		
		def docArgSize = 25
		def docDescSize = 50
		
		def index = 0
		def splitUp = []
		def arg
		def value
		
		def validLogTypes = [ "error", "info", "warn", "fatal", "debug" ]
		def config = Parser.getNewConfig()
		
		/*
		 * If enough arguments aren't passed in display usage information.
		 */
		if (args.size() < 2)
		{
			println ""
			println "Usage:"
			println "   java -jar cflogparser.jar [args...]"

			println ""
			println ""

			println "Args:"
			print "   --dmgt".padRight(docArgSize, " ")
			"Filters files by date modifed where greater than specified. Dates are formatted like yyyy-mm-dd".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --debug-args".padRight(docArgSize, " ")
			"Displays the base paths and files located, then exists.".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --file-list".padRight(docArgSize, " ")
			"A comma-delimited list of files to parse. Each file reference must be a full path.".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --log-types".padRight(docArgSize, " ")
			"A comma-delimited list of log types to parse. Valid values are info,error,debug,warn,fatal".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --max-threads".padRight(docArgSize, " ")
			"Maximum number of threads used to process log files. Be careful as setting this too high can cause system instability. Defaults to 20.".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --output-file".padRight(docArgSize, " ")
			"Name of the file that contains the log parsing results. This is applicable for the excel,text,pdf,summarypdf formats.".wrap(docDescSize, docArgSize, false).each { println it } 
			print "\n   --output-format".padRight(docArgSize, " ")
			"Format to export results to. Formats include excel,console,text,pdf,summarypdf. The default is excel.".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --regex-basePath".padRight(docArgSize, " ")
			"A comma-delimited list of paths to perform the regex search for files in. Required when used with --regex-search.".wrap(docDescSize, docArgSize, false).each { println it }
			print "\n   --regex-search".padRight(docArgSize, " ")
			"A regular expression to match file names against. Required when using --regex-basePath.".wrap(docDescSize, docArgSize, false).each { println it } 
			print "\n   --sortdt".padRight(docArgSize, " ")
			"Sorts the output by date/time in either descending or ascending order. Valid values are desc and asc. Does not sort by default.".wrap(docDescSize, docArgSize, false).each { println it } 
			print "\n   --tail".padRight(docArgSize, " ")
			"Returns the last N log entries ordered by date/time descending.".wrap(docDescSize, docArgSize, false).each { println it }
			println ""
			println ""

			println "Example:"
			println "   To parse errors from two specific log files:"
			println "      java -jar cflogparser.jar --output-file=results.xlsx --file-list=C:\\serverfile1-out.log,C:\\serverfile2-out.log"
			println ""
			println "   To parse errors from all files with a patter:"
			println "      java -jar cflogparser.jar --output-file=results.xlsx --regex-basePath=C:\\ --regex-search=web[0-9]{2,4}-cf[0-9]{2,4}-out\\.log" 
			println ""
			
			return
		}
		
		
		/*
		 * Initialize log4j
		 */
		Logger logger = Logger.getLogger("mainLogger")
		
		URL log4jProperties = Loader.getResource("com/adampresley/cflogparser/log4j.properties")
		PropertyConfigurator.configure(log4jProperties)

		/*
		 * Instantiate the ArgManager. This registers and provides
		 * searching methods and stuff for arguments.
		 */
		def argManager = new ArgManager()
		def newArg = null
		
		/*
		 * Craft an array of all the incoming arguments. Sort them
		 * by order, then execute the process method for each argument
		 * class. Each Arg class knows how to collect it's data.
		 */
		for (index = 0; index < args.size(); index++) {
			if (args[index].contains("=")) {
				splitUp = args[index].split("=")
				arg = splitUp[0]
				value = splitUp[1]
			}
			else {
				arg = args[index]
				value = ""
			}
			
			newArg = argManager.getArgClass(arg, value)
			if (newArg != null) {
				argManager.args << argManager.getArgClass(arg, value)
			}
			else {
				/*
				 * Bad argument yo
				 */
				logger.error "Argument ${arg} is invalid."
				return
			}
		}
		
		argManager.process(config)
		

		/*
		 * Validate our argument combinations. Some args are required based
		 * on which ones are used.
		 */
		try {
			if (config.outputFormat != "console" && config.outputFilename.size() < 1) {
				throw new Exception("The argument --output-file is required")
			}
			
			if (config.argFlags.regexSearchProvided && !config.argFlags.regexBaseProvided) {
				throw new Exception("The argument --regex-basePath is required when --regex-search is provided.")
			}
			
			if (!config.argFlags.fileListRetrieved && !config.argFlags.regexBaseProvided) {
				throw new Exception("Please provide either a file list (--file-list), or a regex path and search criteria (--regex-basePath, --regex-search)")
			}
			
			config.logTypes.each { logType ->
				if (logType != validLogTypes.find { it == logType }) {
					throw new Exception("${logType} is an invalid log type")
				}
			}
		}
		catch (Exception ex) {
			logger.error ex.message
			return
		}

		if (config.debugArgsMode) {
			logger.info "Base paths:"
			config.basePaths.each { logger.info it }
			
			logger.info "The following is the files found for processing:"
			config.fileList.each { logger.info it }
			
			logger.info "Exiting debug mode."
			return
		}
		
		
		/*
		 * It's PARSING time!
		 */
		def p = new Parser(config)
		def info = p.parse()
		
		/*
		 * Write the output. This will depend on the output format.
		 */
		def writer = WriterFactory.getWriter(config)
		if (writer == null) {
			logger.error "No writer matched the output format of ${config.outputFormat}"
			return
		}
		
		writer.write(info)
		logger.info "Done."
	}
}
