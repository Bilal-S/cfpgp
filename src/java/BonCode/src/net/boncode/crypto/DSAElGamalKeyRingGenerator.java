package net.boncode.crypto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;

/**
 * A simple utility class that generates a public/secret keyring containing a DSA signing
 * key and an El Gamal key for encryption.
 * <p>
 * usage: DSAElGamalKeyRingGenerator [-a] identity passPhrase
 * <p>
 * Where identity is the name to be associated with the public key. The keys are placed 
 * in the files pub.[asc|bpg] and secret.[asc|bpg].
 * <p>
 * <b>Note</b>: this example encrypts the secret key using AES_256, many PGP products still
 * do not support this, if you are having problems importing keys try changing the algorithm
 * id to PGPEncryptedData.CAST5. CAST5 is more widely supported.
 */
/**
 * @author User
 *
 */
public class DSAElGamalKeyRingGenerator
{
	
	//set class vars need to use static variables to accommodate the static nature of this class
	private static String _publicRingPath = "public.asc";
	private static String _privateRingPath = "secret.asc";
	private static int _keyLength = 2048;  //forced to use static declaration
	
	
    /**
	 * generic constructor
	 */
	public DSAElGamalKeyRingGenerator() {
		super();
		// do nothing, just have as option
	}

    /**
	 * special constructor
	 */
	public DSAElGamalKeyRingGenerator(String publicPath, String privatePath, int keyLength) {
		// we will set the common values here
		super();
		_publicRingPath = publicPath;
		_privateRingPath = privatePath;
		_keyLength = keyLength;
		
	}


	private static void exportKeyPair(
        OutputStream    secretOut,
        OutputStream    publicOut,
        KeyPair         dsaKp,
        KeyPair         elgKp,
        String          identity,
        char[]          passPhrase,
        boolean         armor)
        throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException
    {
        if (armor)
        {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPKeyPair        dsaKeyPair = new PGPKeyPair(PGPPublicKey.DSA, dsaKp, new Date());
        PGPKeyPair        elgKeyPair = new PGPKeyPair(PGPPublicKey.ELGAMAL_ENCRYPT, elgKp, new Date());
        
        PGPKeyRingGenerator    keyRingGen = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION, dsaKeyPair,
                 identity, PGPEncryptedData.AES_256, passPhrase, true, null, null, new SecureRandom(), "BC");
        
        keyRingGen.addSubKey(elgKeyPair);
        
        keyRingGen.generateSecretKeyRing().encode(secretOut);
        
        secretOut.close();
        
        if (armor)
        {
            publicOut = new ArmoredOutputStream(publicOut);
        }
        
        keyRingGen.generatePublicKeyRing().encode(publicOut);
        
        publicOut.close();
    }
    
   

	/**
     * method that will be used by external classes to call on this Class
     * 
     */
    public void fGenerateRings(String sIdentity, String sPassPhrase) throws Exception {
    	
    	
    	 Security.addProvider(new BouncyCastleProvider());      
         
         KeyPairGenerator    dsaKpg = KeyPairGenerator.getInstance("DSA", "BC");
         
         dsaKpg.initialize(_keyLength);
         
         //
         // this takes a while as the key generator has to generate some DSA params
         // before it generates the key.
         //
         KeyPair             dsaKp = dsaKpg.generateKeyPair();
         
         KeyPairGenerator    elgKpg = KeyPairGenerator.getInstance("ELGAMAL", "BC");
         BigInteger          g = new BigInteger("153d5d6172adb43045b68ae8e1de1070b6137005686d29d3d73a7749199681ee5b212c9b96bfdcfa5b20cd5e3fd2044895d609cf9b410b7a0f12ca1cb9a428cc", 16);
         BigInteger          p = new BigInteger("9494fec095f3b85ee286542b3836fc81a5dd0a0349b4c239dd38744d488cf8e31db8bcb7d33b41abb9e5a33cca9144b1cef332c94bf0573bf047a3aca98cdf3b", 16);
             
         ElGamalParameterSpec         elParams = new ElGamalParameterSpec(p, g);
             
         elgKpg.initialize(elParams);
         
         //
         // this is quicker because we are using pre-generated parameters.
         //
         KeyPair                    elgKp = elgKpg.generateKeyPair();
         
        
         
                   
             FileOutputStream    out1 = new FileOutputStream(_privateRingPath);
             FileOutputStream    out2 = new FileOutputStream(_publicRingPath);
             
             exportKeyPair(out1, out2, dsaKp, elgKp, sIdentity, sPassPhrase.toCharArray(), true);
             //close files
             out1.close();
             out2.close();
         	
    	    	
    }
    
    // getters and setters below
    
    /**
	 * @return the _publicRingPath
	 */
	public static String get_publicRingPath() {
		return _publicRingPath;
	}

	/**
	 * @param ringPath the _publicRingPath to set
	 */
	public static void set_publicRingPath(String ringPath) {
		_publicRingPath = ringPath;
	}

	/**
	 * @return the _privateRingPath
	 */
	public static String get_privateRingPath() {
		return _privateRingPath;
	}

	/**
	 * @param ringPath the _privateRingPath to set
	 */
	public static void set_privateRingPath(String ringPath) {
		_privateRingPath = ringPath;
	}

	/**
	 * @return the _keyLength
	 */
	public static int get_keyLength() {
		return _keyLength;
	}

	/**
	 * @param length the _keyLength to set
	 */
	public static void set_keyLength(int length) {
		_keyLength = length;
	}    
    

}
