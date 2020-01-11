package javad;

import javad.classfile.ClassFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;


/**
 * This is a simple class that shows how to use javad with added API,
 * change pathToTestClass and see the results.
 *
 * @author Igor Polevoy
 */
public class Igor {
    static void usage() {
        System.err.println("this program takes a set of one or more" +
                " .class file names as its argument");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            // args = new String[]{"C:\\gwt\\build\\web\\WEB-INF\\classes\\sample\\client\\Sample$TableAsyncCallback.class"};
            usage();
            System.exit(1);
        }
        for (String givenClassName : args) {
            printNewDump(givenClassName);
        }
    }

    final private static String STR_DELIMITER_LINE = "====================================================";
    private static boolean firstPrint = true;

    // TODO: add an option to store the output into an output stream / StringBuffer or similar,
    //       allowing other classes to just use this simple entry point
    //       (and more important: allow the unit test to do so)
    protected static void printNewDump(String givenClassName) {

        if (!firstPrint) {
            System.out.print("\n\n" + STR_DELIMITER_LINE + "\n\n");
        }
        firstPrint = false;

        System.out.println("Dumping \"" + givenClassName + "\"");
        FileInputStream fin;
        try {
            fin = new FileInputStream(givenClassName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        BufferedInputStream bin = new BufferedInputStream(fin);
        DataInputStream din = new DataInputStream(bin);

        ClassFile classFile = new ClassFile(din);
        System.out.println("Class Modifiers: " + classFile.getClassModifiers());
        System.out.println("Class Name: " + classFile.getClassName());
        System.out.println("Class Superclass: " + classFile.getSuperclassName());
        String[] interfaces = classFile.getInterfaceNames();

        for (String anInterface : interfaces) {
            System.out.println("Interface: " + anInterface);
        }

        String[] fields = classFile.getFields();
        for (String field : fields) {
            System.out.println("Field: " + field);
        }

        ArrayList<String> methods = classFile.getMethods();

        for (Object value : methods) {
            String method = (String) value;
            System.out.println("Method: " + method);
        }


        ArrayList<String> constructors = classFile.getConstructors();
        for (Object o : constructors) {
            String constructor = (String) o;
            System.out.println("Constructor: " + constructor);
        }

        classFile.pr();

    }
}
