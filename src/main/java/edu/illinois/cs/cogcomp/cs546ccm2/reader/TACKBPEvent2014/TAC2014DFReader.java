package edu.illinois.cs.cogcomp.cs546ccm2.reader.TACKBPEvent2014;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.PostParagraph;
import edu.illinois.cs.cogcomp.cs546ccm2.util.IOManager;


public class TAC2014DFReader {
	public static String systemNewLine = System.getProperty("line.separator");
	
	public static void main (String[] args) throws Exception {
//		String docPath = "/shared/shelley/yqsong/eventData/EAE/pilot_data/data/df/";
		String docPath = "/shared/shelley/yqsong/eventData/EAE/LDC2014R43_TAC_2014_KBP_English_Event_Argument_Extraction_Evaluation_Source_Corpus_V1.1/data/mpdf/";
		
		File fileFolder = new File(docPath);
		File[] list = fileFolder.listFiles();
		for (int f = 0; f < list.length; ++f) {
			if (list[f].getAbsolutePath().contains("3f71fead3fa119ccdcdf01769ffee5b1")) 
				System.out.println();
			
			TAC2014DFReader dfReader = new TAC2014DFReader();
			List<PostParagraph> paraList = dfReader.processDoc (list[f].getAbsolutePath());
			
			for (int i = 0; i < paraList.size(); ++i) {
				System.out.println("offset: " + paraList.get(i).offset + ", dateTime: " + 
						paraList.get(i).dateTime
						+ " dateOffset: " + paraList.get(i).dateTimeOffset
						+ " docID: " + paraList.get(i).docID);
//				System.out.println("-----------------------------");
//				System.out.println(paraList.get(i).content);
//				System.out.println("-----------------------------");
				
				String doc = IOManager.readContentCharBuf(list[f].getAbsolutePath());
				
//				System.out.println();
				
				if (doc.substring(paraList.get(i).offset, 
						paraList.get(i).offset + paraList.get(i).content.length()).equals(paraList.get(i).content) == false) 
				{
					System.err.println("ERROR Here!");
					System.err.println(list[f].getAbsolutePath());
				}
					
				
//				System.out.println("-----------------------------");
			}
//			System.out.println();
		}
		
	}
	
	public List<PostParagraph> processDoc (String docPath) throws Exception {
		String doc = IOManager.readContentCharBuf(docPath);
		
		List<String> docLines = readWholeFileAsLines (docPath);
		
		String id = "";
		
		boolean isPost = false;
		List<PostParagraph> paraList = new ArrayList<PostParagraph>();
		
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
				
				postOffset = doc.indexOf(line);
				
			}
			
