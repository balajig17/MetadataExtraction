package main.java.com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.*;

import org.apache.commons.io.FilenameUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFJsonDocumentFormat;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitorEx;

import com.google.gson.Gson;

public class Main {

	private static String destination = Paths.get(".").toAbsolutePath()
			.normalize().toString();
	private static String source = Paths.get(".").toAbsolutePath().normalize()
			.toString();
	private static String startFrom = "";
	private static boolean start = true;

	public static boolean verifyParameters(String[] args) {
		if (args.length == 0) {
			
			System.out.println("Received no arguments");
			System.out.println("Source is set to : " + source);
			System.out.println("Destination is set to : " + destination);
		} else {
			if (args.length != 2 && args.length != 3) {
				System.out
						.println("Expecting 2 or 3 command line arguments got "
								+ args.length);

				return false;
			} else {
				source = args[0];
				destination = args[1];
				if (args.length == 3) {
					startFrom = args[2];
					System.out.println("Trying to resume from " +startFrom );
					start = false;
				}
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
			File dir = new File(source);
			File[] files = dir.listFiles();
			Arrays.sort(files);
			for (File file : dir.listFiles()) {
				if (file.getName().equals(startFrom)) {
					start = true;
				}

				if (FilenameUtils.getExtension(file.getName()).equals("owl")
						&& start &&file.getName().charAt(0)!='.') {
					System.out.println("PROCESSING FILENAME : "
							+ file.getName());

					OWLOntology ontology = loadOntologyFromFile(file
							.getAbsolutePath());
					ArrayList<SweetClass> sweetClassesFromFile = parseClasses(ontology);
					Collections.sort(sweetClassesFromFile);
					Gson gson = new Gson();
					String jsonRep = gson.toJson(sweetClassesFromFile);
					writeJsonToFile(file.getName(), jsonRep);

				}
			}

		}

	}

	public static void writeJsonToFile(String filename, String json)
			throws IOException {
		FileOutputStream outStream = null;
		Path newFilePath = Paths.get(destination, filename + ".json");
		String generatedFilePath = newFilePath.toString();
		File file = new File(filename + ".json");
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

	public static OWLOntology loadOntologyFromFile(String filePath) {
		File file = new File(filePath);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			ontology = manager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			return null;
		}
		return ontology;
	}

	public static ArrayList<SweetClass> parseClasses(OWLOntology ontology) {
		Set<OWLClass> classes = ontology.getClassesInSignature();
		ArrayList<SweetClass> sweetClasses = new ArrayList<SweetClass>();
		for (OWLClass item : classes) {

			String name = Extracter.extractName(item.toString());
			// System.out.println(name);
			SweetClass sweetClass = new SweetClass(name);
			IRI cIRI = item.getIRI();
			Set<OWLSubClassOfAxiom> subClassesOfAxioms = ontology
					.getSubClassAxiomsForSubClass(item);
			Set<OWLEquivalentClassesAxiom> equivalentClasses = ontology
					.getEquivalentClassesAxioms(item);
			Set<OWLAnnotationAssertionAxiom> assertionAxioms = ontology
					.getAnnotationAssertionAxioms(cIRI);
			Set<OWLClassAxiom> axioms = ontology.getAxioms(item);
			for (OWLAnnotationAssertionAxiom a : assertionAxioms) {
				// System.out.println(a.toString());
				if (a.getValue() instanceof OWLLiteral) {
					OWLLiteral val = (OWLLiteral) a.getValue();
					sweetClass.setComment(val.getLiteral());
				}

			}
			for (OWLClassAxiom ax : axioms) {
				for (OWLClassExpression nce : ax.getNestedClassExpressions()) {
					if (nce.getClassExpressionType() != ClassExpressionType.OWL_CLASS) {
						String restriction = Extracter.extractRestrictions(ax
								.toString());
						sweetClass.appendRestriction(restriction);
					}
				}
			}
			for (OWLEquivalentClassesAxiom equiv : equivalentClasses) {
				String equivClass = Extracter.extractEquivalenceClass(name,
						equiv.toString());
				sweetClass.setEquivalentClass(equivClass);
			}
			for (OWLSubClassOfAxiom subClassInfo : subClassesOfAxioms) {
				if (!subClassInfo.toString().contains("ObjectHas")) {
					String subClass = Extracter.extractSubClass(name,
							subClassInfo.toString());
					sweetClass.appendToSubClasses(subClass);
				}

			}
			sweetClasses.add(sweetClass);
			// sweetClass.printInfo();
		}

		return sweetClasses;
	}
}

/*
 * 
 * 
 * RDFJsonDocumentFormat format = new RDFJsonDocumentFormat();
 * 
 * try { manager.saveOntology(ontology,format,outputFile); } catch
 * (OWLOntologyStorageException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); }
 */

