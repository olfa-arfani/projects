package mbds.first.endtoend.applet;

import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * @author Olfa
 * This class defines methods for encrypting and decrypting using the DES
 * algorithm and for generating, reading and writing DES keys.
 */
public class EncryptDecryptDES {
	
	
	private static final String UNICODE_FORMAT = "UTF8";	
	private KeySpec ks;
	private SecretKeyFactory skf;
	private Cipher cipher;
	byte[] arrayBytes;	
	private SecretKey key;
    private final String myEncryptionKey = "MGMCAQACEQDj/0UOgifcgm9/i0gK8mpvAg" +
    		"MBAAECEFGh727PaUhI+mIFpVADW6kC" +
    		"CQD4lLnFoBoNMwIJAOrNRi/SjtXVAghrd3" +
    		"jc/HtmywIJALtc4kz28BOhAgkArjx2GPNU2/c=";

    public static final String DES_ENCRYPTION_SCHEME = "DES";
    /**
     * 
     * @param unencryptedString
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
	public String encrypt(String unencryptedString) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		
		System.out.println("****** in encrypt method"+unencryptedString);
		String encryptedString = "";
		try {
			
			
            byte[] byteKey=myEncryptionKey.getBytes();
            System.out.println("this is the size of my key "+byteKey.length);
			arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
			ks = new DESKeySpec(arrayBytes);
			skf = SecretKeyFactory.getInstance(DES_ENCRYPTION_SCHEME);
			cipher = Cipher.getInstance(DES_ENCRYPTION_SCHEME);
			key = skf.generateSecret(ks);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
			byte[] encryptedText = cipher.doFinal(plainText);
			
			System.out.println("--->"+decrypt(encryptedText));
			encryptedString = new String(Base64.encode(encryptedText));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return encryptedString;
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public String decrypt(byte[] message) throws Exception {
	    	
	    	final Cipher decipher = Cipher.getInstance(DES_ENCRYPTION_SCHEME);
	    	decipher.init(Cipher.DECRYPT_MODE, key);

	    	// final byte[] encData = new
	    	// sun.misc.BASE64Decoder().decodeBuffer(message);
	    	final byte[] plainText = decipher.doFinal(message);

	    	return new String(plainText, "UTF-8");
	    }

	
	
	
	
}
