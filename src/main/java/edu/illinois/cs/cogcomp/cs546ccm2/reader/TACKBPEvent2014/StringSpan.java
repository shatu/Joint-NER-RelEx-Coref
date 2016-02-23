package edu.illinois.cs.cogcomp.cs546ccm2.reader.TACKBPEvent2014;

public class StringSpan {
	
	public int start = 0;
	
	public int end = 0;
	
	public boolean isContaining (StringSpan span) {
		if (span.start >= start && span.end <= end) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isContainedBy (StringSpan span) {
		if (span.start <= start && span.end >= end) {
			return true;
		} else {
			return false;
		}
	}
	
}
