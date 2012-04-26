package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Abstract class that provides a basis for argument classes. Gives 
 * subclasses a config object, and a container for a value. All implementing
 * subclasses must provide a process() method.
 * @author Adam Presley
 */
abstract class BaseArg 
{
	def config
	def value
	
	private final Logger logger = Logger.getLogger("mainLogger")

	BaseArg() {
	}
	
	BaseArg(value) {
		this.value = value
	}
	
	abstract def process()
}
