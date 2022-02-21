package com.helloworldcoin.crypto;

import com.helloworldcoin.crypto.model.Account;
import com.helloworldcoin.util.ByteUtil;
import com.helloworldcoin.util.LogUtil;
import com.helloworldcoin.util.StringUtil;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * account util
 *
 * @author x.king xdotking@gmail.com
 */
public class AccountUtil {

    private static final ECDomainParameters CURVE;
    private static final SecureRandom SECURE_RANDOM;
    private static final boolean COMPRESSED = true;
    private static final BigInteger HALF_CURVE_ORDER;


    private static final byte VERSION = 0x00;

    static {
        JavaCryptographyExtensionProviderUtil.addBouncyCastleProvider();
    }

    static {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        CURVE = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        SECURE_RANDOM = new SecureRandom();
        HALF_CURVE_ORDER = CURVE.getN().shiftRight(1);
    }

    /**
     * generate random accounts
     */
    public static Account randomAccount() {
        try {
            ECKeyPairGenerator generator = new ECKeyPairGenerator();
            ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(CURVE, SECURE_RANDOM);
            generator.init(keygenParams);
            AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
            ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters) keypair.getPrivate();
            ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();
            BigInteger bigIntegerPrivateKey = ecPrivateKeyParameters.getD();
            byte[] bytesPublicKey = pubParams.getQ().getEncoded(COMPRESSED);
            String privateKey = encodePrivateKey0(bigIntegerPrivateKey);
            String publicKey = encodePublicKey0(bytesPublicKey);
            String publicKeyHash = publicKeyHashFromPublicKey(publicKey);
            String address = addressFromPublicKey(publicKey);
            Account account = new Account(privateKey,publicKey,publicKeyHash,address);
            return account;
        } catch (Exception e) {
            LogUtil.error("生成账户失败。",e);
            throw new RuntimeException(e);
        }
    }
    /**
     * generation account from private key
     */
    public static Account accountFromPrivateKey(String privateKey) {
        try {
            BigInteger bigIntegerPrivateKey = decodePrivateKey0(privateKey);
            byte[] bytesPublicKey = publicKeyFromPrivateKey0(bigIntegerPrivateKey);

            String publicKey = encodePublicKey0(bytesPublicKey);
            String publicKeyHash = publicKeyHashFromPublicKey(publicKey);
            String address = addressFromPublicKey(publicKey);
            Account account = new Account(privateKey,publicKey,publicKeyHash,address);
            return account;
        } catch (Exception e) {
            LogUtil.error("'generation account from private key' error.",e);
            throw new RuntimeException(e);
        }
    }


    /**
     * generate public key hash from public key
     */
    public static String publicKeyHashFromPublicKey(String publicKey) {
        try {
            byte[] bytesPublicKey = decodePublicKey0(publicKey);
            byte[] bytesPublicKeyHash = publicKeyHashFromPublicKey0(bytesPublicKey);
            return ByteUtil.bytesToHexString(bytesPublicKeyHash);
        } catch (Exception e) {
            LogUtil.error("'generate public key hash from public key' error.",e);
            throw new RuntimeException(e);
        }
    }
    /**
     * generation public key hash from address
     */
    public static String publicKeyHashFromAddress(String address) {
        try {
            byte[] bytesAddress = Base58Util.decode(address);
            byte[] bytesPublicKeyHash = ByteUtil.copy(bytesAddress, 1, 20);
            return ByteUtil.bytesToHexString(bytesPublicKeyHash);
        } catch (Exception e) {
            LogUtil.error("'generation public key hash from address' error.",e);
            throw new RuntimeException(e);
        }
    }


    /**
     * generation address from public key
     */
    public static String addressFromPublicKey(String publicKey) {
        try {
            byte[] bytesPublicKey = ByteUtil.hexStringToBytes(publicKey);
            return base58AddressFromPublicKey0(bytesPublicKey);
        } catch (Exception e) {
            LogUtil.error("'generation address from public key' error.",e);
            throw new RuntimeException(e);
        }
    }
    /**
     * generate address from public key hash
     */
    public static String addressFromPublicKeyHash(String publicKeyHash) {
        try {
            byte[] bytesPublicKeyHash = ByteUtil.hexStringToBytes(publicKeyHash);
            return base58AddressFromPublicKeyHash0(bytesPublicKeyHash);
        } catch (Exception e) {
            LogUtil.error("'generate address from public key hash' error.",e);
            throw new RuntimeException(e);
        }
    }


    /**
     * signature
     */
    public static String signature(String privateKey, byte[] bytesMessage) {
        try {
            BigInteger bigIntegerPrivateKey = decodePrivateKey0(privateKey);
            byte[] bytesSignature = signature0(bigIntegerPrivateKey,bytesMessage);
            return ByteUtil.bytesToHexString(bytesSignature);
        } catch (Exception e) {
            LogUtil.debug("signature error.");
            throw new RuntimeException(e);
        }
    }
    /**
     * verify signature
     */
    public static boolean verifySignature(String publicKey, byte[] bytesMessage, byte[] bytesSignature) {
        try {
            byte[] bytesPublicKey = decodePublicKey0(publicKey);
            return verifySignature0(bytesPublicKey,bytesMessage,bytesSignature);
        }catch(Exception e) {
            LogUtil.debug("'verify signature' error.");
            return false;
        }
    }

    /**
     * format private key
     */
    public static String formatPrivateKey(String privateKey) {
        return StringUtil.prefixPadding(privateKey,64,"0");
    }

    /**
     * Is it a P2PKH address ?
     */
    public static boolean isPayToPublicKeyHashAddress(String address) {
        try {
            byte[] bytesAddress = Base58Util.decode(address);
            byte[] bytesPublicKeyHash = new byte[20];
            ByteUtil.copyTo(bytesAddress, 1, 20, bytesPublicKeyHash, 0);
            String base58Address = addressFromPublicKeyHash(ByteUtil.bytesToHexString(bytesPublicKeyHash));
            return StringUtil.equals(base58Address,address);
        }catch (Exception e){
            LogUtil.debug("["+address+"] not P2PKH address.");
            return false;
        }
    }


    /**
     * generate public key hash from public key
     */
    private static byte[] publicKeyHashFromPublicKey0(byte[] publicKey) {
        byte[] bytesPublicKeyHash = Ripemd160Util.digest(Sha256Util.digest(publicKey));
        return bytesPublicKeyHash;
    }
    /**
     * generate public key from private key
     */
    private static byte[] publicKeyFromPrivateKey0(BigInteger bigIntegerPrivateKey) {
        byte[] bytePublicKey = CURVE.getG().multiply(bigIntegerPrivateKey).getEncoded(COMPRESSED);
        return bytePublicKey;
    }
    /**
     * Decode the original private key from the encoded private key
     */
    private static BigInteger decodePrivateKey0(String privateKey) {
        BigInteger bigIntegerPrivateKey = new BigInteger(privateKey,16);
        return bigIntegerPrivateKey;
    }
    /**
     * Decode the original public key from the encoded public key
     */
    private static byte[] decodePublicKey0(String publicKey) {
        byte[] bytesPublicKey = ByteUtil.hexStringToBytes(publicKey);
        return bytesPublicKey;
    }
    /**
     * Encode the original private key to generate an encoded private key
     */
    private static String encodePrivateKey0(BigInteger bigIntegerPrivateKey) {
        String hexPrivateKey = bigIntegerPrivateKey.toString(16);
        return formatPrivateKey(hexPrivateKey);
    }

    /**
     * Encode the original public key to generate an encoded public key
     */
    private static String encodePublicKey0(byte[] bytesPublicKey) {
        String publicKey = ByteUtil.bytesToHexString(bytesPublicKey);
        return publicKey;
    }

    /**
     * signature
     */
    private static byte[] signature0(BigInteger privateKey, byte[] message) {
        try {
            ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            ECPrivateKeyParameters ecPrivateKeyParameters = new ECPrivateKeyParameters(privateKey, CURVE);
            signer.init(true, ecPrivateKeyParameters);
            BigInteger[] signature = signer.generateSignature(message);
            BigInteger s = signature[1];
            if (!isCanonical(s)) {
                s = CURVE.getN().subtract(s);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DERSequenceGenerator seq = new DERSequenceGenerator(bos);
            seq.addObject(new ASN1Integer(signature[0]));
            seq.addObject(new ASN1Integer(s));
            seq.close();
            return bos.toByteArray();
        } catch (Exception e) {
            LogUtil.debug("signature error.");
            throw new RuntimeException(e);
        }
    }

    /**
     * verify signature
     */
    private static boolean verifySignature0(byte[] publicKey, byte[] message, byte[] signature) {
        try {
            ECDSASigner signer = new ECDSASigner();
            ECPublicKeyParameters ecPublicKeyParameters = new ECPublicKeyParameters(CURVE.getCurve().decodePoint(publicKey), CURVE);
            signer.init(false, ecPublicKeyParameters);
            ASN1InputStream decoder = new ASN1InputStream(signature);
            DLSequence seq = (DLSequence) decoder.readObject();
            ASN1Integer r = (ASN1Integer) seq.getObjectAt(0);
            ASN1Integer s = (ASN1Integer) seq.getObjectAt(1);
            if (!isCanonical(s.getValue())) {
                return false;
            }
            decoder.close();
            return signer.verifySignature(message, r.getValue(), s.getValue());
        } catch (Exception e) {
            LogUtil.debug("'verify signature' error.");
            return false;
        }
    }

    /**
     * Generate base58 address from public key
     */
    private static String base58AddressFromPublicKey0(byte[] bytesPublicKey) {
        byte[] publicKeyHash = publicKeyHashFromPublicKey0(bytesPublicKey);
        return base58AddressFromPublicKeyHash0(publicKeyHash);
    }

    /**
     * generate base58 address from public key hash
     */
    private static String base58AddressFromPublicKeyHash0(byte[] bytesPublicKeyHash) {

        //Address version number (1 byte) and public key hash (20 bytes)
        byte[] bytesVersionAndPublicKeyHash = ByteUtil.concatenate(new byte[]{VERSION},bytesPublicKeyHash);

        //Address check code (4 bytes)
        byte[] bytesCheckCode = ByteUtil.copy(Sha256Util.doubleDigest(bytesVersionAndPublicKeyHash), 0, 4);

        //Address (25 bytes) = address version number (1 byte) + public key hash (20 bytes) + address check code (4 bytes)
        byte[] bytesAddress = ByteUtil.concatenate(bytesVersionAndPublicKeyHash,bytesCheckCode);

        //Encode address with Base58
        String base58Address = Base58Util.encode(bytesAddress);
        return base58Address;
    }

    /**
     * Here is to solve the transaction malleability attack.
     * Returns true if the S component is "low", that means it is below {@link ECKey#HALF_CURVE_ORDER}. See <a
     * href="https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures">BIP62</a>.
     * 参考：bitcoinj-core-0.15.10.jar org.bitcoinj.core.ECKey.isCanonical()
     */
    private static boolean isCanonical(BigInteger s) {
        return s.compareTo(HALF_CURVE_ORDER) <= 0;
    }
}