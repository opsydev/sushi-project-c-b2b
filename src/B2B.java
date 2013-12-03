import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class B2B {
	private final static String LIST_URL = "http://localhost:4413/ProjectC/Admin/getPOList";
	private final static String FILE_URL = "http://localhost:4413/ProjectC/ViewOrder";
	static List<finalOrderItemBean> finalList = new ArrayList<finalOrderItemBean>();
    static finalOrderItemBean finalItem;
    static HashMap<String,String> wsdlMap= new HashMap<String, String>();
    
    
	public static void main(String[] args) throws Exception {
		wsdlMap.put("http://red.cse.yorku.ca:4413/axis/YYZ.jws", "Toronto");
		wsdlMap.put("http://red.cse.yorku.ca:4413/axis/YHZ.jws", "Halifax");
		wsdlMap.put("http://red.cse.yorku.ca:4413/axis/YVR.jws", "Vancouver");
		if(args.length < 1){
			throw new Exception("Start date is required");
		}
		
		String startDate = args[0];
		String endDate = null;
		String url = LIST_URL + "?start_date="+startDate;
		
		if(args.length > 1){
			endDate = args[1];
			url = url + "&end_date="+endDate;
		}
		
		Map<String, Integer> itemsList = new HashMap<String, Integer>();
		
		URL site = new URL(url);
    URLConnection yc = site.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
    	String fileURL = FILE_URL + "?fn="+ inputLine;
    	
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	Document doc = dBuilder.parse(fileURL);
    	NodeList nl = doc.getElementsByTagName("item");
    	for(int i = 0; i<nl.getLength();i++){
    		Element e = (Element) nl.item(i);
    		String qty = e.getElementsByTagName("quantity").item(0).getTextContent();
    		Integer quantity = Integer.parseInt(qty);
    		String itemNumber= e.getAttribute("number");
    		if(itemsList.containsKey(itemNumber)){
    			Integer q = itemsList.get(itemNumber);
    			quantity = q + quantity;
    		}     			
    		itemsList.put(itemNumber, quantity);
  		}
    }
    
    List<String> suppliers = new ArrayList<String>();
    suppliers.add("http://red.cse.yorku.ca:4413/axis/YYZ.jws");
    suppliers.add("http://red.cse.yorku.ca:4413/axis/YHZ.jws");
    suppliers.add("http://red.cse.yorku.ca:4413/axis/YVR.jws");
    
    Set<String> set = itemsList.keySet();
    // determining the best quote from each supplier and choosing the best
    for(String s :set )
    {
    	finalItem = new finalOrderItemBean();
    	finalItem.setItemNumber(s);
    	finalItem.setQuantity(itemsList.get(s));
    	//System.out.println(s + "\t" + itemsList.get(s));
    	
    	Double minQuote = Double.MAX_VALUE;
    	String tns;
    	for (int i = 0; i<  suppliers.size(); i++) 
    	{
	    	// asking for quote from toronto head quarters
    		tns = suppliers.get(i);
	    	//String tns = "http://red.cse.yorku.ca:4413/axis/YYZ.jws";	
			SOAPMessage msg = MessageFactory.newInstance().createMessage();
			MimeHeaders header = msg.getMimeHeaders();
			header.addHeader("SOAPAction", "");
			SOAPPart soap = msg.getSOAPPart();
			SOAPEnvelope envelope = soap.getEnvelope();
			SOAPBody body = envelope.getBody();
			body.addChildElement("quote").addChildElement("itemNumber").addTextNode(s);
			SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
			SOAPMessage resp = sc.call(msg, new URL(tns));
			sc.close();
			//msg.writeTo(System.out);
			//resp.writeTo(System.out);
			org.w3c.dom.Node node = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName("quoteResponse").item(0);
			Double quote = Double.parseDouble(node.getTextContent());
			if(quote>0)
			{
				minQuote = minQuote<quote?minQuote:quote;
				finalItem.setQuote(minQuote);
				finalItem.setTns(tns);
				finalItem.setSupplier(wsdlMap.get(tns));
			}
    	}
    	finalList.add(finalItem);
		//System.out.println(" the minQuote is " + finalItem.getQuote() + " order id " + finalItem.getItemNumber() + " tns " + finalItem.getSupplier() + " quantity " + finalItem.getQuantity());
    }
    
    // now placing an order with the supplier who gave the best price
    for(int i=0; i<finalList.size(); i++)
    {
    	String tns = finalList.get(i).getTns();
    	SOAPMessage msg = MessageFactory.newInstance().createMessage();
		MimeHeaders header = msg.getMimeHeaders();
		header.addHeader("SOAPAction", "");
		SOAPPart soap = msg.getSOAPPart();
		SOAPEnvelope envelope = soap.getEnvelope();
		SOAPBody body = envelope.getBody();
		Node node = body.addChildElement("order");
		((SOAPElement) node).addChildElement("itemNumber").addTextNode(finalList.get(i).getItemNumber());
		((SOAPElement) node).addChildElement("quantity").addTextNode((finalList.get(i).getQuantity()).toString());
		((SOAPElement) node).addChildElement("key").addTextNode("cse83111p7");
		
		SOAPConnection sc = SOAPConnectionFactory.newInstance().createConnection();
		
		SOAPMessage resp = sc.call(msg, new URL(tns));
		sc.close();
		//msg.writeTo(System.out);
		//resp.writeTo(System.out);
		org.w3c.dom.Node node1 = resp.getSOAPPart().getEnvelope().getBody().getElementsByTagName("orderResponse").item(0);
		//System.out.println("\nThe answer is\n" + node.getTextContent());
		finalList.get(i).setConfirmationNumber(node1.getTextContent());
		System.out.println(" the order return is " + finalList.get(i).getConfirmationNumber());
		
    }  
    in.close();
    
    finalOrderBean finalOrder = new finalOrderBean(finalList);
	
    JAXBContext jx = JAXBContext.newInstance(finalOrder.getClass());
	Marshaller marshaller = jx.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
	
	StringWriter sw = new StringWriter();
	sw.write("<?xml version=\"1.0\"?>");
	sw.write("<?xml-stylesheet type=\"text/xsl\" href=\"../po.xsl\"?>");
//	sw.write("\n");
	marshaller.marshal(finalOrder, new StreamResult(sw));
	System.out.println(sw.toString());
	
	// creating file on the basis of date
	Date n =  new Date();
	String d = (new SimpleDateFormat("yyyy-MM-dd")).format(n).toString();
	File directory = new File (".","finalOrder");
	//File finalFile = new File("/ProjectCb2b/src/finalOrder",d);
	
	System.out.println(directory.getAbsolutePath());
	/*FileWriter fw = new FileWriter(finalFile);
	fw.write(sw.toString());
	fw.close();*/
	
			
    }

}
