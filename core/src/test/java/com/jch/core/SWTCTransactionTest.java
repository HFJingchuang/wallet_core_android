package com.jch.core;

import com.jch.core.swtc.Config;
import com.jch.core.swtc.EDKeyPair;
import com.jch.core.swtc.JWallet;
import com.jch.core.swtc.K256KeyPair;
import com.jch.core.swtc.bean.AmountInfo;
import com.jch.core.swtc.core.coretypes.AccountID;
import com.jch.core.swtc.core.coretypes.Amount;
import com.jch.core.swtc.core.coretypes.uint.UInt32;
import com.jch.core.swtc.core.types.known.tx.signed.SignedTransaction;
import com.jch.core.swtc.core.types.known.tx.txns.Payment;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SWTCTransactionTest {

    final static String SIGNED_MSG = "120000228000000024000000B16140000000000003E8684000000000002710732102889E27E7A9CCE4AC7639A3B8D0B76802CD6DAA611DA9BB374BEDC425890A10DD74473045022100D161365853365028B47FF4F6062A5502DFD178A1DA9D0B719E80F76F14CFF22B0220498573EDA87C26115B7E3384549DC7CA7B6478C8779B515136862B016B16C2B6811477DAE3BDFE78BB2C82F15CAC65E2472AC65C0C2A8314C776498CA6EAE7DF50C685BE6F219B926FDE8B6FF9EA7D09535754E8BDACE8B4A6E1F1";
    final static String SIGNED_MSG1 = "0xf87664808504a817c800831e848094c8da3c2a4f6e4400a338c0d7927f2cb80ab85500865af3107a400087e6b58be8af9531808081eaa0d353c7f9c9765f54cec7f0dde9a996bdd759b87287dfc93b89bf9b5ed9b11cd4a02d94e31de49c14543a3f5439925e4a1d3552f48d0bc6440f2e375fd0786c6e19";
    final static String SIGNED_MSG2 = "0xf86f65808504a817c800831e848094c8da3c2a4f6e4400a338c0d7927f2cb80ab85500865af3107a400080808081eaa077e67ceec7fda9fe12dc85c4044fa0d2652a8897c8c11d8264586b4cc21dc6bea062b4799dc69646605b3111a84fe38ab47297635d04a6ab992e79181d50a31b1d";

    @Test
    public void createTransaction() {
        String account = "jBvrdYc6G437hipoCiEpTwrWSRBS2ahXN6";
        String to = "jKBCwv4EcyvYtD4PafP17PLpnnZ16szQsC";
        String secret = "snBPyRRpE56ea4QGCpTMVTQWoirT2";
        AmountInfo amountInfo = new AmountInfo();
        amountInfo.setCurrency("SWT");// 转出代币简称
        amountInfo.setValue("0.001");// 转出代币数量
        amountInfo.setIssuer("");// 转出代币银关

        Payment payment = new Payment();
        payment.as(AccountID.Account, account);
        payment.as(AccountID.Destination, to);
        payment.setAmountInfo(amountInfo);
        payment.as(Amount.Fee, String.valueOf(Config.FEE));// 交易燃料费
        payment.sequence(new UInt32(177));// 转出地址序列号
        payment.flags(new UInt32(0));
        List<String> memos = new ArrayList<String>();// 交易备注
        memos.add("SWT转账");
        payment.addMemo(memos);
        SignedTransaction signedTx = payment.sign(secret, false);// 签名
        Assert.assertEquals(SIGNED_MSG, signedTx.tx_blob);
    }

    @Test
    public void signLocal() {
        K256KeyPair keyPair = (K256KeyPair) JWallet.generate(false);
        System.out.println(keyPair.getAddress());

        Payment payment = new Payment();
        payment.as(AccountID.Account, keyPair.getAddress());
        payment.as(AccountID.Destination, "j4fkSm9kUHXtXhA3pj2dNnmSHtuqtT76Ka");
        payment.as(Amount.Amount, "0.001");
        payment.as(Amount.Fee, String.valueOf(Config.FEE));// 交易燃料费
        payment.sequence(new UInt32(1));// 转出地址序列号
        payment.flags(new UInt32(0));
        List<String> memos = new ArrayList<String>();// 交易备注
        memos.add("SWT转账");
        payment.addMemo(memos);
        SignedTransaction signedTx = payment.sign(keyPair.getSecret(), false);// 签名
        SignedTransaction signedTx1 = payment.sign(keyPair.canonicalPriHex(), false);// 签名
        Assert.assertEquals(signedTx.tx_blob, signedTx1.tx_blob);

        EDKeyPair keyPair1 = (EDKeyPair) JWallet.generate(true);

        payment = new Payment();
        payment.as(AccountID.Account, keyPair1.getAddress());
        payment.as(AccountID.Destination, "j4fkSm9kUHXtXhA3pj2dNnmSHtuqtT76Ka");
        payment.as(Amount.Amount, "0.001");
        payment.as(Amount.Fee, String.valueOf(Config.FEE));// 交易燃料费
        payment.sequence(new UInt32(1));// 转出地址序列号
        payment.flags(new UInt32(0));
        payment.addMemo(memos);
        signedTx = payment.sign(keyPair1.getSecret(), true);// 签名
        signedTx1 = payment.sign(keyPair1.canonicalPriHex(), true);// 签名
        System.out.println(signedTx1.tx_blob);
        Assert.assertEquals(signedTx.tx_blob, signedTx1.tx_blob);
    }
}