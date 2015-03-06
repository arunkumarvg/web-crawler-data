package com.linkedin.webcrawler.search;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.linkedin.webcrawler.pojo.CompanyRecord;
import com.linkedin.webcrawler.pojo.GoogleResults;

public class GoogleSearchUtil {

	private static CompanyRecord getGoogleSearchResults(String searchString) {
		CompanyRecord company = new CompanyRecord();
		GoogleResults results = null;
		if (searchString == null) {
			company.setCompanyName(searchString);
			company.setCompanyUrl("NOT FOUND");
			company.setImageUrls(getEmptyList());
			return company;
		}
		System.out.println("Company name:" + searchString);
		String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		String query = searchString.trim();
		String charset = "UTF-8";
		try {
			URL url = new URL(address + URLEncoder.encode(query, charset));
			Reader reader = new InputStreamReader(url.openStream(), charset);
			results = new Gson().fromJson(reader, GoogleResults.class);
			if (results.getResponseStatus() == 403) {
				Thread.sleep(1000);
				company = getGoogleSearchResults(searchString);
			} else if (results.getResponseStatus() == 200) {
				company.setCompanyName(results.getResponseData().getResults().get(0).getTitle());
				company.setCompanyUrl(results.getResponseData().getResults().get(0).getUrl());
				company.setImageUrls(getCompanyLogoUrls(results.getResponseData().getResults().get(0).getUrl()));
				return company;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return company;
	}

	public static List<String> getCompanyLogoUrls(String companyUrl) throws IOException {
		List<String> logoUrls = new ArrayList<String>();
		if (companyUrl != null && !(companyUrl.isEmpty())) {
			Document doc = Jsoup.connect(companyUrl).timeout(0).get();
			Elements images = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			for (Element image : images) {
				if (image.attr("src").toUpperCase().contains("LOGO") || image.attr("src").toUpperCase().contains("HEADER") || image.attr("src").toUpperCase().contains("BANNER")) {
					logoUrls.add(image.attr("src"));
				}
			}
		}
		return logoUrls;
	}

	private static CompanyRecord searchCompanyRecord(String company) {
		CompanyRecord companyRecord = null;
		List<String> empty = new ArrayList<String>();
		empty.add("NOT FOUND");
		try {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			companyRecord = getGoogleSearchResults(company);
			return companyRecord;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			companyRecord.setCompanyName(company);
			companyRecord.setCompanyUrl("NOT FOUND");
			companyRecord.setImageUrls(empty);
			return companyRecord;
		}
	}

	public static Map<String, Object[]> getCompanyDetails(List<String> companyList) {
		Map<String, Object[]> mapData = new LinkedHashMap<String, Object[]>();
		mapData.put("Name", new Object[] { "Name", "AKA", "Company Url", "Company LOGO" });
		CompanyRecord companyRecord = new CompanyRecord();
		List<String> companyData = companyList;
		System.out.println("Printing company Details:");
		try {
			for (String company : companyData) {
				printDetails(companyRecord);
				companyRecord = searchCompanyRecord(company);
				if (companyRecord != null) {
					mapData.put(company, new Object[] { company, companyRecord.getCompanyName(), companyRecord.getCompanyUrl(), companyRecord.getImageUrls().toString() });
				}
			}
			System.out.println(" Search  Data updated to Map Object \n");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception :" + e.getMessage());
		}
		return mapData;

	}

	public static void printDetails(CompanyRecord companyRecord) {
		if (companyRecord != null && companyRecord.getCompanyName() != null) {
			System.out.println("Company Name :" + companyRecord.getCompanyName());
			System.out.println("Company Url :" + companyRecord.getCompanyUrl());
			System.out.println("Company Image Logos :" + companyRecord.getImageUrls().toString());
		} else {
			System.out.println("Invalid Details");
		}
	}

	private static List<String> getEmptyList() {
		List<String> empty = new ArrayList<String>();
		empty.add("NOT FOUND");
		return empty;
	}

	public static void main(String[] args) throws IOException {
		List<String> companyData = XLUtil.extractExcelContentByColumnIndex(0);
		System.out.println("Company Details :\n");
		System.out.println(companyData.toString());
		Map<String, Object[]> mapData = getCompanyDetails(companyData);
		XLUtil.writeToFile(mapData);
	}
}