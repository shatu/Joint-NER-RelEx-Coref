package edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREDocumentAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEntity;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEntityMention;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEvent;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEventArgumentMention;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEventMention;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREFiller;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.ERERelation;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.ERERelationArgumentMention;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.ERERelationMention;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.XMLException;
import edu.illinois.cs.cogcomp.cs546ccm2.util.SimpleXMLParser;

public class ReadEREAnnotation {
	
	static boolean isDebug = false;

	public static void main (String[] args) {
		
		String testFile1 = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/ere/cmptxt/NYT_ENG_20131025.0190.rich_ere.xml";
		String testFile2 = "/shared/shelley/yqsong/eventData/LDC2015E29_DEFT_Rich_ERE_English_Training_Annotation_V1.1/data/ere/cmptxt/1d2911e09a6746b942c3e7b3cbdcb0ce.rich_ere.xml";
		ReadEREAnnotation readDoc = new ReadEREAnnotation();
		readDoc.readDocument(testFile2);
	}
	
	public static EREDocumentAnnotation readDocument (String FileStr) {
		EREDocumentAnnotation docAnnotation = new EREDocumentAnnotation();

		try {
			
			Document doc = SimpleXMLParser.getDocument(FileStr);
			
			Element element = doc.getDocumentElement();
			
			NamedNodeMap nnMap = element.getAttributes();
			
			docAnnotation.kit_id = nnMap.getNamedItem("kit_id").getNodeValue();
			docAnnotation.doc_id = nnMap.getNamedItem("doc_id").getNodeValue();
			docAnnotation.source_type = nnMap.getNamedItem("source_type").getNodeValue();
			if (isDebug) {
				System.out.println(docAnnotation.kit_id + "\t" + 
						docAnnotation.doc_id + "\t" + 
						docAnnotation.source_type + "\t");
				System.out.println();
			}
			
			Element entityElement = SimpleXMLParser.getElement(element, "entities");
			NodeList entityNL = entityElement.getElementsByTagName("entity");
			for (int i = 0; i < entityNL.getLength(); ++i) {
				EREEntity entity = readEntity(entityNL.item(i));
				if (isDebug) {
					System.out.println();
				}
				docAnnotation.entityList.add(entity);
			}
			
			Element fillerElement = SimpleXMLParser.getElement(element, "fillers");
			NodeList valueNL = fillerElement.getElementsByTagName("filler");
			for (int i = 0; i < valueNL.getLength(); ++i) {
				EREFiller value = readValue(valueNL.item(i));
				if (isDebug) {
					System.out.println();
				}
				docAnnotation.valueList.add(value);
			}
			

			Element relationElement = SimpleXMLParser.getElement(element, "relations");
			NodeList relationNL = relationElement.getElementsByTagName("relation");
			for (int i = 0; i < relationNL.getLength(); ++i) {
				nnMap = relationNL.item(i).getAttributes();
				ERERelation relation = readRelation(relationNL.item(i));
				
				if (isDebug) {
					System.out.println();
				}
				
				docAnnotation.relationList.add(relation);
			}
			
			Element hopperElement = SimpleXMLParser.getElement(element, "hoppers");
			NodeList eventNL = hopperElement.getElementsByTagName("hopper");
			for (int i = 0; i < eventNL.getLength(); ++i) {
				nnMap = eventNL.item(i).getAttributes();
				EREEvent event = readEvent(eventNL.item(i));
				
				if (isDebug) {
					System.out.println();
				}
				
				docAnnotation.eventList.add(event);
			}
			
		} catch (XMLException e) {
			e.printStackTrace();
		}
		return docAnnotation;
	}
	
