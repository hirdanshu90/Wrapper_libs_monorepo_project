package com.accenture.goss.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * This class provides methods for encrypting and decrypting data using a ChaCha20Poly1305 algorithm
 */
public class ChaCha20Poly1305 {

  private static final String ENCRYPT_ALGO = "ChaCha20-Poly1305";
  private static final int NONCE_LEN = 12; // 96 bits, 12 bytes

  /**
   * This method is used to encrypt the text when nonce is not provide
   */
  public byte[] encrypt(byte[] pText, SecretKey key) throws Exception {
    return encrypt(pText, key, getNonce());
  }

  /**
   * This method is used to encrypt the text when nonce is not provided
   */
  public byte[] encrypt(byte[] pText, SecretKey key, byte[] nonce) throws Exception {

    Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

    // IV, initialization value with nonce
    IvParameterSpec iv = new IvParameterSpec(nonce);

    cipher.init(Cipher.ENCRYPT_MODE, key, iv);

    byte[] output=new byte[0];

    try {
      byte[] encryptedText = cipher.doFinal(pText);

      // append nonce to the end of the encrypted text
      output = ByteBuffer.allocate(encryptedText.length + NONCE_LEN)
        .put(encryptedText)
        .put(nonce)
        .array();
    }
    catch(IllegalBlockSizeException e){
      System.out.println("The block data is corrupted"+e.getMessage());
    }
    catch(BadPaddingException e){
      System.out.println("Data or the key is invalid"+e.getMessage());
    }
    return output;
  }

  /**
   * This method is used to encrypt the text when nonce is not provided
   */
  public byte[] decrypt(byte[] cText, SecretKey key) throws Exception {

    ByteBuffer bb = ByteBuffer.wrap(cText);

    // split cText to get the appended nonce
    byte[] encryptedText = new byte[cText.length - NONCE_LEN];
    byte[] nonce = new byte[NONCE_LEN];
    bb.get(encryptedText);
    bb.get(nonce);

    Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

    IvParameterSpec iv = new IvParameterSpec(nonce);
    byte[] output=new byte[0];
    try{
      cipher.init(Cipher.DECRYPT_MODE, key, iv);

      // decrypted text
      output = cipher.doFinal(encryptedText);

    }
    catch(IllegalBlockSizeException e){
      System.out.println("The block data is corrupted"+e.getMessage());
    }
    catch(BadPaddingException e){
      System.out.println("Data or the key is invalid"+e.getMessage());
    }
    return output;
  }

  /**
   * This is a helper method that provides us with a random nonce when nonce is not provide in input
   */
  private static byte[] getNonce() {
    byte[] newNonce = new byte[12];
    new SecureRandom().nextBytes(newNonce);
    return newNonce;
  }

}
