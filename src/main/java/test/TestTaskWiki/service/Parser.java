package test.TestTaskWiki.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import test.TestTaskWiki.model.Offer;

public class Parser {

	private Parser() {}
	
	public static Map<Integer, Offer> parse(String fileName) throws Exception {
		
		if (fileName == null || fileName.isEmpty())
			throw new IllegalArgumentException("file=" + fileName);
		
		File file = new File(fileName);
		if (!file.isFile())
			throw new IllegalArgumentException("file=" + fileName + " is not file");
		
		SAXParserFactory factory = SAXParserFactory.newInstance(); 
		SAXParser parser = factory.newSAXParser();
		
		 DefaultHandlerImpl handler = new DefaultHandlerImpl();
		
		parser.parse(file, handler);
		
		return handler.getOffers();
	}

}
