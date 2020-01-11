
/**
 * The author of this software is Ian Kaplan
 * Bear Products International
 * www.bearcave.com
 * iank@bearcave.com
 * <p>
 * Copyright (c) Ian Kaplan, 1999, 2000
 * <p>
 * See copyright file for usage and licensing
 */

package javad.attr;

import java.util.Vector;
import java.io.*;

import javad.util.*;
import javad.jconst.*;


/**
 * Attribute for the byte code for a Java method.  The attribute name
 * for the codeAttr is "Code".  Note that if the method is native or
 * abstract, then it can't have a "Code" attribute.
 *
 * <p>
 * The structure of a Code attribute is:
 *
 * <pre>
 * Code_attribute {
 * u2 name_index;
 * u4 total_length;
 * u2 max_stack;
 * u2 max_locals;
 * u4 code_length;
 * u1 code[ code_length ];
 * u2 exceptions_table_length;
 * exceptInfo except[ exceptions_table_length ];
 * u2 attributes_count;
 * attribute_info attributes[ attributes_count ];
 * }
 * </pre>
 *
 * <p>
 * <p>
 * Where
 *
 * <pre>
 * exceptInfo {
 * u2 start_pc;
 * U2 end_pc;
 * u2 handler_pc;
 * u2 catch_type;
 * }
 * </pre>
 *
 * <p>
 * The attribute_name_index and attribute length are read by the
 * attrFactory.allocAttr method.  These values are passed into the class
 * constructor.
 *
 * <p>
 * In the exceptInfo the catch_type is an index into the constant
 * pool.  The object at this index must be a CONSTANT_Class_info
 * object (e.g., constClass_or_String) for an exception class that
 * this exception is designated to catch.  This class must be the
 * class Throwable or one of its subclasses.
 * <p>
 * Although code_length is 32-bits wide, its value must be less than
 * 0x10000 (65536) (see JVM Spec 4.8.1).
 * <p>
 * The max_stack value is the size of the operand stack.  This in
 * in word units, where an int is one word and a long or double
 * is two:
 * <p>
 * At any point in time an operand stack has an associated depth,
 * where a value of type long or double contributes two units to
 * the depth and a value of any other type contributes one unit.
 * (JVM Spec 3.6.2)
 * <p>
 * The max_locals value is the size of the local frame allocated on
 * entry to the method.  This frame will hold local variables and
 * method arguments.  As with max_stack, the units are words.  Like
 * the stack, the frame is word aligned so a boolean or a byte
 * occupies a word and a long or a double occupies two words (JVM
 * Spec 3.6.1).
 * <p>
 * If max_locals is zero, then there are no local variables.
 * <p>
 * If debug is turned and the method has local variables then
 * the codeAttributes table will contain a LocalVariableTable
 * attribute.  This attribute in turn contains a table of
 * local variable information.
 *
 * @author Ian Kaplan
 */
public class Code extends Attribute {
    private int max_stack;
    private int max_locals;
    private byte[] code;
    private CodeException[] exception_table = null;
    private Attribute[] attributes = null;


    //
    // CodeException
    //
    // Exception information
    //
    class CodeException {
        int start_pc;
        int end_pc;
        int handler_pc;

        constClass_or_String catch_type = null;

        CodeException(DataInputStream dStream, ConstPool constPoolSec) {
            start_pc = readUnsignedShort(dStream);
            end_pc = readUnsignedShort(dStream);
            handler_pc = readUnsignedShort(dStream);
            int catchTypeIx = readUnsignedShort(dStream);

            if (catchTypeIx > 0) {
                constBase obj = constPoolSec.constPoolElem(catchTypeIx);
                if (obj != null && obj instanceof constClass_or_String) {
                    catch_type = (constClass_or_String) obj;
                } else {
                    ErrorMessage.errorPrint("CodeException: constClass_or_String expected in const pool index " + catchTypeIx);
                }
            }
        } // exceptInfo constructor

    } // exceptInfo (inner class)


    /**
     * Code constructor
     *
     * <p>
     * <p>
     * Here the length argument is the attribute length (e.g., the
     * total length of the attribute).
     */
    public Code(String name, int length,
                DataInputStream dStream, ConstPool constPoolSec) {
        super(name, length);  // invoke the attrInfo constructor

        max_stack = readUnsignedShort(dStream);
        max_locals = readUnsignedShort(dStream);
        int codeLen = readU4(dStream);

        if (codeLen > 0) {
            code = new byte[codeLen];

            //
            // Read the code into the code buffer.  The code is
            // read byte by byte.  Since buffered input (e.g., the
            // DataInputStream object) is used this should not be
            // a performace issue.
            //
            for (int i = 0; i < code.length; i++) {
                code[i] = (byte) readU1(dStream);
            } // for
        }

        int exceptLen = readUnsignedShort(dStream);

        if (exceptLen > 0) {
            exception_table = new CodeException[exceptLen];

            for (int i = 0; i < exceptLen; i++) {
                exception_table[i] = new CodeException(dStream, constPoolSec);
            }
        }

        int attrCount = readUnsignedShort(dStream);

        if (attrCount > 0) {
            attributes = new Attribute[attrCount];

            for (int i = 0; i < attrCount; i++) {
                attributes[i] = attrFactory.allocAttr(dStream, constPoolSec);
            } // for
        }
    } // codeAttr constructor

    /**
     * If there are local variables in the method, return a vector
     * containing the local variable name declarations.  There should
     * be only one localVarTabAttr in the codeAttributes table.
     */
    public Vector getLocalVarVec() {
        Vector varVec = null;

        if (attributes != null) {
            for (Attribute codeAttribute : attributes) {
                if (codeAttribute instanceof localVarTabAttr) {
                    varVec = ((localVarTabAttr) codeAttribute).getLocalVarVec();
                    break;
                }
            } // for

        } // if

        return varVec;
    }  // getLocalVarVec


    public int getMaxLocals() {
        return max_locals;
    }

    public int getMaxStack() {
        return max_stack;
    }

} // codeAttr
