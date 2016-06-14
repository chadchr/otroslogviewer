/*******************************************************************************
 * Copyright 2011 Krzysztof Otrebski
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
 ******************************************************************************/
package pl.otros.logview.gui.message;

import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

import static org.testng.AssertJUnit.assertEquals;

public class StackTraceFinderTest {

  private final StackTraceFinder finder = new StackTraceFinder();

  @Test
  public void testFindStackTraces() throws IOException {
    // given
    String stacktraceFile = IOUtils.toString(StackTraceColorizer.class.getClassLoader().getResourceAsStream("stacktrace/stacktrace.txt"));

    // when
    SortedSet<SubText> findStackTraces = finder.findStackTraces(stacktraceFile);

    // then
    assertEquals(1, findStackTraces.size());

    SubText subtext = findStackTraces.iterator().next();
    assertEquals(10, subtext.getStart());
    assertEquals(241, subtext.getEnd());
  }

  @Test
  public void testFindInOnlyStackTrace() throws IOException {
    // given
    String stacktraceFile = IOUtils.toString(StackTraceColorizer.class.getClassLoader().getResourceAsStream("stacktrace/stacktTraceOnly.txt"));

    // when
    SortedSet<SubText> findStackTraces = finder.findStackTraces(stacktraceFile);

    // then
    assertEquals(1, findStackTraces.size());
    SubText subtext = findStackTraces.iterator().next();
    // TODO check range
    assertEquals(0, subtext.getStart());
    assertEquals(234, subtext.getEnd());

  }

  @Test
  public void testFindForEmptyPackage() throws IOException {
    // given
    String stacktraceFile = IOUtils.toString(StackTraceColorizer.class.getClassLoader().getResourceAsStream("stacktrace/stacktTraceWtihEmptyPackage.txt"));

    // when
    SortedSet<SubText> findStackTraces = finder.findStackTraces(stacktraceFile);

    // then
    assertEquals(1, findStackTraces.size());
    SubText subtext = findStackTraces.iterator().next();
    assertEquals(0, subtext.getStart());
    assertEquals(213, subtext.getEnd());
  }

  @Test
  public void testFind2StackTraces() throws IOException {
    // given
    String stacktraceFile = IOUtils.toString(StackTraceColorizer.class.getClassLoader().getResourceAsStream("stacktrace/stacktrace2.txt"));

    // when
    SortedSet<SubText> findStackTraces = finder.findStackTraces(stacktraceFile);

    // then
    assertEquals(2, findStackTraces.size());
    Iterator<SubText> iterator = findStackTraces.iterator();
    SubText subtext = iterator.next();
    assertEquals(44, subtext.getStart());
    assertEquals(3142, subtext.getEnd());
    subtext = iterator.next();
    assertEquals(3183, subtext.getStart());
    assertEquals(3439, subtext.getEnd());
  }
}
