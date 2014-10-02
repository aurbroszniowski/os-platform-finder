os-platform-finder
==================

Utility class to return the current OS Platform

The Problem?
------------
You can get OS information in java by using

```
    System.getProperty("os.name");
    System.getProperty("os.version");
    System.getProperty("os.arch");
```

Unfortunately you can not get the detail of the OS platform.
e.g. you can not get
```
Red Hat Enterprise Linux Server release 6.5 (Santiago)
```
or
```
OS X Mavericks (10.9.3)
```

The Solution?
-------------
The class **org.jsoftbiz.utils.OS** included in this project gives you the information of your OS platform

Just do
```
    import org.jsoftbiz.utils.OS;
    
    OS myOS = OS.getOs();
```

and you'll have access to the platform name, extra of the OS system properties:
```
    myOS.getPlatformName()

    myOS.getName()
    myOS.getVersion()
    myOS.getArch()
```

Wanna help?
-----------
I don't have access to all OSes, so if you want to help, look at the table below of tested platforms.
If you have access to one non-tested platform, just get this project, create a jar
```
   mvn clean package
```

it will be located in the target directory
```
   target/os-platform-finder-1.0.jar
```

copy the jar on your OS and execute it

```
   java -jar os-platform-finder-1.0.jar
```

If it is ok, you can do a pull request on this README.md with the updated info.

If it is not ok, then please open an issue, including the details of your OS and the output of the jar execution...

Thanks!


| OS            | Version       | Arch.     | Platform      | Status                  |
| ------------- |:-------------:|:---------:|:-------------:|:----------------------- |
| Windows       | all           | all       | Windows       | OK                      |
| Mac OS        | all           | all       | Mac OS        | OK                      |
| OS X          | all           | all       | OS X          | OK                      |
| Linux         | all           | all       | RedHat        | OK                      |
| Linux         | all           | all       | Linux Mint    | IMPLEMENTED, NOT TESTED |
| Linux         | all           | all       | Ubuntu        | OK                      |
| Linux         | all           | all       | Debian        | IMPLEMENTED, NOT TESTED |
| Linux         | all           | all       | Fedora        | OK |
| Linux         | all           | all       | openSUS       | IMPLEMENTED, NOT TESTED |
| Linux         | all           | all       | Arch Linux    | IMPLEMENTED, NOT TESTED |
| Linux         | all           | all       | CentOS        | OK                      |
| Linux         | all           | all       | Slackware     | IMPLEMENTED, NOT TESTED |
| Linux         | all           | all       | FreeBSD       | IMPLEMENTED, NOT TESTED |
| Solaris       | all           | all       |               | IMPLEMENTED, NOT TESTED |
| AIX           | all           | all       |               | NOT IMPLEMENTED         |


Known issues with :

| OS            | Version       | Arch.     | Platform      | Status                  |
| ------------- |:-------------:|:---------:|:-------------:|:----------------------- |
|               |               |           |               |                         |
|               |               |           |               |                         |

Author
------
Aurélien Broszniowski  - [JSoft.biz](http://www.jsoft.biz)
