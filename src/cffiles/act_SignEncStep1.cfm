<!--- Display Form to Encrypt a File --->

	<div id="createKeys">

		<div class="content">
		<h3>Encrypt and Sign a File</h3>
		</div>
		<div class="content">
			Signing and Encrypting ensures that only the recipient can open 
			your content but at the same time authenticates the sender as
			the valid sender of the message.
			Thus, you will need to sign with your private key and encrypt with
			the recipients public key.
		</div>
		<cfset objH = CreateObject("component","PGPHelper")>
		
		<cfset selPublicKeys = objH.getPublicKeyFiles()>
		<cfset selPrivateKeys = objH.getPrivateKeyFiles()>
		<div class="content">
			<cfif selPublicKeys.RecordCount AND selPrivateKeys.RecordCount>
				<form name="encForselPublicKeysm" id="encForm" method="post" action="index.cfm?action=SignedEncryptStep2" enctype="multipart/form-data">
				
				<table width="80%" border="0" cellpadding="5" cellspacing="15">
			    <caption>
			        <b>Enter File Parameters for Encryption</b>
			    </caption>
			    <tr>
			        <td>
			        	File to Encrypt
			        </td>
			        <td>
			        	<input type="file" name="encFile" id="encFile">
			        </td>
			    </tr>
			    <tr>
			        <td>
			        	Recipient Public Key in
			        </td>
			        <td>			        	
			        	<select name="publicKeyFile" id="publicKeyFile">
			        		<cfoutput query="selPublicKeys">
			        		<option value="#selPublicKeys.Name#">#selPublicKeys.Name#</option>
							</cfoutput>
						</select>			
			        </td>
			    </tr>
			    <tr>
			        <td>
			        	Your Private Key in
			        </td>
			        <td>			        	
			        	<select name="privateKeyFile" id="privateKeyFile">
			        		<cfoutput query="selPrivateKeys">
			        		<option value="#selPrivateKeys.Name#">#selPrivateKeys.Name#</option>
							</cfoutput>
						</select>			
			        </td>
			    </tr>			  
			    <tr>
			        <td>	
						Key Passphrase		        	
			        </td>
			        <td>
			        	<input type="text" value="changeme" id="pass" name="pass">
			        </td>
			    </tr>			      
			    <tr>
			        <td>			        	
			        </td>
			        <td>
			        	<input type="submit" value="submit" name="submit">
			        </td>
			    </tr>
				</table>
				</form>
			<cfelse>
				<p>Please generate a Key Ring or Key File first. Click links above to do so.</p>
			</cfif>
		
			<p>
			We will use defaults to set some other options. Please review code for available treatment.
			</p>
			
		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	