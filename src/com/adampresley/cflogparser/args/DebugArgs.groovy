package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class providing ability to set log parser into debug mode.
 * @author Adam Presley
 */
class DebugArgs extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--debug-args"
	final static def order = 1
	
	static def getInstance() {
		new DebugArgs()
	}
	
	static def getInstance(value) {
		new DebugArgs(value)
	}
	
	DebugArgs() {
	}
	
	DebugArgs(value) {
		this.value = value
	}
	
	def process() {
		config.debugArgsMode = true
	}
}
