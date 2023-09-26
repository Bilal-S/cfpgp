<!--- Process uploaded file for ecryption --->

	<div id="createKeys">

		<div class="content">
		<h3>Encrypt File Processing</h3>
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
			stcArgs.publicKeyPath = objH.getKeysDir() & Form.keyFile;
			stcArgs.targetFile = strUserDir & cffile.SERVERFILENAME & ".asc"; 
			strUserFileRef = cffile.SERVERFILENAME & ".asc";
			strResponse=objPGP.fEncryptSimpleFile(argumentcollection=stcArgs);
			
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
				#Form.keyFile#		
				</p>
								
				<br>
				
				
				<br>
				<a href="user/#strUserFileRef#">Download Encrypted File</a>
				<br>
				
				<p>To decrypt you will need to select this file on server and the corresponding private key file.</p>		
			</cfif>				
			
			
			</cfoutput>			
			
		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	