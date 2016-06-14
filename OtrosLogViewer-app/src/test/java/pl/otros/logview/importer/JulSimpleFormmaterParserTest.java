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
package pl.otros.logview.importer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.otros.logview.api.model.LogData;
import pl.otros.logview.api.model.LogDataCollector;
import pl.otros.logview.TestUtils;
import pl.otros.logview.api.importer.LogImporterUsingParser;
import pl.otros.logview.parser.JulSimpleFormatterParser;
import pl.otros.logview.api.parser.ParsingContext;
import pl.otros.logview.api.reader.ProxyLogDataCollector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class JulSimpleFormmaterParserTest {

  private LogImporterUsingParser importerUsingParser;

  @BeforeMethod
  public void init() {
    JulSimpleFormatterParser parser = new JulSimpleFormatterParser();
    importerUsingParser = new LogImporterUsingParser(parser);
  }

  @Test
  public void testLocalesEnUs() throws IOException {
    importLogs(new Locale("en", "US"), false);
  }

  @Test
  public void testLocalesPlPL() throws IOException {
    importLogs(new Locale("pl", "PL"), false);
  }

  @Test
  public void testLocalesDeDe() throws IOException {
    importLogs(new Locale("de", "DE"), false);
  }

  @Test
  public void testLocalesEsEs() throws IOException {
    importLogs(new Locale("es", "ES"), false);
  }

  @Test
  public void testLocalesEnGb() throws IOException {
    importLogs(new Locale("en", "GB"), false);
  }

  @Test
  public void testTailLocalesEnUs() throws IOException {
    importLogs(new Locale("en", "US"), true);
  }

  @Test
  public void testTailLocalesPlPL() throws IOException {
    importLogs(new Locale("pl", "PL"), true);
  }

  @Test
  public void testTailLocalesDeDe() throws IOException {
    importLogs(new Locale("de", "DE"), true);
  }

  @Test
  public void testTailLocalesEsEs() throws IOException {
    importLogs(new Locale("es", "ES"), true);
  }

  @Test
  public void testTailLocalesEnGb() throws IOException {
    importLogs(new Locale("en", "GB"), true);
  }


  private LogDataCollector importLogs(Locale locale, boolean tail) throws IOException {
    String s = locale.toString();
    String resName = "log_" + s + ".txt";
    InputStream in = this.getClass().getClassLoader().getResourceAsStream(resName);

    assertNotNull("Log input stream from " + resName, in);
    ProxyLogDataCollector collector = new ProxyLogDataCollector();
    ParsingContext parsingContext = new ParsingContext(resName, resName);
    importerUsingParser.initParsingContext(parsingContext);
    if (tail) {
      TestUtils.tailLog(importerUsingParser, in, collector, parsingContext);
    } else {
      importerUsingParser.importLogs(in, collector, parsingContext);
    }

    LogData[] logDatas = collector.getLogData();

    assertEquals("Logs loaded", 10, logDatas.length);

    assertEquals(logDatas[0].getLogSource(), parsingContext.getLogSource());

    for (LogData logData : logDatas) {
      assertEquals(logData.getLogSource(), parsingContext.getLogSource());
    }

    return collector;
  }


}
