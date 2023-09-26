/**
 * @author Bilal Soylu 
 * based on example from bouncy castle
 */
package net.boncode.crypto;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPUtil;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;


/**
 * Class encrypts or decrypts a file
 * For either encryption or decryption the file to be processed needs to be provided
 * For encryption the public key file or keyRing file is needed
 * For decryption the private key file or keyRing file and passphrase are needed
 * @author Bilal Soylu
 *
 */
public class SimpleFileProcessor {


	//set class instance vars need to run operations
	private String _publicRingPath = "public.asc";
	private String _privateRingPath = "secret.asc";
	private String _passPhrase = "changeme";
	private String _contentFile = "";
	private String _targetFile =""; //to overwrite file name during decryption
	private String _operation ="e"; //default to encryption
	
	/*
	 * the constructor can take on two operations "e" for encryption or "d" for decryption
	 * in case of e
	 *    the content file is assumed to be plain text
	 *    the keyPath is pointing to a public key
	 *    if the target file is not provided the content file will be overwritten
	 * in case of d
	 * 	  the content file is assumed to be encrypted
	 *    the keyPath is pointing to a private key
	 *    the passPhrase is required
	 *    if no target file is provided the target file will be extracted from the encrypted file 
	 *    
	 */
	public SimpleFileProcessor(String operation, String contentFile, String keyPath, String passPhrase, String targetFile) {
		// we will set the common values here
		super();
		_contentFile = contentFile;		
		//distinguish between encryption and decryption
		if (operation.toLowerCase().equals("e")) {
			_publicRingPath = keyPath;
			_operation = operation.toLowerCase();
			if (_targetFile == ""){
				_targetFile = contentFile + ".asc"; //change extension
			}			
		} else if (operation.toLowerCase().equals("d")){
			_privateRingPath = keyPath;
			_operation = operation.toLowerCase();
			_targetFile = targetFile;
			if (passPhrase == ""){
				throw new IllegalArgumentException("SimpleFileProcessor contstuctor: passPhrase is required when using operation type 'd'.");				
			}			
			
		} else {
			throw new IllegalArgumentException("SimpleFileProcessor contstuctor: operation is not one of 'e' for encryption or 'd' for decrytion.");
			
		}
	
	}
	
    public SimpleFileProcessor() {
		// standard constructor
    	super();
	}





	/**
     * A simple routine that opens a key ring file and loads the first available key suitable for
     * encryption.
     * 
     * @param in
     * @return
     * @throws IOException
     * @throws PGPException
     */
	
	public static String sPublicFile = "";
	
    private static PGPPublicKey readPublicKey(
        InputStream    in)
        throws IOException, PGPException
    {
        in = PGPUtil.getDecoderStream(in);
        
        PGPPublicKeyRingCollection        pgpPub = new PGPPublicKeyRingCollection(in);

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //
        
        //
        // iterate through the key rings.
        //
        Iterator rIt = pgpPub.getKeyRings();
        
        while (rIt.hasNext())
        {
            PGPPublicKeyRing    kRing = (PGPPublicKeyRing)rIt.next();    
            Iterator                        kIt = kRing.getPublicKeys();
            
            while (kIt.hasNext())
            {
                PGPPublicKey    k = (PGPPublicKey)kIt.next();
                
                if (k.isEncryptionKey())
                {
                    return k;
                }
            }
        }
        
        throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }
    
    /**
     * Search a secret key ring collection for a secret key corresponding to
     * keyID if it exists.
     * 
     * @param pgpSec a secret key ring collection.
     * @param keyID keyID we want.
     * @param pass passphrase to decrypt secret key with.
     * @return
     * @throws PGPException
     * @throws NoSuchProviderException
     */
    private static PGPPrivateKey findSecretKey(
        PGPSecretKeyRingCollection  pgpSec,
        long                        keyID,
        char[]                      pass)
        throws PGPException, NoSuchProviderException
    {    
        PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
        
        if (pgpSecKey == null)
        {
            return null;
        }
        
        return pgpSecKey.extractPrivateKey(pass, "BC");
    }
    
    /**
     * decrypt the passed in message stream
     */
    private void decryptFile(
        InputStream in,
        InputStream keyIn,
        char[]      passwd,
        String      defaultFileName)
        throws Exception
    {
        in = PGPUtil.getDecoderStream(in);
        
        try
        {
            PGPObjectFactory pgpF = new PGPObjectFactory(in);
            PGPEncryptedDataList    enc;

            Object                  o = pgpF.nextObject();
            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList)
            {
                enc = (PGPEncryptedDataList)o;
            }
            else
            {
                enc = (PGPEncryptedDataList)pgpF.nextObject();
            }
            
            //
            // find the secret key
            //
            Iterator                    it = enc.getEncryptedDataObjects();
            PGPPrivateKey               sKey = null;
            PGPPublicKeyEncryptedData   pbe = null;
            PGPSecretKeyRingCollection  pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(keyIn));

            while (sKey == null && it.hasNext())
            {
                pbe = (PGPPublicKeyEncryptedData)it.next();
                
                sKey = findSecretKey(pgpSec, pbe.getKeyID(), passwd);
            }
            
            if (sKey == null)
            {
                throw new IllegalArgumentException("secret key for message not found.");
            }
    
            InputStream         clear = pbe.getDataStream(sKey, "BC");
            
            PGPObjectFactory    plainFact = new PGPObjectFactory(clear);
            
            Object              message = plainFact.nextObject();
    
