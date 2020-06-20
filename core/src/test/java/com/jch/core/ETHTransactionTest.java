package com.jch.core;

import com.jch.core.cypto.ChainIdLong;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.eth.RawTransaction;
import com.jch.core.eth.TransactionEncoder;

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
public class ETHTransactionTest {

    ECKeyPair keyPair;
    final static String SIGNED_MSG = "0xf86b4d8504a817c800831e8480949c5bdcd2c610c80eb8458cd836dd32449ab45ca78086e6b58be8af9529a03fd20219c6a3bd5facdb3f16e3156592fba5e357950a1f71067f44d1718d2629a03b45c980f159177897e07b256bf95040ac2fdc0605de2ce79438fdda323d338b";
    final static String SIGNED_MSG1 = "0xf8724e8504a817c800831e8480949c5bdcd2c610c80eb8458cd836dd32449ab45ca7865af3107a400087e6b58be8af95312aa040a2f296579bf11788d2719b97f6c1d8cc738f60659a324dbe7f7865923ba4d4a05a5703cdac0a5f451823cd5cba44a28d67f110dd258e822fb974c86c459fcb4b";
    final static String SIGNED_MSG2 = "0xf86b508504a817c800831e8480949c5bdcd2c610c80eb8458cd836dd32449ab45ca7865af3107a4000802aa03cd3ead3e1d4d25615608487b4011b832e1298699ada53f8cc68d46148328cbca0212271929c67093b8afeea1368c634e1bb9e48236ffa0bb7e719d200951b8a35";

    @Before
    public void before() {
        keyPair = ECKeyPair.create(Numeric.toBigInt("7D5B4E9A639560D8932329231445EF84E34615406590F5706E887B7CE6AC3373"));
    }


    @Test
    public void createTransaction() {
        BigInteger nonce = new BigInteger("77");
        BigInteger gasPrice = Convert.toWei(new BigDecimal("0.00000002"), Convert.Unit.ETHER).toBigInteger();
        BigInteger gasLimit = new BigInteger("2000000");
        String to = "0x9C5BdcD2C610C80Eb8458CD836dd32449Ab45cA7";
        BigInteger value = Convert.toWei(new BigDecimal("0.0001"), Convert.Unit.ETHER).toBigInteger();
        String data = Numeric.toHexString("测试".getBytes(UTF_8));
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, data);
        byte[] singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.ROPSTEN, keyPair);
        String msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG, msg);

        nonce = new BigInteger("78");
        data = Numeric.toHexString("测试1".getBytes(UTF_8));
        rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data);
        singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.ROPSTEN, keyPair);
        msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG1, msg);

    }

    @Test
    public void createTxTransaction() {
        BigInteger nonce = new BigInteger("80");
        BigInteger gasPrice = Convert.toWei(new BigDecimal("0.00000002"), Convert.Unit.ETHER).toBigInteger();
        BigInteger gasLimit = new BigInteger("2000000");
        String to = "0x9C5BdcD2C610C80Eb8458CD836dd32449Ab45cA7";
        BigInteger value = Convert.toWei(new BigDecimal("0.0001"), Convert.Unit.ETHER).toBigInteger();
        RawTransaction rawTransaction = RawTransaction.createTxTransaction(nonce, gasPrice, gasLimit, to, value);
        byte[] singedTx = TransactionEncoder.signMessage(rawTransaction, ChainIdLong.ROPSTEN, keyPair);
        String msg = Numeric.toHexString(singedTx);
        Assert.assertEquals(SIGNED_MSG2, msg);

    }

}