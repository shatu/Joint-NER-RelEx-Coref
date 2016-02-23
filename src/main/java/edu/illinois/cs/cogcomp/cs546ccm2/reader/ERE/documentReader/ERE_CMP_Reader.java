package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.documentReader;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraph;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraphTAC15;
import edu.illinois.cs.cogcomp.cs546ccm2.util.IOManager;

public class ERE_CMP_Reader {
	public static String systemNewLine = System.getProperty("line.separator");
	static boolean isDebug = true;

	public static void main (String[] args) throws Exception {
//		String file = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/source/cmptxt/1d2911e09a6746b942c3e7b3cbdcb0ce.cmp.txt";

		String file = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/source/cmptxt/cca700aed62fd497e64e507752409b41.cmp.txt";

		List<String> lines = IOManager.readLinesWithoutTrimming(file);
		String content = "";
		for (int i = 0; i < lines.size(); ++i) {
			content += lines.get(i) + " ";
		}
		
		ERE_CMP_Reader reader = new ERE_CMP_Reader();
		List<PostParagraphTAC15> paraList = reader.parse(lines, content);
		
		for (int i = 0; i < paraList.size(); ++i) {
			System.out.println("offset: " + paraList.get(i).offset + ", dateTime: " + 
					paraList.get(i).dateTime);
			System.out.println("-----------------------------");
			System.out.println(paraList.get(i).content);
			System.out.println("-----------------------------");
			
			System.out.println(content.substring(paraList.get(i).offset, 
					paraList.get(i).offset + paraList.get(i).content.length()));
			System.out.println("-----------------------------");
		}
		System.out.println();
	}
	
	public static List<PostParagraphTAC15> parse (List<String> docLines, String docContent) throws Exception {
		
		String id = "";
		
		boolean isPost = false;
		List<PostParagraphTAC15> paraList = new ArrayList<PostParagraphTAC15>();
		
		List<String> postLines = new ArrayList<String>();
		int postOffset = 0;
		
		for (int i = 0; i < docLines.size(); ++i) {
			String line = docLines.get(i);

			if (line.contains("<doc id")) {
				id = getDocID (line);
				
				System.out.println(id);
			}
			
			if (line.contains("</post") == false && isPost == true) {
				postLines.add(line);
			}
			
			if (line.contains("<post")) {
				isPost = true;

				postLines.add(line); // add the fistLine because we need the offset info
				
				postOffset = docContent.indexOf(line);
				
			}
			
			if (line.contains("</post")) {
				List<PostParagraphTAC15> paraListInPost = parsePost (docContent, postLines, postOffset, id);
				paraList.addAll(paraListInPost);
				
				isPost = false;
				postLines = new ArrayList<String>();
			}
			
		}
		
		return paraList;
	}
	