			if (line.contains("</post")) {
				List<PostParagraph> paraListInPost = parsePost (doc, postLines, postOffset, id, docPath);
				paraList.addAll(paraListInPost);
				
				isPost = false;
				postLines = new ArrayList<String>();
			}
			
		}
		
		return paraList;
	}
	
	public List<PostParagraph> parsePost (String doc, List<String> lines, int postOffset, 
			String id, String docPath) {
		String allPostStr = "";
		for (int i = 0; i < lines.size(); ++i) {
			allPostStr += lines.get(i) + systemNewLine;
		}
		
		List<PostParagraph> paraList = new ArrayList<PostParagraph>();
		
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
			for (int i = 1; i < lines.size(); ++i) {
				
				String line = lines.get(i);
				
				if (line.contains("<quote") == false && line.contains("</quote>") == false && isQuote == 0) {
					Pattern bodyPattern = Pattern.compile("<img src=(.*?)/>");
					Matcher bodyMatcher = bodyPattern.matcher(line);
					while (bodyMatcher.find()) {
						String imageContent = (bodyMatcher.group(1));
						
						line = line.replace(imageContent, "");
				    }
					
					while (line.contains("<img src=/>") == true	) {
						int index = line.indexOf("<img src=/>");
						
						String part1 = line.substring(0, index).trim();
						line = line.substring(index + "<img src=/>".length()).trim();
						
						bodyPattern = Pattern.compile("<a href(.*?)</a>");
						bodyMatcher = bodyPattern.matcher(part1);
						while (bodyMatcher.find()) {
							String linkContent = (bodyMatcher.group(1));
							
							part1 = part1.replace(linkContent, "");
					    }
						while (part1.contains("<a href</a>") == true	) {
							index = part1.indexOf("<a href</a>");
							
							String part2 = part1.substring(0, index).trim();
							part1 = part1.substring(index + "<a href</a>".length()).trim();
							
							if (part2.equals("") == false) {
								PostParagraph paragraph = new PostParagraph();
								
								int localOffset = allPostStr.indexOf(part2);
								int globalOffset = postOffset + localOffset;
								
								paragraph.content = part2;
								paragraph.dateTime = dateTime;
								paragraph.docID = id;
								paragraph.offset = globalOffset;
								paragraph.dateTimeOffset = dateTimeOffset;
								paragraph.postAuthor = postAuthor;
								paragraph.postAuthorOffset = postAuthorOffset;
								paragraph.quoteAuthor = quoteAuthor;
								paragraph.quoteAuthorOffset = quoteAuthorOffset;
								
								paraList.add(paragraph);
							}
							
						}
						
						if (part1.equals("") == false && part1.contains("<a href") == false) {
							PostParagraph paragraph = new PostParagraph();
							
							int localOffset = allPostStr.indexOf(part1.trim());
							int globalOffset = postOffset + localOffset;
							
							paragraph.content = part1;
							paragraph.dateTime = dateTime;
							paragraph.docID = id;
							paragraph.offset = globalOffset;
							paragraph.dateTimeOffset = dateTimeOffset;
							paragraph.postAuthor = postAuthor;
							paragraph.postAuthorOffset = postAuthorOffset;
							paragraph.quoteAuthor = quoteAuthor;
							paragraph.quoteAuthorOffset = quoteAuthorOffset;
							
							paraList.add(paragraph);
						}
					}
					
					if (line.equals("") == false && line.contains("<img src=/>") == false) {
						
						bodyPattern = Pattern.compile("<a href(.*?)</a>");
						bodyMatcher = bodyPattern.matcher(line);
						while (bodyMatcher.find()) {
							String linkContent = (bodyMatcher.group(1));
							
							line = line.replace(linkContent, "");
					    }
						while (line.contains("<a href</a>") == true) {
							int index = line.indexOf("<a href</a>");
							
							String part2 = line.substring(0, index).trim();
							line = line.substring(index + "<a href</a>".length()).trim();
							
							if (part2.equals("") == false) {
								PostParagraph paragraph = new PostParagraph();
								
								int localOffset = allPostStr.indexOf(part2);
								int globalOffset = postOffset + localOffset;
								
								paragraph.content = part2;
								paragraph.dateTime = dateTime;
								paragraph.docID = id;
								paragraph.offset = globalOffset;
								paragraph.dateTimeOffset = dateTimeOffset;
								paragraph.postAuthor = postAuthor;
								paragraph.postAuthorOffset = postAuthorOffset;
								paragraph.quoteAuthor = quoteAuthor;
								paragraph.quoteAuthorOffset = quoteAuthorOffset;
								
								paraList.add(paragraph);
							}
							
						}
						
						if (line.equals("") == false && line.contains("<a href") == false) {
							PostParagraph paragraph = new PostParagraph();
							
							int localOffset = allPostStr.indexOf(line.trim());
							int globalOffset = postOffset + localOffset;
							
							paragraph.content = line;
							paragraph.dateTime = dateTime;
							paragraph.docID = id;
							paragraph.offset = globalOffset;
							paragraph.dateTimeOffset = dateTimeOffset;
							paragraph.postAuthor = postAuthor;
							paragraph.postAuthorOffset = postAuthorOffset;
							paragraph.quoteAuthor = quoteAuthor;
							paragraph.quoteAuthorOffset = quoteAuthorOffset;
							
							paraList.add(paragraph);
						}
						
					}
					
				}
				
				if (line.contains("<quote")) {
					isQuote++;
					
					if (isQuote == 1) {
						if (line.contains("<quote orig_author")) {
							int start = line.indexOf("<quote orig_author=") + "<quote orig_author=".length();
							int end = line.indexOf(">");
							quoteAuthor = line.substring(start + 1, end).replaceAll("\"", "").trim();
							if (quoteAuthor.equals("") == false) {
								quoteAuthorOffset = allPostStr.indexOf(postAuthor) + postOffset;
								
								for (int pastPara = 0; pastPara < paraList.size(); ++pastPara) {
									paraList.get(pastPara).postAuthor = postAuthor;
									paraList.get(pastPara).postAuthorOffset = postAuthorOffset;
									paraList.get(pastPara).quoteAuthor = quoteAuthor;
									paraList.get(pastPara).quoteAuthorOffset = quoteAuthorOffset;
								}
							}
						}
					}
				}
				
				if (line.contains("</quote>")) {
					if (isQuote - 1 == 0) {
						
					}
					isQuote--;
				}
				
			}
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
	
	public String getDocID (String line) {
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
