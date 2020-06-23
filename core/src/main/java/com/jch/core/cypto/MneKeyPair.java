package com.jch.core.cypto;

import java.math.BigInteger;

/**
 * 用于助记词生成KeyStore
 */
public class MneKeyPair implements IKeyPair {
    private final String mnemonics;

    public MneKeyPair(String mnemonics) {
        this.mnemonics = mnemonics;
    }

    @Override
    public BigInteger getPrivateKey() {
        return null;
    }

    @Override
    public BigInteger getPublicKey() {
        return null;
    }

    @Override
    public String getSecret() {
        return mnemonics;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public byte[] canonicalPubBytes() {
        return new byte[0];
    }

    @Override
    public byte[] pub160Hash() {
        return new byte[0];
    }

    @Override
    public ECDSASignature sign(byte[] transactionHash) {
        return null;
    }

    @Override
    public byte[] signMessage(byte[] message) {
        return new byte[0];
    }
}
