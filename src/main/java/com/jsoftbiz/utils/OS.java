/*
 * Copyright 2014-2023 Aurélien Broszniowski
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is made to get the OS platform name, e.g.
 * <br>
 * Red Hat Enterprise Linux Server release 5.6 (Tikanga)
 * <br>
 * <br>
 * It currently supports :
 * - Linux
 * - Windows
 * - Mac OS
 * - Solaris
 * <br>
 * TODO :  P-UX / ZOS
 *
 * @author Aurelien Broszniowski
 * http://www.jsoftbiz.com
 */

public class OS {

  public static final OS OS;

  private static final Map<String, String> MAC_OS = new HashMap<String, String>();
  private static final Map<Integer, String> DARWIN = new HashMap<Integer, String>();
  private static final List<String> UNIX = new ArrayList<String>();

  static {
    MAC_OS.put("10.0", "Puma");
    MAC_OS.put("10.1", "Cheetah");
    MAC_OS.put("10.2", "Jaguar");
    MAC_OS.put("10.3", "Panther");
    MAC_OS.put("10.4", "Tiger");
    MAC_OS.put("10.5", "Leopard");
    MAC_OS.put("10.6", "Snow Leopard");
    MAC_OS.put("10.7", "Lion");
    MAC_OS.put("10.8", "Mountain Lion");
    MAC_OS.put("10.9", "Mavericks");
    MAC_OS.put("10.10", "Yosemite");
    MAC_OS.put("10.11", "El Captain");
    MAC_OS.put("10.12", "Sierra");
    MAC_OS.put("10.13", "High Sierra");
    MAC_OS.put("10.14", "Mojave");
    MAC_OS.put("10.15", "Catalina");
    MAC_OS.put("10.16", "Big Sur"); // MacOS Big Sur referred to its version as "10.16" when upgrading from prior versions of macOS
    MAC_OS.put("11", "Big Sur");
    MAC_OS.put("12", "Monterey");
    MAC_OS.put("13", "Ventura");

    DARWIN.put(5, "Puma");
    DARWIN.put(6, "Jaguar");
    DARWIN.put(7, "Panther");
    DARWIN.put(8, "Tiger");
    DARWIN.put(9, "Leopard");
    DARWIN.put(10, "Snow Leopard");
    DARWIN.put(11, "Lion");
    DARWIN.put(12, "Mountain Lion");
    DARWIN.put(13, "Mavericks");
    DARWIN.put(14, "Yosemite");
    DARWIN.put(15, "El Captain");
    DARWIN.put(16, "Sierra");
    DARWIN.put(17, "High Sierra");
    DARWIN.put(18, "Mojave");
    DARWIN.put(19, "Catalina");

    UNIX.addAll(Arrays.asList("Linux", "SunOS", "FreeBSD", "AIX"));

    OS = new OS();
  }

  private static class SingletonHolder {
    private final static OS instance = new OS();
  }

  @Deprecated
  public static OS getOs() {
    return SingletonHolder.instance;
  }

  private final OsInfo osInfo;

  private OS() {
    String name = System.getProperty("os.name");
    String version = System.getProperty("os.version");
    String arch = System.getProperty("os.arch");
    OsInfo osInfo = null;
    if (name != null) {
      // Windows is quite easy to tackle with
      if (name.startsWith("Windows")) {
        osInfo = new OsInfo(name, version, arch, name);
      }
      // Mac requires a bit of work, but at least it's consistent
      else if (name.startsWith("Mac")) {
        osInfo = initMacOsInfo(name, version, arch);
      } else if (name.startsWith("Darwin")) {
        osInfo = initDarwinOsInfo(name, version, arch);
      }
      // Try to detect other POSIX compliant platforms, now the fun begins
      else {
        for (String unixName : UNIX) {
          if (name.startsWith(unixName)) {
            osInfo = initUnixOsInfo(name, version, arch);
          }
        }
      }
    }

    if (osInfo == null) {
      osInfo = new OsInfo(name, version, arch, name);
    }
    this.osInfo = osInfo;
  }

