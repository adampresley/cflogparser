package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to provide the name of the output file. This is where
 * the results of the log parsing will be written to.
 * @author Adam Presley
 */
class OutputFile extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--output-file"
	final static def order = 1
	
	static def getInstance() {
		new OutputFile()
	}
	
	static def getInstance(value) {
		new OutputFile(value)
	}
	
	OutputFile() {
	}
	
	OutputFile(value) {
		this.value = value
	}
	
	def process() {
		config.outputFilename = value
		logger.debug "Output file = ${value}"
	}
}
