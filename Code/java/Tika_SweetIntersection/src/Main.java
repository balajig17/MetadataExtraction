import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ner.NamedEntityParser;
import org.apache.tika.parser.ner.corenlp.CoreNLPNERecogniser;
import org.apache.tika.parser.sweet.SweetParser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.cedarsoftware.util.io.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

	private static TikaExtractor tikaExtractor;
	private static Gson gson;
	private static ExecutorService executor;
	private static BodyContentHandler handler;
	private static SweetParser parser;

	private static int executionLimitSeconds = 10;
	private static String source = Paths.get(".").toAbsolutePath().normalize()
			.toString();
	private static String destination = Paths.get(".").toAbsolutePath().normalize().toString();
	// Make this true if you want to check subdirectories too.
	private static boolean r = false;
	private static String startAfter = "";
	private static boolean process = true;
	private static long processedFileCount =0;
	private static long totalFileCount =0;
	
	
	
	
	public static boolean verifyParameters(String[] args) {
		if (args.length == 0) {
			source = "/Volumes/My Passport/Balaji/newData/JSONData";
			//source = "/Volumes/My Passport/Data/debug_file";
			destination = "/Volumes/My Passport/Data/junk";
			System.out.println("Source is set to : " + source);
			System.out.println("Destination is set to : " + destination);
		} else {
			if (args.length != 2) {
				System.out.println("Expecting 2 command line arguments got "
						+ args.length);
				return false;
			} else {
				source = args[0];
				destination = args[1];
			}
		}

		File rootdir = new File(source);
		File destinationdir = new File(destination);
		if (!rootdir.exists() || !destinationdir.exists()) {
			if (!rootdir.exists()) {
				System.out.println(source
						+ " does not exist please recheck the path");
			} else {
				System.out.println(destination
						+ " does not exist please recheck the path");
			}
			return false;
		}

		return true;
	}
	
	
	
	
	
	
	
	// If program hangs at some point we can avoid processing the same files.
	public static void resume() throws IOException {
		String filePath = FilenameUtils.concat(destination, "resume.txt");
		System.out.println(filePath);
		File file = new File(filePath);
		if (file.exists()) {
			String str = FileUtils.readFileToString(file);
			if (!str.equals("")) {
				startAfter = str;
				process = false;
			} else {
				process = true;
			}
		} else {
			System.out.println("Starting from beginning");
			process = true;
			file.createNewFile();
		}
	}
	
	
	

	public static void saveLastFile(String filename) throws FileNotFoundException {
		String filePath = FilenameUtils.concat(destination, "resume.txt");
		PrintWriter writer = new PrintWriter(new FileOutputStream(filePath, false));
		writer.print(filename);
		writer.close();

	}

	public static void initialize() throws IOException {
		gson = new Gson();
		parser = new SweetParser();
		handler = new BodyContentHandler();
		tikaExtractor = new TikaExtractor();
		executor = Executors.newFixedThreadPool(1);
		resume();
	}

	public static void main(String[] args) throws IOException, SAXException, TikaException {

		if(verifyParameters(args))
		{

			initialize();
			System.out.println("Starting");
			File rootDir = new File(source);
			File [] files = rootDir.listFiles();
			totalFileCount = files.length;
			
			// Start Parsing
			parseAllFiles(rootDir.listFiles());
		}
	}

	public static void parseAllFiles(File[] files) throws IOException, SAXException, TikaException {
		Arrays.sort(files);
		for (File file : files) {
			if (file.isDirectory()) {
				// Optional if we want to check subdirectories too.
				if (r) {
					parseAllFiles(file.listFiles());
				}
			} else {
				processedFileCount++;
				if (process) {
					System.out.println(processedFileCount+ "/ "+totalFileCount + " Processing " + file.toString());
					saveLastFile(file.getName());
					String jsonRep = parseFile(file);
					writeJsonToFile(jsonRep, file.getName());

				} else {
					if (file.getName().equals(startAfter)) {
						System.out.println("Resuming from file" + file.getName());
						process = true;
					}
				}
			}
		}
	}

	public static String parseFile(File file) throws IOException, SAXException, TikaException {

		String fileStr = FileUtils.readFileToString(file);
		// We get the json , we are gonna modify it
		JsonObject jsonObject = (new JsonParser()).parse(fileStr).getAsJsonObject();
		JsonElement contentElement = jsonObject.get("content");
		if (contentElement != null) {

			String content = contentElement.toString();
			content = content.replace("<", "");
			//System.out.println(content.contains("\n"));
			content=content.replace(">", "");
			content=content.replace("/", "");
			content=content.replace("\\n", "");
			content=content.replace("\\t", "");
			content=content.trim().replaceAll(" +", " ");
			jsonObject.remove("content");
			jsonObject.addProperty("content", content);
			JsonElement contentTypeElement = jsonObject.get("metadata").getAsJsonObject().get("Content-Type");
			String contentType = contentTypeElement.toString();

			content = tikaExtractor.autoExtract(content, contentType);

			
			InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
			ExecutorService service = Executors.newSingleThreadExecutor();
			Callable<Object> gettingData = new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					// TODO Auto-generated method stub
					Metadata metadata = new Metadata();
					parser.parse(stream, handler, metadata);
					return metadata;
				}

			};

			Future<Object> futureObject = service.submit(gettingData);
			try {
				Metadata metadata = (Metadata) futureObject.get(executionLimitSeconds, TimeUnit.SECONDS);
				ArrayList<SweetClass> sweetClasses = new ArrayList<SweetClass>();
				for (String val : metadata.getValues("INTERSECTION")) {
					SweetClass sweetClass = gson.fromJson(val, SweetClass.class);
					sweetClasses.add(sweetClass);
				}
				if (sweetClasses.size() != 0) {
					System.out.println("FOUND INTERSECTION with Size " + sweetClasses.size());
				}
				Collections.sort(sweetClasses);
				JsonElement metadataElement = jsonObject.get("metadata");
				JsonObject jsonMetadataObject = metadataElement.getAsJsonObject();

				appendSweetToMetadataObject(jsonMetadataObject, sweetClasses);

				String json = gson.toJson(jsonObject);
				return json;

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				System.out.println("This got timed out");
				e.printStackTrace();
			}

		} else {
			return gson.toJson(jsonObject);
		}
		return gson.toJson(jsonObject);

	}

	public static void appendSweetToMetadataObject(JsonObject jsonMetadataObject, ArrayList<SweetClass> sweetClasses) {
		// Name Of the Fields
		String sweetNameField = "sweetName";
		String restField = "restriction";
		String equivField = "equiv";
		String commentField = "comment";
		String subClassesField = "subclassesOf";
		String occurenceField = "occurence";
		int id = 1;

		for (SweetClass sweetClass : sweetClasses) {
			jsonMetadataObject.addProperty(sweetNameField + id, sweetClass.getName());
			if (sweetClass.getRestrictions().size() > 0 && sweetClass.getRestrictions().get(0) != null) {
				jsonMetadataObject.addProperty(restField + id, sweetClass.getRestrictions().get(0));
			}
			if (sweetClass.getSubClasses().size() > 0 && sweetClass.getSubClasses().get(0) != null) {
				jsonMetadataObject.addProperty(subClassesField + id, sweetClass.getSubClasses().get(0));
			}
			jsonMetadataObject.addProperty(equivField + id, sweetClass.getEquivalentClass());
			jsonMetadataObject.addProperty(commentField + id, sweetClass.getComment());
			jsonMetadataObject.addProperty(occurenceField + id, sweetClass.getOccured());
			id++;

		}
		
	}

	// Wrapper to write JSON to file
	public static void writeJsonToFile(String json, String filename) throws FileNotFoundException {

		String fullPath = FilenameUtils.concat(destination, filename);
		PrintWriter writer = new PrintWriter(new FileOutputStream(fullPath, false));
		json = JsonWriter.formatJson(json);
		writer.write(json);
		writer.close();
	}

}
