package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;

public class Paragraph implements Serializable {

	private static final long serialVersionUID = 1L;

	public Paragraph() {
	}
	
	public Paragraph(int offset, String content) {
		this.offset = offset;  
		this.content = content;
	}

	public int offset = -1;   				//char offset in the original text

	public int offsetFilterTags = -1;		//char offset in the tag trimmed text
	
	public String content;					//content of the "paragraph"
}
