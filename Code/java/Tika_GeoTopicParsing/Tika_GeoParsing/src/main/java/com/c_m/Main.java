package main.java.com.c_m;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.geo.topic.GeoParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.cedarsoftware.util.io.JsonWriter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Main {

	private static TikaExtractor tikaExtractor;
	private static ExecutorService executor;
	private static int executionLimitSeconds = 15;
	private static GeoParser parser;
	private static Gson gson;
	private static BodyContentHandler handler;
	private static boolean r = false;
	private static String destination = Paths.get(".").toAbsolutePath().normalize().toString();
	private static String source = Paths.get(".").toAbsolutePath().normalize().toString();
	private static long foundFileCount=0;
	private static String startAfter = "";
	private static boolean process = true;
	private static long processedFileCount =0;
	private static long totalFileCount =0;

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
		System.out.println("Initializing");
		// We are gonna reuse them.

		parser = new GeoParser();
		handler = new BodyContentHandler();
		gson = new Gson();
		tikaExtractor = new TikaExtractor();
		executor = Executors.newFixedThreadPool(1);
		resume();

	}

	public static boolean verifyParameters(String[] args) {
		if (args.length == 0) {
			source = "/Volumes/My Passport/Data/newData/sweet";

			destination = "/Volumes/My Passport/Data/newData/geo";
			System.out.println("Source is set to : " + source);
			System.out.println("Destination is set to : " + destination);
		} else {
			if (args.length != 2) {
				System.out.println("Expecting 2 command line arguments got " + args.length);
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
				System.out.println(source + " does not exist please recheck the path");
			} else {
				System.out.println(destination + " does not exist please recheck the path");
			}
			return false;
		}

		return true;
	}

	public static void main(String[] args) throws IOException {

		if (verifyParameters(args)) {
			initialize();
			File rootdir = new File(source);
			File [] files = rootdir.listFiles();
			totalFileCount = files.length;
			// Start Parsing
			Arrays.sort(files);
			parseAllFiles(files);
		}

	}

	public static Metadata getGeoInformation(String str) {

		try (InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8))) {

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
				return metadata;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				System.out.println("This got timed out");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Thrown Exception1");
			// e.printStackTrace();
		}
		return null;
	}

	public static void parseAllFiles(File[] files) throws IOException {
		Arrays.sort(files);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (file.isDirectory()) {
				// Explore sub-directories.
				if (r) {

					parseAllFiles(file.listFiles());
				}
			} else {
				processedFileCount++;
				if (process) {

					try {
						
						System.out.println(processedFileCount+ "/ "+totalFileCount + " Processing " + file.toString());
						saveLastFile(file.getName());
						// Read the content on the File
						String fileStr = FileUtils.readFileToString(file);
						JsonObject jsonObject = (new JsonParser()).parse(fileStr).getAsJsonObject();
						JsonObject jsonMetadataObject = jsonObject.getAsJsonObject("metadata").getAsJsonObject();
						jsonMetadataObject.remove("X-TIKA:parse_time_millis");
						jsonMetadataObject.remove("X-Parsed-By");
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
							//System.out.println(content);
							JsonElement contentTypeElement = jsonObject.get("metadata").getAsJsonObject().get("Content-Type");
							String contentType = contentTypeElement.toString();

							content = tikaExtractor.autoExtract(content, contentType);

							Metadata metadata = getGeoInformation(content);
							if (metadata != null) {
								ArrayList<Place> places = parseMetadataForPlaces(metadata);
								String jsonStr = appendPlacesToJsonObject(jsonObject, places);

								writeJsonToFile(jsonStr, file.getName());
							} else {
								String str = jsonObject.toString();
								writeJsonToFile(str, file.getName());

							}
						} else {
							String str = jsonObject.toString();
							writeJsonToFile(str, file.getName());
						}

					} catch (Exception e) {
						System.out.println("Thrown Exception2");
						e.printStackTrace();
					}
				}
				if (!process & file.getName().equals(startAfter)) {
					System.out.println("Resuming after " + startAfter);
					process = true;
				}
			}
		}
	}

	public static String appendPlacesToJsonObject(JsonObject jsonObject, ArrayList<Place> places) {

		
		if (places.size() != 0) {
			foundFileCount++;
			System.out.println("PLACES FOUND! " + places.size());
			System.out.println("Found is! " +foundFileCount);
			
		}

		JsonObject jsonMetadataObject = jsonObject.getAsJsonObject("metadata").getAsJsonObject();

		String locField = "loc";
		String latField = "lat";
		String longField = "long";
		int id = 1;
		for (Place place : places) {
			if (id <= 3) {
				jsonMetadataObject.addProperty(locField + id, place.getName());
				jsonMetadataObject.addProperty(latField + id, place.getLattitude());
				jsonMetadataObject.addProperty(longField + id, place.getLongitude());
				id++;
			}

		}
		String json = gson.toJson(jsonObject);
		return json;
	}

	public static ArrayList<Place> parseMetadataForPlaces(Metadata metadata) {
		// We are gonna fill this array list
		ArrayList<Place> places = new ArrayList<Place>();

		// First Country in the file is special case
		if (metadata.get("Geographic_NAME") != null) {
			String name = metadata.get("Geographic_NAME");
			double longitude = Double.parseDouble(metadata.get("Geographic_LONGITUDE"));
			double lattitude = Double.parseDouble(metadata.get("Geographic_LATITUDE"));
			Place place = new Place(name, longitude, lattitude);
			places.add(place);
		}
		int index = 1;
		String key = "Optional_NAME";
		String combinedKey = key + String.valueOf(index);
		while (metadata.get(combinedKey) != null) {
			String name = metadata.get(combinedKey);
			double longitude = Double.parseDouble(metadata.get("Optional_LONGITUDE" + String.valueOf(index)));
			double lattitude = Double.parseDouble(metadata.get("Optional_LATITUDE" + String.valueOf(index)));
			Place place = new Place(name, longitude, lattitude);
			places.add(place);
			index++;
			combinedKey = key + String.valueOf(index);
		}
		return places;
	}

	public static void writeJsonToFile(String json, String filename) throws FileNotFoundException {
		String fullPath = FilenameUtils.concat(destination, filename);
		PrintWriter writer = new PrintWriter(new FileOutputStream(fullPath, false));
		json = JsonWriter.formatJson(json);
		writer.write(json);
		writer.close();
	}
}
