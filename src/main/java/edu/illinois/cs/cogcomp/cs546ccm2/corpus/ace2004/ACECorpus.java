package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocumentAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.util.XMLException;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;

/**
 * TODO: Does it make sense to assign internal doc ids?
 * 
 * Intended to provide a set of functions to deal with the ACE corpus.
 * 
 * @author shashank
 *
 */

public class ACECorpus {
	
	static boolean isDebug = true;
	private int docCount = 0;
	
	private List<ACEDocument> corpus;

	static String[] failureFileList = new String[] {};

    public static void main (String[] args) throws Exception {

	    String aceCorpusDir = "data/ACE2004/data/English";
		String outputDir = "target/test04_1/";
		
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.prepareCorpus(aceCorpusDir, outputDir);
		
		aceCorpus.readCorpus(outputDir);
	}
    
    public int getDocCount() {
    	return docCount;
    }
    
    public void readCorpus(String inDirPath) {
    	File inDir = new File(inDirPath);
    	File[] docs = inDir.listFiles();
		FileInputStream f;
		ObjectInputStream s;
		ACEDocument aceDoc;
		corpus = new ArrayList<ACEDocument>();
    	for(int i=0; i<docs.length; i++) { 
    		try {
    			f = new FileInputStream(docs[i]);
			    s = new ObjectInputStream(f);
			    aceDoc = (ACEDocument) s.readObject();
			    System.out.println(aceDoc.aceAnnotation.id);
			    corpus.add(aceDoc);
			    docCount++;
			    s.close();
			    f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	System.out.println(docCount + " Files Read successfully");
    }
       
    public void prepareCorpus(String docDirInput, String docDirOutput) {
		File outDir = new File(docDirOutput);
		if (outDir.exists() == true) {
			throw new RuntimeException("Processed Corpus already exists .. exiting");
		}
		outDir.mkdir();
		
		corpus = new ArrayList<ACEDocument>();
		CcgTextAnnotationBuilder taBuilder = new CcgTextAnnotationBuilder(new IllinoisTokenizer());
		AceFileProcessor fileProcessor = new AceFileProcessor(taBuilder);
		processAndDumpDocuments(fileProcessor, docDirInput, docDirOutput);
    }

	private void processAndDumpDocuments(AceFileProcessor processor, String inputFolderStr, String outputFolderStr) {
		HashSet<String> failureFileSet = new HashSet<String>();
		for (int i = 0; i < failureFileList.length; ++i) {
			failureFileSet.add(failureFileList[i]);
		}
		
		File inputFolder = new File (inputFolderStr);
		File[] subFolderList = inputFolder.listFiles();

		for (int folderIndex = 0; folderIndex < subFolderList.length; ++folderIndex) {

			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File directory, String fileName) {
					return fileName.endsWith(".apf.xml");
				}
			};
            File subFolderEntry = subFolderList[folderIndex];
			File[] fileList = subFolderEntry.listFiles(filter);
			for (int fileID = 0; fileID < fileList.length; ++fileID) {
				
				if(failureFileSet.contains(fileList[fileID].getName()))
                	continue;
                String annotationFile = fileList[fileID].getAbsolutePath();

                System.err.println( "reading ace annotation from '" + annotationFile + "'..." );
                ACEDocumentAnnotation annotationACE = null;
                try {
                    annotationACE = ReadACEAnnotation.readDocument(annotationFile);
                } catch (XMLException e) {
                    e.printStackTrace();
                    continue;
                }

                File outputFile = new File (outputFolderStr + annotationACE.id +".ta");
                if (outputFile.exists()) {
                    continue;
                }

                ACEDocument aceDoc = processor.processAceEntry(subFolderEntry, annotationACE, annotationFile);
                corpus.add(aceDoc);
				FileOutputStream f;
				try {
					f = new FileOutputStream(outputFile);
				    ObjectOutputStream s = new ObjectOutputStream(f);
				    s.writeObject(aceDoc);
				    s.close();
				    f.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	private static void checkAlign(String content, ACEDocumentAnnotation annotation) {
//		List<ACEEntity> entities = annotation.entityList;
//		for (ACEEntity entity : entities) {
//			for (ACEEntityMention mention : entity.entityMentionList) {
//				String str1 = content.substring(mention.extentStart, mention.extentEnd+1); 
//				str1 = str1.replaceAll("&amp;", "&"); // To be noticed !!!
//				
//				String str2 = mention.extent;
//				if (!str1.equals(str2)) {
//					System.out.println(str2+"\n"+str1+"\n"+content);
//					System.exit(1);
//				}
//			}
//		}
//	}

	

}
