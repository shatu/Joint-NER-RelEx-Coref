package edu.illinois.cs.cogcomp.cs546ccm2.reader.ace2005.annotationStructure;

import java.io.Serializable;

/**
 * simplest mention structure: extent
 * Created by mssammon on 9/6/15.
 */
public class Mention implements Serializable {

    private String id;

    private int extentStart;
    private int extentEnd;
    private String extent;

}
