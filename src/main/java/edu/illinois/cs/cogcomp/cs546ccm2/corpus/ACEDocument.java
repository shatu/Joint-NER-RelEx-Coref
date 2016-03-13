package edu.illinois.cs.cogcomp.cs546ccm2.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import edu.illinois.cs.cogcomp.core.datastructures.Pair;

/**
 * TODO: replace AnnotatedText with TextAnnotation?
 *
 */
public class ACEDocument implements Serializable {

	private static final long serialVersionUID = 1L;

	public ACEDocumentAnnotation aceAnnotation;
	
	public List<AnnotatedText> taList = new ArrayList<AnnotatedText>();
	
	public String orginalContent; //as read from the file
	
	public String contentRemovingTags; //only the tags are removed and not the content between the tags
	
	public List<String> originalLines; //as read using bufferedreader.readLine()
	
	public List<Pair<String, Paragraph>> paragraphs;  //first elements can be "docID", "dateTime", "headLine", "text"
													  //"postSubject", "poster", "postDate", "postQuote"
	
													 // There can be multiple <"text", content> pairs (almost for all; not sure abt nw subfolder)
	
	public String getDocID() {
		return this.aceAnnotation.id;
	}
	
	public List<ACEEntityMention> getAllEntityMentions() {
		ArrayList<ACEEntityMention> mentions = new ArrayList<>();
		List<ACEEntity> entities = getAllEntities();
		for (ACEEntity entity : entities) {
			for (ACEEntityMention mention : entity.entityMentionList) {
				mentions.add(mention);
			}
		}
		return mentions;
	}
	
	public List<ACEEntity> getAllEntities() {
		return this.aceAnnotation.entityList;
	}
	
	public List<ACERelation> getAllRelations() {
		return this.aceAnnotation.relationList;
	}
	
	//TODO: Understand the data format and complete this function
	public List<ACERelationArgument> getAllRelationArgs() {
		throw new NotImplementedException();
	}

	//TODO: Understand the data format and complete this function
	public List<ACERelationMention> getAllRelationMentions() {
		throw new NotImplementedException();
	}
	
}
