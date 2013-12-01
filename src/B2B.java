import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class B2B {
	private final static String LIST_URL = "http://localhost:4413/ProjectC/Admin/getPOList";
	private final static String FILE_URL = "http://localhost:4413/ProjectC/ViewOrder";
	public static void main(String[] args) throws Exception {
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
    
    Set<String> set = itemsList.keySet();
    for(String s :set ){
    	System.out.println(s + "\t" + itemsList.get(s));
    }
    in.close();
		
	}

}
