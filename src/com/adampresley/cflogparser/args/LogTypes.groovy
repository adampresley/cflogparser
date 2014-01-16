package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class for telling the parser what log types to parse. The following
 * types are valid:
 *
 * <ul>
 *    <li>info</li>
 *    <li>error</li>
 *    <li>debug</li>
 *    <li>warn</li>
 *    <li>fatal</li>
 * </ul>
 *
 * @author Adam Presley
 */
class LogTypes extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--log-types"
	final static def order = 4

	static def getInstance() {
		new LogTypes()
	}

	static def getInstance(value) {
		new LogTypes(value)
	}

	LogTypes() {
	}

	LogTypes(value) {
		this.value = value
	}

	def process() {
		config.logTypes = value.split(",")
		logger.debug "Parsing log types ${value}"
	}
}
