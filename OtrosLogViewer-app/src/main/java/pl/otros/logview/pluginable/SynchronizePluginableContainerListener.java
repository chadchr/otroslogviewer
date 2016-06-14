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
package pl.otros.logview.pluginable;

import pl.otros.logview.api.pluginable.PluginableElement;
import pl.otros.logview.api.pluginable.PluginableElementEventListener;
import pl.otros.logview.api.pluginable.PluginableElementsContainer;

public class SynchronizePluginableContainerListener<T extends PluginableElement> implements PluginableElementEventListener<T> {

  protected PluginableElementsContainer<T> destination;

  public SynchronizePluginableContainerListener(PluginableElementsContainer<T> destination) {
    this.destination = destination;
  }

  @Override
  public void elementAdded(T element) {
    destination.addElement(element);
  }

  @Override
  public void elementRemoved(T element) {
    destination.removeElement(element);
  }

  @Override
  public void elementChanged(T element) {
    destination.changeElement(element);
  }

}
