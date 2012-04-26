package com.adampresley.cflogparser.writer

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * This class is responsible for writing the results of parsing ColdFusion
 * log files into a text file. The report contains simply
 * a header line, followed by each error found reported on a single line.
 * Each column is delimited by a tab character.
 * @author Adam Presley
 */
class TextWriter
{
	def outputFile
	private final Logger logger = Logger.getLogger("mainLogger")	
	
	
	/**
	 * Constructor taking the path and name of the file to write the
	 * text file to.
	 * @author Adam Presley
	 * @param outputFile path and file name to write the Excel file to.
	 */
	public TextWriter(outputFile) {
		this.outputFile = outputFile
	}
	
	
	/**
	 * Creates and writes the text file report containing all errors found
	 * in the parsed ColdFusion log files.
	 * @author Adam Presley
	 * @param errors A collection of error structures.
	 */
	def write(errors) {
		logger.info "Preparing text output..."
		
		new File(this.outputFile).withWriter { writer ->
			writer.writeLine "serverName\tinstance\tdate\ttime\tthread\tmessage"
			
			errors.each { item ->
				def serverName = item?.serverName
				def instance = item?.instance
			
				writer.writeLine "${serverName}\t${instance}\t${item.logType}\t${item.date}\t${item.time}\t${item.thread}\t${item.message}"
			}
		}
	}
}

