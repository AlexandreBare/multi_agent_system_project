
package util;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class AsciiReader {
    protected StreamTokenizer st;
    private final BufferedReader br;

    private final Logger logger = Logger.getLogger(AsciiReader.class.getName());

    // Constructor
    public AsciiReader(String f) throws FileNotFoundException {
        // Default state ok ?
        br = new BufferedReader(new FileReader(f));
        st = new StreamTokenizer(br);
        st.resetSyntax();
        st.commentChar('#');
        st.quoteChar('"');
        st.wordChars('0', '9');
        st.wordChars('-', '-');
        st.wordChars('\\', '\\');
        st.wordChars('/', '/');
        st.wordChars('_', '_');
        st.wordChars('.', '.');
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.whitespaceChars(' ', ' ');
        st.whitespaceChars('\t', '\t');
        st.whitespaceChars('\r', '\r');
        st.whitespaceChars('\n', '\n');
        st.whitespaceChars(':', ':');
        st.eolIsSignificant(false);
    }

    // Read operations
    public String readNext() throws IOException {
        st.nextToken();
        return st.sval;
    }

    public int readInt() throws IOException {
        String str = readNext();
        return Integer.parseInt(str);
    }

    public double readDouble() throws IOException {
        String str = readNext();
        return Double.parseDouble(str);
    }

    public float readFloat() throws IOException {
        String str = readNext();
        return Float.parseFloat(str);
    }

    // verification operations
    /**
     * Reads a token from the stream and checks if it matches the argument
     * @param verify : the token to be verified
     * @post returns if verify matches the read token
     * @throws IOException is thrown when it is no match
     */
    public void check(String verify) throws IOException {
        String token = readNext();
        if (!(token).equals(verify)) {
            throw new IOException(String.format("Expected token '%s', but found token '%s'.", verify, token));
        }
    }

    /**
     * @semantics Reads a Class constructor with its arguments from file and initializes a new object.
     * FileFormat: <classname> nbArgs <int> [<argType><value>]*
     * 			   argType must be String, Int, Float or Double
     */
    public Object readClassConstructor() throws IOException {
        String classname = readNext();

        check("nbArgs");
        int nbArgs = readInt();
        Class[] args = new Class[nbArgs];
        Object[] values = new Object[nbArgs];

        for (int i = 0; i < nbArgs; i++) {
            values[i] = readTypeAndValue();
            switch (values[i].getClass().getName()) {
                case "java.lang.Integer" -> args[i] = Integer.TYPE;
                case "java.lang.Double" -> args[i] = Double.TYPE;
                case "java.lang.Float" -> args[i] = Float.TYPE;
                default -> args[i] = values[i].getClass();
            }
        }

        Object result;
        try {
            Class c = Class.forName(classname);
            Constructor con = c.getConstructor(args);
            result = con.newInstance(values);
        } catch (ClassNotFoundException e) {
            this.logger.severe(
                    String.format("Could not find the following class specified in one of the configuration files: %s", 
                    classname));
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            this.logger.severe(
                    String.format("Could not find a constructor for class '%s' which accepts %d arguments with types in the following order: [%s]",
                    classname, nbArgs, Arrays.stream(args).map(Class::getTypeName).collect(Collectors.joining(", "))));
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            this.logger.severe(String.format("Security exception occurred during instantiation of class %s", classname));
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            this.logger.severe(String.format("Failed to instantiate an object for class '%s' which accepts %d arguments with types and specific values in the following orders respectively:\n\tTypes  - [%s]\n\tValues - [%s]", 
                    classname, nbArgs, Arrays.stream(args).map(Class::getTypeName).collect(Collectors.joining(", ")),
                    Arrays.stream(values).map(Object::toString).collect(Collectors.joining(", "))));
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            this.logger.severe(String.format("Cannot instantiate due to insufficient access (e.g. private constructor) of an object for class '%s' which accepts %d arguments with types in the following order: [%s]",
                    classname, nbArgs, Arrays.stream(args).map(Class::getTypeName).collect(Collectors.joining(", "))));
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            this.logger.severe(String.format("Cannot instantiate an object for class '%s' - illegal argument received:\n\tAccepted types - [%s]\n\tGiven values   - [%s]",
                    classname, Arrays.stream(args).map(Class::getTypeName).collect(Collectors.joining(", ")),
                    Arrays.stream(values).map(Object::toString).collect(Collectors.joining(", "))));
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            this.logger.severe(String.format("Something went wrong during instantiation of an object for class '%s' (exception thrown).",
                    classname));
            throw new RuntimeException(e);
        } 
        
        return result;
    }

    @Nullable
    public Object readTypeAndValue() throws IOException {
        String type = readNext();
        return switch (type) {
            case "String" -> readNext();
            case "Integer" -> readInt();
            case "Float" -> readFloat();
            case "Double" -> readDouble();
            default -> null;
        };
    }

    // terminator
    public void close() {
        try {
            br.close();
        } catch (IOException e) {
            this.logger.severe(String.format("Could not close AsciiReader: %s", e.getMessage()));
        }
    }

}
