package com.tetsuwo.amazon;

import com.tetsuwo.amazon.SignedRequestsHelper;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.StringReader;
import org.xml.sax.InputSource;

public class Amazon {
    /**
     * Returns the output from the given URL.
     * 
     * I tried to hide some of the ugliness of the exception-handling
     * in this method, and just return a high level Exception from here.
     * Modify this behavior as desired.
     * 
     * @param desiredUrl
     * @return
     * @throws Exception
     */
    static private String doHttpUrlConnectionAction(String desiredUrl)	throws Exception {
	URL url = null;
	BufferedReader reader = null;
	StringBuilder stringBuilder;

	try {
	    // create the HttpURLConnection
	    url = new URL(desiredUrl);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    // just want to do an HTTP GET here
	    connection.setRequestMethod("GET");
      
	    // uncomment this if you want to write output to this url
	    //connection.setDoOutput(true);
      
	    // give it 15 seconds to respond
	    connection.setReadTimeout(15*1000);
	    connection.connect();

	    // read the output from the server
	    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    stringBuilder = new StringBuilder();

	    String line = null;
	    while ((line = reader.readLine()) != null) {
		stringBuilder.append(line + "\n");
	    }
	    return stringBuilder.toString();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    // close the reader; this can throw an exception too, so
	    // wrap it in another try/catch block.
	    if (reader != null) {
		try {
		    reader.close();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
	    }
	}
    }

    static private String getTagValue(Document doc, String str) {
	StringBuilder value = new StringBuilder();
	NodeList itemList = doc.getElementsByTagName(str);
	if (itemList == null || itemList.getLength() == 0)
	    return null;
	value.append(itemList.item(0).getFirstChild().getNodeValue());
	for (int i = 1; i <  itemList.getLength(); i++) {
	    value.append(",");
	    value.append(itemList.item(i).getFirstChild().getNodeValue());
	}
	return value.toString();
    }

    /**
     * 受け取ったISBNをAmazon様に問い合わせ書籍情報を取得する．
     * Bookクラスにセットされ返す．
     * ISBNに対応した本が見つかなければ，中身がnullなBookオブジェクトが返る．
     *
     * @param isbn
     * @return Book
     */
    public static Book getInformation(String isbn, String AccessKey, String SecretKey) {
	Book book = new Book();
	try {
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("Service", "AWSECommerceService");
	    params.put("Operation", "ItemLookup");
	    params.put("ResponseGroup", "ItemAttributes");
//	    params.put("ResponseGroup", "Large");
	    params.put("IdType", "ISBN");
	    params.put("ItemId", isbn);
	    params.put("SearchIndex", "Books");
	    params.put("Version", "2009-01-06");
	    params.put("AssociateTag", "dummy");
	    SignedRequestsHelper signedRequestsHelper = new SignedRequestsHelper(AccessKey, SecretKey);
	    String urlStr = signedRequestsHelper.sign(params);
//	    System.out.println(urlStr);

	    String content = doHttpUrlConnectionAction(urlStr);
//	    System.out.println(content);

	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    StringReader strReader = new StringReader(content);
	    Document doc = builder.parse(new InputSource(strReader));

	    book.setAuthor(getTagValue(doc, "Author"));
	    book.setTitle(getTagValue(doc, "Title"));
	    book.setPublicationDate(getTagValue(doc, "PublicationDate"));
	    book.setURL(getTagValue(doc, "DetailPageURL"));
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return book;
    }
}
