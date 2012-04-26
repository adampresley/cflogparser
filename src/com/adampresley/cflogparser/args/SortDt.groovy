package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to provide a sort direction for sorting results on date/time.
 * @author Adam Presley
 */
class SortDt extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--sortdt"
	final static def order = 4
	
	static def getInstance() {
		new SortDt()
	}
	
	static def getInstance(value) {
		new SortDt(value)
	}
	
	SortDt() {
	}
	
	SortDt(value) {
		this.value = value
	}
	
	def process() {
		config.argFlags.sortDt = value
		logger.debug "Output will sort by date/time ${config.argFlags.sortDt}"
	}
}
