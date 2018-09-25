package com.ajoylab.blockchain.wallet.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class BCSecurityKeyStorePreApi23 {

    static
    {
        System.loadLibrary("native-lib");
    }

    public static native String getKeyString();
    public static native String getIvString();

    public static void setData(Context context, String address, String password)
            throws NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException
    {
        SecretKey key = new SecretKeySpec(getKeyString().getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(getIvString().getBytes("UTF-8"));
        byte[] encryptedPassword = encrypt(password, key, iv);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(address + "-pwd", Base64.encodeToString(encryptedPassword, 0));
        editor.commit();
    }

    public static String getData(Context context, String address)
            throws NoSuchPaddingException, UnsupportedEncodingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        byte[] encryptedPassword = Base64.decode(sharedPreferences.getString(address + "-pwd", null), 0);

        SecretKey oldKey = new SecretKeySpec("35TheTru5tWa11ets3cr3tK3y377123!".getBytes("UTF-8"), "AES");
        IvParameterSpec oldIv = new IvParameterSpec("8201va0184a0md8i".getBytes("UTF-8"));
        try
        {
            return decrypt(encryptedPassword, oldKey, oldIv);
        }
        catch (Exception e)
        {
            Log.e("PASSMAN", e.getMessage());

            SecretKey key = new SecretKeySpec(getKeyString().getBytes("UTF-8"), "AES");
            IvParameterSpec iv = new IvParameterSpec(getIvString().getBytes("UTF-8"));
            return decrypt(encryptedPassword, key, iv);
        }
    }

    private static byte[] encrypt(String plainText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, key, iv);
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    private static String decrypt(byte[] cipherText, SecretKey key, IvParameterSpec iv)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(2, key, iv);
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }
}
