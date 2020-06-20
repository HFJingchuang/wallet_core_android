package com.jch.core.swtc;


import com.jch.core.cypto.ECDSASignature;
import com.jch.core.cypto.IKeyPair;
import com.jch.core.utils.HashUtils;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.PrivateKey;

import static com.jch.core.swtc.Config.getB58IdentiferCodecs;

public class EDKeyPair implements IKeyPair {
    public final String secret;
    public final EdDSAPrivateKeySpec keySpec;
    public static final EdDSANamedCurveSpec ed25519 = EdDSANamedCurveTable.getByName("ed25519");

    public EDKeyPair(String secret, EdDSAPrivateKeySpec keySpec) {
        this.secret = secret;
        this.keySpec = keySpec;
    }

    public static EDKeyPair from256Seed(String secret, byte[] seedBytes) {
        EdDSAPrivateKeySpec keySpec = new EdDSAPrivateKeySpec(seedBytes, ed25519);
        return new EDKeyPair(secret, keySpec);
    }

    public static EDKeyPair from128Seed(String secret, byte[] seedBytes) {
        assert seedBytes.length == 16;
        return from256Seed(secret, HashUtils.halfSha512(seedBytes));
    }

    @Override
    public BigInteger getPrivateKey() {
        return Numeric.toBigInt(keySpec.getSeed());
    }

    @Override
    public BigInteger getPublicKey() {
        return Numeric.toBigInt(pubBytes_());
    }

    @Override
    public String getSecret() {
        if (secret != null && !secret.isEmpty()) {
            if (secret.length() < 64) {
                return secret;
            }
        }
        return canonicalPriHex();
    }

    @Override
    public String getAddress() {
        return getB58IdentiferCodecs().encodeAddress(pub160Hash());
    }

    @Override
    public byte[] signMessage(byte[] message) {
        try {
            EdDSAEngine sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            PrivateKey sKey = new EdDSAPrivateKey(keySpec);
            sgr.initSign(sKey);
            sgr.update(message);
            return sgr.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ECDSASignature sign(byte[] transactionHash) {
        return null;
    }

    public boolean verifySignature(byte[] message, byte[] sigBytes) {
        try {
            EdDSAEngine sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
            sgr.initVerify(new EdDSAPublicKey(new EdDSAPublicKeySpec(keySpec.getA(), ed25519)));
            sgr.update(message);
            return sgr.verify(sigBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] pubBytes_() {
        return keySpec.getA().toByteArray();
    }

    public String privHex() {
        return Numeric.toHexStringNoPrefix(keySpec.getSeed()).toUpperCase();
    }

    public String canonicalPubHex() {
        return Numeric.toHexStringNoPrefix(canonicalPubBytes()).toUpperCase();
    }

    public String canonicalPriHex() {
        byte[] pri = new byte[33];
        pri[0] = (byte) 0xed;
        System.arraycopy(keySpec.getSeed(), 0, pri, 1, 32);
        return Numeric.toHexStringNoPrefix(pri).toUpperCase();
    }

    public byte[] canonicalPubBytes() {
        byte[] pub = new byte[33];
        pub[0] = (byte) 0xed;
        System.arraycopy(this.pubBytes_(), 0, pub, 1, 32);
        return pub;
    }

    public byte[] pub160Hash() {
        return HashUtils.SHA256_RIPEMD160(canonicalPubBytes());
    }
}
