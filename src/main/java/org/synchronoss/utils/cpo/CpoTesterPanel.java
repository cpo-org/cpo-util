/*
 *  Copyright (C) 2006  Jay Colson
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.utils.cpo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.log4j.Category;

public class CpoTesterPanel extends JPanel implements ClipboardOwner  {
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
  private static SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPopupMenu menu = new JPopupMenu();

  private CpoTesterPanelNorth cpoTPnorth;
  private CpoClassNode cpoClassNode;
  private JScrollPane jScrollResults = new JScrollPane();
  private JTable jTableResults = new JTable();
  private Category OUT = Category.getInstance(this.getClass());
  
  public CpoTesterPanel(CpoClassNode cpoClassNode) {
    this.cpoClassNode = cpoClassNode;
    this.cpoTPnorth = new CpoTesterPanelNorth(cpoClassNode);
    try {
      jbInit();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setSize(new Dimension(754, 681));
    this.setLayout(borderLayout1);
    jTableResults.setPreferredScrollableViewportSize(new Dimension(100, 100));
    jTableResults.setCellSelectionEnabled(true);
    jTableResults.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {}
      public void keyReleased(KeyEvent e) {}
      public void keyTyped(KeyEvent e) {
        keyTypedEvent(e);
      }
    });
    this.jTableResults.addMouseListener(new MouseListener() {
        public void mouseClicked(MouseEvent e) {
          if (e.isMetaDown()) {
            showSouthMenu(e.getPoint());
          }
        }
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}
    });
    this.cpoTPnorth.jTableParam.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) {
        if (e.isMetaDown()) {
          showMenu(e.getPoint());
        }
      }
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}
      public void mousePressed(MouseEvent e) {}
      public void mouseReleased(MouseEvent e) {}
    });
    this.add(cpoTPnorth,BorderLayout.NORTH);
    jScrollResults.getViewport().add(jTableResults, null);
    this.add(jScrollResults, BorderLayout.CENTER);
    this.cpoTPnorth.jButExec.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        executeQueryGroup();
      }
    });
  }

  private void executeQueryGroup() {
    if (cpoTPnorth.jTableParam.isEditing()) {
      int row = cpoTPnorth.jTableParam.getEditingRow();
      int column = cpoTPnorth.jTableParam.getEditingColumn();
      cpoTPnorth.jTableParam.getCellEditor(row,column).stopCellEditing();
//      cpoTPnorth.jTableParam.editingStopped (new ChangeEvent (cpoTPnorth.jTableParam.getComponentAt(row, column)));
    }
    CpoUtil.updateStatus("");
    if (!((CpoTesterParamModel)cpoTPnorth.jTableParam.getModel()).isTableFilledOut()) {
      int result = JOptionPane.showConfirmDialog(this,"You need to completely fill out the Parameter values in the table before executing a Query Group!\nContinue Anyway or CANCEL?","User Error",JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.CANCEL_OPTION) return;
    }
    // find classname
    String cpoClassName = cpoClassNode.getClassName();
    String cpoClassNameReturnType = cpoClassName;
    CpoClassNode returnClassNode = cpoClassNode;
    if (((CpoQueryGroupNode)cpoTPnorth.jComQueryGroup.getSelectedItem()).getType().equals(Statics.CPO_TYPE_LIST)) {
      returnClassNode = (CpoClassNode)cpoTPnorth.jComClassOut.getSelectedItem();
      cpoClassNameReturnType = returnClassNode.getClassName();
    }
    OUT.debug ("Class name tester will use: "+cpoClassName+" and "+cpoClassNameReturnType+" as the return type class");
    try  {
      Class cpoClass = CpoUtilClassLoader.getInstance(CpoUtil.files,this.getClass().getClassLoader()).loadClass(cpoClassName);
//      Class cpoClass = Class.forName(cpoClassName);
      Object cpoObject = cpoClass.newInstance();
      Object cpoObjectReturnType = CpoUtilClassLoader.getInstance(CpoUtil.files,this.getClass().getClassLoader()).loadClass(cpoClassNameReturnType).newInstance();
      Method[] methods = cpoClass.getMethods();
//      Method[] methods = cpoClass.getDeclaredMethods();
      CpoTesterParamModel ctpm = (CpoTesterParamModel)cpoTPnorth.jTableParam.getModel();
      Enumeration enumParams = ctpm.parameter.keys();
      while (enumParams.hasMoreElements()) {
        String key = (String)enumParams.nextElement();
        String methodName = "set"+key.substring(0,1).toUpperCase()+key.substring(1);
        OUT.debug ("Method name looking for: "+methodName);
        boolean found = false;
        for (int i = 0 ; i < methods.length ; i++) {
          if (methods[i].getName().equals(methodName)) {
            Class[] paramTypes = methods[i].getParameterTypes();
            if (paramTypes.length != 1)//throw new Exception("Param types count is not equal to 1");
              continue;
            methods[i].invoke(cpoObject,new Object[]{getMethObjFrStr(paramTypes[0],(String)ctpm.parameter.get(key))});
            found = true;
          }
        }
        if (!found) throw new Exception("Could not find method: "+methodName);
      }
      Collection result = cpoClassNode.getProxy().executeQueryGroup(cpoObject,cpoObjectReturnType,(CpoQueryGroupNode)cpoTPnorth.jComQueryGroup.getSelectedItem(),cpoTPnorth.jCheckPersist.isSelected());
      TableSorter ts = new TableSorter(new CpoTesterResultsModel(result,returnClassNode));
      this.jTableResults.setModel(ts);
      ts.addMouseListenerToHeaderInTable(this.jTableResults);
      CpoUtil.updateStatus("Query Executed! Rows Returned: "+result.size());
      setColumnSizes(200);
    } catch (NoClassDefFoundError cdfe) {
      CpoUtil.showException(cdfe);
      CpoUtil.setCustomClassPath("Need path to: web-common, wls-startup and log4j!");
    }
    catch (Exception ex)  {
      CpoUtil.showException(ex);
    }
  }
  private void setColumnSizes(int columnSize) {
    this.jTableResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    for (int i = 0; i < this.jTableResults.getColumnCount(); i++) {
      this.jTableResults.getColumnModel().getColumn(i).setPreferredWidth(columnSize);
    }
  }
  private Object getMethObjFrStr(Class cls, String param) throws Exception {
    if (param.equalsIgnoreCase("null") || param.equals(""))
      return null;
    else if (param.equalsIgnoreCase("guid"))
      return this.cpoClassNode.getProxy().getNewGuid();
    try {
//      Object obj = cls.newInstance();
      if (cls == String.class)
        return param;
      else if (cls == int.class)
        return new Integer(param);
      else if (cls == Integer.class)
        return new Integer(param);
      else if (cls == float.class)
        return new Float(param);
      else if (cls == Float.class)
        return new Float(param);
      else if (cls == BigDecimal.class)
        return new BigDecimal(param);
      else if (cls == BigInteger.class)
        return new BigInteger(param);
      else if (cls == Date.class) {
        OUT.debug("Found type Date");
        if (param.equalsIgnoreCase("sysdate")) {
          return new Date();
        }
        if (param.length() == 10) {
           return sdfDateOnly.parse(param);
        } else {
          return sdf.parse(param);
        }
//        return new SimpleDateFormat("MM/dd/yyyy").parse(param);
      }
      else if (cls == java.sql.Date.class) {
        OUT.debug("Found type sql Date");
        if (param.equalsIgnoreCase("sysdate")) {
          return new java.sql.Date(new Date().getTime());
        }
        if (param.length() == 10) {
           return new java.sql.Date(sdfDateOnly.parse(param).getTime());
        } else {
          return new java.sql.Date(sdf.parse(param).getTime());
        }
//        return new SimpleDateFormat("MM/dd/yyyy").parse(param);
      }
      else if (cls == Timestamp.class) {
        OUT.debug("Found type Timestamp");
        if (param.equalsIgnoreCase("sysdate")) {
          return new Timestamp(new Date().getTime());
        }
        if (param.length() == 10) {
           return new Timestamp(sdfDateOnly.parse(param).getTime());
        } else {
          return new Timestamp(sdf.parse(param).getTime());
        }
        
        
//        return new Timestamp(new SimpleDateFormat("MM/dd/yyyy").parse(param).getTime());
      }
      else if (cls == byte[].class) {
        return param.getBytes();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    CpoUtil.showException(new Exception("Could not locate the type for param: "+param+" which is class: "+cls));
    return null;
  }
  private void keyTypedEvent(KeyEvent e) {
    int[] selectedRows = this.jTableResults.getSelectedRows();
    int[] selectedCols = this.jTableResults.getSelectedColumns();
    if (selectedRows.length < 1 || selectedCols.length < 1) return;
    Object valueO = this.jTableResults.getValueAt(selectedRows[0],selectedCols[0]);
    if (valueO == null) return;
    String value = valueO.toString();
//    OUT.debug("value: "+value+" Keyevent: "+(int)e.getKeyChar());
    if ((e.getKeyCode() == KeyEvent.VK_COPY) ||
        ((e.getKeyCode() == KeyEvent.VK_C) && e.isControlDown()) ||
        ((e.getKeyCode() == KeyEvent.VK_C) && e.isMetaDown())||
        (int)e.getKeyChar() == 3) {
      Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(new StringSelection(value),this);
    }
  }
  public void lostOwnership(Clipboard clip, Transferable trans) {
    OUT.debug("Boohoo ...  clipboard lost ownership ...");
  }
  private void insertValueAt(String value) {
    int[] selectedRows = this.cpoTPnorth.jTableParam.getSelectedRows();
    if (selectedRows.length != 1) {
      CpoUtil.updateStatus("Select ONE Row");
      return;
    }
    this.cpoTPnorth.jTableParam.setValueAt(value,selectedRows[0],
        CpoTesterParamModel.COLUMN_PARAMETER);
  }
  private void insertValuesFromResults() {
    int[] selectedRows = this.jTableResults.getSelectedRows();
//    int[] selectedCols = this.jTableResults.getSelectedColumns();
    if (selectedRows.length != 1) {
      CpoUtil.updateStatus("Select ONE Row");
      return;
    }
    for (int i = 0 ; i < this.jTableResults.getColumnCount() ; i++) {
      String setterName = this.jTableResults.getColumnName(i);
      int setColumn = getParamRowForSetter(setterName);
      if (setColumn != -1) {
        String value = this.jTableResults.getValueAt(selectedRows[0],i)==null?null:this.jTableResults.getValueAt(selectedRows[0],i).toString();
        this.cpoTPnorth.jTableParam.setValueAt(value,setColumn,CpoTesterParamModel.COLUMN_PARAMETER);
      }
    }
  }
  private int getParamRowForSetter(String setter) {
    int returnVal = -1;
    for (int i = 0 ; i < this.cpoTPnorth.jTableParam.getRowCount(); i++) {
      if (this.cpoTPnorth.jTableParam.getValueAt(i,CpoTesterParamModel.COLUMN_ATTRIBUTE_NAME).equals(setter))
        returnVal = i;
    }
    return returnVal;
  }
	private void showMenu(Point p) {
    menu.removeAll();
    menu.setLabel("Tester Menu");
    JMenuItem jMenuSetSysdate = new JMenuItem("Set Sysdate");
    jMenuSetSysdate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        insertValueAt(sdf.format(new Date()));
      }
    });
    menu.add(jMenuSetSysdate);
    JMenuItem jMenuSetNewGuid = new JMenuItem("Set New Guid");
    jMenuSetNewGuid.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        String guid;
        try {
          guid = cpoClassNode.getProxy().getNewGuid();
        } catch (Exception pe) {
          CpoUtil.showException(pe);
          return;
        }
        insertValueAt(guid);
      }
    });
    menu.add(jMenuSetNewGuid);
		menu.show(this.cpoTPnorth.jTableParam, (int)p.getX(), (int)p.getY());
	}
  private void showSouthMenu(Point p) {
    menu.removeAll();
    menu.setLabel("Results Menu");
    JMenuItem jMenuPopulateNorth = new JMenuItem("Populate Query");
    jMenuPopulateNorth.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        insertValuesFromResults();
      }
    });
    menu.add(jMenuPopulateNorth);
    menu.show(this.jTableResults, (int)p.getX(), (int)p.getY());
  }
}