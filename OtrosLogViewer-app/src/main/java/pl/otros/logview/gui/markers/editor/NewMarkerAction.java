/*******************************************************************************
 * Copyright 2011 Krzysztof Otrebski
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package pl.otros.logview.gui.markers.editor;

import org.apache.commons.io.IOUtils;
import pl.otros.logview.api.pluginable.AllPluginables;
import pl.otros.logview.api.pluginable.AutomaticMarker;
import pl.otros.logview.api.pluginable.PluginableElementsContainer;
import pl.otros.logview.gui.markers.PropertyFileAbstractMarker;
import pl.otros.logview.loader.AutomaticMarkerLoader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class NewMarkerAction extends AbstractAction {

  private final JFileChooser chooser;
  private final File markersFolder = AllPluginables.USER_MARKERS;

  public NewMarkerAction() {
    super();
    putValue(NAME, "New marker");
    if (!markersFolder.exists()) {
      markersFolder.mkdirs();
    }
    chooser = new JFileChooser(markersFolder);
    chooser.setFileFilter(new FileFilter() {

      @Override
      public String getDescription() {
        return "*.marker";
      }

      @Override
      public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".marker");
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    final JFrame f = new JFrame("Create new marker");
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    Container contentPane = f.getContentPane();
    final MarkerEditor editor = new MarkerEditor();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(editor);

    JButton save = new JButton("Save");
    save.addActionListener(new ActionListener() {

      private final PluginableElementsContainer<AutomaticMarker> markersContainser = AllPluginables.getInstance().getMarkersContainser();

      @Override
      public void actionPerformed(ActionEvent arg0) {

        int showSaveDialog = chooser.showSaveDialog((Component) arg0.getSource());
        if (showSaveDialog != JFileChooser.APPROVE_OPTION) {
          return;
        }
        File saveFile = chooser.getSelectedFile();
        Properties markerPropertiesFromView = editor.getMarkerPropertiesFromView();

        try {
          saveMarker(saveFile, markerPropertiesFromView);
        } catch (IOException e) {
          JOptionPane.showMessageDialog(f, "Error saving marker: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        try {
          AutomaticMarker marker = AutomaticMarkerLoader.loadPropertyBasedMarker(markerPropertiesFromView);
          markersContainser.addElement(marker);
        } catch (Exception e) {
          JOptionPane.showMessageDialog(f, "Error creating marker: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          return;
        }
        f.dispose();
      }
    });
    contentPane.add(save, BorderLayout.SOUTH);

    f.pack();

    f.setVisible(true);
  }

  private void saveMarker(final File markerFile, final Properties markerProperties) throws IOException {
    File file;
    if (!markerFile.getName().endsWith(".marker")) {
      file = new File(markerFile.getAbsoluteFile() + ".marker");
    } else {
      file = markerFile;
    }
    try (FileOutputStream fout = new FileOutputStream(file)) {
      markerProperties.setProperty(PropertyFileAbstractMarker.FILE, file.getName());
      markerProperties.store(fout, "Edited at " + new Date().toString());
    } catch (IOException e) {
      throw e;
    }
  }

}
