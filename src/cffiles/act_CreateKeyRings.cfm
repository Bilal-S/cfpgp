

	<div id="createKeyRing">

		<div class="content">
		<h3>Generate Key Ring Files</h3>
		</div>

		<div class="content">

		<cfscript>
			//initialize variables
			strPublicKeyRingFileName="PublicKR_" & DateFormat(Now(),"MMDDYYYY") & timeFormat(now(),"HHMMSS") & ".asc";
			strPrivateKeyRingFileName="PrivateKR_" & DateFormat(Now(),"MMDDYYYY") & timeFormat(now(),"HHMMSS") & ".asc";
			strPassPhrase ="changeme";
			strKeyID="sampleKey";
			intKeyLength="1024";  //max is 1024
 			//determine path slash
			strOS = Server.OS.Name;
			if(FindNoCase("Windows", strOS)) {
				strSlash = "\";
			} else {
		  		strSlash = "/";
			};			
			
			//get current directory and set full path
			strCurrentDir = GetDirectoryFromPath(getCurrentTemplatePath()) & "keys" & strSlash;
			strFullPrivate = strCurrentDir & strPrivateKeyRingFileName;
			strFullPublic = strCurrentDir & strPublicKeyRingFileName;
			
			
			//init controller
			objPGP = CreateObject("component","PGPController");
			
		</cfscript>
		
		<!--- delete existing Key ring files --->
		<cfif FileExists(strFullPrivate)>
			<cffile action="delete" file="#strFullPrivate#">		
		</cfif>
		
		<cfif FileExists(strFullPublic)>
			<cffile action="delete" file="#strFullPublic#">
		</cfif>
		<!--- make main call --->
		<cfset strResponse=objPGP.fGenerateRings(privatePath=strFullPrivate,publicPath=strFullPublic,passPhrase=strPassPhrase,keyID=strKeyID,keyLength=intKeyLength)>
		

		<cfoutput>
		
		<cfif strResponse NEQ "">
			<p><b>Error Occured</b><br>
			#strResponse#
			</p>
		<cfelse>
			<p>
			The Public and Private Key Ring files have been generated. <br>
			The default password/passphrases ("#strPassPhrase#") has been used. <br>
			The default key ID ("#strKeyID#") has been used <br>
			</p>
			
			<b>Warning</b><br>
			If you regenerate key rings your existing files will be overwritten. You should download or move the 
			generated files to a safe place. <br>
			They are currently located in [#strCurrentDir#] on the server.
			<br>
			
			
			<br>
			<a href="#strPublicKeyRingFileName#">Download Public Key Ring File</a>
			<br>
			<a href="#strPrivateKeyRingFileName#">Download Private Key Ring File</a>			
		</cfif>
		
		</cfoutput>
		</div>

		<div class="clearthis">&nbsp;</div>
	</div>

	