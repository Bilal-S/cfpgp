<!--- Process uploaded file for decryption --->

	<div id="DecryptFile">

		<div class="content">
		<h3>Decrypt File Processing</h3>
		</div>
		
		<!--- at this point we are not validating, we assume all inputs are correct --->
				
			
		<cfscript>
			//init controller
			objPGP = CreateObject("component","PGPController");
			//init helper
			objH = CreateObject("component","PGPHelper");
			
			//get environement values
			 strUserDir = objH.getUserDir();
			
			//set options
			stcArgs = StructNew();
			stcArgs.contentFile = strUserDir & Trim(Form.ufile);
			stcArgs.privateKeyPath = objH.getKeysDir() & Trim(Form.keyFile);
			stcArgs.targetFile = strUserDir & Trim(Form.ufile) & ".txt"; 
			stcArgs.keyPassPhrase = Trim(Form.pass);
			strUserFileRef = Form.ufile & ".txt";
			
			//run decryption
			strResponse=objPGP.fDecryptSimpleFile(argumentcollection=stcArgs);
			
		</cfscript>
		
		<!---<cfdump var="#stcArgs#">--->
		
		<div class="content">
		
			<cfoutput>
			<cfif strResponse NEQ "">
				<p><b>Error Occured</b><br>
				#strResponse#
				</p>
				<p>Please make sure you selected a private key file containing a valid key and supplied a valid key passphrase</p>
			<cfelse>
				<p>
				Your file was decrypted and placed in the following directory on server <br>
				#stcArgs.targetFile#. <br><br>	
				We used the following private key file<br>
				#Form.keyFile#		
				</p>
								
				<br>
				
				
				<br>
				<a href="user/#strUserFileRef#">Download Decrypted File</a>
				<br>
				
						
			</cfif>				
			
			
			</cfoutput>			
			
		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	