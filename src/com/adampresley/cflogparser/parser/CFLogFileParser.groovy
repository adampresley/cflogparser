package com.adampresley.cflogparser.parser

import java.util.concurrent.*
import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * This is responsible for parsing a ColdFusion log file. Since this 
 * class implements Callable it is meant to be called as a Thread.
 *
 * The parser attempts to correctly parse log file entries, and will
 * use best judgement when trying to determine the type of entry.
 * Most lines are in a log4j style format, while others, especially
 * in the case of a hard JRun crash, may differ.
 * @author Adam Presley
 */
class CFLogFileParser implements Callable<ArrayList<Map>>
{
	private final def logFile
	private final Logger logger = Logger.getLogger("mainLogger")	
	private final def __config

	/**
	 * Constructor that takes a single File object that represents a
	 * ColdFusion log file.
	 * @author Adam Presley
	 * @param config The application configuration details
	 * @param logFile the file to parse 
	 */
	public CFLogFileParser(config, logFile) {
		__config = config
		this.logFile = logFile
	}
	

	/**
	 * Called when executing as a Thread, this method does all the work.
	 * It will iterate over all the lines of the log file, attempting to
	 * parse each one. In the end a Collection of error structures will be
	 * returned.
	 *
	 * @author Adam Presley
	 * @return An array of error and info structures that contain the following keys:
	 *         - serverName
	 *         - instance
	 *         - message
	 *         - thread
	 *         - date
	 *         - time
	 */
	@Override
	public ArrayList<Map> call() {
		logger.info "Processing log file ${logFile.getName()}..."

		def filenameSplit = logFile.getName().split("-")
		def f = new File(logFile.getCanonicalPath())
		def container = []

		def serverName = ""
		def instance = ""
		
		if (filenameSplit.size() > 0) serverName = filenameSplit[0]
		if (filenameSplit.size() > 1) instance = filenameSplit[1]
			
		try {
			f.eachLine { line ->
				def cut = line.split(" ")
				def msg = ""
				def row = [:]
					 
				if (cut.size() > 5) {
					
					/*
					 * Error logs 
					 */
					if (__config.logTypes.find { it == "error" } == "error") {
						row = __parseErrors(line, cut, serverName, instance)
						
						if (row.message != null && row.message.trim().size() > 0) {
							container << row
						}
					}
					
					/*
					 * Info logs
					 */
					if (__config.logTypes.find { it == "info" } == "info") {
						row = __parseInfo(line, cut, serverName, instance)

					 	if (row.message != null && row.message.trim().size() > 0) {
					 		container << row
					 	}
					}
					 
				}
			}
		}
		catch (Exception e) {
			logger.error "An error occured while processing file ${this.logFile.getCanonicalPath()}: ${e.message}"
			e.printStackTrace()
		}

		container
	}
	
	private def __parseErrors(line, cut, serverName, instance) {
		def row = [:]
		
		/*
		 * Check for an error line. Pos 3
		 */
		if (cut[2].toLowerCase().trim() == "error") {
			
			/*
			 * Error line with thread name. Standard log4j stuff.
			 */
			if (cut[3] ==~ /(?i)\[[a-z0-9\-_]+\]/) {
				row = __makeRow(
					serverName,
					instance,
					"Error",
					cut[5..-1].join(" ").trim(),
					cut[3].trim(),
					cut[0].trim(),
					cut[1].trim()
				)
				
				logger.debug "Adding error line ${line}"
			}
			
			/*
			 * JRun service error. Usually follows a OOM exception. Skip
			 * these.
			 */
			else if (line.toLowerCase().contains("jrunproxyserver.invokerunnable")) {
				row.msg = ""
			}
			
		}

		row
	}

	private def __parseInfo(line, cut, serverName, instance) {
		def row = [:]
		
		//logger.debug "LINE: ${line}"
		
		/*
		 * Check for an error line. Pos 3
		 */
		if (cut[2].toLowerCase().trim() == "information" || cut[3].toLowerCase().trim() == "info") {
			
			/*
			 * Info line with thread name. Standard log4j stuff. There are two types
			 * of info lines it would seem. The first is for Information logs
			 * created by JRun and ColdFusion. The second are coming from the application
			 * where Log4j is being used.
			 */
			if (cut[2].toLowerCase().trim() == "information" && cut[3] ==~ /(?i)\[[a-z0-9\-_]+\]/) {
				row = __makeRow(
					serverName,
					instance,
					"Information",
					cut[5..-1].join(" ").trim(),
					cut[3].trim(),
					cut[0].trim(),
					cut[1].trim()
				)
				
				logger.debug "Adding info line ${line}"
			}
			else if (cut[3].toLowerCase().trim() == "info" && cut[2] ==~ /(?i)\[[a-z0-9\-_]+\]/) {
				row = __makeRow(
					serverName,
					instance,
					"Information",
					cut[4..-1].join(" ").trim(),
					cut[2].trim(),
					cut[0].trim(),
					cut[1].trim()
				)
				
				logger.debug "Adding info line ${line}"
			}
		}

		row
	}

	private def __makeRow(serverName, instance, logType, message, thread, date, time) {
		[
			serverName: serverName,
			instance: instance,
			logType: logType,
			message: message,
			thread: thread,
			date: date,
			time: time
		]
	}
}
