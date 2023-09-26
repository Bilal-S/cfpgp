<!---
	Component for handling PGP (pretty good privacy) functions.
	This component requires that the related java libraries have been
	placed into the correct folder, e.g.
	
	[cfroot]runtime\lib for Adobe Coldfusion
	
	WEB-INF\lib for Railo
	
	Java Libraries:
		bcpg-jdk16-146.jar
		bcprov-jdk16-146.jar
		PGPController.jar
		
	If high encryption is needed (which is common) you will also need
	to update the policy files with the unrestricted policy files from Sun/Oracle for 
	your JVM
	Policy Files are located normally here
	
	[cfroot]\runtime\jre\lib\security
	
	Replace these files:
		local_policy.jar
		US_export_policy.jar
	
	
	Creative Common License v.2
	(c) 2011 Bilal Soylu
 --->
<cfcomponent displayname="BonCode PGPController" hint="BonCode PGPController" output="false">

	<!--- contructor, if this errors out we have not correctly installed PGP jar files --->
	<cfscript>
		objC = CreateObject("java","net.boncode.crypto.PGPController");
	</cfscript>
	
	<cffunction name="fGenerateRings" access="public" returntype="string" hint="Generates PGP public/private  Ring Files pairs. Will overwrite existing files with same name.">
		<cfargument name="privatePath" type="string" required="true" hint="the file path to use for saving the generated private key ring file">
		<cfargument name="publicPath" type="string" required="true" hint="the file path to use for saving the generated public key ring file">
		<cfargument name="passPhrase" type="string" required="true" hint="the passphrase to use for the private key">
		<cfargument name="keyID" type="string" required="false" default="BonCode PGP Key" hint="the identity for the Key. Should be something meaningfull">
		<cfargument name="keyLength" default="1024" type="numeric" hint="Key length, an indicator of encryption strength, between 512 and 1024 in steps of 64">
		
		<cfset var strReturn="">
		
		<cftry>
			<!--- generate directories --->
			<cfif Not DirectoryExists(GetDirectoryFromPath(Arguments.privatePath))>
				<cfdirectory action="create" directory="#GetDirectoryFromPath(Arguments.privatePath)#">
			</cfif>
			<cfif Not DirectoryExists(GetDirectoryFromPath(Arguments.publicPath))>
				<cfdirectory action="create" directory="#GetDirectoryFromPath(Arguments.publicPath)#">
			</cfif>			
			<!--- interact with java classes --->
			<cfscript>
				//set options
				objC.setPrivatePath(Arguments.privatePath);
				objC.setPublicPath(Arguments.publicPath);
				objC.setKeyIdentity(Arguments.keyID);
				objC.setKeyLength(Arguments.keyLength);
				objC.setKeyPassphrase(Arguments.passPhrase);
				//call ring generation
				strReturn=objC.fGenerateRings();
			</cfscript>
			
			
			<cfcatch type="any">
				<cfset strReturn = "Error (fGenerateRings):" & cfcatch.Message & "-" & cfcatch.Detail>
			</cfcatch>
	
		</cftry>
		
		<cfreturn strReturn>
	</cffunction>


	<cffunction name="fGenerateRSAKeys" access="public" returntype="string" hint="Generates PGP RSA public/private Key File pair. Will overwrite existing files with same name.">
		<cfargument name="privatePath" type="string" required="true" hint="the file path to use for saving the generated private key file">
		<cfargument name="publicPath" type="string" required="true" hint="the file path to use for saving the generated public key file">
		<cfargument name="passPhrase" type="string" required="true" hint="the passphrase to use for the private key">
		<cfargument name="keyID" type="string" required="false" default="BonCode PGP Key" hint="the identity for the Key. Should be something meaningfull">
		<cfargument name="keyLength" default="1024" type="numeric" hint="Key length, an indicator of encryption strength; longer keys will require considerable amount of time to generate.">
		
		<cfset var strReturn="">
		
		<cftry>
			<!--- generate directories --->
			<cfif Not DirectoryExists(GetDirectoryFromPath(Arguments.privatePath))>
				<cfdirectory action="create" directory="#GetDirectoryFromPath(Arguments.privatePath)#">
			</cfif>
			<cfif Not DirectoryExists(GetDirectoryFromPath(Arguments.publicPath))>
				<cfdirectory action="create" directory="#GetDirectoryFromPath(Arguments.publicPath)#">
			</cfif>			
			<!--- interact with java classes --->
			<cfscript>
				//set options
				objC.setPrivatePath(Arguments.privatePath);
				objC.setPublicPath(Arguments.publicPath);
				objC.setKeyIdentity(Arguments.keyID);
				objC.setKeyLength(Arguments.keyLength);
				objC.setKeyPassphrase(Arguments.passPhrase);
				//call key generation
				strReturn=objC.fGenerateRSAKeys();
			</cfscript>
			
			
			<cfcatch type="any">
				<cfset strReturn = "Error (fGenerateRSAKeys):" & cfcatch.Message & "-" & cfcatch.Detail>
			</cfcatch>
	
		</cftry>
		
		<cfreturn strReturn>
	</cffunction>
	
	
	
	<cffunction name="fEncryptSimpleFile" access="public" returntype="string" hint="Encrypt a file using pgp public key information. Returns error string or empty string if success.">
		<cfargument name="contentFile" type="string" required="true" hint="the file path to the file to be encrypted.">
		<cfargument name="publicKeyPath" type="string" required="true" hint="the file path to the public key (ring) file. Normally with .asc extension. If multiple keys are in the file, we will use first public key.">
		<cfargument name="targetFile" type="string" default="" required="false" hint="optional. The encrypted file name to use. If not provided the clear text file with .asc extension is output. Existing files will be overwritten.">

		
		<cfset var strReturn="">		
		
		<cftry>
			<cfscript>
				//check inputs
				if (Not FileExists(Arguments.contentFile)) strReturn="The unencrypted file[#Arguments.contentFile#] could not be found."; 
				if (Not FileExists(Arguments.publicKeyPath)) strReturn="The public key file[#Arguments.publicKeyPath#] could not be found."; 
					
				//interact with java
				if (strReturn IS "") {
					//set options					
					objC.setPublicPath(Arguments.publicKeyPath);
					objC.setContentFile(Arguments.contentFile);					
					objC.setTargetFile(Arguments.targetFile);

					//call encryption
					strReturn=objC.fEncryptSimpleFile();
				};
			</cfscript>
			
			
			<cfcatch type="any">
				<cfset strReturn = "Error (fEncryptSimpleFile):" & cfcatch.Message & "-" & cfcatch.Detail>
			</cfcatch>
	
		</cftry>
		
		<cfreturn strReturn>
	</cffunction>	
	
	<cffunction name="fDecryptSimpleFile" access="public" returntype="string" hint="Decrypt a file using pgp private key information. Returns error string or empty string if success.">
		<cfargument name="contentFile" type="string" required="true" hint="the file path to the file to be decrypted.">
		<cfargument name="privateKeyPath" type="string" required="true" hint="the file path to the private key (ring) file. Normally with .asc extension. If multiple keys are in the file, we will use first private key.">
		<cfargument name="keyPassPhrase" type="string" required="true" hint="the passphrase to use with the private key.">
		<cfargument name="targetFile" type="string" default="" required="false" hint="optional. The output target for the decrypted file. If not provided the creation file before encryption name is used. Existing files will be overwritten.">

		
		<cfset var strReturn="">		
		
		<cftry>
			<cfscript>
				//check inputs
				if (Not FileExists(Arguments.contentFile)) strReturn="The encrypted file[#Arguments.contentFile#] could not be found."; 
				if (Not FileExists(Arguments.privateKeyPath)) strReturn="The private key file[#Arguments.privateKeyPath#] could not be found."; 
					
				//interact with java
				if (strReturn IS "") {
					//set options					
					objC.setPrivatePath(Arguments.privateKeyPath);
					objC.setContentFile(Arguments.contentFile);
					objC.setKeyPassphrase(Arguments.keyPassPhrase);
					objC.setTargetFile(Arguments.targetFile);

					//call decryption
					strReturn=objC.fDecryptSimpleFile();
					
					//if we have a missing target file and no error, we have the wrong passphrase
					if (NOT IsDefined("strReturn") OR IsDefined("strReturn") AND strReturn IS "" AND Arguments.targetFile NEQ "" AND Not FileExists(Arguments.targetFile)) {
						strReturn="File could not be decrypted, please check your private key pass phrase.";
					} 
				};
			</cfscript>
			
			
			<cfcatch type="any">
				<cfset strReturn = "Error (fDecryptSimpleFile):" & cfcatch.Message & "-" & cfcatch.Detail>
			</cfcatch>
	
		</cftry>
		
		<cfreturn strReturn>
	</cffunction>	
	
	
	<cffunction name="fEncryptOnePassSignature" access="public" returntype="string" hint="Encrypt and Sign a file using pgp private key. This ensures that sender and recipient are authenticated. Returns error string or empty string if success.">
		<cfargument name="contentFile" type="string" required="true" hint="the file path to the file to be encrypted.">
		<cfargument name="privateKeyPath" type="string" required="true" hint="the file path to the private key (ring) file. Normally with .asc extension. If multiple keys are in the file, we will use first private key. This private key is used to sign message.">
		<cfargument name="keyPassPhrase" type="string" required="true" hint="the passphrase to use with the private key.">
		<cfargument name="publicKeyPath" type="string" required="true" hint="the file path to the public key (ring) file. This key will be used to encrypt the content. Only the recipient will be able to decrypt. Normally with .asc extension. If multiple keys are in the file, we will use first public key.">
		<cfargument name="targetFile" type="string" default="" required="false" hint="optional. The output target for the encrypted file. Existing files will be overwritten.">
		
		<cfset var strReturn="">
		
		<cftry>	
		
			<cfscript>
				//check inputs
				if (Not FileExists(Arguments.contentFile)) strReturn="The plain file [#Arguments.contentFile#] could not be found."; 
				if (Not FileExists(Arguments.privateKeyPath)) strReturn="The private key file [#Arguments.privateKeyPath#] could not be found."; 
				if (Not FileExists(Arguments.publicKeyPath)) strReturn="The public key file [#Arguments.publicKeyPath#] could not be found."; 
				
				//interact with java
				if (strReturn IS "") {
					//set options					
					objC.setPrivatePath(JavaCast("string",Arguments.privateKeyPath));
					objC.setContentFile(JavaCast("string",Arguments.contentFile));
					objC.setKeyPassphrase(JavaCast("string",Arguments.keyPassPhrase));
					objC.setTargetFile(JavaCast("string",Arguments.targetFile));
					objC.setPublicPath(JavaCast("string",Arguments.publicKeyPath));

					//call encryption
					strReturn=objC.fEncryptOnePassSignature();
					
					//if we have a missing target file and no error, we have the wrong passphrase
					if (Not IsDefined("strReturn") OR strReturn IS "" AND Arguments.targetFile NEQ "" AND Not FileExists(Arguments.targetFile)) {
						strReturn="File could not be encrypted and signed, please check your inputs including private key pass phrase.";
					} 
				};
			</cfscript>
			
	
			<cfcatch type="any">
				<cfset strReturn = "Error (fEncryptOnePassSignature):" & cfcatch.Message & "-" & cfcatch.Detail>
			</cfcatch>
	
		</cftry>
		
		<cfreturn strReturn>
	</cffunction>	
	
	<cffunction name="fDecryptOnePassSignature" access="public" returntype="string" hint="Decrypt a file using pgp private key information that is also signed. Returns error string or empty string if success.">
		<cfargument name="contentFile" type="string" required="true" hint="the file path to the file to be decrypted.">
		<cfargument name="privateKeyPath" type="string" required="true" hint="the file path to the private key (ring) file. Normally with .asc extension. If multiple keys are in the file, we will use first private key.">
		<cfargument name="keyPassPhrase" type="string" required="true" hint="the passphrase to use with the private key.">
		<cfargument name="publicKeyPath" type="string" required="true" hint="the file path to the public key (ring) file. The corresponding private key was used to sign the content. Normally with .asc extension. If multiple keys are in the file, we will use first public key.">
		<cfargument name="targetFile" type="string" default="" required="false" hint="optional. The output target for the decrypted file. If not provided the creation file before encryption name is used. Existing files will be overwritten.">

		
		<cfset var strReturn="">		
		
		<cftry>
			<cfscript>
				//check inputs
				if (Not FileExists(Arguments.contentFile)) strReturn="The encrypted file[#Arguments.contentFile#] could not be found."; 
				if (Not FileExists(Arguments.privateKeyPath)) strReturn="The private key file[#Arguments.privateKeyPath#] could not be found."; 
				if (Not FileExists(Arguments.publicKeyPath)) strReturn="The public key file[#Arguments.publicKeyPath#] could not be found."; 
				
				//interact with java
				if (strReturn IS "") {
					//set options					
					objC.setPrivatePath(JavaCast("string",Arguments.privateKeyPath));
					objC.setContentFile(JavaCast("string",Arguments.contentFile));
					objC.setKeyPassphrase(JavaCast("string",Arguments.keyPassPhrase));
					objC.setTargetFile(JavaCast("string",Arguments.targetFile));
					objC.setPublicPath(JavaCast("string",Arguments.publicKeyPath));

					//call decryption
					strReturn=objC.fDecryptOnePassSignature();
					
					//if we have a missing target file and no error, we have the wrong passphrase
					if (Not IsDefined("strReturn") OR strReturn IS "" AND Arguments.targetFile NEQ "" AND Not FileExists(Arguments.targetFile)) {
						strReturn="Generic Error. File could not be decrypted, please check your private key pass phrase.";
					} 
				};
			</cfscript>
			
			
			<cfcatch type="any">
				<cfset strReturn = "Error (fDecryptOnePassSignature):" & cfcatch.Message & "-" & cfcatch.Detail>
			</cfcatch>
	
		</cftry>
		
		<cfreturn strReturn>
	</cffunction>		

</cfcomponent>