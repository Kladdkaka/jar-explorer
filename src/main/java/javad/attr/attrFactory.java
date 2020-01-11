
/*

  The author of this software is Ian Kaplan
  Bear Products International
  www.bearcave.com
  iank@bearcave.com

  Copyright (c) Ian Kaplan, 1999, 2000

  See copyright file for usage and licensing

*/

package javad.attr;

import java.io.*;

import javad.util.*;
import javad.jconst.*;


/**
 * Attribute "factory" class used to allocate attributes.
 *
 * <p>
 * <p>
 * This class reads the first two entries in the attribute structure,
 * the attribute_name_index and the attribute_length.  The factory
 * class uses the attribute name to select the proper attribute to
 * allocate.  As specified in the JVM spec, unknown attributes are
 * skipped.
 *
 * @author Ian Kaplan
 */
public final class attrFactory {
    static dataRead dataIn = new dataRead();

    private attrFactory() {
    }

    //
    // skip_data
    //
    // Unknown attributes are allowed and are simply skipped.
    //
    private static void skip_data(int len, DataInputStream dStream) {
        int junk;

        for (int i = 0; i < len; i++) {
            junk = dataIn.readU1(dStream);
        }
    } // skip_data


    /**
     * Read data from the class file and allocate the correct
     * attribute subclass.  If there is no known attribute
     * corresponding to the attribute name, skip the attribute.
     *
     * @return An attrInfo object or null.
     */
    public static Attribute allocAttr(DataInputStream dStream,
                                      ConstPool constPoolSec) {
        int name_index;
        int length;
        constBase obj;
        constUtf8 name;
        Attribute retObj = null;

        name_index = dataIn.readUnsignedShort(dStream);
        length = dataIn.readU4(dStream);

        obj = constPoolSec.constPoolElem(name_index);
        if (obj != null && obj instanceof constUtf8) {
            String nameStr;

            name = (constUtf8) obj;
            nameStr = name.getString();

            if (nameStr.compareTo("SourceFile") == 0) {
                retObj = new srcFileAttr(nameStr, length, dStream, constPoolSec);
            } else if (nameStr.compareTo("ConstantValue") == 0) {
                retObj = new constValueAttr(nameStr, length, dStream, constPoolSec);
            } else if (nameStr.compareTo("Code") == 0) {
                retObj = new Code(nameStr, length, dStream, constPoolSec);
            } else if (nameStr.compareTo("Exceptions") == 0) {
                retObj = new exceptAttr(nameStr, length, dStream, constPoolSec);
            } else if (nameStr.compareTo("InnerClasses") == 0) {
            } else if (nameStr.compareTo("LineNumberTable") == 0) {
                retObj = new LineNumberTable(nameStr, length, dStream);
            } else if (nameStr.compareTo("LocalVariableTable") == 0) {
                retObj = new localVarTabAttr(nameStr, length, dStream, constPoolSec);
            } else if (nameStr.compareTo("Synthetic") == 0) {
                retObj = new Synthetic(nameStr, length);
            } else if (nameStr.compareTo("Deprecated") == 0) {
                retObj = new Deprecated(nameStr, length);
            } else {
                // unrecognized attributes are skipped.
                skip_data(length, dStream);
            }
        } else {
            ErrorMessage.errorPrint("allocAttr: bad name index");
        }

        return retObj;
    } // attrAlloc

} // attrFactory
