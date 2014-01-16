package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to provide the output format to write the results in. Valid
 * output formats include:
 *
 * <ul>
 *    <li>excel</li>
 *    <li>csv</li>
 *    <li>text</li>
 *    <li>console</li>
 *    <li>pdf</li>
 *    <li>summarypdf</li>
 * </ul>
 *
 * @author Adam Presley
 */
class OutputFormat extends BaseArg
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--output-format"
	final static def order = 1

	static def getInstance() {
		new OutputFormat()
	}

	static def getInstance(value) {
		new OutputFormat(value)
	}

	OutputFormat() {
	}

	OutputFormat(value) {
		this.value = value
	}

	def process() {
		config.outputFormat = value
		logger.debug "Output format = ${value}"
	}
}
