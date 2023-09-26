<!--- Display Form to Encrypt a File --->

	<div id="createKeys">

		<div class="content">
		<h3>Encrypt File</h3>
		</div>
		<cfset objH = CreateObject("component","PGPHelper")>
		<cfset selKeys = objH.getPublicKeyFiles()>
		<div class="content">
			<cfif selKeys.RecordCount>
				<form name="encForm" id="encForm" method="post" action="index.cfm?action=EncryptStep2" enctype="multipart/form-data">
				
				<table width="80%" border="0">
			    <caption>
			        Enter File Parameters for Encryption
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
			        	Public Key in
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

	