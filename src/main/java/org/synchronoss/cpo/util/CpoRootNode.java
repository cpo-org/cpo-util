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
package org.synchronoss.cpo.util;

import javax.swing.*;
import java.util.*;

public class CpoRootNode extends AbstractCpoNode {

  public CpoRootNode(Proxy proxy) {
    super(proxy);
  }

  @Override
  public Proxy getUserObject() {
    return (Proxy)super.getUserObject();
  }

  @Override
  public JPanel getPanelForSelected() {
    return null;
  }

  @Override
  public String getToolTipText() {
    return getUserObject().getMetaXmlFullName();
  }

  public List<AbstractCpoNode> getChangedNodes() {
    List<AbstractCpoNode> changedNodes = new ArrayList<AbstractCpoNode>();
    changedNodes.addAll(newChildren);
    changedNodes.addAll(dirtyChildren);
    changedNodes.addAll(removeChildren);
    return changedNodes;
  }

  public boolean isUnsaved() {
    if (this.isChildDirty())
      return true;

    if (this.isChildNew())
      return true;

    if (this.isChildRemove())
      return true;

    return false;
  }
}
