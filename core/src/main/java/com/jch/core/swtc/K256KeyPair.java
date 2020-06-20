package com.jch.core.swtc;

import com.jch.core.cypto.ECDSASignature;
import com.jch.core.cypto.IKeyPair;
import com.jch.core.utils.B16;
import com.jch.core.utils.HashUtils;
import com.jch.core.utils.Sha512;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static com.jch.core.swtc.Config.getB58IdentiferCodecs;

public class K256KeyPair implements IKeyPair {
    private final static String EC_PREFIX = "00";
    String secretKey;
    BigInteger privateKey, publicKey;
    byte[] pubBytes, priBytes;

    // See https://wiki.ripple.com/Account_Family

    public K256KeyPair(String secretKey, BigInteger privateKey, BigInteger publicKey) {
        this.secretKey = secretKey;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.pubBytes = publicKey.toByteArray();
        this.priBytes = privateKey.toByteArray();
    }

    @Override
    public BigInteger getPublicKey() {
        return publicKey;
    }

    @Override
    public String getSecret() {
        if (secretKey != null && !secretKey.isEmpty()) {
            if (secretKey.length() < 64) {
                return secretKey;
            }
        }
        return canonicalPriHex();
    }

    @Override
    public BigInteger getPrivateKey() {
        return privateKey;
    }

    @Override
    public String getAddress() {
        return getB58IdentiferCodecs().encodeAddress(pub160Hash());
    }

    @Override
    public ECDSASignature sign(byte[] transactionHash) {
        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey, SECP256K1.params());
        signer.init(true, privKey);
        BigInteger[] sigs = signer.generateSignature(transactionHash);
        BigInteger r = sigs[0], s = sigs[1];
        BigInteger otherS = SECP256K1.order().subtract(s);
        if (s.compareTo(otherS) == 1) {
            s = otherS;
        }
        return new ECDSASignature(r, s);

    }

    @Override
    public byte[] signMessage(byte[] message) {
        byte[] hash = HashUtils.halfSha512(message);
        ECDSASignature signature = sign(hash);
        byte[] der = signature.encodeToDER();
        if (!ECDSASignature.isStrictlyCanonical(der)) {
            throw new IllegalStateException("Signature is not strictly canonical");
        }
        return der;
    }

    public boolean verifySignature(byte[] message, byte[] sigBytes, BigInteger publicKey) {
        byte[] hash = HashUtils.halfSha512(message);
        return K256KeyPair.verify(hash, sigBytes, publicKey);
    }

    /**
     * @param secretKey secret point on the curve as BigInteger
     * @return corresponding public point
     */
    public static byte[] getPublic(BigInteger secretKey) {
        return SECP256K1.basePointMultipliedBy(secretKey);
    }

    /**
     * @param privateGen secret point on the curve as BigInteger
     * @return the corresponding public key is the public generator
     * (aka public root key, master public key).
     * return as byte[] for convenience.
     */
    public static byte[] computePublicGenerator(BigInteger privateGen) {
        return getPublic(privateGen);
    }

    public static BigInteger computePublicKey(BigInteger secret) {
        return Numeric.toBigInt(getPublic(secret));
    }

    public static BigInteger computePrivateGen(byte[] seedBytes) {
        return generateKey(seedBytes, null);
    }

    public static byte[] computePublicKey(byte[] publicGenBytes, int accountNumber) {
        ECPoint rootPubPoint = SECP256K1.curve().decodePoint(publicGenBytes);
        BigInteger scalar = generateKey(publicGenBytes, accountNumber);
        ECPoint point = SECP256K1.basePoint().multiply(scalar);
        ECPoint offset = rootPubPoint.add(point);
        return offset.getEncoded(true);
    }

    public static BigInteger computeSecretKey(BigInteger privateGen, byte[] publicGenBytes, int accountNumber) {
        return generateKey(publicGenBytes, accountNumber).add(privateGen).mod(SECP256K1.order());
    }

    /**
     * @param seedBytes     - a bytes sequence of arbitrary length which will be hashed
     * @param discriminator - nullable optional uint32 to hash
     * @return a number between [1, order -1] suitable as a private key
     */
    public static BigInteger generateKey(byte[] seedBytes, Integer discriminator) {
        BigInteger key = null;
        for (long i = 0; i <= 0xFFFFFFFFL; i++) {
            Sha512 sha512 = new Sha512().add(seedBytes);
            if (discriminator != null) {
                sha512.addU32(discriminator);
            }
            sha512.addU32((int) i);
            byte[] keyBytes = sha512.finish256();
            key = Numeric.toBigInt(keyBytes);
            if (key.compareTo(BigInteger.ZERO) == 1 && key.compareTo(SECP256K1.order()) == -1) {
                break;
            }
        }
        return key;
    }

    public static boolean verify(byte[] data, byte[] sigBytes, BigInteger pub) {
        ECDSASignature signature = ECDSASignature.decodeFromDER(sigBytes);
        if (signature == null) {
            return false;
        }
        ECDSASigner signer = new ECDSASigner();
        ECPoint pubPoint = SECP256K1.curve().decodePoint(pub.toByteArray());
        ECPublicKeyParameters params = new ECPublicKeyParameters(pubPoint, SECP256K1.params());
        signer.init(false, params);
        return signer.verifySignature(data, signature.r, signature.s);
    }

    public byte[] canonicalPubBytes() {
        return pubBytes;
    }

    public byte[] pub160Hash() {
        return HashUtils.SHA256_RIPEMD160(pubBytes);
    }

    public String canonicalPubHex() {
        return EC_PREFIX + B16.toStringTrimmed(pubBytes);
    }

    public String privHex() {
        return B16.toStringTrimmed(priBytes);
    }

    public String canonicalPriHex() {
        return EC_PREFIX + privHex();
    }
}
