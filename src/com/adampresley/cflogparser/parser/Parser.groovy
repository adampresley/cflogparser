package com.adampresley.cflogparser.parser

import java.util.concurrent.*
import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * When the application is run a list of files is constructed based on 
 * various potential parameters. That list is passed here, and a thread
 * pool is created to handle processing of these files. Each thread in the 
 * pool will call the CFLogFileParser class to process a log file. The number 
 * of maximum threads is configurable from command line arguments.
 * @author Adam Presley
 */
class Parser
{
	def basePath
	def filePattern
	def filenameList
	def maxThreads

	private final def __config
	private final Logger logger = Logger.getLogger("mainLogger")
	

	/**
	 * Constructor taking a config object detailing how to process log files.
	 * @author Adam Presley
	 * @param config a map of configuration keys
	 */
	Parser(config) {
		__config = config
		
		this.basePath = config.basePath
		this.filenameList = config.fileList
		this.maxThreads = config.maxThreads
	}
	
	/**
	 * Simple closure to do timing of code sections. Will output, using 
	 * log4j, the execution time in friendly terms.
	 * @author Adam Presley
	 * @param codeBlock a closure containing the code to execute
	 */
	def timeMe = { Closure codeBlock ->
		def start = new Date().getTime()
		codeBlock()
		def end = new Date().getTime()
		
		def totalTime = end - start
		def verbage = ((totalTime > 60000) ? "${(totalTime / 1000) / 60}m" : ((totalTime > 1000) ? "${totalTime / 1000}s" : "${totalTime}ms"))
		
		logger.info "Executed in ${verbage}"
	}
	
	
	/**
	 * Returns a base config object used to setup this parser.
	 * @author Adam Presley
	 */
	static def getNewConfig() {
		[
			argFlags: [
				fileListRetrieved: false,
				regexBaseProvided: false,
				regexSearchProvided: false,
				dtmGtProvided: false,
				summaryPdf: false,
				sortDt: ""
			],
			debugArgsMode: false,
			outputFilename: "",
			basePaths: [],
			fileList: [],
			maxThreads: 20,
			dmGt: null,
			outputFormat: "excel",
			tail: 0,
			logTypes: [ "error" ]
		]
	}
	 

	/**
	 * Begins the log file parsing process. Here a thread pool is created
	 * and worker threads spawned for each file to be parsed. Once parsing
	 * of all files is complete, and the thread pool is shut down all
	 * errors from the parsed logs are returned.
	 * @author Adam Presley
	 * @return A collection of error structures. Each error structure contains
	 *         a server name, ColdFusion instance name, the error message,
	 *         the thread of execution, and a date and time.
	 */
	def parse() {
		def rows = []

		def threads = []
		def pool = Executors.newFixedThreadPool(maxThreads)
		
		logger.info "${filenameList.size()} file(s) to process."
		
		timeMe {
			/*
			 * Iterate over each file and queue up a new CFLogFileParser
			 * thread. In the future I need to abstract this a bit to be
			 * more flexible with different log file types.
			 */
			filenameList.each {
				Callable<ArrayList<Map>> c = new CFLogFileParser(__config, it)
				Future<ArrayList<Map>> f = pool.submit(c)
				
				threads << f
			}
			
			/*
			 * Execute each thread, waiting for them all to finish.
			 */
			threads.each {
				rows.addAll(it.get())
			}
		}
		
		pool.shutdown()
		
		/*
		 * Do any post-processing.
		 */
		__postProcess(rows)
	}
	
	/**
	 * Performs post procesing on any errors and info found in log files. This is,
	 * for the moment, a hacky one-stop-shop to do all the final tasks
	 * such as sorting and tail.
	 * @author Adam Presley
	 * @param rows the collection of error and info structures
	 * @return Returns a potentially transformed set of error structures.
	 */
	private def __postProcess(rows) {
		def result = []
		result.addAll(rows)
		
		/*
		 * User requested sorts?
		 */
		if (__config.argFlags.sortDt != null) {
			result.sort { a, b ->
				def sortReturn = 0
				
				/*
				 * Get the date and time. The CF dates are partial, so assume
				 * this year.
				 */
				try {
					def theFirstDate = Date.parse("M/d HH:mm:ss", "${a.date} ${a.time}")
					def theSecondDate = Date.parse("M/d HH:mm:ss", "${b.date} ${b.time}")
					
					if (__config.argFlags.sortDt.toLowerCase() == "asc") {
						sortReturn = theFirstDate <=> theSecondDate
					}
					else {
						sortReturn = theSecondDate <=> theFirstDate
					}
				}
				catch (Exception e) {
					sortReturn = 0
				}
				
				sortReturn
			}
		}
		
		/*
		 * Are we doing a tail of the errors?
		 */
		if (__config.tail.toInteger() > 0) {
			result.sort { a, b ->
				def sortReturn = 0
				
				/*
				 * Get the date and time. The CF dates are partial, so assume
				 * this year.
				 */
				try {
					def theFirstDate = Date.parse("M/d HH:mm:ss", "${a.date} ${a.time}")
					def theSecondDate = Date.parse("M/d HH:mm:ss", "${b.date} ${b.time}")
					
					sortReturn = theSecondDate <=> theFirstDate
				}
				catch (Exception e) {
					sortReturn = 0
				}
				
				sortReturn
			}
			
			result = result[0..<__config.tail.toInteger()]
		}
		
		result
	}
}
