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
import javax.swing.tree.TreeNode;
import javax.swing.JPanel;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.*;

public class CpoQueryGroupNode extends AbstractCpoNode {
  private String groupName,type,class_id,group_id;
  private ArrayList qNodes; //CpoQueryNode(s)
  
  public CpoQueryGroupNode(String groupName, String class_id, String group_id, String type, AbstractCpoNode parent) {
    this.groupName = groupName;
    this.type = type;
    this.class_id = class_id;
    this.group_id = group_id;
    this.parent = parent;
    if (parent != null)
      this.addObserver(parent.getProxy());
  }

  public JPanel getPanelForSelected() {
    return null;
  }
  
  public TreeNode getChildAt(int childIndex) {
    if (childIndex >= qNodes.size()) return null;
    else return (TreeNode)qNodes.get(childIndex);
  }

  public int getChildCount() {
    return this.qNodes.size();
  }

  public int getIndex(TreeNode node) {
    return this.qNodes.indexOf(node);
  }

  public boolean getAllowsChildren() {
    return true;
  }

  public boolean isLeaf() {
    return false;
  }

  public Enumeration children() {
    if (qNodes == null) // due to panel not being removed from center pane ... this should be fixed
      refreshChildren();
    return new Enumeration() {
      Iterator iter = qNodes.iterator();
      public Object nextElement() {
        return iter.next();
      }
      public boolean hasMoreElements() {
        return iter.hasNext();
      }
    };
  }
  public void refreshChildren() {
    try {
      this.qNodes = getProxy().getQueries(this);
    } catch (Exception pe) {
      CpoUtil.showException(pe);
    }    
  }

  public String toString() {
    return this.groupName+" ("+this.type+")";
  }
  public String getClassId() {
    return this.class_id;
  }
  public String getGroupId() {
    return this.group_id;
  }
  public String getGroupName() {
    return this.groupName;
  }
  public String getType() {
    return this.type;
  }
  public void addNewQueryNode() {
    if (this.qNodes == null)
      this.refreshChildren();
    String queryId;
    int seqNo;
    try {
      queryId = this.getProxy().getNewGuid();
      seqNo = this.getNextQuerySeqNo();
    } catch (Exception pe) {
      CpoUtil.showException(pe);
      return;
    }
    CpoQueryNode cqn = new CpoQueryNode(queryId, this.getGroupId(), seqNo, null, this);
    this.qNodes.add(cqn);
    cqn.setNew(true);
  }
  private int getNextQuerySeqNo() {
    int nextSeqNo = 0;
    for (int i = 0 ; i < this.qNodes.size() ; i++) {
      if (nextSeqNo <= ((CpoQueryNode)this.qNodes.get(i)).getSeqNo()) {
        nextSeqNo = ((CpoQueryNode)this.qNodes.get(i)).getSeqNo()+1;
      }
    }
    return nextSeqNo;
  }
  public void setGroupName(String groupName) {
    if ((groupName == null && this.groupName == null) || (this.groupName != null && this.groupName.equals(groupName))) return;
    this.groupName = groupName;
    this.setDirty(true);
  }
}