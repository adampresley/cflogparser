package com.adampresley.cflogparser.writer

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * This class is responsible for writing the results of parsing ColdFusion
 * log files into a CSV file. The report contains simply
 * a header line, followed by each error found reported on a single line.
 * Each column is delimited by a comma character.
 * @author Adam Presley
 */
class CsvWriter
{
	def outputFile
	private final Logger logger = Logger.getLogger("mainLogger")


	/**
	 * Constructor taking the path and name of the file to write the
	 * CSV file to.
	 * @author Adam Presley
	 * @param outputFile path and file name to write the CSV file to.
	 */
	public CsvWriter(outputFile) {
		this.outputFile = outputFile
	}


	/**
	 * Creates and writes the CSV file report containing all errors found
	 * in the parsed ColdFusion log files.
	 * @author Adam Presley
	 * @param errors A collection of error structures.
	 */
	def write(errors) {
		logger.info "Preparing CSV output..."

		new File(this.outputFile).withWriter { writer ->
			writer.writeLine "\"serverName\",\"instance\",\"logType\",\"date\",\"time\",\"thread\",\"message\""

			errors.each { item ->
				def serverName = item?.serverName
				def instance = item?.instance

				writer.writeLine "\"${serverName}\",\"${instance}\",\"${item.logType}\",\"${item.date}\",\"${item.time}\",\"${item.thread}\",\"${item.message}\""
			}
		}
	}
}

