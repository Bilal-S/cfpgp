<!--- Process uploaded file for ecryption --->

	<div id="createKeys">

		<div class="content">
		<h3>Encrypt and Sign File Processing</h3>
		</div>
		
		<!--- at this point we are not validating, we assume all inputs are correct --->
		<!--- attempt to upload file --->		
		<cfset objH = CreateObject("component","PGPHelper")>
		<cfset strUserDir = objH.getUserDir()>
		<cffile action="UPLOAD" filefield="encFile" destination="#strUserDir#" nameconflict="MAKEUNIQUE">
		<cfset strFileName = cffile.serverfile>
		
		
		<cfscript>
			//init controller
			objPGP = CreateObject("component","PGPController");
			
			//set options
			stcArgs = StructNew();
			stcArgs.contentFile = strUserDir & strFileName;
			stcArgs.publicKeyPath = objH.getKeysDir() & Form.publicKeyFile;
			stcArgs.privateKeyPath = objH.getKeysDir() & Form.privateKeyFile;
			stcArgs.targetFile = strUserDir & cffile.SERVERFILENAME & ".asc"; 
			stcArgs.keyPassPhrase =Form.pass;
			strUserFileRef = cffile.SERVERFILENAME & ".asc";
			strResponse=objPGP.fEncryptOnePassSignature(argumentcollection=stcArgs);
			
		</cfscript>
		
		<div class="content">
		
			<cfoutput>
			<cfif strResponse NEQ "">
				<p><b>Error Occured</b><br>
				#strResponse#
				</p>
				<p>Please make sure you selected a public key file containing a valid key</p>
			<cfelse>
				<p>
				Your file was encrypted and placed in the following directory on server <br>
				#stcArgs.targetFile#. <br><br>	
				We used the following public key file<br>
				#Form.publicKeyFile#<br>
				We used the following private key to sign<br>
				#Form.privateKeyFile#<br>		
				</p>
								
				<br>
				
				
				<br>
				<a href="user/#strUserFileRef#">Download Encrypted File</a>
				<br>
				
				<p>To decrypt you will need to select this file on server and the corresponding private key file to decrypt and public key file to check the signature.</p>		
			</cfif>				
			
			
			</cfoutput>			
			
		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	