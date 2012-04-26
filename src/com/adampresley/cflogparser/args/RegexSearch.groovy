package com.adampresley.cflogparser.args

import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * Argument class to provide a regular expression to match files against.
 * File names that match the regex are included in log parsing.
 * @author Adam Presley
 */
class RegexSearch extends BaseArg 
{
	private final Logger logger = Logger.getLogger("mainLogger")

	final static def argString = "--regex-search"
	final static def order = 3
	
	static def getInstance() {
		new RegexSearch()
	}
	
	static def getInstance(value) {
		new RegexSearch(value)
	}
	
	RegexSearch() {
	}
	
	RegexSearch(value) {
		this.value = value
	}
	
	def process() {
		config.argFlags.regexSearchProvided = true
		logger.debug "Generating file list from regex specification (?i)${value}..."

		config.basePaths.each { basePath ->
			logger.debug "Searching for files in ${basePath}..."
			
			new File(basePath).eachFileMatch(~/(?i)${value}/) {
				def doAdd = true
				
				if (config.argFlags.dtmGtProvided) {
					if (new Date(it.lastModified()) < config.dmGt) {
						doAdd = false
					}
				}
				
				if (doAdd) config.fileList << it
			}
		}
	}
}
