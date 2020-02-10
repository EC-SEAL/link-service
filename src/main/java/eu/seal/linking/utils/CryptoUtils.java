package eu.seal.linking.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class CryptoUtils
{
    public static String generateMd5(String message) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(message.getBytes());

        return DatatypeConverter.printHexBinary(digest).toLowerCase();
    }
}
