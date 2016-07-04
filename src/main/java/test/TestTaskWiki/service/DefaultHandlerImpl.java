package test.TestTaskWiki.service;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import test.TestTaskWiki.model.Offer;

class DefaultHandlerImpl extends DefaultHandler {
	
	private Map<Integer, Offer> offers = new HashMap<>();
	private Offer offer; 
	private boolean pic = false;

	@Override
	public void startDocument() throws SAXException {
		System.out.println("startDocument");
	}

	@Override
	public void endDocument() throws SAXException {
		System.out.println("endDocument");
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
//		System.out.println("startElement");
//		System.out.println(qName);
		
		switch (qName.toLowerCase()) {
		case "offer":
//			System.out.println(attributes.getValue("id"));
			offer = new Offer(Integer.valueOf(attributes.getValue("id")));
			break;
		case "param":
//			System.out.println(attributes.getValue("name"));
			offer.addHash(attributes.getValue("name").hashCode());
			break;
		case "picture":
			pic = true;
		default:
			if (offer != null) {
				offer.addHash(qName.toLowerCase().hashCode());
			}
			break;
		}
			
		
//		System.out.println("---------------------");
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
//		System.out.println("endElement");

//		System.out.println(qName);
		
		if (qName.toLowerCase().equals("picture"))
			pic = false;
		
		switch (qName.toLowerCase()) {
		case "offer":
			offers.put(offer.getId(), offer);
			offer = null;
			break;
		case "picture":
			pic = false;
		default:
			break;
		}
		
//		System.out.println("---------------------");
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
//		System.out.println("characters");
		
		String param = new String(ch, start, length);
//		System.out.println(param);
		
		if (offer != null && param != null && !param.isEmpty()) {
			if (pic) 
				offer.getPictures().add(param);
			
			offer.addHash(param.hashCode());
		}
//		System.out.println("---------------------");
	}

	public Map<Integer, Offer> getOffers() {
		return offers;
	}

}
