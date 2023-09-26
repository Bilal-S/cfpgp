<!--- 
Welcome to BonCode PGP Sample Files.

If you want to install manually please open the SourceInfo.zip file and peruse the ReadeMe file.


Bilal Soylu
11/2009
 --->



<!--- this acts as director for requests --->


<!--- load header --->
<cfinclude template="view_header.cfm">


<!--- determine what content to load --->
	<cfparam name="URL.Action" default="nothing">

	<cfswitch expression="#URL.Action#">
	
		<cfcase value="GenerateKeyRings">
			<cfinclude template="act_CreateKeyRings.cfm">	
		</cfcase>
		<cfcase value="GenerateKeys">
			<cfinclude template="act_CreateKeys.cfm">
		</cfcase>
		<cfcase value="EncryptStep1">
			<cfinclude template="act_EncStep1.cfm">
		</cfcase>
		<cfcase value="EncryptStep2">
			<cfinclude template="act_EncStep2.cfm">
		</cfcase>		
		<cfcase value="DecryptStep1">
			<cfinclude template="act_DecStep1.cfm">
		</cfcase>
		<cfcase value="DecryptStep2">
			<cfinclude template="act_DecStep2.cfm">
		</cfcase>		
		<cfcase value="SignedDecryptStep1">
			<cfinclude template="act_SignDecStep1.cfm">
		</cfcase>
		<cfcase value="SignedDecryptStep2">
			<cfinclude template="act_SignDecStep2.cfm">
		</cfcase>				
		<cfcase value="SignedEncryptStep1">	
			<cfinclude template="act_SignEncStep1.cfm">
		</cfcase>
		<cfcase value="SignedEncryptStep2">	
			<cfinclude template="act_SignEncStep2.cfm">
		</cfcase>		
		
		<cfdefaultcase>
			<cfinclude template="view_welcome.cfm">	
		</cfdefaultcase>
	
	</cfswitch>



<!--- load footer --->
<cfinclude template="view_footer.cfm">



	

	


