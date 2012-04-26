package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to provide a list of files to process and parse.
 * @author Adam Presley
 */
class FileList extends BaseArg 
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--file-list"
	final static def order = 3
	
	static def getInstance() {
		new FileList()
	}
	
	static def getInstance(value) {
		new FileList(value)
	}
	
	FileList() {
	}
	
	FileList(value) {
		this.value = value
	}
	
	def process() {
		logger.debug "Parsing file list..."
		config.argFlags.fileListRetrieved = true

		def fileList = value.split(",")*.toFile()
		
		if (config.argFlags.dtmGtProvided) {
			fileList = fileList.findAll {
				new Date(it.lastModified()).compareTo(config.dmGt) == 1
			}
		}
		
		config.fileList.addAll(fileList)
	}
}
