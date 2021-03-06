package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.illinois.cs.cogcomp.annotation.AnnotatorException;
import edu.illinois.cs.cogcomp.annotation.handler.IllinoisPOSHandler;
import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Relation;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocument;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACEDocumentAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.ACorpus;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.AnnotatedText;
import edu.illinois.cs.cogcomp.cs546ccm2.util.XMLException;
import edu.illinois.cs.cogcomp.nlp.tokenizer.IllinoisTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.CcgTextAnnotationBuilder;

/**
 * 
 * Intended to provide a set of functions to deal with the ACE corpus.
 * 
 * @author shashank
 *
 */

public class ACECorpus extends ACorpus {
	
	static String NAME = "ACE2004";
	
	private static IntPair arabic_treebankSpan;
	private static IntPair nwireSpan;
	private static IntPair fisher_transcriptsSpan;
	private static IntPair chinese_treebankSpan;
	private static IntPair bnewsSpan;

	static String[] failureFileList = new String[] {};
	
	public List<ACEDocument> getarabic_treebankDocs() {
		return docs.subList(arabic_treebankSpan.getFirst(), arabic_treebankSpan.getSecond());
	}
	
	public List<ACEDocument> getnwireDocs() {
		return docs.subList(nwireSpan.getFirst(), nwireSpan.getSecond());
	}
	
	public List<ACEDocument> getfisher_transcriptsDocs() {
		return docs.subList(fisher_transcriptsSpan.getFirst(), fisher_transcriptsSpan.getSecond());
	}
	
	public List<ACEDocument> getchinese_treebankDocs() {
		return docs.subList(chinese_treebankSpan.getFirst(), chinese_treebankSpan.getSecond());
	}
	
	public List<ACEDocument> getbnewsDocs() {
		return docs.subList(bnewsSpan.getFirst(), bnewsSpan.getSecond());
	}
	
	public static String getName() {
		return NAME;
	}
	
    public static void main (String[] args) throws Exception {
		
    	String aceCorpusDir = CCM2Constants.ACE04CorpusPath + "/data/English";
    	String outputDir = CCM2Constants.ACE04ProcessedPath;
		
		ACECorpus aceCorpus = new ACECorpus();
		aceCorpus.prepareCorpus(aceCorpusDir, outputDir);
		
		String processedCorpusDir = outputDir;
		aceCorpus.initCorpus(processedCorpusDir);
		
		if(!ACECorpus.isCorpusReady()) {
			System.out.println("Some problem in initializing the corpus -- please make sure that you run readCorpus() before trying to read the corpus");
			System.exit(-1);
		}
		
		System.out.println(aceCorpus.getarabic_treebankDocs().size());
		System.out.println(aceCorpus.getbnewsDocs().size());
		System.out.println(aceCorpus.getchinese_treebankDocs().size());
		System.out.println(aceCorpus.getfisher_transcriptsDocs().size());
		System.out.println(aceCorpus.getnwireDocs().size());
		System.out.println(aceCorpus.getAllDocs().size());
		
//		aceCorpus.testRelationView();
//		aceCorpus.printRelationTypes();
//		aceCorpus.testCoRefView();
	}
    
    public void testRelationView() {
    	ACEDocument doc = getDocFromID("AFP_ENG_20030304.0250");
		for(AnnotatedText ta: doc.taList) {
			List<Constituent> annots = ta.getTa().getView(CCM2Constants.RelExGold).getConstituents();
			for(Constituent annot: annots) {
				if(annot.getOutgoingRelations().size() > 0) {
					System.out.println(annot.getOutgoingRelations().size());
					for(Relation rel : annot.getOutgoingRelations()) {
						System.out.println(rel.getRelationName() + "-->" + rel.getSource() + "-->" + rel.getSource().getLabel() + "-->" + 
								rel.getTarget() + "-->" + rel.getTarget().getLabel());
					}
				}
			}
		}
    }
    
    public void testCoRefView() {
    	ACEDocument doc = getDocFromID("AFP_ENG_20030304.0250");
		for(AnnotatedText ta: doc.taList) {
			List<Constituent> annots = ta.getTa().getView(CCM2Constants.CoRefGold).getConstituents();
			for(Constituent annot: annots) {
				if(annot.getIncomingRelations().size() != 0)
					continue;
				if(annot.getOutgoingRelations().size() > 0) {
					System.out.println(annot.getOutgoingRelations().size());
					Constituent chain = annot;
					while(chain.getOutgoingRelations().size() > 0) {
						Relation rel = chain.getOutgoingRelations().get(0);
						System.out.println(rel.getRelationName() + "-->" + rel.getSource() + "-->" + rel.getSource().getLabel() + "-->" + 
								rel.getTarget() + "-->" + rel.getTarget().getLabel());
						
						chain = rel.getTarget();
						System.out.println("*****");
					}
				}
				else {
					System.out.println(annot.getOutgoingRelations().size());
					System.out.println(annot + "-->" + annot.getLabel() + "-->");
					System.out.println("^^^^^^^^^^^^^^^^^");
				}
			}
		}
    }
    
