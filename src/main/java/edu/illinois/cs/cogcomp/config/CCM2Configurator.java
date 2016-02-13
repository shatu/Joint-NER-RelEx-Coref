package edu.illinois.cs.cogcomp.config;

import edu.illinois.cs.cogcomp.core.utilities.configuration.Configurator;
import edu.illinois.cs.cogcomp.core.utilities.configuration.Property;
import edu.illinois.cs.cogcomp.core.utilities.configuration.ResourceManager;

/**
 * Default configuration parameters for CCM2
 * 
 * @author sgupta96.
 */
public class CCM2Configurator extends Configurator {

    public final Property WN_PATH = new Property("wnPath", "wordnet-dict");
    public final Property PARAPHRASE_PATH = new Property("paraphrasePath", "paraphrase.txt");
    //public final Property PARAGRAM_PATH = new Property("paragramPath", "paragram-data");

    @Override
    public ResourceManager getDefaultConfig() {
        Property[] props = {WN_PATH, PARAPHRASE_PATH};

        return new ResourceManager(generateProperties(props));
    }
}
