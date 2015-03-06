package com.linkedin.webcrawler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.linkedin.webcrawler.search.GoogleSearchUtil;
import com.linkedin.webcrawler.search.XLUtil;

public class WebCrawlerMain {
	//private static String filePath = System.getProperty("filePath");
	private static String inputFile= System.getProperty("inputFilePath");
	private static String outputFile= System.getProperty("outputFilePath");

	private static Properties getProperties(String filepath) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(filepath);
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	public static void main(String[] args) throws IOException {
		long startTime = 0;
		long endTime = 0;
		long totalTime = 0;
//		Properties props = getProperties(filePath);
//		inputFile = props.getProperty("inputXLFilePath");
//		outputFile = props.getProperty("outputXLFilePath");
		System.out.println(inputFile);
		System.out.println(outputFile);
		System.out.println(" Input file path is :: \t" +inputFile );
		System.out.println(" Start of Extraction for XL sheet updation \n");
		startTime = System.currentTimeMillis();

		List<String> companyData = XLUtil.extractExcelContentByColumnIndex(0,inputFile);
		System.out.println("Company Details :\n");
		System.out.println(companyData.toString());
		Map<String, Object[]> mapData = GoogleSearchUtil.getCompanyDetails(companyData);
		System.out.println(" End of Extraction for XL sheet updation \n");
		System.out.println(" Updating data to an XL Sheet \n");
		XLUtil.writeToFile(mapData,outputFile);
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println(" Total time took for the operation  is : " + totalTime + " in ms\n");

	}
}
