/*
 * Copyright 2012 Krzysztof Otrebski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.otros.logview.gui.actions;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.DataConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;
import pl.otros.logview.api.ConfKeys;
import pl.otros.logview.api.OtrosApplication;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Krzysztof Otrebski
 * Date: 4/2/12
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectToSocketHubAppenderActionTest {


  @Test
  public void tryToConnect() throws IOException {
    OtrosApplication otrosApplication = new OtrosApplication();
    ConnectToSocketHubAppenderAction action = new ConnectToSocketHubAppenderAction(otrosApplication);
    DataConfiguration dc = new DataConfiguration(new BaseConfiguration());
    String hostAndPort = "abc:50";
    SocketFactory socketFactory = mock(SocketFactory.class);
    Socket mockSocket = mock(Socket.class);
    when(socketFactory.createSocket("abc", 50)).thenReturn(mockSocket);

    Socket socket = action.tryToConnectToSocket(dc, hostAndPort, socketFactory);

    assertEquals(mockSocket, socket);
    assertEquals(1, dc.getList(ConfKeys.SOCKET_HUB_APPENDER_ADDRESSES).size());
    assertEquals("abc:50", dc.getList(ConfKeys.SOCKET_HUB_APPENDER_ADDRESSES).get(0));
  }

  @Test
  public void tryToConnectFail() throws IOException {
    OtrosApplication otrosApplication = new OtrosApplication();
    ConnectToSocketHubAppenderAction action = new ConnectToSocketHubAppenderAction(otrosApplication);
    DataConfiguration dc = new DataConfiguration(new BaseConfiguration());
    String hostAndPort = "abc:50";
    SocketFactory socketFactory = mock(SocketFactory.class);
    when(socketFactory.createSocket("abc", 50)).thenThrow(new UnknownHostException());

    try {
      action.tryToConnectToSocket(dc, hostAndPort, socketFactory);
      Assert.fail();
    } catch (UnknownHostException e) {
      //success
    }

    assertEquals(0, dc.getList(ConfKeys.SOCKET_HUB_APPENDER_ADDRESSES).size());
  }
}
