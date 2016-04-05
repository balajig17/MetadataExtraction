package main.java.com.cmenekse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {

	private static String destination = Paths.get(".").toAbsolutePath()
			.normalize().toString();
	private static String source = Paths.get(".").toAbsolutePath().normalize()
			.toString();

	public static boolean verifyParameters(String[] args) {
		if (args.length == 0) {
			System.out.println("No arguments are given!");
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

	public static void main(String[] args) throws IOException {

		if (verifyParameters(args)) {
			// TODO Auto-generated method stub
			ArrayList<SweetClass> sweetClasses = new ArrayList<SweetClass>();
			File dir = new File(source);
			File[] files = dir.listFiles();
			Gson gson = new Gson();
			Arrays.sort(files);
			for (File file : dir.listFiles()) {
				String contents = FileUtils.readFileToString(file, "UTF-8");

				System.out.println(" PROCESSED FILENAME : " + file.getName());

				ArrayList<SweetClass> sweetClassesFromFile = gson.fromJson(
						contents, new TypeToken<ArrayList<SweetClass>>() {
						}.getType());
				sweetClasses.addAll(sweetClassesFromFile);

			}
			// Set<SweetClass> s = new LinkedHashSet<SweetClass>(sweetClasses);
			sweetClasses = removeDuplicates(sweetClasses);
			for (int i = 0; i < sweetClasses.size(); i++) {
				if (sweetClasses.get(i).getName() != null) {
					String name = sweetClasses.get(i).getName();
					sweetClasses.get(i).setName(name.toUpperCase());
				}
			}
			try {
				Collections.sort(sweetClasses);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String jsonRep = gson.toJson(sweetClasses);
			writeJsonToFile("sweet", jsonRep);
		}

	}

	public static void writeJsonToFile(String filename, String json)
			throws IOException {
		FileOutputStream outStream = null;
		Path newFilePath = Paths.get(destination, filename + ".json");
		String generatedFilePath = newFilePath.toString();
		File file = new File(generatedFilePath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			outStream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OutputStreamWriter outWriter = new OutputStreamWriter(outStream);
		try {
			outWriter.append(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		outWriter.close();
		outStream.close();

	}

	public static ArrayList<SweetClass> removeDuplicates(
			ArrayList<SweetClass> sweetClasses) {
		Set<SweetClass> qualitySet = new HashSet<SweetClass>();
		Set<SweetClass> junk = new HashSet<SweetClass>();
		for (SweetClass s : sweetClasses) {
			if (s.getComment().equals("") && s.getEquivalentClass().equals("")
					&& s.getRestrictions().size() == 0
					&& s.getSubClasses().size() == 0) {
				junk.add(s);
			} else {
				qualitySet.add(s);
			}
		}
		for (SweetClass s : junk) {
			if (!qualitySet.contains(junk)) {
				// qualitySet.add(s);
			}
		}
		sweetClasses.clear();
		sweetClasses.addAll(qualitySet);

		return sweetClasses;

	}

}
