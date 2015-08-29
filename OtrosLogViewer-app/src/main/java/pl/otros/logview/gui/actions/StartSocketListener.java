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
package pl.otros.logview.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.configuration.BaseConfiguration;

import net.miginfocom.swing.MigLayout;
import pl.otros.logview.BufferingLogDataCollectorProxy;
import pl.otros.logview.gui.ConfKeys;
import pl.otros.logview.gui.Icons;
import pl.otros.logview.gui.LogViewPanelWrapper;
import pl.otros.logview.gui.OtrosApplication;
import pl.otros.logview.gui.StatusObserver;
import pl.otros.logview.gui.config.LogTableFormatConfigView;
import pl.otros.logview.gui.table.TableColumns;
import pl.otros.logview.importer.LogImporter;
import pl.otros.logview.pluginable.AllPluginables;
import pl.otros.logview.reader.SocketLogReader;
import pl.otros.swing.table.ColumnLayout;

public class StartSocketListener extends OtrosAction {

  private Collection<SocketLogReader> logReaders = null;
  private BufferingLogDataCollectorProxy logDataCollector;

  private LogViewPanelWrapper logViewPanelWrapper;

  public StartSocketListener(OtrosApplication otrosApplication,Collection<SocketLogReader> logReaders) {
		super(otrosApplication);
		this.logReaders = logReaders;
    putValue(Action.NAME, "Start socket listener");
    putValue(Action.SHORT_DESCRIPTION, "Start socket listener on port.");
    putValue(Action.LONG_DESCRIPTION, "Start socket listener on port.");
    putValue(SMALL_ICON, Icons.PLUGIN_PLUS);

  }

  @Override
  public void actionPerformed(ActionEvent arg0) {

    LogImporterAndPort chooseLogImporter = chooseLogImporter();
    if (chooseLogImporter == null) {
      return;
    }

    StatusObserver observer = getOtrosApplication().getStatusObserver();
    if (logViewPanelWrapper == null) {
        TableColumns[] tableColumnsToUse = TableColumns.values();
        String useLayout = getOtrosApplication().getConfiguration().getString(ConfKeys.DEFAULTS_USELAYOUT, null);
        if (useLayout != null) {
            final List<ColumnLayout> columnLayouts = LogTableFormatConfigView.loadColumnLayouts(getOtrosApplication().getConfiguration());
            for (ColumnLayout columnLayout : columnLayouts) {
                if (columnLayout.getName().equals(useLayout)) {
                    List<TableColumns> visCols = new ArrayList<TableColumns>();
                    List<String> colNames = columnLayout.getColumns();
                    for (String colName : colNames) {
                        TableColumns tc = TableColumns.getColumnByName(colName);
                        if (tc != null) {
                            visCols.add(tc);
                        }
                    }
                    tableColumnsToUse =  visCols.toArray(new TableColumns[visCols.size()]);
                    break;
                }
            }
        }
        logViewPanelWrapper = new LogViewPanelWrapper("Socket", null, tableColumnsToUse, getOtrosApplication());

        logViewPanelWrapper.goToLiveMode();
        BaseConfiguration configuration = new BaseConfiguration();
        configuration.addProperty(ConfKeys.TAILING_PANEL_PLAY, true);
        configuration.addProperty(ConfKeys.TAILING_PANEL_FOLLOW, true);
        logDataCollector = new BufferingLogDataCollectorProxy(logViewPanelWrapper.getDataTableModel(), 4000, configuration);
    }


    getOtrosApplication().addClosableTab("Socket listener","Socket listener",Icons.PLUGIN_CONNECT,logViewPanelWrapper,true);

    SocketLogReader logReader = null;
    if (logReader == null || logReader.isClosed()) {
      logReader = new SocketLogReader(chooseLogImporter.logImporter, logDataCollector, observer, chooseLogImporter.port);

      try {
        logReader.start();
        logReaders.add(logReader);
        observer.updateStatus(String.format("Socket opened on port %d with %s.", chooseLogImporter.port, chooseLogImporter.logImporter));
      } catch (Exception e) {
        e.printStackTrace();
        observer.updateStatus("Failed to open listener " + e.getMessage(), StatusObserver.LEVEL_ERROR);
      }
    }
  }

  private LogImporterAndPort chooseLogImporter() {
    String defaultImporterName = this.getOtrosApplication().getConfiguration().getString(ConfKeys.DEFAULTS_LOGIMPORTER, null);
    int defaultImporterIdx = -1;
    Collection<LogImporter> elements = AllPluginables.getInstance().getLogImportersContainer().getElements();
    LogImporter[] importers = elements.toArray(new LogImporter[0]);
    String[] names = new String[elements.size()];
    for (int i = 0; i < names.length; i++) {
      names[i] = importers[i].getName();
      if (names[i].equals(defaultImporterName)) {
          defaultImporterIdx = i;
      }
    }

    int socketPort = this.getOtrosApplication().getConfiguration().getInteger(ConfKeys.DEFAULTS_SOCKETPORT, 50505);
    JComboBox box = new JComboBox(names);
    if (defaultImporterIdx != -1) {
        box.setSelectedIndex(defaultImporterIdx);
    }
    SpinnerNumberModel numberModel = new SpinnerNumberModel(socketPort, 1025, 65000, 1);
    JSpinner jSpinner = new JSpinner(numberModel);
    MigLayout migLayout = new MigLayout();
    JPanel panel = new JPanel(migLayout);
    panel.add(new JLabel("Select log importer"));
    panel.add(box, "wrap");
    panel.add(new JLabel("Select port"));
    panel.add(jSpinner, "span");

    if (logReaders.size() > 0) {
      panel.add(new JLabel("Opened sockets"), "wrap, growx");
      JTable jTable = new JTable(logReaders.size(), 2);
      jTable.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Log importer");
      jTable.getTableHeader().getColumnModel().getColumn(1).setHeaderValue("Port");
      int row = 0;
      for (SocketLogReader socketLogReader : logReaders) {
        jTable.setValueAt(socketLogReader.getLogImporter().getName(), row, 0);
        jTable.setValueAt(Integer.toString(socketLogReader.getPort()), row, 1);
        row++;
      }
      JScrollPane jScrollPane = new JScrollPane(jTable);
      panel.add(jScrollPane, "wrap, span");
    }
    int showConfirmDialog = JOptionPane.showConfirmDialog(null, panel, "Choose log importer and port", JOptionPane.OK_CANCEL_OPTION);
    if (showConfirmDialog != JOptionPane.OK_OPTION) {
      return null;
    }

    socketPort = numberModel.getNumber().intValue();
    this.getOtrosApplication().getConfiguration().setProperty(ConfKeys.DEFAULTS_SOCKETPORT, Integer.valueOf(socketPort));
    LogImporter selImporter =  importers[box.getSelectedIndex()];
    this.getOtrosApplication().getConfiguration().setProperty(ConfKeys.DEFAULTS_LOGIMPORTER, selImporter.getName());
    return new LogImporterAndPort(selImporter, socketPort);
  }

  public static class LogImporterAndPort {

    private int port;
    private LogImporter logImporter;

    public LogImporterAndPort(LogImporter logImporter, int port) {
      this.logImporter = logImporter;
      this.port = port;
    }

    public int getPort() {
      return port;
    }

    public LogImporter getLogImporter() {
      return logImporter;
    }

  }

}
