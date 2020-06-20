package com.jch.core;

import com.jch.core.cypto.ChainIdLong;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.moac.RawTransaction;
import com.jch.core.moac.TransactionEncoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.web3j.compat.Compat.UTF_8;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MOACTransactionTest {

    private ECKeyPair keyPair;

    final static String SIGNED_MSG = "0xf86f62808504a817c800831e848094c8da3c2a4f6e4400a338c0d7927f2cb80ab855008086e6b58be8af95808081e9a00f0978bc27caab6ecba284ecf43f1c9f6fe887bfebfd2420ccfacebbca25bff7a05c3810ee26898720c84ef244107cd690c87defcfcfc386c521fcb37b1e4ce59b";
    final static String SIGNED_MSG1 = "0xf87664808504a817c800831e848094c8da3c2a4f6e4400a338c0d7927f2cb80ab85500865af3107a400087e6b58be8af9531808081eaa0d353c7f9c9765f54cec7f0dde9a996bdd759b87287dfc93b89bf9b5ed9b11cd4a02d94e31de49c14543a3f5439925e4a1d3552f48d0bc6440f2e375fd0786c6e19";
    final static String SIGNED_MSG2 = "0xf86f65808504a817c800831e848094c8da3c2a4f6e4400a338c0d7927f2cb80ab85500865af3107a400080808081eaa077e67ceec7fda9fe12dc85c4044fa0d2652a8897c8c11d8264586b4cc21dc6bea062b4799dc69646605b3111a84fe38ab47297635d04a6ab992e79181d50a31b1d";

    @Before
    public void before() {
        keyPair = ECKeyPair.create(Numeric.toBigInt("da6f39ef485ce945be669a3bcb3a2863330f8ff0d783d3bed260b6af65911e3a"));
    }


    @Test
    public void createTransaction() {
        BigInteger nonce = new BigInteger("98");
        BigInteger gasPrice = Convert.toWei(new BigDecimal("0.00000002"), Convert.Unit.ETHER).toBigInteger();
        BigInteger gasLimit = new BigInteger("2000000");
        String to = "0xC8da3C2A4f6E4400A338c0D7927F2Cb80ab85500";
        BigInteger value = Convert.toWei(new BigDecimal("0.0001"), Convert.Unit.ETHER).toBigInteger();
        String data = Numeric.toHexString("测试".getBytes(UTF_8));
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, data);
        byte[] singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.MC_MAINNET, keyPair);
        String msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG, msg);

        nonce = new BigInteger("100");
        data = Numeric.toHexString("测试1".getBytes(UTF_8));
        rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.MC_MAINNET, keyPair);
        msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG1, msg);

        nonce = new BigInteger("98");
        data = Numeric.toHexString("测试".getBytes(UTF_8));
        rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, data, BigInteger.ZERO, null);
        singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.MC_MAINNET, keyPair);
        msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG, msg);
    }

    @Test
    public void createTxTransaction() {
        BigInteger nonce = new BigInteger("101");
        BigInteger gasPrice = Convert.toWei(new BigDecimal("0.00000002"), Convert.Unit.ETHER).toBigInteger();
        BigInteger gasLimit = new BigInteger("2000000");
        String to = "0xC8da3C2A4f6E4400A338c0D7927F2Cb80ab85500";
        BigInteger value = Convert.toWei(new BigDecimal("0.0001"), Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createTxTransaction(nonce, gasPrice, gasLimit, to, value);
        byte[] singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.MC_MAINNET, keyPair);
        String msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG2, msg);

        rawTransaction = RawTransaction.createTxTransaction(nonce, gasPrice, gasLimit, to, value, BigInteger.ZERO, null);
        singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.MC_MAINNET, keyPair);
        msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG2, msg);
    }


}