package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to tell log parser the maximum number of threads used in parsing
 * log files.
 * @author Adam Presley
 */
class MaxThreads extends BaseArg 
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--max-threads"
	final static def order = 5
	
	static def getInstance() {
		new MaxThreads()
	}
	
	static def getInstance(value) {
		new MaxThreads(value)
	}
	
	MaxThreads() {
	}
	
	MaxThreads(value) {
		this.value = value
	}
	
	def process() {
		config.maxThreads = value.toInteger()
		logger.debug "Max threads set to ${config.maxThreads}"
	}
}
