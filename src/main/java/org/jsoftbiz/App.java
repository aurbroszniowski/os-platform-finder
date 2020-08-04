/*
 * Copyright 2014-2020 Aurélien Broszniowski
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

package org.jsoftbiz;

import org.jsoftbiz.utils.OS;

public class App {

  public static void main(String[] args) {

    System.out.println("name " + System.getProperty("os.name"));
    System.out.println("version " + System.getProperty("os.version"));
    System.out.println("arch " + System.getProperty("os.arch"));


    OS myOS = OS.OS;
    System.out.println("Your OS is :");
    System.out.println(" - Platform name = " + myOS.getPlatformName());
    System.out.println(" - OS name = " + myOS.getName());
    System.out.println(" - OS version = " + myOS.getVersion());
    System.out.println(" - OS architecture = " + myOS.getArch());
  }
}
