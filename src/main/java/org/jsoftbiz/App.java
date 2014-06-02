package org.jsoftbiz;


import org.jsoftbiz.utils.OS;

public class App {

  public static void main(String[] args) {
    OS myOS = OS.getOs();
    System.out.println("Your OS is :");
    System.out.println(" - Platform name = " + myOS.getPlatformName());
    System.out.println(" - OS name = " + myOS.getName());
    System.out.println(" - OS version = " + myOS.getVersion());
    System.out.println(" - OS architecture = " + myOS.getArch());
  }
}
