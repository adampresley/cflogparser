package com.adampresley.cflogparser.writer

/**
 * Factory class for retrieving log file writers.
 * @author Adam Presley
 */
class WriterFactory
{
	/**
	 * Returns an instance of a log file writer based on the value found
	 * in the key named "outputFormat" in the config map.
	 * @author Adam Presley
	 * @param config a map of config items. Must contain a key named outputFormat
	 * @return a log file writer object
	 */
	static def getWriter(config) {
		def result = null

		switch (config.outputFormat.toLowerCase()) {
			case "console":
				result = new ConsoleWriter()
				break

			case "csv":
				result = new CsvWriter(config.outputFilename)
				break

			case "text":
				result = new TextWriter(config.outputFilename)
				break

			case "pdf":
				result = new PDFWriter(config.outputFilename)
				break

			case "summarypdf":
				result = new SummaryPDFWriter(config.outputFilename)
				break

			default:
				result = new ExcelWriter(config.outputFilename)
				break
		}

		result
	}
}
