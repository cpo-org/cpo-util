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

import javax.swing.*;
import java.awt.*;

public class MainFrame_AboutBoxPanel extends JPanel {
  
  /** Version Id for this class. */
  private static final long serialVersionUID=1L;

  private JLabel titleLabel = new JLabel();
  private JLabel titleValue = new JLabel();
  private JLabel versionLabel = new JLabel();
  private JLabel versionValue = new JLabel();
  private JLabel authorLabel = new JLabel();
  private JLabel authorValue = new JLabel();
  private JLabel copyrightLabel = new JLabel();
  private JLabel copyrightValue = new JLabel();
  private JLabel companyLabel = new JLabel();
  private JLabel companyValue = new JLabel();

  private JLabel jvmLabel = new JLabel();
  private JLabel jvmValue = new JLabel();

  public MainFrame_AboutBoxPanel() {
    try {
      jbInit();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.setLayout(new GridBagLayout());

    titleLabel.setText("Title:");
    this.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    titleValue.setText(CpoUtil.props.getProperty("cpoutil.title"));
    this.add(titleValue, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    versionLabel.setText("Version:");
    this.add(versionLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    versionValue.setText(CpoUtil.props.getProperty("cpoutil.version"));
    this.add(versionValue, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    authorLabel.setText("Author:");
    this.add(authorLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    authorValue.setText(CpoUtil.props.getProperty("cpoutil.author"));
    this.add(authorValue, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    copyrightLabel.setText("Copyright:");
    this.add(copyrightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    copyrightValue.setText(CpoUtil.props.getProperty("cpoutil.copyright"));
    this.add(copyrightValue, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    companyLabel.setText("Company:");
    this.add(companyLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    companyValue.setText(CpoUtil.props.getProperty("cpoutil.company"));
    this.add(companyValue, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    jvmLabel.setText("Java Version:");
    this.add(jvmLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

    jvmValue.setText(System.getProperty("java.version"));
    this.add(jvmValue, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
  }
}