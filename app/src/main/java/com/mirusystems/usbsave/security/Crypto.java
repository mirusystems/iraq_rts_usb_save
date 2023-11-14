package com.mirusystems.usbsave.security;

import android.util.Base64;
import android.util.Log;

import com.mirusystems.security.Base64Util;
import com.mirusystems.usbsave.utility.ByteUtil;
import com.mirusystems.utility.MiruUtility;
import com.mirusystems.utility.Trace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static final String TAG = Crypto.class.getSimpleName();

    private static final Trace trace = Trace.getInstance();
    private static volatile Key PK1 = null;
    private static volatile Key PK2 = null;
    private static volatile Key BEK = null;
    private static volatile Key BSK = null;
    private static volatile Key CSK = null;
    private static volatile Key VSK = null;
    private static volatile Key VPK = null;

    private static volatile Key PK3 = null;
    private static volatile Key BSK2 = null;
    private static volatile Key BPK2 = null;
    private static volatile Key CSK2 = null;
    private static volatile Key CPK2 = null;
    private static final byte[] CIPHER_BYTE = {124, -51, 67, -45, 99, 37, 85, 113, -22, 36, 92, 59, 12, -104, 19, -86};

    public Crypto() {
    }

    public static void algorithms() {
        Provider[] var3;
        int var2 = (var3 = Security.getProviders()).length;

        for(int var1 = 0; var1 < var2; ++var1) {
            Provider p = var3[var1];
            trace.d("\n****************************************");
            trace.d("\n" + p.getName());
            trace.d("\n" + p.getInfo());
            trace.d("\n----------------------------------------");
            Iterator var5 = p.stringPropertyNames().iterator();

            while(var5.hasNext()) {
                String key = (String)var5.next();
                trace.d("\n" + key + " = " + p.getProperty(key));
            }

            trace.d("\n****************************************");
        }

    }

    public static String getPlainContents(String contents) {
        String plaincontents = contents;
        if(contents != null) {
            try {
                byte[] decbytes = Base64Util.decode(contents);
                byte[] plainbytes = AESDecryptBuffer(CIPHER_BYTE, decbytes);
                if (plainbytes != null) {
                    plaincontents = new String(plainbytes, 0, plainbytes.length);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return(plaincontents);
    }

    public static byte[] SHA1Digest(byte[] data) throws Exception {
        return MessageDigest.getInstance("SHA1").digest(data);
    }

    /**
     * SHA-256 digest.
     *
     * @param data Data to encrypt.
     * @return Data encrypted.
     * @throws Exception Exception encrypting data.
     */
    public static byte[] SHA1_digest(byte[] data) throws Exception {
        return MessageDigest.getInstance("SHA1").digest(data);
    }

    public static byte[] SHA2_digest(byte[] data) throws Exception {
        return MessageDigest.getInstance("SHA-256").digest(data);
    }

    public static String SHA1_digestStrg(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.reset();
        digest.update(data);
        return String.format("%040x", new BigInteger(1, digest.digest()));
    }

    public static String SHA2_digestStrg(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        digest.update(data);
        return String.format("%064x", new BigInteger(1, digest.digest()));
    }

    public static String GetHashTable(String src_filepath) {
        try {
            FileInputStream fis = new FileInputStream(new File(src_filepath));
            byte[] byteArray = new byte[16384];
            int bytesCount = 0;
//            MessageDigest digest = MessageDigest.getInstance("SHA1");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            };
            fis.close();
//            return String.format("%040x", new BigInteger(1, digest.digest()));
            return String.format("%064x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return(null);
        }
    }

    public static String GetConfigHashTable(String src_filepath) {
        try {
            FileInputStream fis = new FileInputStream(new File(src_filepath));
            byte[] byteArray = new byte[16384];
            int bytesCount = 0;
//            MessageDigest digest = MessageDigest.getInstance("SHA1");
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
                break;
            };
            fis.close();
//            return String.format("%040x", new BigInteger(1, digest.digest()));
            return String.format("%064x", new BigInteger(1, digest.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return(null);
        }
    }

//    public static String GetHashTable(String src_filepath) {
//        try {
//            FileInputStream fis = new FileInputStream(new File(src_filepath));
//            byte[] byteArray = new byte[16384];
//            String hashcode = null;
//            int bytesCount = fis.read(byteArray);
//
//            if(bytesCount != -1) {
//                hashcode = HmacSHA2Hash(Crypto.getPlainContents(SYMKEYS.HMAC_KEY), byteArray);
//            };
//            fis.close();
//            return hashcode;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return(null);
//        }
//    }

    public static String HmacSHA2Hash(String key, byte[] data) throws Exception {
        if(key == null || data == null)
            return null;
        Mac sha2Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha2Hmac.init(secret_key);
        byte[] result = sha2Hmac.doFinal(data);
        if(result == null) {
            Log.d(TAG, "HmacSHA2Hash: result is null !!");
        }
        return Base64.encodeToString(result, Base64.NO_WRAP);
    }

    public static byte[] TDES_encrypt(byte[] data, Key key) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(1, (SecretKey)key, iv);
        return cipher.doFinal(data);
    }

    public static byte[] TDES_decrypt(byte[] data, Key key) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(2, (SecretKey)key, iv);
        return cipher.doFinal(data);
    }

    public static byte[] AES_encrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(1, (SecretKey)key);
        return cipher.doFinal(data);
    }

    public static byte[] AES_decrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        cipher.init(2, (SecretKey)key);
        return cipher.doFinal(data);
    }

    public static byte[] RSA_sign(byte[] data, Key key) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign((PrivateKey)key);
        signature.update(data);
        return signature.sign();
    }

    public static boolean RSA_verify(byte[] data, byte[] sign, Key key) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify((PublicKey)key);
        signature.update(data);
        return signature.verify(sign);
    }

    public static byte[] RSASign(byte[] data, Key key) throws Exception {
        Signature signature = Signature.getInstance(CertificateUtils.SHA256_WITH_RSA);
        signature.initSign((PrivateKey) key);
        signature.update(data);
        byte[] bytesign = signature.sign();

//        Log.d(TAG, "RSASign: key="+ByteUtil.hexify(key.getEncoded()));
//        Log.d(TAG, "RSASign: sign="+ByteUtil.hexify(bytesign));
        return bytesign;
    }

    public static boolean RSAVerify(byte[] data, byte[] sign, Key key) throws Exception {
        Signature signature = Signature.getInstance(CertificateUtils.SHA256_WITH_RSA, CertificateUtils.getProvider());

//        Log.d(TAG, "RSAVerify: key="+ByteUtil.hexify(key.getEncoded()));
//        Log.d(TAG, "RSAVerify: sign="+ByteUtil.hexify(sign));
//        Log.d(TAG, "RSAVerify: data="+ByteUtil.hexify(data));
        signature.initVerify((PublicKey) key);
        signature.update(data);

        boolean result = false;
        try {
            result = signature.verify(sign);
        } catch (Exception ex) {
            trace.d("\nSignature fail: " + ex.getMessage());
            trace.e(ex);
        }
        return result;
    }

    public static byte[] TDESEncrypt(byte[] data, Key key) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);

        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, (SecretKey) key, iv);

        return cipher.doFinal(data);
    }

    public static byte[] AESEncryptBuffer(byte[] enc_key, byte[] srcBuffer) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey secretKey = new SecretKeySpec(enc_key.clone(), "AES");
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return cipher.doFinal(srcBuffer, 0, srcBuffer.length);
        } catch (Exception e) {
            return(null);
        }
    }

    public static byte[] AESDecryptBuffer(byte[] dec_key, byte[] encBuffer) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey secretKey = new SecretKeySpec(dec_key.clone(), "AES");
            IvParameterSpec iv = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            return cipher.doFinal(encBuffer, 0, encBuffer.length);
        } catch (Exception e) {
            return(null);
        }
    }

    public static byte[] AESEncryptBuffer(int modes, byte[] enc_key, byte[] srcBuffer, int srclength) {
        try {
            Cipher cipher;
            SecretKey secretKey = new SecretKeySpec(enc_key.clone(), "AES");
            if(modes == MiruUtility.ECB) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return cipher.doFinal(srcBuffer, 0, srclength);
            } else {
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
                return cipher.doFinal(srcBuffer, 0, srclength);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return(null);
        }
    }

    public static byte[] FileToAESEncryptBytes(byte[] enc_key, String src_filepath) {
        byte[] plainText = MiruUtility.convertFileToByteArray(src_filepath);
        if(plainText == null) {
            Log.d(TAG, "AESEncryptFile: plainText is null");
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            SecretKey secretKey = new SecretKeySpec(enc_key.clone(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(plainText);
        } catch (Exception e) {
        }
        return null;
    }

    public static byte[] FileToAESDecryptBytes(byte[] enc_key, String src_filepath) {
        byte[] ciperText = MiruUtility.convertFileToByteArray(src_filepath);
        if(ciperText == null) {
            Log.d(TAG, "AESEncryptFile: plainText is null");
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            SecretKey secretKey = new SecretKeySpec(enc_key.clone(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(ciperText);
        } catch (Exception e) {
        }
        return null;
    }

    public static int AESEncryptFile(int modes, byte[] enc_key, String src_filepath, String enc_filepath) {
        byte[] plainText = MiruUtility.convertFileToByteArray(src_filepath);
        if(plainText == null) {
            Log.d(TAG, "AESEncryptFile: plainText is null");
            return (0);
        }
        byte[] ciperText = AESEncryptBuffer(modes, enc_key, plainText, plainText.length);
        if(ciperText == null) {
            Log.d(TAG, "AESEncryptFile: ciperText is null");
            return (0);
        }

        MiruUtility.convertByteArrayToFile(ciperText, ciperText.length, enc_filepath);
        Log.d(TAG, "AESEncryptFile: ciper length="+ciperText.length);
        return(ciperText.length);
    }

    public static int AESEncryptFileWithLen(int modes, byte[] enc_key, String src_filepath, String enc_filepath) {
        byte[] plainText = MiruUtility.convertFileToByteArray(src_filepath);
        if(plainText == null) {
            Log.d(TAG, "AESEncryptFile: plainText is null");
            return (0);
        }
        byte[] ciperText = AESEncryptBuffer(modes, enc_key, plainText, plainText.length);
        if(ciperText == null) {
            Log.d(TAG, "AESEncryptFile: ciperText is null");
            return (0);
        }

        byte[] lenbytes = ByteUtil.IntToByteArray(plainText.length);
        MiruUtility.convertByteArrayToFile(lenbytes, ciperText, null, 0, null, null, enc_filepath);
        Log.d(TAG, "AESEncryptFileWithLen: ciper length="+ciperText.length+", orglen="+plainText.length);
        return(ciperText.length);
    }

    public static byte[] AESDecryptBuffer(int modes, byte[] dec_key, byte[] encBuffer, int enclength) {
        try {
            Cipher cipher;
            SecretKey secretKey = new SecretKeySpec(dec_key.clone(), "AES");
            if(modes == MiruUtility.ECB) {
                cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                return cipher.doFinal(encBuffer, 0, enclength);
            } else {
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
                return cipher.doFinal(encBuffer, 0, enclength);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return(null);
        }
    }

    public static int AESDecryptFile(int modes, byte[] dec_key, String enc_filepath, String dec_filepath) {
        byte[] ciperText = MiruUtility.convertFileToByteArray(enc_filepath);
        if(ciperText == null) {
            Log.d(TAG, "AESDecryptFile: ciperText is null");
            return (0);
        }
        byte[] plainText = AESDecryptBuffer(modes, dec_key, ciperText, ciperText.length);
        if(plainText == null) {
            Log.d(TAG, "AESDecryptFile: plainText is null");
            return (0);
        }
        MiruUtility.convertByteArrayToFile(plainText, plainText.length, dec_filepath);
        Log.d(TAG, "AESDecryptFile: plain length="+plainText.length);
        return(plainText.length);
    }


    public static byte[] generateHostKey(byte[] shData, String stid)
    {
        byte[] key2 = stid.getBytes();
        byte[] keyData = new byte[32];
        int k = 0;

        Log.d("SEC", "Hash Bytes=["+ ByteUtil.toHexString(shData)+"]");
        for(int i = 0 ; i < 32 ; ++i)
        {
            if(i > 15) {
                if(i >= shData.length)
                    keyData[i] = ((byte) (0x80+i));
                else
                    keyData[i] = ((byte) (~shData[i]));
            } else {
                keyData[i] = ((i % 2) == 0) ? (shData[i]) : ((byte) (~key2[k]));
                if ((i % 2) == 1) k++;
            }
        }
        Log.d("SEC", "EncHostKey=["+ ByteUtil.toHexString(keyData)+"]");
        return(keyData);
    }

    public void createiv() {
        /*
         * iv create
         */
//        String hash;
//        try {
//            hash = pcosUtil.getHash(creationDateTime.getBytes());
//
//        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
//            throw e;
//        }
    }

    public static boolean decryptCiperContentsFile(String ciperfilepath, String plainfilepath) {
        if (new File(ciperfilepath).exists() == false)
        {
            Log.d(TAG, "decryptCiperContentsFile: "+ciperfilepath+" file not found !");
            return(false);
        }
        File f = new File(plainfilepath);
        if (f.exists() == true)
            f.delete();

        FileReader fr = null;
        BufferedReader br = null;
        try
        {
            fr = new FileReader(ciperfilepath);
            br = new BufferedReader(fr);
            int linecnt = 0;
            while(true)
            {
                String line = br.readLine();
                if(line == null)
                    break;
                String plaintext = getPlainContents(line);
                Log.d(TAG, "decryptCiperContentsFile: enc="+line+", plain="+plaintext);
                if(plaintext.length() < 500)
                    ByteUtil.AppendStrToFile(plaintext, plainfilepath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return(false);
        } finally{
            // BufferedReader FileReader를 닫아준다.
            if(br != null) try{br.close();}catch(IOException e){}
            if(fr != null) try{fr.close();}catch(IOException e){}
        }
        return(true);
    }

    /**
     * AES encryptation.
     *
     * @param data Data to encrypt.
     * @return Data encrypted.
     * @throws Exception Exception encrypting data.
     */
    public static byte[] AESEncryptHost(byte[] data, String stationid, byte[] hashData) throws Exception {

        Log.d("CRYPTO", "AESEncryptHost() stationid=["+stationid+"], hashData=["+ByteUtil.hexify(hashData)+"]");
        Cipher ce = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] key = generateHostKey(hashData, stationid);
//        Log.d(TAG, "AESEncryptHost: hashData" + ByteUtil.toHexString(hashData));
//        Log.d(TAG, "AESEncryptHost: key " + ByteUtil.toHexString(key));
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        String ivhash = "2023-12-18 14:14:14";
        String hash =  SHA2_digestStrg(ivhash.getBytes("UTF-8"));
        String ivString = hash.substring(0, 8)+hash.substring(hash.length()-8, hash.length());
        Log.d(TAG, "AESEncryptHost:ivString " + ivString);
        byte [] IV_VECTOR = ivString.getBytes();
        //SYMKEYS.IV_VECTOR 대체
        IvParameterSpec iv = new IvParameterSpec(IV_VECTOR);
//       IvParameterSpec iv = new IvParameterSpec(new byte[16]);
//        Log.d("SEC", "IV=["+ ByteUtil.toHexString(iv.getIV())+"]");
        ce.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return ce.doFinal(data);
    }


    public static byte[] AESDecryptHost(byte[] data, String stationid, byte[] hashData) throws Exception {

        Log.d("CRYPTO", "AESEncryptHost() stationid=["+stationid+"], hashData=["+ByteUtil.hexify(hashData)+"]");
        Cipher ce = Cipher.getInstance("AES/CBC/PKCS7Padding");
        byte[] key = generateHostKey(hashData, stationid);
//        Log.d(TAG, "AESEncryptHost: hashData" + ByteUtil.toHexString(hashData));
//        Log.d(TAG, "AESEncryptHost: key " + ByteUtil.toHexString(key));
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        String ivhash = "2023-12-18 14:14:14";
        String hash =  SHA2_digestStrg(ivhash.getBytes("UTF-8"));
        String ivString = hash.substring(0, 8)+hash.substring(hash.length()-8, hash.length());
        Log.d(TAG, "AESEncryptHost:ivString " + ivString);
        byte [] IV_VECTOR = ivString.getBytes();
        //SYMKEYS.IV_VECTOR 대체
        IvParameterSpec iv = new IvParameterSpec(IV_VECTOR);
//       IvParameterSpec iv = new IvParameterSpec(new byte[16]);
//        Log.d("SEC", "IV=["+ ByteUtil.toHexString(iv.getIV())+"]");
        ce.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return ce.doFinal(data);
    }


    public static Key getPK1() throws Exception {
        return PK1;
    }

    public static Key getPK2() throws Exception {
        return PK2;
    }

    public static Key getPK3() throws Exception {
        return PK3;
    }

    public static Key getCSK() throws Exception {
        return CSK;
    }

    public static Key getCSK(byte[] vers) throws Exception {
        if (vers != null && (ByteUtil.hexify(vers).equals("00 03") || ByteUtil.hexify(vers).equals("00 02"))) {
            Log.d(TAG, "getCSK: CSK2");
            return CSK2;
        }
        Log.d(TAG, "getCSK: CSK");
        return CSK;
    }

    public static Key getTSK() throws Exception {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(ByteUtil.getBytes("30 81 9F 30 0D 06 09 2A 86 48 86 F7 0D 01 01 01 05 00 03 81 8D 00 30 81 89 02 81 81 00 A7 41 7A 37 89 C7 8D D4 79 EB 37 62 F3 E0 DE FD 7D 99 7C EB 2A 8A 85 5E 32 88 ED 80 80 5F BD AB 2D 2F 6F 06 04 D8 E6 80 59 A3 DD C7 D2 9E 90 B1 94 5B 9B BF EB 7B EE A9 CA DC A0 2E 0F 38 28 04 A5 FC CA 9E AD E1 4D 4D 6D 74 D3 C0 AB 3C F2 66 72 37 2F A8 E6 3A 1D E4 81 85 CF 7F 5B 72 51 34 83 89 AC 1C 6C 4D 54 F7 92 60 00 B7 D5 DC 05 D4 A2 FC 76 0F 53 FC E9 2D ED BC 8E 3C F0 12 00 ED 02 03 01 00 01")));
    }

    public static Key getBEK() throws Exception {
        return BEK;
    }

    public static Key getBSK(byte[] vers) throws Exception {
        if (vers != null && (ByteUtil.hexify(vers).equals("00 03") || ByteUtil.hexify(vers).equals("00 02"))) {
            Log.d(TAG, "getBSK: BSK2");
            return BSK2;
        }
        Log.d(TAG, "getBSK: BSK");
        return BSK;
    }

    public static Key getCPK(byte[] vers) throws Exception {
        if(vers != null && (ByteUtil.hexify(vers).equals("00 03") || ByteUtil.hexify(vers).equals("00 02"))) {
            Log.d(TAG, "getCPK: CPK2");
            return CPK2;
        }
        Log.d(TAG, "getCPK: VPK");
        return VPK;
    }

    public static Key getBPK(byte[] vers) throws Exception {
        if(vers != null && (ByteUtil.hexify(vers).equals("00 03") || ByteUtil.hexify(vers).equals("00 02"))) {
            Log.d(TAG, "getBPK: BPK2");
            return BPK2;
        }
        Log.d(TAG, "getBPK: VPK");
        return VPK;
    }

    public static Key getBSK() throws Exception {
        return BSK;
    }

    public static Key getVSK() throws Exception {
        return VSK;
    }

    public static Key getVPK() throws Exception {
        return VPK;
    }


}

