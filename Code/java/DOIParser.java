package org.apache.tika.parser.doi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DOIParser extends AbstractParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3476430064240659042L;

	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.application("doi"));
	public static final String DOI_MIME_TYPE = "application/doi";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext arg0) {
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream arg0, ContentHandler arg1, Metadata arg2, ParseContext arg3)
			throws IOException, SAXException, TikaException {
		
		
	}
	
	public static void main(String argv[]) {
		try {
			
		URL url = new URL("http://polar.usc.edu/a");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		
		String data = "{\"lsturl\": \"http://teststring\", \"format\": \"json\"}";

		conn.setDoOutput(true);
		OutputStream os = conn.getOutputStream();
		os.write(data.getBytes());
		os.flush();

		System.out.println(conn.getResponseCode());
		if(conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
			throw new RuntimeException("Failed: " +conn.getResponseCode());
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String output ;
		while((output = br.readLine()) != null) {
			System.out.println(output);
		}
		conn.disconnect();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


}
