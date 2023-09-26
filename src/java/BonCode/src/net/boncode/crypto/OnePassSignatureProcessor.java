/**
 * 
 */
package net.boncode.crypto;

//bouncy castle imports
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;

//java imports
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Bilal Soylu
 * 
 */

public class OnePassSignatureProcessor {

	/**
	 * This is the primary function that will create encrypt a file and sign it
	 * with a one pass signature. This leans on an C# example by John Opincar
	 * @author Bilal Soylu
	 * @param targetFileName
	 *            -- file name on drive systems that will contain encrypted content
	 * @param embeddedFileName
	 *            -- the original file name before encryption
	 * @param secretKeyRingInputStream
	 *            -- Private Key Ring File
	 * @param targetFileStream
	 *            -- The stream for the encrypted target file
	 * @param secretKeyPassphrase
	 *            -- The private key password for the key retrieved from
	 *            collection used for signing
	 * @param signPublicKeyInputStream
	 *            -- the public key of the target recipient to be used to
	 *            encrypt the file
	 * @throws Exception
	 */
	public void fEncryptOnePassSignatureLocal(String targetFileName,
			String embeddedFileName, InputStream secretKeyRingInputStream,
			 OutputStream targetFileStream, String secretKeyPassphrase,			
			InputStream signPublicKeyInputStream, InputStream contentStream) throws Exception {
		// ** INIT
		// read public Key from stream (file, if keyring we use the first working key)
		PGPPublicKey encKey = readPublicKey(signPublicKeyInputStream);
		// need to convert the password to a character array
		char[] password = secretKeyPassphrase.toCharArray();
		int BUFFER_SIZE = 1 << 16; // should always be power of 2(one shifted bitwise 16 places)
		//for now we will always do integrity checks and armor file
		boolean armor = true;
		boolean withIntegretyCheck = true;
		//set default provider, we will pass this along
		BouncyCastleProvider bcProvider = new BouncyCastleProvider();

		// armor stream if set
		if (armor)
			targetFileStream = new ArmoredOutputStream(targetFileStream);

		// Init encrypted data generator
		PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(
				SymmetricKeyAlgorithmTags.CAST5, withIntegretyCheck,
				new SecureRandom(), bcProvider);
		encryptedDataGenerator.addMethod(encKey);
		OutputStream encryptedOut = encryptedDataGenerator.open(targetFileStream,new byte[BUFFER_SIZE]);

		// start compression
		PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(
				CompressionAlgorithmTags.ZIP);
		OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);

		//start signature
		//PGPSecretKeyRingCollection pgpSecBundle = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(secretKeyRingInputStream));
		//PGPSecretKey pgpSecKey = pgpSecBundle.getSecretKey(keyId);
		PGPSecretKey pgpSecKey = readSecretKey(secretKeyRingInputStream);
		if (pgpSecKey == null)
			throw new Exception("No secret key could be found in specified key ring collection.");
		PGPPrivateKey pgpPrivKey = pgpSecKey.extractPrivateKey(password,bcProvider);

		PGPSignatureGenerator signatureGenerator = new PGPSignatureGenerator(
				pgpSecKey.getPublicKey().getAlgorithm(),
				HashAlgorithmTags.SHA1, bcProvider);
		
		signatureGenerator.initSign(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);
		// iterate to find first signature to use
		for (@SuppressWarnings("rawtypes")
		Iterator i = pgpSecKey.getPublicKey().getUserIDs(); i.hasNext();) {
			String userId = (String) i.next();
			PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
			spGen.setSignerUserID(false, userId);
			signatureGenerator.setHashedSubpackets(spGen.generate());
			// Just the first one!
			break;
		}
		signatureGenerator.generateOnePassVersion(false).encode(compressedOut);

		// Create the Literal Data generator output stream
		PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
		// get file handle
		File actualFile = new File(targetFileName);
		// create output stream
		OutputStream literalOut = literalDataGenerator.open(compressedOut,
				PGPLiteralData.BINARY, embeddedFileName,
				new Date(actualFile.lastModified()), new byte[BUFFER_SIZE]);
		
		
		// read input file and write to target file using a buffer
		byte[] buf = new byte[BUFFER_SIZE];
		int len;
		while ((len = contentStream.read(buf, 0, buf.length)) > 0) {
			literalOut.write(buf, 0, len);
			signatureGenerator.update(buf, 0, len);
		}
		// close everything down we are done
		literalOut.close();
		literalDataGenerator.close();
		signatureGenerator.generate().encode(compressedOut);
		compressedOut.close();
		compressedDataGenerator.close();
		encryptedOut.close();
		encryptedDataGenerator.close();
		