	public static List<PostParagraphTAC15> parsePost (String doc, List<String> lines, int postOffset, 
			String id) {
		String allPostStr = "";
		for (int i = 0; i < lines.size(); ++i) {
			allPostStr += lines.get(i) + systemNewLine;
		}
		
		List<PostParagraphTAC15> paraList = new ArrayList<PostParagraphTAC15>();
		
		int isQuote = 0;
		
		String postAuthor = "";
		int postAuthorOffset = -1;
		
		String quoteAuthor = "";
		int quoteAuthorOffset = -1;
		
		String dateTime = "";
		int dateTimeOffset = -1;
		
		String postLine = lines.get(0);
		if (postLine.contains("<post author=")) {
			int start = postLine.indexOf("<post author=") + "<post author=".length();
			int end = postLine.indexOf("datetime=");
			postAuthor = postLine.substring(start + 1, end).replaceAll("\"", "").trim();
			if (postAuthor.equals("") == false) {
				postAuthorOffset = postLine.indexOf(postAuthor) + postOffset;
			}

			start = postLine.indexOf("datetime=") + "datetime=".length();
			end = postLine.indexOf("id=");
			dateTime = postLine.substring(start + 1, end).replaceAll("\"", "").trim();
			dateTimeOffset = postLine.indexOf(dateTime) + postOffset;
		}
		
		if (lines.size() > 1) {
			String postContent = "";
			int postContentIndex = postOffset + postLine.length() + 1;
			for (int i = 1; i < lines.size(); ++i) {
				
				String line = lines.get(i);
				
				postContent += line + " ";
			}
			
			Pattern bodyPattern = Pattern.compile("<img src=(.*?)/>");
			Matcher bodyMatcher = bodyPattern.matcher(postContent);
			while (bodyMatcher.find()) {
				String imageContent = (bodyMatcher.group(1));
				
				char[] array = new char[imageContent.length()];
		 	    int pos = 0;
		 	    while (pos < imageContent.length()) {
		 	        array[pos] = ' ';
		 	        pos++;
		 	    }
		 	    String spaceString = new String(array);
		 	    postContent = postContent.replace(imageContent, spaceString);
		    }
			
			bodyPattern = Pattern.compile("<a href=(.*?)</a>");
			bodyMatcher = bodyPattern.matcher(postContent);
			while (bodyMatcher.find()) {
				String linkContent = (bodyMatcher.group(1));
				
				char[] array = new char[linkContent.length()];
		 	    int pos = 0;
		 	    while (pos < linkContent.length()) {
		 	        array[pos] = ' ';
		 	        pos++;
		 	    }
		 	    String spaceString = new String(array);
		 	   postContent = postContent.replace(linkContent, spaceString);
		    }
			
			while (postContent.contains("<img src=")) {
		 		int p = postContent.indexOf("<img src=");
		 		int q = postContent.indexOf("/>");
		 		char[] array = new char[q - p + 2];
		 	    int pos = 0;
		 	    while (pos < q - p + 2) {
		 	        array[pos] = ' ';
		 	        pos++;
		 	    }
		 	    
		 	   String spaceString = new String(array);
		 	   postContent = postContent.substring(0,p) + spaceString 
		 			   + postContent.substring(q+2, postContent.length());
		 	}
			
			while (postContent.contains("<a href=")) {
		 		int p = postContent.indexOf("<a href=");
		 		int q = postContent.indexOf("</a>");
		 		char[] array = new char[q - p + 4];
		 	    int pos = 0;
		 	    while (pos < q - p + 4) {
		 	        array[pos] = ' ';
		 	        pos++;
		 	    }
		 	    
		 	   String spaceString = new String(array);
		 	   postContent = postContent.substring(0,p) + spaceString 
		 			   + postContent.substring(q+4, postContent.length());
		 	}
			
			while (postContent.contains("<quote")) {
		 		int p = postContent.indexOf("<quote");
		 		int q = postContent.lastIndexOf("</quote>");
		 		char[] array = new char[q - p + 8];
		 	    int pos = 0;
		 	    while (pos < q - p + 8) {
		 	        array[pos] = ' ';
		 	        pos++;
		 	    }
		 	    
		 	   String spaceString = new String(array);
		 	   postContent = postContent.substring(0,p) + spaceString 
		 			   + postContent.substring(q+8, postContent.length());
		 	}
			
//			postContent.replaceAll("<img src=", "         ");
//			postContent.replaceAll("/>", "  ");
//			postContent.replaceAll("<a href=", "        ");
//			postContent.replaceAll("</a>", "    ");
			
			PostParagraphTAC15 paragraph = new PostParagraphTAC15();
			
			paragraph.content = postContent;
			paragraph.dateTime = dateTime;
			paragraph.docID = id;
			paragraph.offset = postContentIndex;
			paragraph.dateTimeOffset = dateTimeOffset;
			paragraph.postAuthor = postAuthor;
			paragraph.postAuthorOffset = postAuthorOffset;
			paraList.add(paragraph);
		}

		return paraList;
	}
	

	public PostParagraph getParagraph (String doc, String line) throws Exception {
		PostParagraph paragraph = new PostParagraph();
		
		paragraph.content = line;
		
		paragraph.offset = doc.indexOf(line);
		
		if (paragraph.offset == -1) 
			throw new Exception("cannot find the paragraph");
		
		return paragraph;
	}
		
	public String getDocDateTime (String line) {
		String dateTime = "";
		
		
		int start = line.indexOf("datetime=") + "datetime=".length();
		int end = line.indexOf("id=");
		
		dateTime = line.substring(start + 1, end).replaceAll("\"", "").trim();
		
		return dateTime;
	}
	
	public static String getDocID (String line) {
		String id = "";
		
		int start = line.indexOf("<doc id=");
		int end = line.indexOf(">");
		
		id = line.substring(start + "<doc id=".length(), end).replaceAll("\"", "");
		
		return id;
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
