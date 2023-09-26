<!--- Display Form to Decrypt a File --->

	<div id="DecryptFile">

		<div class="content">
		<h3>Decrypt Signed File</h3>
		</div>
		<div class="content">
		Signed files are used to authenticate both ends of the transmission. Both recipient and sender
		are authenticated this way.
		You will need to know the corresponding keys that were used to encrypt and sign the message.
		Your public key was used to encrypt the message while the senders private key was used to sign it.
		Thus, to properly decrypt it you will need your private key and the senders public key.
		<br>
		</div>		
		<cfset objH = CreateObject("component","PGPHelper")>
	
		<cfset selUFiles = objH.getUserFiles()>
		<cfset selPublicKeys = objH.getPublicKeyFiles()>
		<cfset selPrivateKeys = objH.getPrivateKeyFiles()>		
		<div class="content">
			<cfif selPrivateKeys.RecordCount AND selPublicKeys.RecordCount AND selUFiles.RecordCount >
				<form name="encForm" id="encForm" method="post" action="index.cfm?action=SignedDecryptStep2">
				
				<table width="80%" border="0" cellpadding="5" cellspacing="15">
			    <caption>
			        <b>Enter File Parameters for Decrypting Signed File</b>
			    </caption>
			    <tr>
			        <td>
			        	Select File to Decrypt
			        </td>
			        <td>
			        	<select name="ufile" id="ufile">
			        		<cfoutput query="selUFiles">
			        		<option value="#selUFiles.Name#">#selUFiles.Name#</option>
							</cfoutput>
						</select>
			        </td>
			    </tr>
			    <tr>
			        <td>
			        	Decryption Private Key in
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
			        	Signature Public Key in
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
			        </td>
			        <td>
			        	<input type="submit" value="submit" name="submit">
			        </td>
			    </tr>
				</table>
				</form>
			<cfelse>
				<p>Information is missing. Using the links above do the following: <br>
				i) Generate Key or Key ring files <br>
				ii) Upload a file to be encrypted <br>
				</p>
			</cfif>
		
			<p>
			We will use defaults to set some other options. Please review code for available treatment.
			</p>
			
		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	