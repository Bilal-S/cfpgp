package net.boncode.crypto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;



/**
 * A simple utility class that generates a RSA PGPPublicKey/PGPSecretKey pair.

 * Where identity is the name to be associated with the public key. The keys are placed 
 * in the files provided
 */
public class RSAKeyPairGenerator
{
    private static void exportKeyPair(
        OutputStream    secretOut,
        OutputStream    publicOut,
        PublicKey       publicKey,
        PrivateKey      privateKey,
        String          identity,
        char[]          passPhrase,
        boolean         armor)
        throws IOException, InvalidKeyException, NoSuchProviderException, SignatureException, PGPException
    {    
        if (armor)
        {
            secretOut = new ArmoredOutputStream(secretOut);
        }

        PGPSecretKey    secretKey = new PGPSecretKey(PGPSignature.DEFAULT_CERTIFICATION, PGPPublicKey.RSA_GENERAL, publicKey, privateKey, new Date(), identity, PGPEncryptedData.CAST5, passPhrase, null, null, new SecureRandom(), "BC");
        
        secretKey.encode(secretOut);
        
        secretOut.close();
        
        if (armor)
        {
            publicOut = new ArmoredOutputStream(publicOut);
        }

        PGPPublicKey    key = secretKey.getPublicKey();
        
        key.encode(publicOut);
        
        publicOut.close();
    }
    
    public void fGenerateRSAKeys(String secretPath, String publicPath, String passPhrase, String keyIdentity, int keyLength) throws Exception {
    	
    	 Security.addProvider(new BouncyCastleProvider());

         KeyPairGenerator    kpg = KeyPairGenerator.getInstance("RSA", "BC");
         
         kpg.initialize(keyLength);
         
         KeyPair                    kp = kpg.generateKeyPair();
                 
         //generate armored keys
             
         FileOutputStream    out1 = new FileOutputStream(secretPath);
         FileOutputStream    out2 = new FileOutputStream(publicPath);
         
         exportKeyPair(out1, out2, kp.getPublic(), kp.getPrivate(), keyIdentity, passPhrase.toCharArray(), true);
         
         //close output files
         out1.close();
         out2.close();
    }
    

}