  public String getName() {
    return osInfo.getName();
  }

  public String getArch() {
    return osInfo.getArch();
  }

  public String getVersion() {
    return osInfo.getVersion();
  }

  public String getPlatformName() {
    return osInfo.getPlatformName();
  }

  @Override
  public String toString() {
    return "OS{" +
           "Name = " + osInfo.getName() + ", " +
           "Architecture = " + osInfo.getArch() + ", " +
           "Platform = " + osInfo.getPlatformName() + ", " +
           "Version = " + osInfo.getVersion() +
           '}';
  }

  private OsInfo initMacOsInfo(final String name, final String version, final String arch) {
    int dotIndex = version.indexOf('.');
    int numericVersion = Integer.parseInt(dotIndex < 0 ? version : version.substring(0, dotIndex));

    dotIndex = version.indexOf('.', dotIndex + 1);
    String versionKey = dotIndex < 3 ? version : version.substring(0, dotIndex);

    if (numericVersion < 10) {
      return new OsInfo(name, version, arch, "Mac OS " + version);
    } else if (numericVersion == 10) {
      String versionName = MAC_OS.containsKey(versionKey) ? MAC_OS.get(versionKey) : "unknown";
      return new OsInfo(name, version, arch, "OS X " + versionName + " (" + version + ")");
    } else {
      String versionName = MAC_OS.containsKey(String.valueOf(numericVersion)) ? MAC_OS.get(String.valueOf(numericVersion)) : "unknown";
      return new OsInfo(name, version, arch, "OS X " + versionName + " (" + version + ")");
    }
  }

  private OsInfo initDarwinOsInfo(final String name, final String version, final String arch) {
    String[] versions = version.split("\\.");
    int numericVersion = Integer.parseInt(versions[0]);
    String versionName = DARWIN.containsKey(numericVersion) ? DARWIN.get(numericVersion) : "unknown";
    return new OsInfo(name, version, arch, "OS X " + versionName + " (" + version + ")");
  }

  private OsInfo initUnixOsInfo(final String name, final String version, final String arch) {
    OsInfo osInfo;
    // The most likely is to have a LSB compliant distro
    osInfo = getPlatformNameFromLsbRelease(name, version, arch);

    // Try new /etc/os-release file from systemd
    if (osInfo == null) {
      osInfo = getPlatformNameFromOsRelease(name, version, arch);
    }

    // Generic Linux platform name
    if (osInfo == null) {
      osInfo = getPlatformNameFromFile(name, version, arch, "/etc/system-release");
    }

    File dir = new File("/etc/");
    if (dir.exists()) {
      // if generic 'system-release' file is not present, then try to find another one
      if (osInfo == null) {
        osInfo = getPlatformNameFromFile(name, version, arch, getFileEndingWith(dir, "-release"));
      }

      // if generic 'system-release' file is not present, then try to find '_version'
      if (osInfo == null) {
        osInfo = getPlatformNameFromFile(name, version, arch, getFileEndingWith(dir, "_version"));
      }

      // try with /etc/issue file
      if (osInfo == null) {
        osInfo = getPlatformNameFromFile(name, version, arch, "/etc/issue");
      }
    }

    // if nothing found yet, looks for the version info
    File fileVersion = new File("/proc/version");
    if (fileVersion.exists()) {
      if (osInfo == null) {
        osInfo = getPlatformNameFromFile(name, version, arch, fileVersion.getAbsolutePath());
      }
    }

    // if nothing found, well...
    if (osInfo == null) {
      osInfo = new OsInfo(name, version, arch, name);
    }

    return osInfo;
  }

