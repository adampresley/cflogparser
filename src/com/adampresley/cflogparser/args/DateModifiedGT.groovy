package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class for providing minimum date/time modified check. This
 * value is used by the log parser to exclude any files that are older
 * that this date value.
 * @author Adam Presley
 */
class DateModifiedGT extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--dmgt"
	final static def order = 2
	
	static def getInstance() {
		new DateModifiedGT()
	}
	
	static def getInstance(value) {
		new DateModifiedGT(value)
	}
	
	DateModifiedGT() {
	}
	
	DateModifiedGT(value) {
		this.value = value
	}
	
	def process() {
		config.argFlags.dtmGtProvided = true
		config.dmGt = Date.parse("yyyy-MM-dd", value)
		
		logger.debug "Excluding files with dates less than ${config.dmGt}"
	}
}
