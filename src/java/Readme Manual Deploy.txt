Instruction on Manual Deployment:
=================================
Bilal Soylu
12/2011
=================================
V.2


To manually install files you will need to copy files into different areas of your CF or Railo installation.

Important:
After you have copied the jar files you will need to restart your ColdFusion Application Server.


There are three different types of files to consider.

a) JAR files from Bouncy Castle and BonCode.
--------------------------------------------
You will need to download the version 146 for your version of the JVM (1.6 for CF 8 + 9, 1.4 for CF 7)
TO do this go to: http://bouncycastle.org/latest_releases.html

For JVM 1.6 you will need:
		bcpg-jdk16-146.jar
		bcprov-jdk16-146.jar


The BonCode jar file is already in this distribution, it is compiled for JVM 1.6; the source code is also here if you need to compile 
it for any other JVM.
For BonCode you will need:
		PGPController.jar

Deploy these jar files in the in
Adobe COldfusion : [cfroot]runtime\lib
Railo :            WEB-INF\lib


b) Unrestricted profile files for your JVM.
-------------------------------------------
The local and export policy files as installed with CF are restrictive; for most cryptography you may want to exchange these
policy files with the unrestricted files. Review the JCE subdirectory for Copyright and Export notices.
You will need to go to Sun's website to download these files:
GO to the "Other Downloads" section of the JDK download area. 
Download "Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 6"

Make backup of these files [cfroot]\runtime\jre\lib\security:
		local_policy.jar
		US_export_policy.jar

Policy files need to be deployed in the following folders:	
	Adobe Coldfusion: [cfroot]\runtime\jre\lib\security
	Railo: [jreroot]\lib\security


c) CFML files
-------------------

There is one CFC which handles all interactions with the java classes.
There are sample files to show how to use the cfc.
You can copy these into any web directory in which ColdFusion files will run.
If you use the installer on Windows, the installer would place these files into 
[webroot]\BonCodePGP directory.