  private String getFileEndingWith(final File dir, final String fileEndingWith) {
    File[] fileList = dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        return filename.endsWith(fileEndingWith);
      }
    });
    if (fileList.length > 0) {
      return fileList[0].getAbsolutePath();
    } else {
      return null;
    }
  }

  private OsInfo getPlatformNameFromFile(final String name, final String version, final String arch, final String filename) {
    if (filename == null) {
      return null;
    }
    File f = new File(filename);
    if (f.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        return readPlatformName(name, version, arch, br);
      } catch (IOException e) {
        return null;
      }
    }
    return null;
  }

  OsInfo readPlatformName(final String name, final String version, final String arch, final BufferedReader br) throws IOException {
    String line;
    String lineToReturn = null;
    int lineNb = 0;
    while ((line = br.readLine()) != null) {
      if (lineNb++ == 0) {
        lineToReturn = line;
      }
      if (line.startsWith("PRETTY_NAME")) return new OsInfo(name, version, arch, line.substring(13, line.length() - 1));
    }
    return new OsInfo(name, version, arch, lineToReturn);
  }

  private OsInfo getPlatformNameFromOsRelease(final String name, final String version, final String arch) {
    String fileName = "/etc/os-release";
    File f = new File(fileName);
    if (f.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        return readPlatformNameFromOsRelease(name, version, arch, br);
      } catch (IOException e) {
        return null;
      }
    }
    return null;
  }

  private OsInfo readPlatformNameFromOsRelease(final String name, final String version, final String arch, final BufferedReader br) throws IOException {
    String distribName = "Linux";
    String distribVersion = "";
    String distribId = null;

    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("NAME="))
        distribName = line.replace("NAME=", "").replace("\"", "");
      if (line.startsWith("VERSION="))
        distribVersion = line.replace("VERSION=", "").replace("\"", "") + " ";
      if (line.startsWith("ID="))
        distribId = line.replace("ID=", "").replace("\"", "");
    }
    if (distribId != null) {
      return new OsInfo(name, version, arch, distribName + " " + distribVersion + "(" + distribId + ")");
    }
    return null;
  }

  private OsInfo getPlatformNameFromLsbRelease(final String name, final String version, final String arch) {
    String fileName = "/etc/lsb-release";
    File f = new File(fileName);
    if (f.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        return readPlatformNameFromLsb(name, version, arch, br);
      } catch (IOException e) {
        return null;
      }
    }
    return null;
  }

  OsInfo readPlatformNameFromLsb(final String name, final String version, final String arch, final BufferedReader br) throws IOException {
    String distribDescription = null;
    String distribCodename = null;

    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("DISTRIB_DESCRIPTION"))
        distribDescription = line.replace("DISTRIB_DESCRIPTION=", "").replace("\"", "");
      if (line.startsWith("DISTRIB_CODENAME")) distribCodename = line.replace("DISTRIB_CODENAME=", "");
    }
    if (distribDescription != null && distribCodename != null) {
      return new OsInfo(name, version, arch, distribDescription + " (" + distribCodename + ")");
    }
    return null;
  }

  public String getShellExtension() {
    if (isWindows()) {
      return ".bat";
    } else if (isPosix()) {
      return ".sh";
    } else {
      throw new RuntimeException("Can not detect OS");
    }
  }

  public boolean isWindows() {
    return osInfo.isWindows();
  }

  public boolean isMac() {
    return osInfo.isMac();
  }

  public boolean isUnix() {
    return osInfo.isUnix();
  }

  public Boolean isPosix() {
    return osInfo.isMac() || osInfo.isUnix();
  }

  static class OsInfo {
    private String name;
    private String arch;
    private String version;
    private String platformName;

    private OsInfo(final String name, final String version, final String arch, final String platformName) {
      this.name = name;
      this.arch = arch;
      this.version = version;
      this.platformName = platformName;
    }

    public String getName() {
      return name;
    }

    public String getArch() {
      return arch;
    }

    public String getVersion() {
      return version;
    }

    public String getPlatformName() {
      return platformName;
    }

    public boolean isWindows() {
      return (name != null) && name.contains("Win");
    }

    public boolean isMac() {
      return (name != null) && name.contains("Mac");
    }

    public boolean isUnix() {
      return (name != null) && (name.contains("nix") || name.contains("nux") || name.contains("AIX") || name.contains("FreeBSD"));
    }
  }

}