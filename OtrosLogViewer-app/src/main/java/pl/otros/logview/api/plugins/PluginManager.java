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

package pl.otros.logview.api.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.otros.logview.api.BaseLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 */
public class PluginManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class.getName());

  public Collection<PluginInfo> loadPlugins(File dirWithPlugins) {
    BaseLoader baseLoader = new BaseLoader();
    File[] dirs = dirWithPlugins.listFiles(File::isDirectory);

    Collection<PluginInfo> pluginCollection = new ArrayList<>();
    for (File dir : dirs) {
      pluginCollection.addAll(baseLoader.load(dir, PluginInfo.class));
      LOGGER.info(String.format("Loaded %d plugins from %s", baseLoader.load(dir, PluginInfo.class).size(), dir.getAbsolutePath()));
    }

    return pluginCollection;
  }

  public static void main(String[] args) {
    new PluginManager().loadPlugins(new File("./"));
  }
}
