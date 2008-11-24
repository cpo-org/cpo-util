/*
 *  Copyright (C) 2008
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

import org.apache.log4j.Logger;

import java.io.*;

/**
 * SwingWorker used to launch a progress box up while the sql is being exported.
 */
public class ExportClassSwingWorker extends SwingWorker {

    private static final Logger logger = Logger.getLogger(ExportClassSwingWorker.class);

    ProgressFrame pf = null;
    AbstractCpoNode menuNode = null;

    Exception error = null;

    public ExportClassSwingWorker(AbstractCpoNode menuNode) {
        setLocalName("ExportWorker");
        this.menuNode = menuNode;
    }

    @Override
    public Object construct() {
        logger.debug("Exporting...");
        pf = new ProgressFrame("Exporting...", -1);
        pf.start();

        String dir = menuNode.getProxy().getSqlDir();
        String server = menuNode.getProxy().getServer();

        File file = null;
        FileWriter fw = null;
        try {
            // First let's make sure that the sql dir exists
            File sqlDir = new File(dir);
            if (!sqlDir.exists()) {
                if (!sqlDir.mkdirs()) {
                    throw new SqlDirRequiredException("Unable to create directory: " + sqlDir.getPath(), server);
                }
            }

            if (!sqlDir.isDirectory()) {
                throw new SqlDirRequiredException("The sql dir is not a directory: " + sqlDir.getPath(), server);
            }

            if (!sqlDir.canWrite()) {
                throw new SqlDirRequiredException("Unable to write to directory: " + sqlDir.getPath(), server);
            }

            SQLExporter sqlEx = new SQLExporter(menuNode.getProxy().getTablePrefix(), menuNode.getProxy().getSqlDelimiter());
            SQLClassExport classExport = sqlEx.exportSQL(menuNode);

            StringBuffer sql = new StringBuffer();
            sql.append(classExport.getDeleteSql());
            sql.append(classExport.getInsertQueryTextSql());
            sql.append(classExport.getInsertSql());

            CpoClassNode classNode = (CpoClassNode)menuNode;
            String fileName = classNode.getClassName() + ".sql";

            file = new File(dir, fileName);
            fw = new FileWriter(file);
            fw.write(sql.toString());
            fw.flush();
            fw.close();

            CpoServerNode serverNode = menuNode.getProxy().getServerNode();
            pf.progressMade(new ProgressMaxEvent(this, (serverNode.getChildCount() - 1)));
            pf.setLabel("Generating create all sql...");
            String createSql = sqlEx.exportCreateAll(serverNode, pf);

            // write out the create all file
            file = new File(dir, Statics.CREATE_ALL_FILE_NAME);
            fw = new FileWriter(file);
            fw.write(createSql);
            fw.flush();
            fw.close();

        } catch (Exception ex) {
            error = ex;
            logger.error("Exception caught", ex);
        } finally {
            try {
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
            }
            pf.stop();
        }
        return null;
    }

    @Override
    public void finished() {
        if (error != null) {
            CpoUtil.showException(error);
        } else {
            CpoUtil.updateStatus("Exported SQL for class: " + menuNode.toString());
        }
    }
}