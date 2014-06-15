package org.jsoftbiz.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Aurelien Broszniowski
 */

public class OSTest {

  /**
   * Those tests below are to verify the logic of the private method that returns the Distro info from one of the
   * system files.
   * I'm using a Reflection hack here to test the private method, because I want to keep that method in that class.
   * I want everything in one single class since this utility class is meant to be copied in other projects, and not
   * included as a jar.
   * We'll see if this gets too complicated in the future
   */
  @Test
  public void testReleaseFileWithLinuxPrettyName() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    Method method = getPrivateMethodToTest("readPlatformName");

    BufferedReader mockFile = mock(BufferedReader.class);
    when(mockFile.readLine()).thenReturn("NAME=Fedora", "PRETTY_NAME=\"Fedora 17 (Beefy Miracle)\"", "VERSION_ID=17", null);
    String platformName = (String)method.invoke(method, mockFile);
    Assert.assertThat(platformName, is(equalTo("Fedora 17 (Beefy Miracle)")));
  }

  @Test
  public void testReleaseFileWithOneLine() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    Method method = getPrivateMethodToTest("readPlatformName");

    BufferedReader mockFile = mock(BufferedReader.class);
    when(mockFile.readLine()).thenReturn("Fedora version 19", null);
    String platformName = (String)method.invoke(method, mockFile);
    Assert.assertThat(platformName, is(equalTo("Fedora version 19")));
  }

  @Test
  public void testReleaseFileWithTwoLines() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    Method method = getPrivateMethodToTest("readPlatformName");

    BufferedReader mockFile = mock(BufferedReader.class);
    when(mockFile.readLine()).thenReturn("Fedora version 19", "second line", null);
    String platformName = (String)method.invoke(method, mockFile);
    Assert.assertThat(platformName, is(equalTo("Fedora version 19")));
  }

  @Test
  public void testLsbRelease() throws NoSuchMethodException, IOException, InvocationTargetException, IllegalAccessException {
    Method method = getPrivateMethodToTest("readPlatformNameFromLsb");

    BufferedReader mockFile = mock(BufferedReader.class);
    when(mockFile.readLine()).thenReturn("DISTRIB_ID=Ubuntu", "DISTRIB_RELEASE=9.10", "DISTRIB_CODENAME=karmic",
        "DISTRIB_DESCRIPTION=\"Ubuntu 9.10\"", null);
    String platformName = (String)method.invoke(method, mockFile);
    Assert.assertThat(platformName, is(equalTo("Ubuntu 9.10 (karmic)")));
  }

  private Method getPrivateMethodToTest(String methodName) throws NoSuchMethodException {
    Class params[] = new Class[1];
    params[0] = BufferedReader.class;
    Method method = OS.class.getDeclaredMethod(methodName, params);
    method.setAccessible(true);
    return method;
  }
}