            if (message instanceof PGPCompressedData)
            {
                PGPCompressedData   cData = (PGPCompressedData)message;
                PGPObjectFactory    pgpFact = new PGPObjectFactory(cData.getDataStream());
                
                message = pgpFact.nextObject();
            }
            
                       
            if (message instanceof PGPLiteralData)
            {
                PGPLiteralData      ld = (PGPLiteralData)message;
                String              outFileName = ld.getFileName();
                //determine output file name. If we have one passed we will use it
                if (this._targetFile.length() != 0){
                	 outFileName = this._targetFile;
                } else if (ld.getFileName().length() == 0)
                {
                    outFileName = defaultFileName;
                }
                FileOutputStream    fOut = new FileOutputStream(outFileName);
                
                InputStream    unc = ld.getInputStream();
                int    ch;
                
                while ((ch = unc.read()) >= 0)
                {
                    fOut.write(ch);
                }
                fOut.close();
            }
            else if (message instanceof PGPOnePassSignatureList)
            {
            	//We cannot use this class use OnePassSignatureProcessor            	
                throw new PGPException("encrypted message contains a signed message - not literal data. Please use OnePassSignatureProcessor instead.");
            }
            else
            {
                throw new PGPException("message is not a simple encrypted file - type unknown.");
            }

            if (pbe.isIntegrityProtected())
            {
                if (!pbe.verify())
                {
                    System.err.println("message failed integrity check");
                }
                else
                {
                    System.err.println("message integrity check passed");
                }
            }
            else
            {
                System.err.println("no message integrity check");
            }
        }
        catch (PGPException e)
        {
            System.err.println(e);
            if (e.getUnderlyingException() != null)
            {
                e.getUnderlyingException().printStackTrace();
            }
        }
        
    }

    private void encryptFile(
        OutputStream    out,
        String          fileName,
        PGPPublicKey    encKey,
        boolean         armor,
        boolean         withIntegrityCheck)
        throws IOException, NoSuchProviderException
    {    
        if (armor)
        {
            out = new ArmoredOutputStream(out);
        }
        
        try
        {
            ByteArrayOutputStream       bOut = new ByteArrayOutputStream();
            
    
            PGPCompressedDataGenerator  comData = new PGPCompressedDataGenerator(
                                                                    PGPCompressedData.ZIP);
                                                                    
            PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, new File(fileName));
            
            comData.close();
            
            PGPEncryptedDataGenerator   cPk = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5, withIntegrityCheck, new SecureRandom(), "BC");
                
            cPk.addMethod(encKey);
            
            byte[]                bytes = bOut.toByteArray();
            
            OutputStream    cOut = cPk.open(out, bytes.length);

            cOut.write(bytes);
            
            cOut.close();

            out.close();
        }
        catch (PGPException e)
        {
            System.err.println(e);
            if (e.getUnderlyingException() != null)
            {
                e.getUnderlyingException().printStackTrace();
            }
        }
    }

    /*
     * returns Error or Empty String
     */
    public String fProcessFile() throws Exception {
    	String sReturn =""; //return the response
    	
    	 Security.addProvider(new BouncyCastleProvider());

   
         
         if (_operation.equals("e"))
         {   
        	 //we will set the target file to same as content file if not specified
        	 if (this._targetFile.length() == 0) this._targetFile = this._contentFile + ".asc";
        	 
        	 FileInputStream     keyIn = new FileInputStream(_publicRingPath);
        	 FileOutputStream    out = new FileOutputStream(_targetFile);
        	 encryptFile(out, _contentFile, readPublicKey(keyIn), true, true);
        	 
        	 //close streams
        	 keyIn.close();
             out.close();
             
         }
         else if (_operation.equals("d"))
         {
             FileInputStream    in = new FileInputStream(_contentFile);
             FileInputStream    keyIn = new FileInputStream(_privateRingPath);
             
             decryptFile(in, keyIn, _passPhrase.toCharArray(), new File(_targetFile).getName() + ".out");
             //close streams
             in.close();
             keyIn.close();
             
             
         }
         else
         {
        	 sReturn ="usage problem: SimpleFileProcessor requires operation, file [secretKeyFile passPhrase|pubKeyFile]";
             System.err.println("usage problem: SimpleFileProcessor requires operation, file [secretKeyFile passPhrase|pubKeyFile]");
         }    	    	

    
    	return sReturn;
    }
    
    public static void main(
        String[] args)
        throws Exception
    {
      
    }
    
    /*
     * Getters and Setters
     * 
     */
    
	public  String get_publicRingPath() {
		return this._publicRingPath;
	}

	public  void set_publicRingPath(String ringPath) {
		this._publicRingPath = ringPath;
	}

	public  String get_privateRingPath() {
		return this._privateRingPath;
	}

	public  void set_privateRingPath(String ringPath) {
		this._privateRingPath = ringPath;
	}

	public  String get_passPhrase() {
		return this._passPhrase;
	}

	public  void set_passPhrase(String phrase) {
		this._passPhrase = phrase;
	}

	public  String get_contentFile() {
		return this._contentFile;
	}

	public  void set_contentFile(String file) {
		this._contentFile = file;
	}

	public  String get_targetFile() {
		return this._targetFile;
	}

	public  void set_targetFile(String file) {
		this._targetFile = file;
	}

	public  String get_operation() {
		return this._operation;
	}

	public  void set_operation(String operation) {
		this._operation = operation;
	}
    

    
    
       
}
