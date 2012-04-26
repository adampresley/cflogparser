package com.adampresley.cflogparser.writer

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * This class is responsible for writing the results of parsing ColdFusion
 * log files onto the console. Useful for piping data and paring in console
 * scripting, such as Bash or Windows Powershell.
 * @author Adam Presley
 */
class ConsoleWriter
{
	private final Logger logger = Logger.getLogger("mainLogger")	
	private final Logger output = Logger.getLogger("consoleOutputFormat")
	
	
	/**
	 * Constructor
	 * @author Adam Presley
	 */
	public ExcelWriter() {
	}
	
	
	/**
	 * Writes the errors found in the ColdFusion logs to the console.
	 * @author Adam Presley
	 * @param errors A collection of error structures.
	 */
	def write(errors) {
		/*
		 * Write out the error log.
		 */
		errors.each { item ->
			def serverName = item?.serverName
			def instance = item?.instance
			
			output.info "${serverName}\t${instance}\t${item.logType}\t${item.date}\t${item.time}\t${item.thread}\t${item.message}"
		}
	}
}

