<cfcomponent>
	<cfscript>
		this.os = Server.OS.Name;
		if(FindNoCase("Windows", this.os)) this.slash = "\";
		else                               this.slash = "/";
	</cfscript>

	<!--- Return files in keys subdir --->
	<cffunction name="getKeyFiles" hint="returns key files in keys directory">		
		<cfset strCurrentDir = GetDirectoryFromPath(getCurrentTemplatePath()) & "keys" & this.slash>
		
		<cfdirectory action="list" directory="#strCurrentDir#" filter="*.asc" name="selKeys">
		
		<cfreturn selKeys>
	</cffunction>

	<!--- return public keys in subdir --->
	<cffunction name="getPublicKeyFiles" hint="returns public key files are key ring files in keys directory">
		<cfset selAllKeys = getKeyFiles()>
		<cfquery name="selPublic" dbtype="query">
			SELECT *
			FROM selAllKeys
			WHERE Name LIKE 'Public%'
		</cfquery>

		<cfreturn selPublic>
	</cffunction>
	
	<!--- return private keys in subdir --->
	<cffunction name="getPrivateKeyFiles" hint="returns public key files are key ring files in keys directory">
		<cfset selAllKeys = getKeyFiles()>
		<cfquery name="selPrivate" dbtype="query">
			SELECT *
			FROM selAllKeys
			WHERE Name LIKE 'Private%'
		</cfquery>

		<cfreturn selPrivate>
	</cffunction>	
	<!--- return files in user data subdir --->
	<cffunction name="getUserFiles" hint="returns encrypted user files in keys directory">		
		<cfset strCurrentDir = GetDirectoryFromPath(getCurrentTemplatePath()) & "user" & this.slash>
		
		<cfdirectory action="list" directory="#strCurrentDir#" filter="*.asc" name="selKeys">
		
		<cfreturn selKeys>
	</cffunction>	
	
	
	
	<cffunction name="getKeysDir" hint="returns keys directory">
		<cfset strKeyDir = GetDirectoryFromPath(getCurrentTemplatePath()) & "keys" & this.slash>
		<cfreturn strKeyDir>
	</cffunction>
		
	<cffunction name="getUserDir" hint="returns user directory">
		<cfset strUDir = GetDirectoryFromPath(getCurrentTemplatePath()) & "user" & this.slash>
		<cfif Not DirectoryExists(strUDir)>
			<cfdirectory action="create" directory="#strUDir#">
		</cfif>		
		<cfreturn strUDir>
	</cffunction>

</cfcomponent>

