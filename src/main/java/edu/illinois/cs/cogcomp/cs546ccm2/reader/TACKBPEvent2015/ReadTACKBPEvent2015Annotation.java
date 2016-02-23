package edu.illinois.cs.cogcomp.cs546ccm2.reader.TACKBPEvent2015;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREDocumentAnnotation;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEvent;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.ERE.annotationStructure.EREEventMention;
import edu.illinois.cs.cogcomp.cs546ccm2.reader.commondatastructure.XMLException;
import edu.illinois.cs.cogcomp.cs546ccm2.util.SimpleXMLParser;

public class ReadTACKBPEvent2015Annotation {
	
	static boolean isDebug = false;

	public static void main (String[] args) {
		
		String testFile2 = "/shared/shelley/yqsong/eventData/LDC2015E73_TAC_KBP_2015_Event_Nugget_Training_Data_Annotation/data/event_nugget/1b386c986f9d06fd0a0dda70c3b8ade9.event_nuggets.xml";
		ReadTACKBPEvent2015Annotation readDoc = new ReadTACKBPEvent2015Annotation();
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

		if (isDebug) {
			System.out.println();
		}
		return  eventMention;
	}
	
}
