/*
 * Copyright 2012 Krzysztof Otrebski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.otros.logview.gui.actions.read;

import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.commons.vfs2.FileObject;

import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.OtrosApplication;
import pl.otros.logview.gui.table.TableColumns;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.io.LoadingInfo;
import pl.otros.logview.io.Utils;
import pl.otros.logview.parser.TableColumnNameSelfDescribable;

public class LogFileInNewTabOpener {

  static final Logger LOGGER = Logger.getLogger(LogFileInNewTabOpener.class.getName());
  private LogImporterProvider importerProvider;
	private OtrosApplication otrosApplication;

	public LogFileInNewTabOpener(LogImporterProvider importerProvider, OtrosApplication otrosApplication) {
    this.importerProvider = importerProvider;
		this.otrosApplication = otrosApplication;
	}

  public void open(FileObject file) {
    try {
      boolean tailing = true;
      // Do not tail for http(s)
      if (file.getName().getScheme().startsWith("http")) {
        tailing = false;
      }
      final LoadingInfo openFileObject = Utils.openFileObject(file, tailing);

      LogImporter importer = chooseImporter(openFileObject);
      if (isInvalid(importer)) {
        handleInvalidImporter(file);
        return;
      }

      final LogViewPanelWrapper panel = createPanelForLog(file, openFileObject, importer);
      otrosApplication.addClosableTab(getTabName(file),  file.getName().getFriendlyURI(),Icons.FOLDER_OPEN,panel,true);
      startThreadToImportLogDataFromFile(file, openFileObject, importer, panel);
    } catch (Exception e1) {
      LOGGER.severe("Error loading log (" + file.getName().getFriendlyURI() + "): " + e1.getMessage());
      JOptionPane.showMessageDialog(null, "Error loading log: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void startThreadToImportLogDataFromFile(FileObject file, final LoadingInfo openFileObject, LogImporter importer, final LogViewPanelWrapper panel) {
    new Thread(new ImportLogRunnable(openFileObject, panel, file, importer)).start();
  }

  private LogViewPanelWrapper createPanelForLog(FileObject file, final LoadingInfo openFileObject, LogImporter importer) {
    TableColumns[] tableColumnsToUse = TableColumns.ALL_WITHOUT_LOG_SOURCE;
    if (importer instanceof TableColumnNameSelfDescribable) {
      TableColumnNameSelfDescribable describable = (TableColumnNameSelfDescribable) importer;
      tableColumnsToUse = describable.getTableColumnsToUse();
    }
    final LogViewPanelWrapper panel = new LogViewPanelWrapper(file.getName().getBaseName(), openFileObject.getObserableInputStreamImpl(),
        tableColumnsToUse,otrosApplication);

    return panel;
  }


  private String getTabName(FileObject file) {
    return Utils.getFileObjectShortName(file);
  }

  private LogImporter chooseImporter(LoadingInfo openFileObject) {
    return importerProvider.getLogImporter(openFileObject);
  }

  private void handleInvalidImporter(final FileObject file) {
    LOGGER.severe("Error loading log (" + file.getName().getFriendlyURI() + "): no suitable log parser found");

    String errorMessage = "Error loading log file: no suitable log parser found for " + file.getName().getFriendlyURI() + "\n"
        + "Go https://github.com/otros-systems/otroslogviewer/wiki/Log4jPatternLayout to check how to parse log4j custom pattern.";
    JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private boolean isInvalid(LogImporter importer) {
    return importer == null;
  }

}
