package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.documentReader;


import java.io.File;
import java.util.List;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraphTAC15;
import edu.illinois.cs.cogcomp.cs546ccm2.util.IOManager;

public class ERE_MPDF_Reader {

	public static String systemNewLine = System.getProperty("line.separator");
	static boolean isDebug = true;

	public static void main (String[] args) throws Exception {
//		String filePath = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/source/mpdfxml/1d2911e09a6746b942c3e7b3cbdcb0ce.mpdf.xml";
//		String filePath = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/source/mpdfxml/NYT_ENG_20130422.0048.xml";
		String filePath = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/source/mpdfxml/5c59566e9132c060423cad5b2d1bac1e.mpdf.xml";

		List<String> lines = IOManager.readLinesWithoutTrimming(filePath);
		String content = "";
		for (int i = 0; i < lines.size(); ++i) {
			content += lines.get(i) + " ";
		}
		
		File file = new File (filePath);
		String fileName = file.getName();
		if (fileName.contains("mpdf")) {
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
		} else {
			ERE_NW_Reader nwReader = new ERE_NW_Reader();
			List<PostParagraphTAC15> paraList = nwReader.parse (lines, content); 
			
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
		


	}
	
//	public List<PostParagraph> parse (List<String> docLines, String docContent) throws Exception {
//		
//		String id = "";
//		
//		boolean isPost = false;
//		List<PostParagraph> paraList = new ArrayList<PostParagraph>();
//		
//		List<String> postLines = new ArrayList<String>();
//		int postOffset = 0;
//		
//		for (int i = 0; i < docLines.size(); ++i) {
//			String line = docLines.get(i);
//
//			if (line.contains("My question is why are people jumping on Huckabee's bandwagon. The rest of it was food for thought.") ) {
//				System.out.println();
//			}
//
//
//			if (line.contains("<doc id")) {
//				id = getDocID (line);
//				
//				System.out.println(id);
//			}
//			
//			if (line.contains("</post") == false && isPost == true) {
//				postLines.add(line);
//			}
//			
//			if (line.contains("<post")) {
//				isPost = true;
//
//				postLines.add(line); // add the fistLine because we need the offset info
//				
//				postOffset = docContent.indexOf(line);
//				
//			}
//			
//			if (line.contains("</post")) {
//				List<PostParagraph> paraListInPost = parsePost (docContent, postLines, postOffset, id);
//				paraList.addAll(paraListInPost);
//				
//				isPost = false;
//				postLines = new ArrayList<String>();
//			}
//			
//		}
//		
//		return paraList;
//	}
//	
//	public List<PostParagraph> parsePost (String doc, List<String> lines, int postOffset, 
//			String id) {
//		String allPostStr = "";
//		for (int i = 0; i < lines.size(); ++i) {
//			allPostStr += lines.get(i) + systemNewLine;
//		}
//		
//		List<PostParagraph> paraList = new ArrayList<PostParagraph>();
//		
//		int isQuote = 0;
//		
//		String postAuthor = "";
//		int postAuthorOffset = -1;
//		
//		String quoteAuthor = "";
//		int quoteAuthorOffset = -1;
//		
//		String dateTime = "";
//		int dateTimeOffset = -1;
//		
//		String postLine = lines.get(0);
//		if (postLine.contains("<post author=")) {
//			int start = postLine.indexOf("<post author=") + "<post author=".length();
//			int end = postLine.indexOf("datetime=");
//			postAuthor = postLine.substring(start + 1, end).replaceAll("\"", "").trim();
//			if (postAuthor.equals("") == false) {
//				postAuthorOffset = postLine.indexOf(postAuthor) + postOffset;
//			}
//
//			start = postLine.indexOf("datetime=") + "datetime=".length();
//			end = postLine.indexOf("id=");
//			dateTime = postLine.substring(start + 1, end).replaceAll("\"", "").trim();
//			dateTimeOffset = postLine.indexOf(dateTime) + postOffset;
//		}
//		
//		if (lines.size() > 1) {
//			for (int i = 1; i < lines.size(); ++i) {
//				
//				String line = lines.get(i);
//				
//				if (line.contains("<quote") == false && line.contains("</quote>") == false && isQuote == 0) {
//					Pattern bodyPattern = Pattern.compile("<img src=(.*?)/>");
//					Matcher bodyMatcher = bodyPattern.matcher(line);
//					while (bodyMatcher.find()) {
//						String imageContent = (bodyMatcher.group(1));
//						
//						line = line.replace(imageContent, "");
//				    }
//					
//					while (line.contains("<img src=/>") == true	) {
//						int index = line.indexOf("<img src=/>");
//						
//						String part1 = line.substring(0, index).trim();
//						line = line.substring(index + "<img src=/>".length()).trim();
//						
//						bodyPattern = Pattern.compile("<a href(.*?)</a>");
//						bodyMatcher = bodyPattern.matcher(part1);
//						while (bodyMatcher.find()) {
//							String linkContent = (bodyMatcher.group(1));
//							
//							part1 = part1.replace(linkContent, "");
//					    }
//						while (part1.contains("<a href</a>") == true	) {
//							index = part1.indexOf("<a href</a>");
//							
//							String part2 = part1.substring(0, index).trim();
//							part1 = part1.substring(index + "<a href</a>".length()).trim();
//							
//							if (part2.equals("") == false) {
//								PostParagraph paragraph = new PostParagraph();
//								
//								int localOffset = allPostStr.indexOf(part2);
//								int globalOffset = postOffset + localOffset;
//								
//								paragraph.content = part2;
//								paragraph.dateTime = dateTime;
//								paragraph.docID = id;
//								paragraph.offset = globalOffset;
//								paragraph.dateTimeOffset = dateTimeOffset;
//								paragraph.postAuthor = postAuthor;
//								paragraph.postAuthorOffset = postAuthorOffset;
//								paragraph.quoteAuthor = quoteAuthor;
//								paragraph.quoteAuthorOffset = quoteAuthorOffset;
//								
//								paraList.add(paragraph);
//							}
//							
//						}
//						
//						if (part1.equals("") == false && part1.contains("<a href") == false) {
//							PostParagraph paragraph = new PostParagraph();
//							
//							int localOffset = allPostStr.indexOf(part1.trim());
//							int globalOffset = postOffset + localOffset;
//							
//							paragraph.content = part1;
//							paragraph.dateTime = dateTime;
//							paragraph.docID = id;
//							paragraph.dateTimeOffset = dateTimeOffset;
//							paragraph.postAuthor = postAuthor;
//							paragraph.postAuthorOffset = postAuthorOffset;
//							paragraph.quoteAuthor = quoteAuthor;
//							paragraph.quoteAuthorOffset = quoteAuthorOffset;
//							
//							paraList.add(paragraph);
//						}
//					}
//					
//					if (line.equals("") == false && line.contains("<img src=/>") == false) {
//						
//						bodyPattern = Pattern.compile("<a href(.*?)</a>");
//						bodyMatcher = bodyPattern.matcher(line);
//						while (bodyMatcher.find()) {
//							String linkContent = (bodyMatcher.group(1));
//							
//							line = line.replace(linkContent, "");
//					    }
//						while (line.contains("<a href</a>") == true) {
//							int index = line.indexOf("<a href</a>");
//							
//							String part2 = line.substring(0, index).trim();
//							line = line.substring(index + "<a href</a>".length()).trim();
//							
//							if (part2.equals("") == false) {
//								PostParagraph paragraph = new PostParagraph();
//								
//								int localOffset = allPostStr.indexOf(part2);
//								int globalOffset = postOffset + localOffset;
//								
//								paragraph.content = part2;
//								paragraph.dateTime = dateTime;
//								paragraph.docID = id;
//								paragraph.offset = globalOffset;
//								paragraph.dateTimeOffset = dateTimeOffset;
//								paragraph.postAuthor = postAuthor;
//								paragraph.postAuthorOffset = postAuthorOffset;
//								paragraph.quoteAuthor = quoteAuthor;
//								paragraph.quoteAuthorOffset = quoteAuthorOffset;
//								
//								paraList.add(paragraph);
//							}
//							
//						}
//						
//						if (line.equals("") == false && line.contains("<a href") == false) {
//							PostParagraph paragraph = new PostParagraph();
//							
//							int localOffset = allPostStr.indexOf(line.trim());
//							int globalOffset = postOffset + localOffset;
//							
//							paragraph.content = line;
//							paragraph.dateTime = dateTime;
//							paragraph.docID = id;
//							paragraph.offset = globalOffset;
//							paragraph.dateTimeOffset = dateTimeOffset;
//							paragraph.postAuthor = postAuthor;
//							paragraph.postAuthorOffset = postAuthorOffset;
//							paragraph.quoteAuthor = quoteAuthor;
//							paragraph.quoteAuthorOffset = quoteAuthorOffset;
//							
//							paraList.add(paragraph);
//						}
//						
//					}
//					
//				}
//				
//				if (line.contains("<quote")) {
//					isQuote++;
//					
//					if (isQuote == 1) {
//						if (line.contains("<quote orig_author")) {
//							int start = line.indexOf("<quote orig_author=") + "<quote orig_author=".length();
//							int end = line.indexOf(">");
//							quoteAuthor = line.substring(start + 1, end).replaceAll("\"", "").trim();
//							if (quoteAuthor.equals("") == false) {
//								quoteAuthorOffset = allPostStr.indexOf(postAuthor) + postOffset;
//								
//								for (int pastPara = 0; pastPara < paraList.size(); ++pastPara) {
//									paraList.get(pastPara).postAuthor = postAuthor;
//									paraList.get(pastPara).postAuthorOffset = postAuthorOffset;
//									paraList.get(pastPara).quoteAuthor = quoteAuthor;
//									paraList.get(pastPara).quoteAuthorOffset = quoteAuthorOffset;
//								}
//							}
//						}
//					}
//				}
//				
//				if (line.contains("</quote>")) {
//					if (isQuote - 1 == 0) {
//						
//					}
//					isQuote--;
//				}
//				
//			}
//		}
//
//		return paraList;
//	}
//	
//
//	public PostParagraph getParagraph (String doc, String line) throws Exception {
//		PostParagraph paragraph = new PostParagraph();
//		
//		paragraph.content = line;
//		
//		paragraph.offset = doc.indexOf(line);
//		
//		if (paragraph.offset == -1) 
//			throw new Exception("cannot find the paragraph");
//		
//		return paragraph;
//	}
//		
//	public String getDocDateTime (String line) {
//		String dateTime = "";
//		
//		
//		int start = line.indexOf("datetime=") + "datetime=".length();
//		int end = line.indexOf("id=");
//		
//		dateTime = line.substring(start + 1, end).replaceAll("\"", "").trim();
//		
//		return dateTime;
//	}
//	
//	public String getDocID (String line) {
//		String id = "";
//		
//		int start = line.indexOf("<doc id=");
//		int end = line.indexOf(">");
//		
//		id = line.substring(start + "<doc id=".length(), end).replaceAll("\"", "");
//		
//		return id;
//	}
//	
	
}
