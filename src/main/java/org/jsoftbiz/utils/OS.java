package org.jsoftbiz.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is made to get the OS platform name, e.g.
 * <p/>
 * Red Hat Enterprise Linux Server release 5.6 (Tikanga)
 * <p/>
 * <p/>
 * It currently supports :
 * - Linux
 * - Windows
 * - Mac OS
 * <p/>
 * TODO : Sun OS, AIX
 *
 * @author Aurelien Broszniowski
 *         http://www.jsoft.biz
 */

public class OS {

  private String name;
  private String arch;
  private String version;
  private String platformName;

  public OS(final String name, final String version, final String arch, final String platformName) {
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

  private static final Map<Double, String> macOs = new HashMap<Double, String>();
  private static final Map<Integer, String> darwin = new HashMap<Integer, String>();
  private static final Map<String, String> linux = new HashMap<String, String>();

  static {
    macOs.put(10.0, "Puma");
    macOs.put(10.1, "Cheetah");
    macOs.put(10.2, "Jaguar");
    macOs.put(10.3, "Panther");
    macOs.put(10.4, "Tiger");
    macOs.put(10.5, "Leopard");
    macOs.put(10.6, "Snow Leopard");
    macOs.put(10.7, "Snow Lion");
    macOs.put(10.8, "Mountain Lion");
    macOs.put(10.9, "Mavericks");
    macOs.put(10.10, "Yosemite");

    darwin.put(5, "Puma");
    darwin.put(6, "Jaguar");
    darwin.put(7, "Panther");
    darwin.put(8, "Tiger");
    darwin.put(9, "Leopard");
    darwin.put(10, "Snow Leopard");
    darwin.put(11, "Lion");
    darwin.put(12, "Mountain Lion");
    darwin.put(13, "Mavericks");
    darwin.put(14, "Yosemite");

    linux.put("Annvix", "/etc/annvix-release");
  }

  public static OS getOs() {
    String name = System.getProperty("os.name");
    String version = System.getProperty("os.version");
    String arch = System.getProperty("os.arch");

    if (name != null) {
      // Windows is quite easy to tackle with
      if (name.startsWith("Windows")) {
        return new OS(name, version, arch, name);
      }

      // Mac requires a bit of work, but at least it's consistent
      if (name.startsWith("Mac")) {
        return returnMacOsInfo(name, version, arch);
      }

      if (name.startsWith("Darwin")) {
        return returnDarwinOsInfo(name, version, arch);
      }

      // Try to detect a unix platform
      if (name.startsWith("Linux")) {
        return returnLinuxOsInfo(name, version, arch);
      }
    }
    return new OS(name, version, arch, name);
  }

  private static OS returnDarwinOsInfo(final String name, final String version, final String arch) {
    String[] versions = version.split("\\.");
    int numericVersion = Integer.parseInt(versions[0]);
    return new OS(name, version, arch, "OS X " + darwin.get(numericVersion) + " (" + version + ")");
  }

  private static OS returnMacOsInfo(final String name, final String version, final String arch) {
    String[] versions = version.split("\\.");
    double numericVersion = Double.parseDouble(versions[0] + "." + versions[1]);
    if (numericVersion < 10)
      return new OS(name, version, arch, "Mac OS " + version);
    else
      return new OS(name, version, arch, "OS X " + macOs.get(numericVersion) + " (" + version + ")");
  }

  private static OS returnLinuxOsInfo(final String name, final String version, final String arch) {
    // Generic Linux platform name
    String platformName = readPlatformFromReleaseFile("/etc/system-release");
    if (platformName != null) return new OS(name, version, arch, platformName);

    File dir = new File("/etc/");
    if (dir.exists()) {
      // if generic 'system-release' file is not present, then try to find another one
      platformName = readPlatformFromReleaseFile(getFileEndingWith(dir, "-release"));
      if (platformName != null) return new OS(name, version, arch, platformName);

      // if generic 'system-release' file is not present, then try to find '_version'
      platformName = readPlatformFromReleaseFile(getFileEndingWith(dir, "_version"));
      if (platformName != null) return new OS(name, version, arch, platformName);

      // try with /etc/issue file
      platformName = readPlatformFromReleaseFile("/etc/issue");
      if (platformName != null) return new OS(name, version, arch, platformName);
    }

    // if nothing found yet, looks for the version file (not all linux distros)
    File fileVersion = new File("/proc/version");
    if (fileVersion.exists()) {
      platformName = readPlatformFromReleaseFile(fileVersion.getAbsolutePath());
      if (platformName != null) return new OS(name, version, arch, platformName);
    }
    return new OS(name, version, arch, name);
  }

  private static String getFileEndingWith(final File dir, final String fileEndingWith) {
    File[] fileList = dir.listFiles(new FilenameFilter() {
      public boolean accept(File dir, String filename) {
        return filename.endsWith(fileEndingWith);
      }
    });
    return fileList[0].getAbsolutePath();
  }

  private static String readPlatformFromReleaseFile(String filename) {
    File f = new File(filename);
    if (f.exists()) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        String lineToReturn = null;
        int lineNb = 0;
        while ((line = br.readLine()) != null) {
          if (lineNb++ == 1) {
            lineToReturn = line;
          }
          if (line.startsWith("PRETTY_NAME")) return line.substring(13, line.length() - 1);
        }
        return lineToReturn;
      } catch (IOException e) {
        return null;
      }
    }
    return null;
  }

}