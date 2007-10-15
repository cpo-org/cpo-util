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
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.util.EventObject;
import javax.swing.event.CellEditorListener;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellEditor;

public class CpoAttMapTableEditor implements TableCellEditor {
  JTextField jText = new JTextField();
  JComboBox jCombo;
  TableCellEditor editorText;
  TableCellEditor editorCombo;
  TableCellEditor editor;
  public CpoAttMapTableEditor(String[] cpoSqlTypes) {
    jCombo = new JComboBox(cpoSqlTypes);
    editorCombo = new DefaultCellEditor(jCombo);
    editorText = new DefaultCellEditor(jText);
    editor = editorText;
  }

  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    if (column == 2) {
      this.editor = this.editorCombo;
      jCombo.setSelectedItem(value);
    } else
      this.editor = this.editorText;
    return editor.getTableCellEditorComponent(table,value,isSelected,row,column);
  }

  public Object getCellEditorValue() {
    return editor.getCellEditorValue();
  }

  public boolean isCellEditable(EventObject anEvent) {
    return editor.isCellEditable(anEvent);
  }

  public boolean shouldSelectCell(EventObject anEvent) {
    return editor.shouldSelectCell(anEvent);
  }

  public boolean stopCellEditing() {
    return editor.stopCellEditing();
  }

  public void cancelCellEditing() {
    editor.cancelCellEditing();
  }

  public void addCellEditorListener(CellEditorListener l) {
    editor.addCellEditorListener(l);
  }

  public void removeCellEditorListener(CellEditorListener l) {
    editor.removeCellEditorListener(l);
  }
}