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
import java.util.Comparator;
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
 * - macOS
 * - Solaris
 * <br>
 * TODO : HP-UX / z/OS
 *
 * @author Aurelien Broszniowski
 * @see <a href="https://www.jsoft.biz">https://www.jsoft.biz</a>
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
    MAC_OS.put("14", "Sonoma");
    MAC_OS.put("15", "Sequoia");
    MAC_OS.put("26", "Tahoe");

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
    DARWIN.put(20, "Big Sur");
    DARWIN.put(21, "Monterey");
    DARWIN.put(22, "Ventura");
    DARWIN.put(23, "Sonoma");
    DARWIN.put(24, "Sequoia");
    DARWIN.put(25, "Tahoe");

    UNIX.addAll(Arrays.asList("Linux", "SunOS", "FreeBSD", "AIX"));

    OS = new OS();
  }

  @Deprecated
  public static OS getOs() {
    return OS;
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
    int numericVersion = parseMajorVersion(version);
    if (numericVersion < 0) {
      return new OsInfo(name, version, arch, "OS X unknown (" + version + ")");
    }

    String versionKey = getMajorMinorVersion(version);

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
    int numericVersion = parseMajorVersion(version);
    String versionName = DARWIN.containsKey(numericVersion) ? DARWIN.get(numericVersion) : "unknown";
    return new OsInfo(name, version, arch, "OS X " + versionName + " (" + version + ")");
  }

  private int parseMajorVersion(final String version) {
    if (version == null || version.length() == 0) {
      return -1;
    }
    int dotIndex = version.indexOf('.');
    String majorVersion = dotIndex < 0 ? version : version.substring(0, dotIndex);
    try {
      return Integer.parseInt(majorVersion);
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private String getMajorMinorVersion(final String version) {
    if (version == null) {
      return null;
    }
    int firstDotIndex = version.indexOf('.');
    if (firstDotIndex < 0) {
      return version;
    }
    int secondDotIndex = version.indexOf('.', firstDotIndex + 1);
    return secondDotIndex < 0 ? version : version.substring(0, secondDotIndex);
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
    if (fileList == null) {
      return null;
    }
    Arrays.sort(fileList, new Comparator<File>() {
      public int compare(File first, File second) {
        return first.getName().compareTo(second.getName());
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
      try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
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
      String prettyName = parseKeyValue(line, "PRETTY_NAME");
      if (prettyName != null) {
        return new OsInfo(name, version, arch, prettyName);
      }
    }
    if (lineToReturn == null) {
      return null;
    }
    return new OsInfo(name, version, arch, lineToReturn);
  }

  private OsInfo getPlatformNameFromOsRelease(final String name, final String version, final String arch) {
    String fileName = "/etc/os-release";
    File f = new File(fileName);
    if (f.exists()) {
      try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
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
    boolean hasPlatformData = false;

    String line;
    while ((line = br.readLine()) != null) {
      String parsedName = parseKeyValue(line, "NAME");
      if (parsedName != null) {
        distribName = parsedName;
        hasPlatformData = true;
      }
      String parsedVersion = parseKeyValue(line, "VERSION");
      if (parsedVersion != null) {
        distribVersion = parsedVersion + " ";
        hasPlatformData = true;
      }
      String parsedId = parseKeyValue(line, "ID");
      if (parsedId != null) {
        distribId = parsedId;
      }
    }
    if (distribId != null) {
      return new OsInfo(name, version, arch, distribName + " " + distribVersion + "(" + distribId + ")");
    }
    if (hasPlatformData) {
      return new OsInfo(name, version, arch, (distribName + " " + distribVersion).trim());
    }
    return null;
  }

  private OsInfo getPlatformNameFromLsbRelease(final String name, final String version, final String arch) {
    String fileName = "/etc/lsb-release";
    File f = new File(fileName);
    if (f.exists()) {
      try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
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
      String parsedDescription = parseKeyValue(line, "DISTRIB_DESCRIPTION");
      if (parsedDescription != null) {
        distribDescription = parsedDescription;
      }
      String parsedCodename = parseKeyValue(line, "DISTRIB_CODENAME");
      if (parsedCodename != null) {
        distribCodename = parsedCodename;
      }
    }
    if (distribDescription != null && distribCodename != null) {
      return new OsInfo(name, version, arch, distribDescription + " (" + distribCodename + ")");
    }
    return null;
  }

  private String parseKeyValue(final String line, final String key) {
    String trimmedLine = line.trim();
    String keyPrefix = key + "=";
    if (!trimmedLine.startsWith(keyPrefix)) {
      return null;
    }
    return stripOptionalQuotes(trimmedLine.substring(keyPrefix.length()).trim());
  }

  private String stripOptionalQuotes(final String value) {
    if (value.length() < 2) {
      return value;
    }
    char firstChar = value.charAt(0);
    char lastChar = value.charAt(value.length() - 1);
    if ((firstChar == '"' && lastChar == '"') || (firstChar == '\'' && lastChar == '\'')) {
      return value.substring(1, value.length() - 1);
    }
    return value;
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

  public boolean isPosix() {
    return osInfo.isMac() || osInfo.isUnix();
  }

  static class OsInfo {
    private final String name;
    private final String arch;
    private final String version;
    private final String platformName;

    OsInfo(final String name, final String version, final String arch, final String platformName) {
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
      return (name != null) && name.startsWith("Windows");
    }

    public boolean isMac() {
      return (name != null) && (name.startsWith("Mac") || name.startsWith("Darwin"));
    }

    public boolean isUnix() {
      return (name != null) && (name.contains("nix") || name.contains("nux") || name.startsWith("AIX") || name.startsWith("FreeBSD") || name.startsWith("SunOS"));
    }
  }

}
