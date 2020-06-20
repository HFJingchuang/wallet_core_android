package com.jch.core.cypto;

import java.math.BigInteger;

public interface IKeyPair {
    BigInteger getPrivateKey();

    BigInteger getPublicKey();

    String getSecret();

    String getAddress();

    byte[] canonicalPubBytes();

    byte[] pub160Hash();

    ECDSASignature sign(byte[] transactionHash);

    byte[] signMessage(byte[] message);
}
