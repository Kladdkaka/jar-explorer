package org.jarexplorer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


/**
 * Displays a tree view of class internals:
 * implemented interfaces, superclass, fields, methods, constructors, etc.
 *
 * @author Igor Polevoy
 * <br/>
 * August 22 2007
 */
public class ClassExplorerDialog extends CenteredDialog {
    /**
     * Constructs an instance
     *
     * @param owner            - top level frame
     * @param classInformation - instance
     */
    public ClassExplorerDialog(Frame owner, ClassInformation classInformation) {
        super(owner, classInformation.getJarFileName() + ":" + classInformation.getClassPath(), false);

        getContentPane().setLayout(new BorderLayout());

        //build center panel - tree
        DefaultMutableTreeNode classNode;
        DefaultMutableTreeNode constructorsNode;
        DefaultMutableTreeNode interfacesNode;
        DefaultMutableTreeNode fieldsNode;
        DefaultMutableTreeNode methodsNode;
        DefaultMutableTreeNode modifiersNode;

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("<html><font color='blue'>Class:</font>" + classInformation.getClassName() + "</html>");

        root.add(classNode = new DefaultMutableTreeNode("<html><font color='blue'>General Class Information</font></html>"));
        root.add(interfacesNode = new DefaultMutableTreeNode("<html><font color='blue'>Implemented Interfaces</font></html>"));
        root.add(constructorsNode = new DefaultMutableTreeNode("<html><font color='blue'>Constructors</font></html>"));
        root.add(fieldsNode = new DefaultMutableTreeNode("<html><font color='blue'>Fields</font></html>"));
        root.add(methodsNode = new DefaultMutableTreeNode("<html><font color='blue'>Methods</font></html>"));
        JTree tree = new JTree();

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        renderer.setFont(new Font("Monospaced", Font.PLAIN, 14));

        classNode.add(new DefaultMutableTreeNode("<html><font color='blue'>Jar File: </font>" + classInformation.getJarFileName() + "</html>"));
        classNode.add(new DefaultMutableTreeNode("<html><font color='blue'>Superclass: </font>" + classInformation.getSuperclass() + "</html>"));
        classNode.add(modifiersNode = new DefaultMutableTreeNode("<html><font color='blue'>Modifiers:</font>" + classInformation.getModifiers() + "</html>"));

        ((DefaultTreeModel) tree.getModel()).setRoot(root);

        //interfaces
        String[] interfacesArray = classInformation.getInterfaces();
        for (String interfaceName : interfacesArray) {
            interfacesNode.add(new DefaultMutableTreeNode(interfaceName));
        }

        //constructors
        ArrayList<String> constructorList = classInformation.getConstructors();
        for (Object value : constructorList) {
            String constructor = (String) value;
            constructorsNode.add(new DefaultMutableTreeNode(constructor));
        }

        //methods
        ArrayList<String> methodsList = classInformation.getMethods();
        for (Object o : methodsList) {
            String method = (String) o;
            methodsNode.add(new DefaultMutableTreeNode(method));
        }

        //fields
        String[] fieldsList = classInformation.getFields();
        for (String field : fieldsList) {
            fieldsNode.add(new DefaultMutableTreeNode(field));
        }

        //build south panel
        JPanel southPanel = new JPanel();
        JButton closeB = new JButton("Close");
        southPanel.add(closeB);
        closeB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        //build mail panel
        getContentPane().add(new JScrollPane(tree), BorderLayout.CENTER);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
    }
}

