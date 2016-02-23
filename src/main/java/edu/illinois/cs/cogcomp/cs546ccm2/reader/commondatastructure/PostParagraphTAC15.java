package edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure;

import java.io.Serializable;

public class PostParagraphTAC15 extends Paragraph implements Serializable {

	public PostParagraphTAC15() {
	}
	
	public PostParagraphTAC15(int offset, String content) {
		super(offset, content);
		offsetFilterTags = offset;
	}
	
	private static final long serialVersionUID = 1L;
	
	public String docID;
	
	public String dateTime;
	public int dateTimeOffset;
	
	public String postAuthor = "";
	public int postAuthorOffset = -1;
	
}
