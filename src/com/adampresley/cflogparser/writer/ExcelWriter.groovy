package com.adampresley.cflogparser.writer

import org.apache.poi.poifs.filesystem.*
import org.apache.poi.xssf.extractor.*
import org.apache.poi.xssf.usermodel.*
import org.apache.log4j.*
import org.apache.log4j.helpers.*

/**
 * This class is responsible for writing the results of parsing ColdFusion
 * log files into a Microsoft Excel report file. The report contains simply
 * a header line, followed by each error found reported on a single line.
 * @author Adam Presley
 */
class ExcelWriter
{
	def outputFile
	private final Logger logger = Logger.getLogger("mainLogger")	
	
	
	/**
	 * Constructor taking the path and name of the file to write the
	 * Excel report to.
	 * @author Adam Presley
	 * @param outputFile path and file name to write the Excel file to.
	 */
	public ExcelWriter(outputFile) {
		this.outputFile = outputFile
	}
	
	
	/**
	 * Creates and writes the Excel file report containing all errors found
	 * in the parsed ColdFusion log files.
	 * @author Adam Presley
	 * @param errors A collection of error structures.
	 */
	def write(errors) {
		logger.info "Preparing Excel output..."
		
		/*
		 * Create a workbook and worksheet.
		 */
		XSSFWorkbook wb = new XSSFWorkbook()
		XSSFCreationHelper helper = wb.getCreationHelper()
		XSSFSheet sheet = wb.createSheet("Error Log")
		
		/*
		 * Loop over each error structure and create a sheet. Each
		 * error structure represents a error log file.
		 */
		def rowIndex = 0

		/*
		 * Write the header row.
		 */
		XSSFRow header = sheet.createRow(rowIndex++)
		header.createCell(0).setCellValue("serverName")
		header.createCell(1).setCellValue("instance")
		header.createCell(2).setCellValue("logType")
		header.createCell(3).setCellValue("date")
		header.createCell(4).setCellValue("time")
		header.createCell(5).setCellValue("thread")
		header.createCell(6).setCellValue("message")
		

		/*
		 * Write out the error log.
		 */
		errors.each { item ->
			def serverName = item?.serverName
			def instance = item?.instance
			
			XSSFRow row = sheet.createRow(rowIndex++)
				
			row.createCell(0).setCellValue(helper.createRichTextString(serverName))
			row.createCell(1).setCellValue(helper.createRichTextString(instance))
			row.createCell(2).setCellValue(helper.createRichTextString(item.logType))
			row.createCell(3).setCellValue(item.date)
			row.createCell(4).setCellValue(item.time)
			row.createCell(5).setCellValue(helper.createRichTextString(item.thread))
			row.createCell(6).setCellValue(helper.createRichTextString(item.message))
		}
		
		logger.info "Writing file ${this.outputFile}..."
		
		FileOutputStream out = new FileOutputStream(this.outputFile)
		wb.write(out)
		out.close()
	}
}