		if (armor) targetFileStream.close();

	}
	/**
	 * Try to find a public key in the Key File or Key Ring File
	 * We will use the first one for now.
	 * @author Bilal Soylu
	 * @param in -- File Stream to KeyRing or Key
	 * @return first public key
	 * @throws IOException
	 * @throws PGPException
	 */
	private static PGPPublicKey readPublicKey(InputStream in)
			throws IOException, PGPException {
		in = PGPUtil.getDecoderStream(in);

		PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in);

		//
		// we are only looking for the first key that matches
		//

		//
		// iterate through the key rings.
		//
		Iterator rIt = pgpPub.getKeyRings();

		while (rIt.hasNext()) {
			PGPPublicKeyRing kRing = (PGPPublicKeyRing) rIt.next();
			Iterator kIt = kRing.getPublicKeys();

			while (kIt.hasNext()) {
				PGPPublicKey k = (PGPPublicKey) kIt.next();

				if (k.isEncryptionKey()) {
					return k;
				}
			}
		}

		throw new IllegalArgumentException(
				"Can't find encryption key in key ring.");
	}

	
	
	/**
	 * Find first secret key in key ring or key file. 
	 * A secret key contains a private key that can be accessed with a password.
	 * @author Bilal Soylu
	 * @param in -- input Key file or key ring file
	 * @param passwd -- password for key
	 * @return matching private key
	 * @throws IOException
	 * @throws PGPException
	 * @throws NoSuchProviderException
	 */
	private static PGPSecretKey readSecretKey(InputStream in)
			throws IOException, PGPException, NoSuchProviderException {
		
		PGPSecretKey               sKey = null;
		try {
			in = PGPUtil.getDecoderStream(in);
			PGPSecretKeyRingCollection pgpPriv = new PGPSecretKeyRingCollection(in);
	
			// we just loop through the collection till we find a key suitable for
			// decrypt
			Iterator  it = pgpPriv.getKeyRings();       
			PGPSecretKeyRing   pbr = null;
	
	        while (sKey == null && it.hasNext())
	        {
	        	Object readData = it.next();
	        	if (readData instanceof PGPSecretKeyRing) {	        		
		        	pbr = (PGPSecretKeyRing)readData;	            
		            sKey =  pbr.getSecretKey();
	            }
	        }
	        
	        if (sKey == null)
	        {
	            throw new IllegalArgumentException("secret key for message not found.");
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
        return sKey;	
	}	
	
	

	/**
	 * fDecryptOnePassSignature will decrypt a file that was encrypted using
	 * public key, then signed with a private key as one pass signature based on
	 * example of verifyAndDecrypt() by Raul
	 * 
	 * @param encryptedInputStream
	 * @param signPublicKeyInputStream
	 * @param secretKeyInputStream
	 * @param secretKeyPassphrase
	 * @return
	 * @throws Exception
	 */
	public void fDecryptOnePassSignatureLocal(InputStream encryptedInputStream,
			InputStream signPublicKeyInputStream,
			InputStream secretKeyInputStream, String secretKeyPassphrase,
			OutputStream targetStream) throws Exception {

		Security.addProvider(new BouncyCastleProvider());

		// The decrypted results.
		// StringBuffer result = new StringBuffer();
		// The private key we use to decrypt contents.
		PGPPrivateKey privateKey = null;
		// The PGP encrypted object representing the data to decrypt.
		PGPPublicKeyEncryptedData encryptedData = null;

		// Get the list of encrypted objects in the message. The first object in
		// the
		// message might be a PGP marker, however, so we skip it if necessary.
		PGPObjectFactory objectFactory = new PGPObjectFactory(PGPUtil.getDecoderStream(encryptedInputStream));
		Object firstObject = objectFactory.nextObject();
		System.out.println("firstObject is " + firstObject);
		PGPEncryptedDataList dataList = (PGPEncryptedDataList) (firstObject instanceof PGPEncryptedDataList ? firstObject
				: objectFactory.nextObject());

		// Find the encrypted object associated with a private key in our key
		// ring.
		@SuppressWarnings("rawtypes")
		Iterator dataObjectsIterator = dataList.getEncryptedDataObjects();
		PGPSecretKeyRingCollection secretKeyCollection = new PGPSecretKeyRingCollection(
				PGPUtil.getDecoderStream(secretKeyInputStream));
		while (dataObjectsIterator.hasNext()) {
			encryptedData = (PGPPublicKeyEncryptedData) dataObjectsIterator.next();
			System.out.println("next data object is " + encryptedData);
			PGPSecretKey secretKey = secretKeyCollection.getSecretKey(encryptedData.getKeyID());
			
			if (secretKey != null) {
				// This object was encrypted for this key. If the passphrase is
				// incorrect, this will generate an error.
				privateKey = secretKey.extractPrivateKey(secretKeyPassphrase.toCharArray(), "BC");
				break;
			}
		}

		if (privateKey == null) {
			System.out.println();
			throw new RuntimeException("secret key for message not found");
		}

		// Get a handle to the decrypted data as an input stream
		InputStream clearDataInputStream = encryptedData.getDataStream(	privateKey, "BC");
		PGPObjectFactory clearObjectFactory = new PGPObjectFactory(	clearDataInputStream);
		Object message = clearObjectFactory.nextObject();

		System.out.println("message for PGPCompressedData check is " + message);

		// Handle case where the data is compressed
		if (message instanceof PGPCompressedData) {
			PGPCompressedData compressedData = (PGPCompressedData) message;
			objectFactory = new PGPObjectFactory(compressedData.getDataStream());
			message = objectFactory.nextObject();
		}

		System.out.println("message for PGPOnePassSignature check is "	+ message);

		PGPOnePassSignature calculatedSignature = null;
		if (message instanceof PGPOnePassSignatureList) {
			calculatedSignature = ((PGPOnePassSignatureList) message).get(0);
			PGPPublicKeyRingCollection publicKeyRingCollection = new PGPPublicKeyRingCollection(
					PGPUtil.getDecoderStream(signPublicKeyInputStream));
			PGPPublicKey signPublicKey = publicKeyRingCollection
					.getPublicKey(calculatedSignature.getKeyID());
			calculatedSignature.initVerify(signPublicKey, "BC");
			message = objectFactory.nextObject();
		}

		System.out.println("message for PGPLiteralData check is " + message);

		// We should only have literal data, from which we can finally read the
		// decrypted message.
		if (message instanceof PGPLiteralData) {
			InputStream literalDataInputStream = ((PGPLiteralData) message).getInputStream();
			int nextByte;

			while ((nextByte = literalDataInputStream.read()) >= 0) {
				// InputStream.read guarantees to return a byte (range 0-255),
				// so we
				// can safely cast to char.
				calculatedSignature.update((byte) nextByte); // also update
																// calculated
																// one pass
																// signature
				// result.append((char) nextByte);
				// add to file instead of StringBuffer
				targetStream.write((char) nextByte);
			}
			targetStream.close();
		} else {
			throw new RuntimeException("unexpected message type " + message.getClass().getName());
		}

		if (calculatedSignature != null) {
			PGPSignatureList signatureList = (PGPSignatureList) objectFactory.nextObject();
			System.out.println("signature list (" + signatureList.size() + " sigs) is " + signatureList);
			PGPSignature messageSignature = (PGPSignature) signatureList.get(0);
			System.out.println("verification signature is " + messageSignature);
			if (!calculatedSignature.verify(messageSignature)) {
				throw new RuntimeException("signature verification failed");
			}
		}

		if (encryptedData.isIntegrityProtected()) {
			if (encryptedData.verify()) {
				System.out.println("message integrity protection verification succeeded");
			} else {
				throw new RuntimeException("message failed integrity check");
			}
		} else {
			System.out.println("message not integrity protected");
		}

		//close streams
		clearDataInputStream.close();
		
		
	}

}
