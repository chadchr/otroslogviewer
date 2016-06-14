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
package pl.otros.logview.gui;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;
import org.pushingpixels.trident.ease.Sine;
import pl.otros.logview.api.StatusObserver;

import javax.swing.*;
import java.awt.*;

public class JLabelStatusObserver implements StatusObserver {

  private final Color colorNormal;
  private final Color colorWarning = Color.YELLOW;
  private final Color colorError = Color.RED;
  private final JLabel label;

  public JLabelStatusObserver(JLabel label) {
    super();
    this.label = label;
    colorNormal = label.getBackground();
    label.setOpaque(true);
  }

  @Override
  public void updateStatus(String text) {
    updateStatus(text, StatusObserver.LEVEL_NORMAL);

  }

  @Override
  public void updateStatus(String text, int level) {
    label.setText(text);
    if (level == LEVEL_NORMAL) {
      label.setBackground(colorNormal);
    } else {
      Color blinkColor = (level == LEVEL_WARNING) ? colorWarning : colorError;
      Timeline timeline = new Timeline(label);
      timeline.setDuration(200);
      timeline.setEase(new Sine());
      timeline.addPropertyToInterpolate("background", colorNormal, blinkColor);
      timeline.playLoop(8, RepeatBehavior.REVERSE);
    }
  }

}
