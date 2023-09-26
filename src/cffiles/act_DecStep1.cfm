<!--- Display Form to Decrypt a File --->

	<div id="DecryptFile">

		<div class="content">
		<h3>Decrypt File</h3>
		</div>
		<cfset objH = CreateObject("component","PGPHelper")>
		<cfset selKeys = objH.getPrivateKeyFiles()>
		<cfset selUFiles = objH.getUserFiles()>
		<div class="content">
			<cfif selKeys.RecordCount AND selUFiles.RecordCount >
				<form name="encForm" id="encForm" method="post" action="index.cfm?action=DecryptStep2">
				
				<table width="80%" border="0">
			    <caption>
			        Enter File Parameters for Decryption
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
			        	Private Key in
			        </td>
			        <td>			        	
			        	<select name="keyFile" id="keyFile">
			        		<cfoutput query="selKeys">
			        		<option value="#selKeys.Name#">#selKeys.Name#</option>
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

	