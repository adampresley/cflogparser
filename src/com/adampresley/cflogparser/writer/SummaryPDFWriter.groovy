package com.adampresley.cflogparser.writer

import org.apache.log4j.*
import org.apache.log4j.helpers.*

import net.sf.jasperreports.engine.*
import net.sf.jasperreports.engine.data.*

/**
 * This class is responsible for writing the results of parsing ColdFusion
 * log files into a text file. The report contains simply
 * a header line, followed by each error found reported on a single line.
 * Each column is delimited by a tab character.
 * @author Adam Presley
 */
class SummaryPDFWriter
{
	def outputFile
	private final Logger logger = Logger.getLogger("mainLogger")	
	
	
	/**
	 * Constructor taking the path and name of the file to write the
	 * text file to.
	 * @author Adam Presley
	 * @param outputFile path and file name to write the Excel file to.
	 */
	public SummaryPDFWriter(outputFile) {
		this.outputFile = outputFile
	}
	
	
	/**
	 * Creates and writes the text file report containing all errors found
	 * in the parsed ColdFusion log files.
	 * @author Adam Presley
	 * @param errors A collection of error structures.
	 */
	def write(errors) {
		logger.info "Preparing summary PDF output..."
		
		/*
		 * Go over the errors and add some aggregate data to it.
		 */
		errors.each { item ->
			def type = "Other"
			def typeItem = ""		// Future use where I will provide breakdown of individual templates that were OOM, or not found, etc...
			
			/*
			 * OOM - Out of Memory
			 */
			if (item.message.toLowerCase().contains("java heap space") || item.message.toLowerCase().contains("gc overhead limit")) {
				type = "Out Of Memory"
				typeItem = ""
			}
			
			/*
			 * Database Query Issues
			 */
			if (item.message.toLowerCase().contains("error executing database query") || item.message.toLowerCase().contains("queryparamtag\$invaliddataexception")) {
				type = "Query Errors"
				typeItem = ""
			}
			
			/*
			 * Undefined variables or invalid types.
			 */
			if (item.message.toLowerCase().contains("function is not of type") || item.message.toLowerCase().contains("is undefined")) {
				type = "Variable Missing/Bad Type"
				typeItem = ""
			}
			
			/*
			 * Web service errors.
			 */
			if (item.message.toLowerCase().contains("web service operation caused an invocation exception")) {
				type = "Webservice Errors"
				typeItem = ""
			}
			
			/*
			 * Timeouts, page or otherwise.
			 */ 
			if (item.message.toLowerCase().contains("request has exceeded the allowable time limit")) {
				type = "Timeouts"
				typeItem = ""
			}
			
			if (item.message.toLowerCase().contains("could not find the coldfusion component")) {
				type = "Missing CFC/Interface"
				typeItem = ""
			}
			
			
			item.type = type
			item.item = typeItem
		}
		
		def ds = new JRMapCollectionDataSource(errors)
		JasperReport jasperReport
		JasperPrint jasperPrint

		logger.debug "Compiling summaryReport.jrxml"
		jasperReport = JasperCompileManager.compileReport(Class.getResourceAsStream("/com/adampresley/cflogparser/reports/summaryReport.jrxml")) 
		jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), ds) 
		JasperExportManager.exportReportToPdfFile(jasperPrint, this.outputFile)
	}
}

