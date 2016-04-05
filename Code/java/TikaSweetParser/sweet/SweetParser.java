package org.apache.tika.parser.sweet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class SweetParser extends AbstractParser
{
	private boolean initialized = false;
	private ArrayList<SweetClass> sweetClasses;
	private Hashtable<String,SweetClass> sweetClassesHashed;
	private Gson gson;
	private int resultsToReturn = 5;
	public ArrayList<MaxentTagger> taggers;
	private Hashtable<String,Integer> occurences;

	private void initialize() throws IOException
	{

		sweetClassesHashed = new Hashtable<String,SweetClass>();
		occurences = new Hashtable<String,Integer>();
		Path taggerPath = Paths.get("english-left3words-distsim.tagger");
		//MaxentTagger tagger = new MaxentTagger("edu/stanford/nlp/tagger/models/english-left3words-distsim.tagger");
		MaxentTagger tagger = new MaxentTagger(taggerPath.toString());
		taggers = new ArrayList<MaxentTagger>();
		taggers.add(tagger);

		File file = new File("sweet.json");
		if (!file.exists() || file.isDirectory())
		{
			System.out.println("SweetParser cannot be initialized missing sweet.json file");
		} else
		{

			String sweetContents = FileUtils.readFileToString(file, "UTF-8");
			gson = new Gson();
			sweetClasses = gson.fromJson(sweetContents, new TypeToken<ArrayList<SweetClass>>()
			{
			}.getType());
			for(SweetClass sweetClass : sweetClasses)
			{
				sweetClassesHashed.put(sweetClass.getName(), sweetClass);
			}
			initialized = true;
			System.out.println("SweetParser up and running!");
		}
	}

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context)
	{
		return null;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException
	{
		if (!initialized)
		{
			initialize();
		}
		
		ArrayList<String> entities = new ArrayList<String>();
		ArrayList<String> tokens = new ArrayList<String>();
		// Read tokens from the stream

		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new InputStreamReader(stream, StandardCharsets.UTF_8), new CoreLabelTokenFactory(), "");
		while (ptbt.hasNext())
		{
			CoreLabel label = ptbt.next();
			tokens.add(label.toString());
		}

		for (String token : tokens)
		{
			String annotated = taggers.get(0).tagString(token);
			// Tagger tags them as word_TYPE
			String type = annotated.substring(annotated.indexOf('_') + 1);
			type = type.trim();
			if (type.equals("NN"))
			{
				String extracted = annotated.substring(0, annotated.indexOf('_'));
				entities.add(extracted);

			}
		}
		ArrayList<String> intersected = getIntersection(entities);
		for (String intersection : intersected)
		{
			metadata.add("INTERSECTION", intersection);
		}

	}
	
	
	public void parse(String str,Metadata metadata) throws IOException, SAXException, TikaException
	{
		if (!initialized)
		{
			initialize();
		}
		ArrayList<String> entities = new ArrayList<String>();
		ArrayList<String> tokens = new ArrayList<String>();
		// Read tokens from the stream
		InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new InputStreamReader(stream, StandardCharsets.UTF_8), new CoreLabelTokenFactory(), "");
		while (ptbt.hasNext())
		{
			CoreLabel label = ptbt.next();
			tokens.add(label.toString());
		}

		for (String token : tokens)
		{
			String annotated = taggers.get(0).tagString(token);
			// Tagger tags them as word_TYPE
			String type = annotated.substring(annotated.indexOf('_') + 1);
			type = type.trim();
			if (type.equals("NN"))
			{
				String extracted = annotated.substring(0, annotated.indexOf('_'));
				entities.add(extracted);

			}
		}
		ArrayList<String> intersected = getIntersection(entities);
		for (String intersection : intersected)
		{
			metadata.add("INTERSECTION", gson.toJson(intersection));
		}

	}
	
	

	public ArrayList<String> getIntersection(ArrayList<String> entities)
	{

		occurences.clear();
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		ArrayList<String> intersected = new ArrayList<String>();
		for (String entity : entities)
		{

			SweetClass sweetClass = search(entity);
			if (sweetClass != null)
			{
				//intersected.add(sweetClass);
				if(occurences.containsKey(entity))
				{
					Integer val = occurences.get(entity);
					occurences.put(entity,val+1);
				}
				else
				{
					occurences.put(entity, 1);
				}
			}
		}
		
		Set<String> keys = occurences.keySet();
		for(String key:keys)
		{
			Pair pair = new Pair(key,occurences.get(key));
			pairs.add(pair);
		}
		Collections.sort(pairs);
		int min = Math.min(pairs.size(),resultsToReturn);
		for(int i =0;i<min;i++)
		{
			int index = pairs.size()-1-i;
			String key = convertToUpperCase(pairs.get(i).name);
			SweetClass sweetClass = sweetClassesHashed.get(key);
			JsonElement jsonElement = gson.toJsonTree(sweetClass);
			jsonElement.getAsJsonObject().addProperty("occurence", pairs.get(i).occurence);
			intersected.add(gson.toJson(jsonElement));
		}
		
		return intersected;

	}

	public SweetClass search(String key)
	{
		key = convertToUpperCase(key);
		SweetClass sweetClass = sweetClassesHashed.get(key);
		if(sweetClass!=null)
		{
			return sweetClass;
		}
		else
		{
			return null;
		}
	}
	
	/*
	public SweetClass binarySearch(String searchedName, ArrayList<SweetClass> sweetClasses, int low, int high)
	{
		searchedName = convertToUpperCase(searchedName);
		if (low <= high)
		{
			int mid = (low + high) / 2;
			String upperCaseName = convertToUpperCase(sweetClasses.get(mid).getName());
			if (searchedName.compareTo(upperCaseName) > 0)
			{
				return binarySearch(searchedName, sweetClasses, mid + 1, high);
			} else if (searchedName.compareTo(upperCaseName) < 0)
			{
				return binarySearch(searchedName, sweetClasses, 0, mid - 1);
			} else
			{
				return sweetClasses.get(mid);
			}
		}
		return null;
	}*/

	public String convertToUpperCase(String str)
	{
		String upperCase = "";
		for (int i = 0; i < str.length(); i++)
		{
			int ascii = (int) str.charAt(i);
			if (ascii >= 97 && ascii <= 122)
			{
				ascii = ascii - 32;
			}
			upperCase += Character.toString((char) ascii);
		}
		return upperCase;
	}
	
	public static void main(String[] args) throws IOException, SAXException, TikaException
	{
		File file = new File("checkCapital.txt");
		Metadata md = new Metadata();
		BodyContentHandler handler = new BodyContentHandler();
		InputStream stream = TikaInputStream.get(file,md);
		SweetParser parser = new SweetParser();
		parser.parse(stream,handler, md);
		System.out.println("Done");
	}
	
	
	
	
	
}
