# CFML PGP Library
This is the PGP implementation for use with CFML engines like Adobe ColdFusion, Railo, and Lucee.

Pretty Good Privacy (PGP) is a computer program that provides cryptographic privacy and authentication. 
PGP is often used for signing, encrypting and decrypting e-mails to increase the security of e-mail communications. 
It was created by Philip Zimmermann in 1991.

The use of PGP in file exchanges has increased over the years and has become a common way of securing file contents. Thus, encountering use of PGP in projects has become more common as well.

There are several implementations for popular platforms such as Java and .net; however the availability of easy to implement PGP solutions for ColdFusion is limited. In particular, I found existing implementation rather difficult to use.
Thus, I embarked on this project. To make a long story short, I ended up using the underlying work of the league of bouncy castle (bouncycastle.org) folks. The complete source code for those libraries can be downloaded from there.

I am packaging this in an installer for Windows as the installation is slightly more complex than I would like. There are instructions for manual installation if you do not use Windows OS or want to do this yourself.

Sample code should show most common operations. I have not exposed all areas of PGP. 

Please read the &quot;Readme Manual Deploy.txt&quot; file for manual installs.

## RAILO Users:
-----------
There is a library conflict based on older versions of certain jars distributed with Railo, please remove them and copy the newer ones from this distribution to make things work without errors.
Railo uses version 1.36 of the BouncyCastle libraries, the BonCode Library requires 1.46, and the current version is 1.51.
a) remove: {railo-root}/lib/ext/bcprov-jdk14.jar
b) Add 1.46 or later libraries to {webroot}/WEB-INF/railo/lib/

## Version 2 added:
Updated underlying libraries to latest release.
Added support for Unicode file names.
Added support for Single Pass Signed file creation.
Improved Examples and made them easier to use.
Improved release of file resources (locks).

To upgrade uninstall (remove jars), and install (copy) new jars. Or use installer.
Last Update:
Added installer support for ColdFusion 10.
Requirements:
This has been tested for CF 8,CF 9, CF10 and Railo 3.x
You can use the PGP libraries in Flex as well if you backend it with ColdFusion.
Simply change the Access Modifier of the functions in the main controller cfc.

## issues
To report issues use the github issue tracker.




