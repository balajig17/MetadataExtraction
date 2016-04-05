import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.ArrayUtils;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class TikaExtractor 
{
	private HtmlParser htmlParser; 
	private  PDFParser pdfParser;
	private String[] unauthorized={"application/javascript","application/x-javascript"};
	
	
	
	
	
	
	public  String extractPDF(String content)
	{
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		try {
			pdfParser.parse(stream, handler, metadata);
		} catch (IOException | SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return handler.toString();
	}
	
	public  String extractHTMLBody(String content) 
	{
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		try {
			htmlParser.parse(stream, handler, metadata);
		} catch (IOException | SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		return handler.toString();
		
		
	}
	
	
	public String autoExtract(String content,String contentType)
	{
		contentType = contentType.substring(1,contentType.length()-1);
		if(ArrayUtils.contains(unauthorized,contentType))
		{
			return "";
			
		}
		else
		{
			if(contentType.equals("application/pdf"))
			{
				//String extractedContent = extractPDF(content);
				//return extractedContent;
				return content;
			}
			else if (contentType.equals("text/html"))
			{
				//String extractedContent= extractHTMLBody(content);
				//return extractedContent;
				return content;
			}
			else if (contentType.equals("text/plain"))
			{
				return content;
			}
		}
		return content;
	}
	
	
	public TikaExtractor()
	{
		
		htmlParser = new HtmlParser();
		pdfParser = new PDFParser();
	}




}
