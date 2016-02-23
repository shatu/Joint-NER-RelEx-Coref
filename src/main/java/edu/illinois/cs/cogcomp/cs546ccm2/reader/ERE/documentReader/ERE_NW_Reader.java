package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.documentReader;


import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraphTAC15;

public class ERE_NW_Reader {

	public static String systemNewLine = System.getProperty("line.separator");
	static boolean isDebug = true;

	public static List<PostParagraphTAC15> parse (List<String> docLines, String docContent) throws Exception {
		String id = "";
		String date = "";
		String para = "";
		int dateOffset = 0;
		
		boolean isPara = false;
		List<PostParagraphTAC15> paraList = new ArrayList<PostParagraphTAC15>();
				
		
		for (int i = 0; i < docLines.size(); ++i) {
			String line = docLines.get(i);

			if (line.contains("<DOC id")) {
				id = getDocID (line);
				String[] tokens = id.split("[._]");
				date = tokens[2];
				dateOffset = docContent.indexOf(date);
			}
			
			if (line.contains("</P>") == false && isPara == true) {
				para += line + systemNewLine;
				
			}
			
			if (line.contains("<P>")) {
				isPara = true;
			}
			
			if (line.contains("</P>")) {
				PostParagraphTAC15 paragraph = new PostParagraphTAC15();
				paragraph.content = para.trim();
				paragraph.offset = docContent.indexOf(para.trim());
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
	
	public static String getDocID (String line) {
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
	
	
}
