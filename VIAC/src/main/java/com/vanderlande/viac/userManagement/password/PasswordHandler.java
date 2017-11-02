package com.vanderlande.viac.userManagement.password;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * The Class PasswordHandler.
 * 
 * @author dedhor
 * 
 *         This class handles password-specific tasks. You can also generate a new random password (e.g. for a new
 *         user).
 */
public class PasswordHandler
{

    /** The instance. */
    private static PasswordHandler instance;

    /**
     * Instantiates a new password handler.
     */
    private PasswordHandler()
    {
    }

    /**
     * Gets the single instance of PasswordHandler.
     *
     * @return single instance of PasswordHandler
     */
    public static PasswordHandler getInstance()
    {
        if (instance == null)
            instance = new PasswordHandler();
        return instance;
    }

    /**
     * Checks if is valid password.
     *
     * @param attemptedPassword
     *            the attempted password
     * @param encryptedPassword
     *            the encrypted password
     * @param salt
     *            the salt
     * @return true, if is valid password
     * @throws Exception
     *             the exception
     */
    public boolean isValidPassword(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
        throws Exception
    {
        return Arrays.equals(encryptedPassword, getEncryptedPassword(attemptedPassword, salt));
    }

    /**
     * Gets the encrypted password.
     *
     * @param password
     *            the password
     * @param salt
     *            the salt
     * @return the encrypted password
     * @throws Exception
     *             the exception
     */
    public byte[] getEncryptedPassword(String password, byte[] salt)
        throws Exception
    {
        // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
        // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
        String algorithm = "PBKDF2WithHmacSHA1";
        // SHA-1 generates 160 bit hashes, so that's what makes sense here
        int derivedKeyLength = 160;
        // Pick an iteration count that works for you. The NIST recommends at
        // least 1,000 iterations:
        // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
        int iterations = 20000;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
        return f.generateSecret(spec).getEncoded();
    }

    /**
     * Generate salt.
     *
     * @return the byte[]
     * @throws NoSuchAlgorithmException
     *             the no such algorithm exception
     */
    public byte[] generateSalt()
        throws NoSuchAlgorithmException
    {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

        // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
        byte[] salt = new byte[8];
        random.nextBytes(salt);

        return salt;
    }

    /**
     * Gets the random password.
     *
     * @param length
     *            the length
     * @return the random password
     */
    public String getRandomPassword(int length)
    {
        if (length > 128)
        {
            length = 128;
        }
        if (length < 1)
        {
            length = 1;
        }

        SecureRandom random = new SecureRandom();
        return new BigInteger(1024, random).toString(32).substring(0, length);
    }
}
