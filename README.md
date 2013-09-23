lircj
=====

Introduction
------------

LIRCJ provides a Java wrapper, using JUDS, to a LIRC daemon.

License
-------

Make sure you read and understand the README.LICENSE file. LIRC, and JUDS itself all use open source licenses. You must respect the terms of the GPL 3 license.

This software makes use of JUDS. You must respect the terms under which JUDS is licensed.

Pre-Requisites
--------------

You must have a properly installed and configured lirc installation, the best information I have found for this is here:

http://wiki.xbmc.org/index.php?title=How-to:Setup_Lirc

Since JUDS is not in any public Maven repository, you may need to build JUDS yourself. It is very easy to build JUDS, see:

https://github.com/mcfunley/juds

You may need to do the following if the build fails:

$sudo apt-get install libc6-dev-i386

You can install JUDS into your local Maven repository:

$mvn install:install-file -Dfile=juds-0.94.jar -DgroupId=com.etsy.net -DartifactId=juds -Dversion=0.94 -Dpackaging=jar

Distribution Contents
---------------------

The main artefacts are:

 * lircj-<version>.jar           - jar file for compiling and running against
 * lircj-<version>-sources.jar   - all source code
 * lircj-<version>-javadoc.jar   - generated javadoc API documentation

Usage
-----

There are only two classes, LircBridge and LircListener.

I encourage you to read the javadoc for that class.

You simply create an instance of the LircBridge and add your LircListener to it.
