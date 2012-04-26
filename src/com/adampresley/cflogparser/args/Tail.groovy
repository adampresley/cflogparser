package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class for returning the last N error log items, ordered by
 * date/time descending.
 * @author Adam Presley
 */
class Tail extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--tail"
	final static def order = 3
	
	static def getInstance() {
		new Tail()
	}
	
	static def getInstance(value) {
		new Tail(value)
	}
	
	Tail() {
	}
	
	Tail(value) {
		this.value = value
	}
	
	def process() {
		config.tail = value
	}
}
