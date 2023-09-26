/**
 * 
 */
package net.boncode.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bouncycastle.openpgp.PGPPublicKey;



/**
 * @author Bilal Soylu
 *
 */
/**
 * @author Bilal Soylu
 *
 */
public class PGPController {
	
	// instance vars
	private String publicPath = ""; //used for rings as well as keys
	private String privatePath = ""; //used for rings as well as keys
	private String keyIdentity = "keyID"; //should be something meaningful
	private String keyPassphrase = "changeme"; //should be something meaningful
	private String targetFile =""; //some calls may take a target file as optional overwrite
	private int keyLength = 1024;
	private String contentFile = ""; //if encrypting or decrypting should contain path to file
	private String version = "3.0.0"; //version for internal tracking
	
	/**
	 * generates valid private/public keyring files, set the parameters before
	 * calling this function: needed are publicPath, privatePath, keyIdentity, keyLength
	 * this expanded equivalent of calling class DSAElGamalKeyRingGenerator
	 * @return empty string for success or error
	 */
	public String  fGenerateRings () {
		String response = "";
		try { 
			DSAElGamalKeyRingGenerator objKeyRing = new DSAElGamalKeyRingGenerator(this.publicPath,this.privatePath,this.keyLength);
			objKeyRing.fGenerateRings(this.keyIdentity,this.keyPassphrase);
		}
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            response = e.getMessage();
        }	
        return response;
	}
	
	/**
	 * An interface class to RSA PGP utility that generates a RSA PGPPublicKey/PGPSecretKey pair.
	 * Please set the following properties
	 * 		publicPath,privatePath,
	 * Optionally set
	 * 		keyLength=1024,keyIdentity=keyID,keyPassphrase=changeme
	 * 
	 * Attention ! This will overwrite key files with the same name as specified in publicPath and privatePath
	 * 
	 * @author Bilal Soylu
	 * @return empty string for success or error
	 */
	public String fGenerateRSAKeys() {
		String sReturn =""; 
		
		if (this.publicPath.length() !=0 && this.privatePath.length() !=0 ) {
			RSAKeyPairGenerator rsakg = new RSAKeyPairGenerator();
			try {
				rsakg.fGenerateRSAKeys(this.privatePath, this.publicPath, this.keyPassphrase, this.keyIdentity, this.keyLength);
			} catch (Exception e) {
				e.printStackTrace();
				sReturn = e.getMessage();
			}
		}	
		
		return sReturn;
	}
	
	/**
	 * encrypt a file using a public key
	 * if we are using a public key ring file the first public key will be used to encrypt
	 * @author Bilal Soylu
	 * @return Error string or empty string if successful 
	 */
	public String  fEncryptSimpleFile () {
		String response = "";
		
		try { 
			//check for necessary info set
			if (this.contentFile.length()==0)response = "File to encrypt is missing. Please set property.";
			if (this.publicPath.length()==0) response = "Public key or public key ring file is required. Please set property.";
			
			//call encryption
			if (response.length()==0){
				SimpleFileProcessor sf = new SimpleFileProcessor();
				//set the options
				sf.set_contentFile(this.contentFile);
				sf.set_targetFile(this.targetFile);
				sf.set_publicRingPath(this.publicPath);
				sf.set_operation("e");
				response =  sf.fProcessFile();
			}
		}
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            response = e.getMessage();
        }	
        return response;
	}	
	
	/**
	 * decrypt a file that has been encrypted using a public key
	 * if the file has been encrypted using a OnePassSignature the fDecryptOnePassSignature method should be used instead
	 * if we are using a key ring file, the first private key will be used to decrypt
	 * @author Bilal Soylu
	 * @return Error string or empty string if successful 
	 */
	public String  fDecryptSimpleFile () {
		String response = "";
		
		try { 
			//check for necessary info set
			if (this.contentFile.length()==0)response = "File to decrypt is missing. Please set property.";
			if (this.privatePath.length()==0) response = "Private key or private key ring file is required. Please set property.";
			if (this.keyPassphrase.length()==0) response = "passphrase for private key ring file is required. Please set property.";
			
			//call decryption
			if (response.length()==0){
				SimpleFileProcessor sf = new SimpleFileProcessor();
				//set the options
				sf.set_contentFile(this.contentFile);
				sf.set_targetFile(this.targetFile);
				sf.set_privateRingPath(this.privatePath);
				sf.set_passPhrase(this.keyPassphrase);
				sf.set_operation("d");
				response =  sf.fProcessFile();
			}
		}
        catch (Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            response = e.getMessage();
        }	
        return response;
	}	
	
	/**
	 * Decrypts a file that has been encrypted using our public key, then signed with a private Key
	 * for which we have a public key
	 * You will need to set privatePath, publicPath, contentFile, keyPassphrase
	 * Optionally you can provide targetFile. If no targetFile is set, then, the "_clear.txt" extension
	 * is added automatically to the decrypted file
	 * 
	 * @author Bilal Soylu
	 * @return Error string or empty string if successful
	 */
	public String fDecryptOnePassSignature ()  {
		String sReturn ="";
	
		try {
			//call the local function with input streams
			FileInputStream privateKeyStream = new FileInputStream(this.privatePath);
			FileInputStream publicKeyStream = new FileInputStream(this.publicPath);
			FileInputStream contentFileStream = new FileInputStream(this.contentFile);
			//determine target file
			if (this.targetFile.length()==0) this.targetFile = this.contentFile + "_clear.txt";
			FileOutputStream  targetFileStream = new FileOutputStream(this.targetFile);
			
			try {
				//call function to do this
				OnePassSignatureProcessor ops = new OnePassSignatureProcessor();
				ops.fDecryptOnePassSignatureLocal(contentFileStream, publicKeyStream, privateKeyStream, this.keyPassphrase, targetFileStream);
			} catch (Exception e){
				
	            System.err.println(e);
	            e.printStackTrace();
	            sReturn = e.getMessage();
				
			}
			
			finally {
				
				//close file streams
				privateKeyStream.close();
				publicKeyStream.close();
				contentFileStream.close();
			}
			
		} catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            sReturn = e.getMessage();           
			
		}
		
		
		
		return sReturn;
	}	
	
	/**
	 * Encrypts file with supplied public key and signs it with a one pass signature
	 * this methodology is normally used to guarantee that only recipient can
	 * read content and allow recipient to authenticate sender
	 * 
	 * @author Bilal Soylu
	 * @return Error string or empty string if successful
	 */
	public String fEncryptOnePassSignature ()  {
		String sReturn ="";
	
		try {
			
			//call the local function with input streams
			FileInputStream privateKeyStream = new FileInputStream(this.privatePath);
			FileInputStream publicKeyStream = new FileInputStream(this.publicPath);
			FileInputStream contentFileStream = new FileInputStream(this.contentFile);
			//determine target file
			if (this.targetFile.length()==0) this.targetFile = this.contentFile + "_pgp.asc";
			FileOutputStream  targetFileStream = new FileOutputStream(this.targetFile);
			//determine content file name
			File contentFilePointer = new File (this.contentFile);
			
			try {
				//Initialize one pass signature processor
				OnePassSignatureProcessor ops = new OnePassSignatureProcessor();
				//run the process
				ops.fEncryptOnePassSignatureLocal(this.targetFile, contentFilePointer.getName(), privateKeyStream, targetFileStream, this.keyPassphrase, publicKeyStream,contentFileStream);
			} catch (Exception e ){
				
	            System.err.println(e);
	            e.printStackTrace();
	            sReturn = e.getMessage();
	            
			}
			//dispose of all file pointers and streams
			privateKeyStream.close();
			publicKeyStream.close();
			contentFileStream.close();
			targetFileStream.close();
			contentFilePointer = null;
			
			
			
		} catch (Exception e){
            System.err.println(e);
            e.printStackTrace();
            sReturn = e.getMessage();
           
            
			
		} finally {
			//close streams
			
		}
		
		
		
		return sReturn;
	}		
	
	//getters and setters below

	/**
	 * @return the publicPath
	 */
	public String getPublicPath() {
		return publicPath;
	}

	/**
	 * @param publicPath the publicPath to set
	 */
	public void setPublicPath(String publicPath) {
		this.publicPath = publicPath;
	}

	/**
	 * @return the privatePath
	 */
	public String getPrivatePath() {
		return privatePath;
	}

	/**
	 * @param privatePath the privatePath to set
	 */
	public void setPrivatePath(String privatePath) {
		this.privatePath = privatePath;
	}

	/**
	 * @return the keyIdentity
	 */
	public String getKeyIdentity() {
		return keyIdentity;
	}

	/**
	 * @param keyIdentity the keyIdentity to set
	 */
	public void setKeyIdentity(String keyIdentity) {
		this.keyIdentity = keyIdentity;
	}

	/**
	 * @return the keyLength
	 */
	public int getKeyLength() {
		return keyLength;
	}

	/**
	 * @param keyLength the keyLength to set
	 */
	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	/**
	 * @return the keyPassphrase
	 */
	public String getKeyPassphrase() {
		return keyPassphrase;
	}

	/**
	 * @param keyPassphrase the keyPassphrase to set
	 */
	public void setKeyPassphrase(String keyPassphrase) {
		this.keyPassphrase = keyPassphrase;
	}
	/**
	 * @return the targetFile
	 */
	public String getTargetFile() {
		return targetFile;
	}
	/**
	 * @param targetFile the targetFile to set
	 */
	public void setTargetFile(String targetFile) {
		this.targetFile = targetFile;
	}
	/**
	 * @return the contentFile
	 */
	public String getContentFile() {
		return contentFile;
	}
	/**
	 * @param contentFile the contentFile to set
	 */
	public void setContentFile(String contentFile) {
		this.contentFile = contentFile;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	

	
	
}