	public static ERERelation readRelation (Node node) throws XMLException {
		ERERelation relation = new ERERelation();
		
		NamedNodeMap nnMap = node.getAttributes();
		
		relation.id = nnMap.getNamedItem("id").getNodeValue();
		relation.type = nnMap.getNamedItem("type").getNodeValue();
		relation.subtype = nnMap.getNamedItem("subtype").getNodeValue();

		if (isDebug) {
			System.out.println("Relation:\t" + relation.id
					);
		}
		if (isDebug) {
			System.out.println();
		}
		
		NodeList nlMention = ((Element)node).getElementsByTagName("relation_mention");
		
		for (int i = 0; i < nlMention.getLength(); ++i) {
			ERERelationMention relationMention = readRelationMention (nlMention.item(i));
			relation.relationMentionList.add(relationMention);
		}
		
		return relation;
	}
	
	public static ERERelationMention readRelationMention (Node node) throws XMLException {
		ERERelationMention relationMention = new ERERelationMention();
		
		NamedNodeMap nnMap = node.getAttributes();
		
		relationMention.id = nnMap.getNamedItem("id").getNodeValue();
		relationMention.realis = nnMap.getNamedItem("realis").getNodeValue();

		if (isDebug) {
			System.out.println("  Relation mention:\t" + relationMention.id + "\t"
					+ relationMention.realis
					);
		}
		
		boolean hasTrigger = false;
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); ++i) {
			if (nodeList.item(i).getNodeName().equals("trigger")) {
				hasTrigger = true;
			}
		}
		if (hasTrigger) {
			Element trigger = SimpleXMLParser.getElement(((Element)node), "trigger");

			NamedNodeMap triggerMap = trigger.getAttributes();
			
			relationMention.triggerSource = triggerMap.getNamedItem("source").getNodeValue();
			relationMention.triggerOffset = Integer.parseInt(triggerMap.getNamedItem("offset").getNodeValue());
			relationMention.triggerLength = Integer.parseInt(triggerMap.getNamedItem("length").getNodeValue());
			relationMention.triggerStr = SimpleXMLParser.getContentString(trigger);
			
			if (isDebug) {
				System.out.println("  Relation trigger:\t" + relationMention.triggerSource
						+ "\t" + relationMention.triggerOffset 
						+ "\t" + relationMention.triggerLength
						+ "\t" + relationMention.triggerStr);
			}
		}


		NodeList nlMention1 = ((Element)node).getElementsByTagName("rel_arg1");
		
		for (int i = 0; i < nlMention1.getLength(); ++i) {
			ERERelationArgumentMention relationArgumentMention = readRelationArgumentMention (nlMention1.item(i));
			relationMention.relationArgumentMentionList.add(relationArgumentMention);
		}
		
		NodeList nlMention2 = ((Element)node).getElementsByTagName("rel_arg2");
		
		for (int i = 0; i < nlMention2.getLength(); ++i) {
			ERERelationArgumentMention relationArgumentMention = readRelationArgumentMention (nlMention2.item(i));
			relationMention.relationArgumentMentionList.add(relationArgumentMention);
		}
		
		if (isDebug) {
			System.out.println();
		}
		return  relationMention;
	}
	
	public static ERERelationArgumentMention readRelationArgumentMention (Node node) throws XMLException {
		ERERelationArgumentMention relationArgumentMention = new ERERelationArgumentMention();
		
		NamedNodeMap nnMap = node.getAttributes();
		
		for (int i = 0; i < nnMap.getLength(); ++i) {
			String attr = nnMap.item(i).getNodeName();
			if (attr.equals("filler_id") == true) {
				relationArgumentMention.isFiller = true;	
			}
		}
		if (relationArgumentMention.isFiller == true) {
			relationArgumentMention.filler_id = nnMap.getNamedItem("filler_id").getNodeValue();
		} else {
			relationArgumentMention.entity_id = nnMap.getNamedItem("entity_id").getNodeValue();
			relationArgumentMention.entity_mention_id = nnMap.getNamedItem("entity_mention_id").getNodeValue();
		}
		
		relationArgumentMention.role = nnMap.getNamedItem("role").getNodeValue();

		relationArgumentMention.argStr = SimpleXMLParser.getContentString((Element)node);
		
		if (isDebug) {
			System.out.println("    Relation argument mention:\t" 
					+ relationArgumentMention.entity_id + "\t" 
					+ relationArgumentMention.entity_mention_id + "\t" 
					+ relationArgumentMention.filler_id + "\t" 
					+ relationArgumentMention.role + "\t" 
					+ relationArgumentMention.argStr );
		}
		
		return  relationArgumentMention;
	}
	
	public static EREEvent readEvent (Node node) throws XMLException {
		EREEvent event = new EREEvent();
		
		NamedNodeMap nnMap = node.getAttributes();
		
		event.id = nnMap.getNamedItem("id").getNodeValue();

		if (isDebug) {
			System.out.println("Event:\t" + event.id
					);
		}
		if (isDebug) {
			System.out.println();
		}
		
		NodeList nlMention = ((Element)node).getElementsByTagName("event_mention");
		
		for (int i = 0; i < nlMention.getLength(); ++i) {
			EREEventMention eventMention = readEventMention (nlMention.item(i));
			event.eventMentionList.add(eventMention);
		}
		
		return event;
	}
	
	public static EREEventMention readEventMention (Node node) throws XMLException {
		EREEventMention eventMention = new EREEventMention();
		
		NamedNodeMap nnMap = node.getAttributes();
		
		eventMention.id = nnMap.getNamedItem("id").getNodeValue();
		eventMention.type = nnMap.getNamedItem("type").getNodeValue();
		eventMention.subtype = nnMap.getNamedItem("subtype").getNodeValue();
		eventMention.realis = nnMap.getNamedItem("realis").getNodeValue();

		if (isDebug) {
			System.out.println("  Event mention:\t" + eventMention.id + "\t"
					+ eventMention.type + "\t"
					+ eventMention.subtype + "\t"
					+ eventMention.realis
					);
		}
		
		Element trigger = SimpleXMLParser.getElement(((Element)node), "trigger");
		NamedNodeMap triggerMap = trigger.getAttributes();
	
		eventMention.triggerSource = triggerMap.getNamedItem("source").getNodeValue();
		eventMention.triggerOffset = Integer.parseInt(triggerMap.getNamedItem("offset").getNodeValue());
		eventMention.triggerLength = Integer.parseInt(triggerMap.getNamedItem("length").getNodeValue());
		eventMention.triggerStr = SimpleXMLParser.getContentString(trigger);

		if (isDebug) {
			System.out.println("  Event trigger:\t" + eventMention.triggerSource
					+ "\t" + eventMention.triggerOffset 
					+ "\t" + eventMention.triggerLength
					+ "\t" + eventMention.triggerStr);
		}

		NodeList nlMention = ((Element)node).getElementsByTagName("em_arg");
		
		for (int i = 0; i < nlMention.getLength(); ++i) {
			EREEventArgumentMention eventArgumentMention = readEventArgumentMention (nlMention.item(i));
			eventMention.eventArgumentMentionList.add(eventArgumentMention);
		}
		
		if (isDebug) {
			System.out.println();
		}
		return  eventMention;
	}
	
	public static EREEventArgumentMention readEventArgumentMention (Node node) throws XMLException {
		EREEventArgumentMention eventArgumentMention = new EREEventArgumentMention();
		
		NamedNodeMap nnMap = node.getAttributes();
		for (int i = 0; i < nnMap.getLength(); ++i) {
			String attr = nnMap.item(i).getNodeName();
			if (attr.equals("filler_id") == true) {
				eventArgumentMention.isFiller = true;	
			}
		}
		if (eventArgumentMention.isFiller == true) {
			eventArgumentMention.filler_id = nnMap.getNamedItem("filler_id").getNodeValue();
		} else {
			eventArgumentMention.entity_id = nnMap.getNamedItem("entity_id").getNodeValue();
			eventArgumentMention.entity_mention_id = nnMap.getNamedItem("entity_mention_id").getNodeValue();
		}
		
		eventArgumentMention.role = nnMap.getNamedItem("role").getNodeValue();
		eventArgumentMention.realis = nnMap.getNamedItem("realis").getNodeValue();

		eventArgumentMention.argStr = SimpleXMLParser.getContentString((Element)node);
		
		if (isDebug) {
			System.out.println("    Event argument mention:\t" 
					+ eventArgumentMention.isFiller + "\t" 
					+ eventArgumentMention.filler_id + "\t" 
					+ eventArgumentMention.entity_id + "\t" 
					+ eventArgumentMention.entity_mention_id + "\t" 
					+ eventArgumentMention.role + "\t" 
					+ eventArgumentMention.realis + "\t" 
					+ eventArgumentMention.argStr );
		}
		
		return  eventArgumentMention;
	}
		
	public static EREEntity readEntity (Node node) throws XMLException {
		EREEntity entity = new EREEntity();

		NamedNodeMap nnMap = node.getAttributes();
		
		entity.id = nnMap.getNamedItem("id").getNodeValue();
		entity.type = nnMap.getNamedItem("type").getNodeValue();
		entity.specificity = nnMap.getNamedItem("specificity").getNodeValue();
		
		if (isDebug) {
			System.out.println("Entity:\t" + entity.id + "\t" + entity.type + "\t" + entity.type + "\t" + entity.specificity);
		}
		if (isDebug) {
			System.out.println();
		}
		
		NodeList nl = ((Element)node).getElementsByTagName("entity_mention");
		
		for (int i = 0; i < nl.getLength(); ++i) {
			EREEntityMention entityMention = readEntityMention (nl.item(i));
			entity.entityMentionList.add(entityMention);
		}
		
		return  entity;
	}

	public static EREEntityMention readEntityMention (Node node) throws XMLException {
		EREEntityMention entityMention = new EREEntityMention();

		NamedNodeMap nnMap = node.getAttributes();
		
		entityMention.id = nnMap.getNamedItem("id").getNodeValue();
		entityMention.noun_type = nnMap.getNamedItem("noun_type").getNodeValue();
		entityMention.source = nnMap.getNamedItem("source").getNodeValue();
		entityMention.offset = Integer.parseInt(nnMap.getNamedItem("offset").getNodeValue());
		entityMention.length = Integer.parseInt(nnMap.getNamedItem("length").getNodeValue());
		
		NodeList nl = ((Element)node).getElementsByTagName("mention_text");
		
		if (nl.getLength() > 0) {
			entityMention.text = SimpleXMLParser.getContentString((Element) nl.item(0));
		}
		
		if (isDebug) {
			System.out.println("  Entity mention:\t" + entityMention.id + "\t" + entityMention.noun_type + "\t" + entityMention.source
					+ "\t" + entityMention.offset + "\t" + entityMention.length + "\t" + entityMention.text );
		}
		
		return  entityMention;
	}
	
	public static EREFiller readValue (Node node) throws XMLException {
		EREFiller value = new EREFiller();
		
		NamedNodeMap nnMap = node.getAttributes();
		
		value.id = nnMap.getNamedItem("id").getNodeValue();
		value.source = nnMap.getNamedItem("source").getNodeValue();
		value.offset = Integer.parseInt(nnMap.getNamedItem("offset").getNodeValue());
		value.length = Integer.parseInt(nnMap.getNamedItem("length").getNodeValue());
		value.type = nnMap.getNamedItem("type").getNodeValue();
		
		value.textStr = SimpleXMLParser.getContentString((Element) node);
		
		if (isDebug) {
			System.out.println("Value:\t" + value.id + "\t" + value.source + "\t" +
					value.offset + "\t" + value.length + "\t" +
					value.type + "\t" + value.textStr
					);
		}
		if (isDebug) {
			System.out.println();
		}
		
		return value;
	}
	
}
