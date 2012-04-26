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
class PDFWriter
{
	def outputFile
	private final Logger logger = Logger.getLogger("mainLogger")	
	
	
	/**
	 * Constructor taking the path and name of the file to write the
	 * text file to.
	 * @author Adam Presley
	 * @param outputFile path and file name to write the Excel file to.
	 */
	public PDFWriter(outputFile) {
		this.outputFile = outputFile
	}
	
	
	/**
	 * Creates and writes the text file report containing all errors found
	 * in the parsed ColdFusion log files.
	 * @author Adam Presley
	 * @param errors A collection of error structures.
	 */
	def write(errors) {
		logger.info "Preparing PDF output..."
		
		def ds = new JRMapCollectionDataSource(errors)
		JasperReport jasperReport
		JasperPrint jasperPrint

		logger.debug "Compiling lineItemReport.jrxml"
		jasperReport = JasperCompileManager.compileReport(Class.getResourceAsStream("/com/adampresley/cflogparser/reports/lineItemReport.jrxml")) 
		jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), ds) 
		JasperExportManager.exportReportToPdfFile(jasperPrint, this.outputFile)
	}
}

