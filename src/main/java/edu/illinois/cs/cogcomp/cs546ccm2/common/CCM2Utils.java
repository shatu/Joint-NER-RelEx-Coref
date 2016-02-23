package edu.illinois.cs.cogcomp.cs546ccm2.common;

/**
 * A home for general-purpose classes.
 *
 * @author shashank
 */
public class CCM2Utils {

    /**
     * explicitly force failure if array of parameters is not suitable for a given main() method
     *
     * @param args arguments to be checked
     * @param numArgs how many arguments there should be
     * @param className name of client (for error reporting)
     * @param argDesc description of arguments required
     * @return
     */
    public static String[] checkArgsOrDie( String[] args, int numArgs, String className, String argDesc ) {
        if ( args.length != numArgs ) {
            System.err.println( "Usage: " + className + " " + argDesc );
            System.exit( -1 );
        }
        return args;
    }
}
