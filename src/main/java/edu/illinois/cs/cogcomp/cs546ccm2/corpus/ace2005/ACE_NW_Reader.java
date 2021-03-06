package edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;
import edu.illinois.cs.cogcomp.cs546ccm2.common.CCM2Constants;
import edu.illinois.cs.cogcomp.cs546ccm2.corpus.Paragraph;
import edu.illinois.cs.cogcomp.cs546ccm2.util.IOManager;

public class ACE_NW_Reader {
	
	static boolean isDebug = false;

	public static void main (String[] args) {
		String file = CCM2Constants.ACE05CorpusPath + "/nw/AFP_ENG_20030304.0250.sgm";
		List<String> lines = IOManager.readLinesWithoutTrimming(file);
		String content = "";
		for (int i = 0; i < lines.size(); ++i) {
			content += lines.get(i) + " ";
		}
		String contentRemovingTags = content;
	 	while (contentRemovingTags.contains("<")) {
	 		int p = contentRemovingTags.indexOf('<');
	 		int q = contentRemovingTags.indexOf('>');
	 		contentRemovingTags = contentRemovingTags.substring(0,p) 
	 				+ contentRemovingTags.substring(q+1, contentRemovingTags.length());
	 	}
		parse(content, contentRemovingTags);		
	}
	
	public static List<Pair<String, Paragraph>> parse (String content, String contentRemovingTags) {
		List<Pair<String, Paragraph>> paragraphs = new ArrayList<Pair<String, Paragraph>>();
		
		Pattern pattern = null;
		Matcher matcher = null;
		
		String docID = "";
		String dateTime = "";
		String headLine = "";
		String text = "";
		
		pattern = Pattern.compile("<DOCID>(.*?)</DOCID>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			docID = (matcher.group(1)).trim();
	    }
		int index1 = content.indexOf(docID);
		Paragraph para1 = new Paragraph(index1, docID);
		Pair<String, Paragraph> pair1 = new Pair<String, Paragraph>("docID", para1);
		paragraphs.add(pair1);
		
		pattern = Pattern.compile("<DATETIME>(.*?)</DATETIME>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			dateTime = (matcher.group(1)).trim();
	    }
		int index2 = content.indexOf(dateTime);
		Paragraph para2 = new Paragraph(index2, dateTime);
		Pair<String, Paragraph> pair2 = new Pair<String, Paragraph>("dateTime", para2);
		paragraphs.add(pair2);
		
		pattern = Pattern.compile("<HEADLINE>(.*?)</HEADLINE>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			headLine = (matcher.group(1)).trim();
	    }
		int index3 = content.indexOf(headLine);
		Paragraph para3 = new Paragraph(index3, headLine);
		Pair<String, Paragraph> pair3 = new Pair<String, Paragraph>("headLine", para3);
		paragraphs.add(pair3);
		
		pattern = Pattern.compile("<TEXT>(.*?)</TEXT>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			text = (matcher.group(1)).trim();
			int index4 = content.indexOf(text);
			Paragraph para4 = new Paragraph(index4, text);
			Pair<String, Paragraph> pair4 = new Pair<String, Paragraph>("text", para4);
			paragraphs.add(pair4);
	    }
		
		int index = 0;
		for (int i = 0; i < paragraphs.size(); ++i) {
			int offsetWithFiltering = contentRemovingTags.indexOf(paragraphs.get(i).getSecond().content, index);
			paragraphs.get(i).getSecond().offsetFilterTags = offsetWithFiltering;
			
			index += paragraphs.get(i).getSecond().content.length();
		}
		
		if (isDebug) {
			for (int i = 0; i < paragraphs.size(); ++i) {
				System.out.println(paragraphs.get(i).getFirst() + "--> " + paragraphs.get(i).getSecond().content);
				System.out.println(content.substring(paragraphs.get(i).getSecond().offset, 
						paragraphs.get(i).getSecond().offset + paragraphs.get(i).getSecond().content.length()));
				System.out.println(contentRemovingTags.substring(paragraphs.get(i).getSecond().offsetFilterTags, 
						paragraphs.get(i).getSecond().offsetFilterTags + paragraphs.get(i).getSecond().content.length()));
				System.out.println();
			}
		}
		
//		if (isDebug) {
//			for (int i = 0; i < paragraphs.size(); ++i) {
//				System.out.println(paragraphs.get(i).getFirst() + "--> " + paragraphs.get(i).getSecond().content);
//				System.out.println(content.substring(paragraphs.get(i).getSecond().offset, 
//						paragraphs.get(i).getSecond().offset + paragraphs.get(i).getSecond().content.length()));
//				System.out.println();
//			}
//		}

		return paragraphs;
	}
	
}
