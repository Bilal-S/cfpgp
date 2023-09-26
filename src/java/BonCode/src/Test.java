/**
 * 
 */
import org.bouncycastle.openpgp.PGPException;

import net.boncode.crypto.*;
/**
 * @author Bilal Soylu
 *
 */
public class Test {

	/** 
	 * Test PGP2 function
	 * @param args
	 */
	public static void main(String[] args) {
		
		//testRSAKeyGeneration();
		testDecryptOnePassSignature();
		//testEncryptOnePassSignature();
		System.out.println("Completed.");
	}
	
	private static void testRSAKeyGeneration() {
		PGPController objC = new PGPController();
		String result = "";
		//set the options		
		objC.setPrivatePath("c:\\temp\\NewPrivateKey.asc");
		objC.setKeyPassphrase("1234567");
		objC.setPublicPath("c:\\temp\\NewPublicKey.asc");
		objC.setKeyIdentity("dongo Key");
		objC.setKeyLength(1024);
		//call method
		objC.fGenerateRSAKeys();	
		
		
	}
	
	private static void testEncryptOnePassSignature(){
		PGPController objC = new PGPController();
		String result = "";
		//set the options
		/*
		objC.setContentFile("c:\\temp\\test\\BSF_AMEDISYS.12042009.asc");
		objC.setPrivatePath("c:\\temp\\gnu\\VerianAmedisysSecret.asc");
		objC.setKeyPassphrase("1234567");
		objC.setPublicPath("c:\\temp\\test\\BOADPULL112009.asc");
		*/
		/*
		objC.setContentFile("D:\\Temp\\s1\\License.txt");
		objC.setPrivatePath("D:\\Temp\\s1\\Private_12102011165005.asc");
		objC.setKeyPassphrase("changeme");
		objC.setPublicPath("D:\\Temp\\s1\\Public_08152011064535.asc");
		*/		
		objC.setContentFile("D:\\Temp\\s3\\README1.txt");
		objC.setPrivatePath("D:\\Temp\\s3\\Private_08152011064535.asc");
		objC.setKeyPassphrase("changeme");
		objC.setPublicPath("D:\\Temp\\s3\\Public_12182011144221.asc");		
		objC.fEncryptOnePassSignature();		
		
	}
	
	private static void testDecryptOnePassSignature(){
		PGPController objC = new PGPController();
		String result = "";
		//set the options
		/*
		objC.setContentFile("c:\\temp\\test\\BSF_AMEDISYS.12042009.asc");
		objC.setPrivatePath("c:\\temp\\gnu\\VerianAmedisysSecret.asc");
		objC.setKeyPassphrase("1234567");
		objC.setPublicPath("c:\\temp\\test\\BOADPULL112009.asc");
		*/
		/*
		objC.setContentFile("D:\\Tomcat\\webapps\\railo\\PGP\\user\\License1.asc.asc");
		objC.setPrivatePath("D:\\Tomcat\\webapps\\railo\\PGP\\keys\\Private_08152011064535.asc");
		objC.setKeyPassphrase("changeme");
		objC.setPublicPath("D:\\Tomcat\\webapps\\railo\\PGP\\keys\\Public_08152011064535.asc");		
		objC.fDecryptOnePassSignature();
		*/
		/*
		objC.setContentFile("D:\\Temp\\s2\\License.txt_pgp2.asc");
		objC.setPrivatePath("D:\\Temp\\s2\\Private_08152011064535.asc");
		objC.setKeyPassphrase("changeme");
		objC.setPublicPath("D:\\Temp\\s2\\Public_12102011165005.asc");	
		*/
		
		objC.setContentFile("D:\\Temp\\s3\\README1.txt_pgp.asc");
		objC.setPrivatePath("D:\\Temp\\s3\\Private_12182011144221.asc");
		objC.setKeyPassphrase("changeme");
		objC.setPublicPath("D:\\Temp\\s3\\Public_08152011064535.asc");
		
		System.out.println(objC.fDecryptOnePassSignature());	
			
	}
	
	private static void testKeyGeneration() {
		try {
			// test Key ring generation
			/*
			DSAElGamalKeyRingGenerator keyRingG = new DSAElGamalKeyRingGenerator("c:\\temp\\test\\public.asc","c:\\temp\\test\\private.asc",1024);
			keyRingG.fGenerateRings("TestRing","yokohama");
			*/
			PGPController objC = new PGPController();
			
			objC.setPrivatePath("c:\\temp\\test\\B3Private.asc");
			objC.setPublicPath("c:\\temp\\test\\B3Public.asc");
			objC.setKeyIdentity("wonderkey");
			//objC.setKeyLength(1024);
			objC.fGenerateRings();
			
			
		} 
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        }		
	}
	
	
	private static void testCntrlEncrypt() {
		PGPController objC = new PGPController();
		String result = "";
		//set the options
		objC.setContentFile("c:\\temp\\example.txt");
		objC.setPublicPath("c:\\temp\\EncryptedExample.asc");
		
		
	
		//call function
		try {
			result = objC.fEncryptSimpleFile();
			System.err.println(result);
		} catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        };	
		
	}

	
	
	private static void testCntrlDecrypt() {
		PGPController objC = new PGPController();
		String result = "";
		//set the options
		//objC.setContentFile("c:\\temp\\SampleNew1.txt.asc");
		//objC.setPrivatePath("c:\\temp\\B3Private.asc");
		
		objC.setContentFile("c:\\temp\\SampleNew1.txt.asc");
		objC.setPrivatePath("c:\\temp\\B3Private.asc");
		objC.setKeyPassphrase("wonderwrong");
		
		/*
		 * contentFile="c:\temp\CFEncryptedSampleNew101.asc",privateKeyPath="c:\temp\B3Private.asc",keyPassPhrase="1234567",targetFile="c:\temp\DecodedBaby.txt"
		 */
		
	
		//call function
		try {
			result = objC.fDecryptSimpleFile();
			System.err.println(result);
		} catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        };	
		
	}	
	private static void testFileEncrypt() {
		SimpleFileProcessor sf = new SimpleFileProcessor();
		String result = "";
		//set the options
		sf.set_contentFile("c:\\temp\\SampleNew1.txt");
		sf.set_targetFile("c:\\temp\\SampleNewEncrypted1.asc");
		sf.set_publicRingPath("c:\\temp\\VerianAmedisysBPublic.asc");
		sf.set_operation("e");
		//call function
		try {
			result = sf.fProcessFile();
			System.err.println(result);
		} catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
        };	
		
	}

}
