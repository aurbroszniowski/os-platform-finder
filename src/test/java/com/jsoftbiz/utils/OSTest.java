/*
 * Copyright 2014 Aurélien Broszniowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jsoftbiz.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static com.jsoftbiz.utils.OS.OS;
import static com.jsoftbiz.utils.OS.OsInfo;

/**
 * @author Aurelien Broszniowski
 */

public class OSTest {

  @Test
  public void testReleaseFileWithLinuxPrettyName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    BufferedReader mockFile = reader("NAME=Fedora", "PRETTY_NAME=\"Fedora 17 (Beefy Miracle)\"", "VERSION_ID=17");

    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = OS.OS.readPlatformName(name, version, arch, mockFile);
    Assert.assertThat(osInfo.getName(), is(equalTo(name)));
    Assert.assertThat(osInfo.getVersion(), is(equalTo(version)));
    Assert.assertThat(osInfo.getArch(), is(equalTo(arch)));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Fedora 17 (Beefy Miracle)")));
  }

  @Test
  public void testReleaseFileWithOneLine() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    String line = "Fedora version 19";
    BufferedReader mockFile = reader(line);

    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = OS.readPlatformName(name, version, arch, mockFile);
    Assert.assertThat(osInfo.getName(), is(equalTo(name)));
    Assert.assertThat(osInfo.getVersion(), is(equalTo(version)));
    Assert.assertThat(osInfo.getArch(), is(equalTo(arch)));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo(line)));
  }

  @Test
  public void testReleaseFileWithTwoLines() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    BufferedReader mockFile = reader("Fedora version 19", "second line");

    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = OS.readPlatformName(name, version, arch, mockFile);
    Assert.assertThat(osInfo.getName(), is(equalTo(name)));
    Assert.assertThat(osInfo.getVersion(), is(equalTo(version)));
    Assert.assertThat(osInfo.getArch(), is(equalTo(arch)));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Fedora version 19")));
  }

  @Test
  public void testLsbRelease() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
    BufferedReader mockFile = reader("DISTRIB_ID=Ubuntu", "DISTRIB_RELEASE=9.10", "DISTRIB_CODENAME=karmic",
        "DISTRIB_DESCRIPTION=\"Ubuntu 9.10\"");

    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = OS.OS.readPlatformNameFromLsb(name, version, arch, mockFile);
    Assert.assertThat(osInfo.getName(), is(equalTo(name)));
    Assert.assertThat(osInfo.getVersion(), is(equalTo(version)));
    Assert.assertThat(osInfo.getArch(), is(equalTo(arch)));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Ubuntu 9.10 (karmic)")));
  }

  @Test
  public void testReleaseFileWithUnquotedPrettyName() throws IOException {
    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = OS.readPlatformName(name, version, arch, reader("PRETTY_NAME=Fedora 17 (Beefy Miracle)"));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Fedora 17 (Beefy Miracle)")));
  }

  @Test
  public void testReleaseFileWithSingleQuotedPrettyName() throws IOException {
    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = OS.readPlatformName(name, version, arch, reader("PRETTY_NAME='Fedora 17 (Beefy Miracle)'"));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Fedora 17 (Beefy Miracle)")));
  }

  @Test
  public void testEmptyReleaseFileReturnsNull() throws IOException {
    OsInfo osInfo = OS.readPlatformName("some name", "4.1.4", "68000", reader());
    Assert.assertThat(osInfo, is(equalTo(null)));
  }

  @Test
  public void testOsReleaseWithoutId() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = invokeReadPlatformNameFromOsRelease(name, version, arch, reader("NAME=\"Distroless\"", "VERSION=\"1.0\""));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Distroless 1.0")));
  }

  @Test
  public void testOsReleaseWithVersionIdFallback() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = invokeReadPlatformNameFromOsRelease(name, version, arch, reader("NAME=\"Alpine Linux\"", "ID=alpine", "VERSION_ID=3.13.5"));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Alpine Linux 3.13.5 (alpine)")));
  }

  @Test
  public void testOsReleaseWithVersionIdAndCodenameFallback() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    String name = "some name";
    String version = "4.1.4";
    String arch = "68000";
    OsInfo osInfo = invokeReadPlatformNameFromOsRelease(name, version, arch, reader("NAME=\"Debian GNU/Linux\"", "ID=debian", "VERSION_ID=\"10\"", "VERSION_CODENAME=buster"));
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo("Debian GNU/Linux 10 (buster) (debian)")));
  }

  @Test
  public void testMacOsRecentVersions() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    assertMacOsPlatformName("14.7.6", "OS X Sonoma (14.7.6)");
    assertMacOsPlatformName("15.5", "OS X Sequoia (15.5)");
    assertMacOsPlatformName("26.0", "OS X Tahoe (26.0)");
  }

  @Test
  public void testDarwinRecentVersions() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    assertDarwinPlatformName("23.6.0", "OS X Sonoma (23.6.0)");
    assertDarwinPlatformName("24.5.0", "OS X Sequoia (24.5.0)");
    assertDarwinPlatformName("25.0.0", "OS X Tahoe (25.0.0)");
  }

  @Test
  public void testMalformedMacOsVersionsAreUnknown() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    assertMacOsPlatformName("", "OS X unknown ()");
    assertMacOsPlatformName("Tahoe", "OS X unknown (Tahoe)");
    assertDarwinPlatformName("", "OS X unknown ()");
    assertDarwinPlatformName("Tahoe", "OS X unknown (Tahoe)");
  }

  @Test
  public void testDeprecatedGetOsReturnsPublicSingleton() {
    Assert.assertThat(com.jsoftbiz.utils.OS.getOs(), is(equalTo(OS)));
  }

  @Test
  public void testDarwinOsInfoIsMacAndPosix() {
    OsInfo darwin = new OsInfo("Darwin", "25.0.0", "x86_64", "OS X Tahoe (25.0.0)");
    Assert.assertThat(darwin.isMac(), is(equalTo(true)));
    Assert.assertThat(darwin.isUnix(), is(equalTo(false)));
    Assert.assertThat(OS.isPosix(), is(equalTo(true)));
  }

  private void assertMacOsPlatformName(String version, String expectedPlatformName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    OsInfo osInfo = invokeOsInfoInitializer("initMacOsInfo", "Mac OS X", version, "x86_64");
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo(expectedPlatformName)));
  }

  private void assertDarwinPlatformName(String version, String expectedPlatformName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    OsInfo osInfo = invokeOsInfoInitializer("initDarwinOsInfo", "Darwin", version, "x86_64");
    Assert.assertThat(osInfo.getPlatformName(), is(equalTo(expectedPlatformName)));
  }

  private OsInfo invokeOsInfoInitializer(String methodName, String name, String version, String arch) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = com.jsoftbiz.utils.OS.class.getDeclaredMethod(methodName, String.class, String.class, String.class);
    method.setAccessible(true);
    return (OsInfo)method.invoke(OS, name, version, arch);
  }

  private OsInfo invokeReadPlatformNameFromOsRelease(String name, String version, String arch, BufferedReader br) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = com.jsoftbiz.utils.OS.class.getDeclaredMethod("readPlatformNameFromOsRelease", String.class, String.class, String.class, BufferedReader.class);
    method.setAccessible(true);
    return (OsInfo)method.invoke(OS, name, version, arch, br);
  }

  private BufferedReader reader(String... lines) {
    StringBuilder content = new StringBuilder();
    for (String line : lines) {
      content.append(line).append('\n');
    }
    return new BufferedReader(new StringReader(content.toString()));
  }

}
