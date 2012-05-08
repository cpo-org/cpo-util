/*
 * Copyright (C) 2003-2012 David E. Berry, Michael A. Bellomo
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.util.jdbc;

import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.util.*;

import javax.swing.*;
import java.util.*;

public class JdbcArgumentTableModel extends CoreArgumentTableModel {

  public JdbcArgumentTableModel(CpoFunctionNode cpoFunctionNode) {
    super(cpoFunctionNode);
  }

  public List<String> getColumnNames() {
    return Arrays.asList("Seq Num", "Attribute", "Data Name", "Data Type", "In/Out/Both", "DB Table", "DB Column", "Transform Class", "Changed?");
  }

  public List<Class<?>> getColumnClasses() {
    Class<?>[] columnClasses = {String.class, JdbcCpoAttribute.class, String.class, String.class, JComboBox.class, String.class, String.class, String.class, String.class};
    return Arrays.asList(columnClasses);
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    CpoArgumentNode cpoArgumentNode = (CpoArgumentNode)cpoFunctionNode.getChildAt(rowIndex);

    if (!(cpoArgumentNode instanceof JdbcArgumentNode)) {
      // if it's not a jdbc attribute, return null here...should never happen
      return null;
    }

    JdbcArgumentNode jdbcArgumentNode = (JdbcArgumentNode)cpoArgumentNode;
    JdbcCpoArgument jdbcArgument = jdbcArgumentNode.getUserObject();
    JdbcCpoAttribute jdbcAttribute = jdbcArgumentNode.getCpoAttribute();

    if (columnIndex == 0) {
      return cpoFunctionNode.getIndex(cpoArgumentNode);
    } else if (columnIndex == 1) {
      return cpoArgumentNode.getCpoAttribute();
    } else if (columnIndex == 2) {
      return jdbcAttribute != null ? jdbcAttribute.getDataName() : null;
    } else if (columnIndex == 3) {
      return jdbcAttribute != null ? jdbcAttribute.getDataType() : null;
    } else if (columnIndex == 4) {
      return jdbcArgument.getScope();
    } else if (columnIndex == 5) {
      return jdbcAttribute != null ? jdbcAttribute.getDbTable() : null;
    } else if (columnIndex == 6) {
      return jdbcAttribute != null ? jdbcAttribute.getDbColumn() : null;
    } else if (columnIndex == 7) {
      return jdbcAttribute != null ? jdbcAttribute.getTransformClassName() : null;
    } else if (columnIndex == 8) {
      if (cpoArgumentNode.isNew()) {
        return "New";
      } else if (cpoArgumentNode.isRemove()) {
        return "Removed";
      } else if (cpoArgumentNode.isDirty()) {
        return "Changed";
      } else {
        return "";
      }
    } else {
      return null;
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    CpoArgumentNode cpoArgumentNode = (CpoArgumentNode)cpoFunctionNode.getChildAt(rowIndex);

    if (!(cpoArgumentNode instanceof JdbcArgumentNode)) {
      // if it's not a jdbc attribute, return null here...should never happen
      return;
    }

    JdbcArgumentNode jdbcArgumentNode = (JdbcArgumentNode)cpoArgumentNode;
    JdbcCpoArgument jdbcArgument = jdbcArgumentNode.getUserObject();

    if (columnIndex == 1) {
      jdbcArgument.setAttribute((CpoAttribute)aValue);
    }
    if (columnIndex == 4) {
      jdbcArgument.setScope((String)aValue);
    }
    this.fireTableDataChanged();
  }
}