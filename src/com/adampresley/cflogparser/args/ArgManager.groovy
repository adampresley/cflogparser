package com.adampresley.cflogparser.args

/**
 * This class registers all possible command line arguments, and provides
 * methods to retrieve argument classes by arg name, as well as execute
 * a queued up set of arguments and their values. To queue up arguments
 * one would create the argument class, then add it to the args collection.
 *
 * Each argument class must provide two static variables:
 *    - argString - The string representing the command line argument
 *    - order - The order in which the argument is processed. Lower numbers are processed first.
 *
 * @author Adam Presley
 */
class ArgManager
{
	private def __argClasses
	def args = []
	
	/**
	 * Constructor registers all known command line argument classes.
	 * @author Adam Presley
	 */
	ArgManager() {
		__argClasses = [
			new DebugArgs(),
			new FileList(),
			new RegexBasePath(),
			new RegexSearch(),
			new MaxThreads(),
			new DateModifiedGT(),
			new OutputFile(),
			new OutputFormat(),
			new Tail(),
			new SortDt(),
			new LogTypes()
		]
	}
	
	/**
	 * Executes each queued up command line argument object in the "args" 
	 * collection. The provided config structure will be passed to each
	 * argument object before then calling the process() method.
	 * @author Adam Presley
	 * @param config configuration object which the argument classes will work against
	 */
	def process(config) {
		args.sort { it.order }.each {
			it.config = config
			it.process()
		}
	}
	
	/**
	 * Retrieves an argument class by the command line string name.
	 * @author Adam Presley
	 * @param argString command line argument string
	 * @return an argument class that matches the named argument string. 
	 */
	def getArgClass(argString) {
		def result = __argClasses.find { it.argString == argString }
		result?.getInstance()
	}

	/**
	 * Retrieves an argument class by the command line string name while
	 * providing the class a value for the argument.
	 * @author Adam Presley
	 * @param argString command line argument string
	 * @param value value to populate into the argument
	 * @return an argument class that matches the named argument string. 
	 */
	def getArgClass(argString, value) {
		def result = __argClasses.find { it.argString == argString }
		result?.getInstance(value)
	}
}
