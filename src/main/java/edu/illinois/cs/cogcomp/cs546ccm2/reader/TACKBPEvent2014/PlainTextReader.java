package edu.illinois.cs.cogcomp.cs546ccm2.reader.TACKBPEvent2014;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraph;
import edu.illinois.cs.cogcomp.cs546ccm2.util.IOManager;

public class PlainTextReader {
	public static void main (String[] args) throws Exception {
		
		String docPath = "/shared/shelley/yqsong/eventData/plainTextTest/testfiles";
		
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
		String date = Calendar.getInstance().toString();
		String para = "";
		int dateOffset = 0;
		
		List<PostParagraph> paraList = new ArrayList<PostParagraph>();
				
		
		for (int i = 0; i < docLines.size(); ++i) {
			String line = docLines.get(i);

			PostParagraph paragraph = new PostParagraph();
			paragraph.content = line.trim();
			paragraph.offset = doc.indexOf(line.trim());
			paragraph.docID = id;
			paragraph.dateTime = date;
			paragraph.dateTimeOffset = dateOffset;
			
			paraList.add(paragraph);
			
			para = "";			
		}
		
		return paraList;
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