    public void printRelationTypes() {
    	HashSet<String> relationTypes = new HashSet<String>();
    	for(ACEDocument doc : getAllDocs()) {
    		for(AnnotatedText ta: doc.taList) {
    			List<Constituent> annots = ta.getTa().getView(CCM2Constants.RelExGold).getConstituents();
    			for(Constituent annot: annots) {
    				if(annot.getOutgoingRelations().size() > 0) {
    					for(Relation rel : annot.getOutgoingRelations()) {
    						relationTypes.add(rel.getRelationName());
    					}
    				}
    			}
    		}
    	}
    	
    	for(String relType : relationTypes) {
    		System.out.println(relType);
    	}
    	
    }
    
    @SuppressWarnings("unchecked")
	public void initCorpus(String inDirPath) {
    	if(isInit) {
    		System.out.println("Corpus has already been initialized");
    		return;
    	}
    	docs = new ArrayList<ACEDocument>();
    	docIDtoDocMap = new HashMap<>();
    	File inDir = new File(inDirPath);
    	File[] subFolderList = inDir.listFiles();
		for (int i = 0; i < subFolderList.length; ++i) {
	    	File infile = new File(subFolderList[i], getName() + ".obj");
			ObjectInputStream s;
			List<ACEDocument> tempDocs;
			
			String part = subFolderList[i].getName();
			try {
			    s = new ObjectInputStream(new FileInputStream(infile));
			    tempDocs = (List<ACEDocument>) s.readObject();
			    //System.out.println(aceDoc.aceAnnotation.id);
			    switch(part) {
			    	case "arabic_treebank":  
			    		arabic_treebankSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "bnews":
			    		bnewsSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "chinese_treebank": 
			    		chinese_treebankSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "fisher_transcripts": 
			    		fisher_transcriptsSpan = new IntPair(docCount, docCount + tempDocs.size());
			    	case "nwire": 
			    		nwireSpan = new IntPair(docCount, docCount + tempDocs.size());
			    }
			    docs.addAll(tempDocs);
			    docCount += tempDocs.size();
			    s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}    	
    	System.out.println(docCount + " Files Read successfully");
    	
    	for(int i=0; i< docs.size(); i++) {
    		docIDtoDocMap.put(docs.get(i).getDocID(), i);
    	}
    	
    	checkConsistency();
    	isInit = true;
    }
    
    private void checkConsistency() {
    	for(String docID: docIDtoDocMap.keySet()) {
    		if(!docID.equals(docs.get(docIDtoDocMap.get(docID)).getDocID())) {
    			throw new RuntimeException("Problem in creating docID to Doc map");
    		}	
    	}
    	System.out.println("Consistency Test Passed");
    }
       
    public void prepareCorpus(String docDirInput, String docDirOutput) throws AnnotatorException {
		File outDir = new File(docDirOutput);
		if (outDir.exists() == true) {
			throw new RuntimeException("Processed Corpus already exists .. exiting");
		}
		outDir.mkdir();

		CcgTextAnnotationBuilder taBuilder = new CcgTextAnnotationBuilder(new IllinoisTokenizer());
		AceFileProcessor fileProcessor = new AceFileProcessor(taBuilder);
		processAndDumpDocuments(fileProcessor, docDirInput, docDirOutput);
    }

	private void processAndDumpDocuments(AceFileProcessor processor, String inputFolderStr, String outputFolderStr) throws AnnotatorException {
		HashSet<String> failureFileSet = new HashSet<String>();
		IllinoisPOSHandler posTagger = new IllinoisPOSHandler();
		
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
			File outputFolder = new File (outputFolderStr, subFolderEntry.getName());
			outputFolder.mkdir();
			docs = new ArrayList<>();
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

                //TODO: Add more views before dumping ACEDocument? -- POSTags and Shallow Parser?
                ACEDocument aceDoc = processor.processAceEntry(subFolderEntry, annotationACE, annotationFile);
                
                //Add POS Tags
                for(AnnotatedText at : aceDoc.taList) {
                	posTagger.addView(at.getTa());
                }
                
                docs.add(aceDoc);
			}
			
			File outputFile = new File (outputFolder, getName() + ".obj");
            FileOutputStream f;
			try {
				f = new FileOutputStream(outputFile);
			    ObjectOutputStream s = new ObjectOutputStream(f);
			    s.writeObject(docs);
			    s.close();
			    f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
