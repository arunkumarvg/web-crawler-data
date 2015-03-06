package com.linkedin.webcrawler.search;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLUtil {
	static XSSFRow row;

	public static ArrayList<String> extractExcelContentByColumnIndex(int columnIndex, String filepath) {
		ArrayList<String> columndata = null;
		try {
			System.out.println(filepath);
			File f = new File(filepath);
			if (f.exists()) {
				FileInputStream ios = new FileInputStream(f);
				XSSFWorkbook workbook = new XSSFWorkbook(ios);
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				columndata = new ArrayList<String>();

				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						if (row.getRowNum() > 0) { // To filter column headings
							if (cell.getColumnIndex() == columnIndex) {// To match column
																													// index
								switch (cell.getCellType()) {
								case Cell.CELL_TYPE_NUMERIC:
									columndata.add(cell.getNumericCellValue() + "");
									break;
								case Cell.CELL_TYPE_STRING:
									columndata.add(cell.getStringCellValue());
									break;
								}
							}
						}
					}
				}
				ios.close();
			}
			System.out.println(columndata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columndata;
	}

	public static ArrayList<String> extractExcelContentByColumnIndex(int columnIndex) {
		ArrayList<String> columndata = null;
		try {
			File f = new File("Sample.xlsx");
			FileInputStream ios = new FileInputStream(f);
			XSSFWorkbook workbook = new XSSFWorkbook(ios);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			columndata = new ArrayList<String>();

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();

					if (row.getRowNum() > 0) { // To filter column headings
						if (cell.getColumnIndex() == columnIndex) {// To match column index
							switch (cell.getCellType()) {
							case Cell.CELL_TYPE_NUMERIC:
								columndata.add(cell.getNumericCellValue() + "");
								break;
							case Cell.CELL_TYPE_STRING:
								columndata.add(cell.getStringCellValue());
								break;
							}
						}
					}
				}
			}
			ios.close();
			//System.out.println(columndata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return columndata;
	}

	public static void writeToFile(Map<String, Object[]> mapInfo) throws IOException {
		// Create blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet("CompanyInfo");
		// Create row object
		XSSFRow row;

		// Iterate over data and write to sheet
		Set<String> keyid = mapInfo.keySet();
		int rowid = 0;
		for (String key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = mapInfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}
		// Write the workbook in file system
		FileOutputStream out = new FileOutputStream(new File("Writesheet.xlsx"));
		workbook.write(out);
		out.close();
		System.out.println("Writesheet.xlsx written successfully");
	}

	public static void writeToFile(Map<String, Object[]> mapInfo, String outputFile) throws IOException {
		// Create blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank sheet
		XSSFSheet spreadsheet = workbook.createSheet("CompanyInfo");
		// Create row object
		XSSFRow row;

		// Iterate over data and write to sheet
		Set<String> keyid = mapInfo.keySet();
		int rowid = 0;
		for (String key : keyid) {
			row = spreadsheet.createRow(rowid++);
			Object[] objectArr = mapInfo.get(key);
			int cellid = 0;
			for (Object obj : objectArr) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}
		// Write the workbook in file system
		FileOutputStream out = new FileOutputStream(new File(outputFile));
		workbook.write(out);
		out.close();
		System.out.println("Writesheet.xlsx written successfully");
	}

	public static void main(String[] args) throws IOException {
		List<String> listData = extractExcelContentByColumnIndex(0);
		for (String data : listData) {
			System.out.println(data);
		}
		// Map<String, Object[]> empinfo = new TreeMap<String, Object[]>();
		// empinfo.put("1", new Object[] { "EMP ID", "EMP NAME", "DESIGNATION" });
		// empinfo.put("2", new Object[] { "tp01", "Gopal", "Technical Manager" });
		// empinfo.put("3", new Object[] { "tp02", "Manisha", "Proof Reader" });
		// empinfo.put("4", new Object[] { "tp03", "Masthan", "Technical Writer" });
		// empinfo.put("5", new Object[] { "tp04", "Satish", "Technical Writer" });
		// empinfo.put("6", new Object[] { "tp05", "Krishna", "Technical Writer" });
		// writeToFile(empinfo);
	}
}
