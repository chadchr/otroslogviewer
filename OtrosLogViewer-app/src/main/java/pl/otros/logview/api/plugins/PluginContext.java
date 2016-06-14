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

import com.google.common.annotations.Beta;
import pl.otros.logview.api.OtrosApplication;

import javax.swing.*;

/**
 */
@Beta
public interface PluginContext {

  OtrosApplication getOtrosApplication();

  void addClosableTab(String name, String tooltip, Icon icon, JComponent component, boolean show);

  LogOpenHandler getLogOpenHandler();

  /**
   * Adds MenuActionProvider. Before displaying log table context menu, actions from providers will be collected and
   * displayed in menu.
   *
   * @param menuActionProvider
   */
  void addLogViewPanelMenuActionProvider(MenuActionProvider menuActionProvider);
}
