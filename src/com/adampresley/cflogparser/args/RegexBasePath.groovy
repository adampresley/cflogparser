package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to provide a starting path to where log files are found.
 * This argument is required when performing a regular expression file pattern.
 * @author Adam Presley
 */
class RegexBasePath extends BaseArg 
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--regex-basePath"
	final static def order = 2

	static def getInstance() {
		new RegexBasePath()
	}
	
	static def getInstance(value) {
		new RegexBasePath(value)
	}
	
	RegexBasePath() {
	}
	
	RegexBasePath(value) {
		this.value = value
	}
	
	def process() {
		config.argFlags.regexBaseProvided = true
		config.basePaths = value.split(",")*.trim()
	}
}
