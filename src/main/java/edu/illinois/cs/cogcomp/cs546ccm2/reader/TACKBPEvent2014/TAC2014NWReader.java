package edu.illinois.cs.cogcomp.cs546ccm2.reader.TACKBPEvent2014;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraph;
import edu.illinois.cs.cogcomp.cs546ccm2.util.IOManager;


public class TAC2014NWReader {
	
	public static String systemNewLine = System.getProperty("line.separator");

	public static void main (String[] args) throws Exception {
		
	//	String docPath = "/shared/shelley/yqsong/eventData/EAE/pilot_data/data/nw/";
		String docPath = "/shared/shelley/yqsong/eventData/EAE/LDC2014R43_TAC_2014_KBP_English_Event_Argument_Extraction_Evaluation_Source_Corpus_V1.1/data/nw/";
		
		File fileFolder = new File(docPath);
		File[] list = fileFolder.listFiles();
		for (int f = 0; f < list.length; ++f) {
			TAC2014NWReader nwReader = new TAC2014NWReader();
			List<PostParagraph> paraList = nwReader.processDoc (list[f].getAbsolutePath());
			
			for (int i = 0; i < paraList.size(); ++i) {
				System.out.println("offset: " + paraList.get(i).offset + ", dateTime: " + 
						paraList.get(i).dateTime);
				System.out.println("-----------------------------");
				System.out.println(paraList.get(i).content);
				System.out.println("-----------------------------");
				
				String doc = IOManager.readContentCharBuf(list[f].getAbsolutePath());
				
				System.out.println(doc.substring(paraList.get(i).offset, 
						paraList.get(i).offset + paraList.get(i).content.length()));
				System.out.println("-----------------------------");
			}
			System.out.println();
		}
		
	}

	public List<PostParagraph> processDoc (String docPath) throws Exception {
		String doc = IOManager.readContentCharBuf(docPath);
		
		List<String> docLines = readWholeFileAsLines (docPath);
		
		String id = "";
		String date = "";
		String para = "";
		int dateOffset = 0;
		
		boolean isPara = false;
		List<PostParagraph> paraList = new ArrayList<PostParagraph>();
				
		
		for (int i = 0; i < docLines.size(); ++i) {
			String line = docLines.get(i);

			if (line.contains("<DOC id")) {
				id = getDocID (line);
				String[] tokens = id.split("[._]");
				date = tokens[2];
				dateOffset = doc.indexOf(date);
			}
			
			if (line.contains("</P>") == false && isPara == true) {
				para += line + systemNewLine;
				
			}
			
			if (line.contains("<P>")) {
				isPara = true;
			}
			
			if (line.contains("</P>")) {
				PostParagraph paragraph = new PostParagraph();
				paragraph.content = para.trim();
				paragraph.offset = doc.indexOf(para.trim());
				paragraph.docID = id;
				paragraph.dateTime = date;
				paragraph.dateTimeOffset = dateOffset;
				
				paraList.add(paragraph);
				
				isPara = false;
				para = "";
			}
		}
		
		return paraList;
	}
	
	public String getDocID (String line) {
		String id = "";
		
		String[] tokens = line.split(" ");
		String[] subTokens = tokens[1].split("=");
		
		id = subTokens[1].replaceAll("\"", "");
		
		return id;
	}
	
	public String getDateTime (String line) {
		String dateTime = "";
		
		int start = line.indexOf("<DATELINE>");
		int end = line.indexOf("-</DATELINE>");
		
		dateTime = line.substring(start, end);
		
		return dateTime;
	}
	
	public List<String> readWholeFileAsLines (String filePath) {
		List<String> content = new ArrayList<String>();
		try {
			FileReader reader = new FileReader(filePath);
			BufferedReader br = new BufferedReader(reader);
			
			String line = "";
			while ((line = br.readLine()) != null) {
				content.add(line);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
