package edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure;

import java.io.Serializable;

public class PostParagraph extends Paragraph implements Serializable {

	public PostParagraph() {
	}
	
	public PostParagraph(int offset, String content) {
		super(offset, content);
		offsetFilterTags = offset;
	}
	
	private static final long serialVersionUID = 1L;
	
	public String docID;
	
	public String dateTime;
	public int dateTimeOffset;
	
	public String postAuthor = "";
	public int postAuthorOffset = -1;
	
	public String quoteAuthor = "";
	public int quoteAuthorOffset = -1;

}
