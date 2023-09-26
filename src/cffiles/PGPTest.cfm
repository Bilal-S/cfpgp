

<cfset objPGP = CreateObject("component","PGPController")>




			
<cfscript>
	//generate rings
	//strResponse=objPGP.fGenerateRings(privatePath="c:\temp\test\Private.asc",publicPath="c:\temp\Public.asc",passPhrase="changeme",keyID="another key",keyLength=1024);
	
	//generate Keys
	strResponse=objPGP.fGenerateRSAKeys(privatePath="c:\temp\Private.asc",publicPath="c:\temp\Public.asc",passPhrase="changeme",keyID="nextKey 3",keyLength=2048);
	
	//encrypt
	//strResponse=objPGP.fEncryptSimpleFile(contentFile="c:\temp\SampleNew1.txt",publicKeyPath="c:\temp\B3Public.asc",targetFile="c:\temp\CFEncryptedSampleNew101.asc");


	//decrypt
	//strResponse=objPGP.fDecryptSimpleFile(contentFile="c:\temp\CFEncryptedSampleNew101.asc",privateKeyPath="c:\temp\Private.asc",keyPassPhrase="changeme",targetFile="c:\temp\DecodedBaby.txt");

	//onePassSignure decrypt
	//strResponse=objPGP.fDecryptOnePassSignature(publicKeyPath="c:\temp\test\PublicKey.asc",contentFile="c:\temp\test\Content.asc",privateKeyPath="c:\temp\gnu\SecretToPublicSignKey.asc",keyPassPhrase="changeme",targetFile="c:\temp\CCDecodedBaby.txt");

</cfscript>		
<hr>	
<cfoutput>#strResponse#
<hr>
<br>
Done #Now()#.

</cfoutput>