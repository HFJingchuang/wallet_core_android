package com.jch.core;

import com.jch.core.cypto.CipherException;
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
import com.jch.core.wallet.ChainTypes;
import com.jch.core.wallet.Wallet;
import com.jch.core.wallet.WalletFile;

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
        System.out.println(signedTx.hash);
        Assert.assertEquals(SIGNED_MSG, signedTx.tx_blob);
    }

    @Test
    public void signLocal() throws CipherException {
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
        System.out.println(keyPair1.getSecret());
        System.out.println(keyPair1.getAddress());
        WalletFile walletFile = Wallet.createLight(ChainTypes.SWTC, "'pwd123456'", keyPair1);
        System.out.println(walletFile.toString());
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

    @Test
    public void submit_multisigned() {

        String sign1 = "{\"Account\":\"j3UbbRX36997CWXqYqLUn28qH55v9Dh37n\",\"Amount\":\"1000000\",\"Destination\":\"jpmKEm2sUevfpFjS7QHdT8Sx7ZGoEXTJAz\",\"Fee\":\"20000\",\"Flags\":0,\"Memos\":[{\"Memo\":{\"MemoData\":\"6D756C74697369676E6564207061796D656E742074657374\"}}],\"Sequence\":1,\"Signers\":[{\"Signer\":{\"Account\":\"jhEXgnPdLijQ8Gaqz4FCxUFAQE31LqoNMq\",\"SigningPubKey\":\"028749EB830410A57E89EC765DF551F7006CA19CFEBF4C43EFD87CDDA52976D2FF\",\"TxnSignature\":\"304402207A70842D53F500EC8A103B356CCF5F88CEB5EBA12F6FA1AD9AB74439CEC9396902200E4CD874DF2AC2AA184A6ED9FDA6FECAC15D9AC83D88D593239F66D5B40518A9\"}}],\"SigningPubKey\":\"\",\"TransactionType\":\"Payment\"}";
        String sign2 = "{\"Account\":\"j3UbbRX36997CWXqYqLUn28qH55v9Dh37n\",\"Amount\":\"1000000\",\"Destination\":\"jpmKEm2sUevfpFjS7QHdT8Sx7ZGoEXTJAz\",\"Fee\":\"20000\",\"Flags\":0,\"Memos\":[{\"Memo\":{\"MemoData\":\"6D756C74697369676E6564207061796D656E742074657374\"}}],\"Sequence\":1,\"Signers\":[{\"Signer\":{\"Account\":\"jhEXgnPdLijQ8Gaqz4FCxUFAQE31LqoNMq\",\"SigningPubKey\":\"028749EB830410A57E89EC765DF551F7006CA19CFEBF4C43EFD87CDDA52976D2FF\",\"TxnSignature\":\"304402207A70842D53F500EC8A103B356CCF5F88CEB5EBA12F6FA1AD9AB74439CEC9396902200E4CD874DF2AC2AA184A6ED9FDA6FECAC15D9AC83D88D593239F66D5B40518A9\"}},{\"Signer\":{\"Account\":\"jUv833RRTAZhbUyRzSsAutM9GwbprregiE\",\"SigningPubKey\":\"022EB4FEDEAA5EC1584B673A0B2C4425D0A98A4909EB39C10EC1C40631B0FB9C26\",\"TxnSignature\":\"3045022100A0F05ABA054AF9E2305D3DB1A032468F16EEDFCBA17AF2C724AFAFB7BEE199BA0220541FF8327831057BB0B63F3FECCD252CA873DCE467594FD91A2B095A80217FAE\"}}],\"SigningPubKey\":\"\",\"TransactionType\":\"Payment\"}";

        String account = "j3UbbRX36997CWXqYqLUn28qH55v9Dh37n";
        String to = "jpmKEm2sUevfpFjS7QHdT8Sx7ZGoEXTJAz";
        AmountInfo amountInfo = new AmountInfo();
        amountInfo.setCurrency("SWT");// 转出代币简称
        amountInfo.setValue("1");// 转出代币数量
        amountInfo.setIssuer("");// 转出代币银关

        Payment payment = new Payment();
        payment.as(AccountID.Account, account);
        payment.as(AccountID.Destination, to);
        payment.setAmountInfo(amountInfo);
        payment.as(Amount.Fee, "20000");// 交易燃料费
        payment.sequence(new UInt32(1));// 转出地址序列号
        payment.flags(new UInt32(0));
        List<String> memos = new ArrayList<String>();// 交易备注
        memos.add("multisigned payment test");
        payment.addMemo(memos);
        SignedTransaction signedTx = payment.multiSign("ssmhW3gLLg8wLPzko3dx1LbuDcwCW", false);// 签名
        Assert.assertEquals(sign1, signedTx.txn.toJSON().toString());

        signedTx = signedTx.txn.multiSign("ssXLTUGS6ZFRpGRs5p94BBu6mV1vv", false);// 签名
        Assert.assertEquals(sign2, signedTx.txn.toJSON().toString());
    }

    @Test
    public void submit_multisigned_ed() {
        String testAddress = "jhEXgnPdLijQ8Gaqz4FCxUFAQE31LqoNMq";
        String testSecret = "ssmhW3gLLg8wLPzko3dx1LbuDcwCW";
        String address_ed = "jfqiMxoT228vp3dMrXKnJXo6V9iYEx94pt";
        String secret_ed = "sEdTJSpen5J8ZA7H4cVGDF6oSSLLW2Y";

        String sign1 = "{\"Account\":\"j3UbbRX36997CWXqYqLUn28qH55v9Dh37n\",\"Amount\":\"1000000\",\"Destination\":\"jpmKEm2sUevfpFjS7QHdT8Sx7ZGoEXTJAz\",\"Fee\":\"20000\",\"Flags\":0,\"Memos\":[{\"Memo\":{\"MemoData\":\"6D756C74697369676E6564207061796D656E742074657374\"}}],\"Sequence\":1,\"Signers\":[{\"Signer\":{\"Account\":\"jhEXgnPdLijQ8Gaqz4FCxUFAQE31LqoNMq\",\"SigningPubKey\":\"028749EB830410A57E89EC765DF551F7006CA19CFEBF4C43EFD87CDDA52976D2FF\",\"TxnSignature\":\"304402207A70842D53F500EC8A103B356CCF5F88CEB5EBA12F6FA1AD9AB74439CEC9396902200E4CD874DF2AC2AA184A6ED9FDA6FECAC15D9AC83D88D593239F66D5B40518A9\"}}],\"SigningPubKey\":\"\",\"TransactionType\":\"Payment\"}";
        String sign2 = "{\"Account\":\"j3UbbRX36997CWXqYqLUn28qH55v9Dh37n\",\"Amount\":\"1000000\",\"Destination\":\"jpmKEm2sUevfpFjS7QHdT8Sx7ZGoEXTJAz\",\"Fee\":\"20000\",\"Flags\":0,\"Memos\":[{\"Memo\":{\"MemoData\":\"6D756C74697369676E6564207061796D656E742074657374\"}}],\"Sequence\":1,\"Signers\":[{\"Signer\":{\"Account\":\"jhEXgnPdLijQ8Gaqz4FCxUFAQE31LqoNMq\",\"SigningPubKey\":\"028749EB830410A57E89EC765DF551F7006CA19CFEBF4C43EFD87CDDA52976D2FF\",\"TxnSignature\":\"304402207A70842D53F500EC8A103B356CCF5F88CEB5EBA12F6FA1AD9AB74439CEC9396902200E4CD874DF2AC2AA184A6ED9FDA6FECAC15D9AC83D88D593239F66D5B40518A9\"}},{\"Signer\":{\"Account\":\"jfqiMxoT228vp3dMrXKnJXo6V9iYEx94pt\",\"SigningPubKey\":\"ED68635043BC70DE82272BF5990642400CF79089B2ABCF8EF9D10FFFB96A658763\",\"TxnSignature\":\"6930B6966DED1C8D4064C79228EFDB2B7D6C822BE0719BC66BE58FAAC7C6BFD5D7207F0B82E9AD4EE600045CD7A1CD6417BA51372B10A2948044A8CAA6254B0F\"}}],\"SigningPubKey\":\"\",\"TransactionType\":\"Payment\"}";

        String account = "j3UbbRX36997CWXqYqLUn28qH55v9Dh37n";
        String to = "jpmKEm2sUevfpFjS7QHdT8Sx7ZGoEXTJAz";
        AmountInfo amountInfo = new AmountInfo();
        amountInfo.setCurrency("SWT");// 转出代币简称
        amountInfo.setValue("1");// 转出代币数量
        amountInfo.setIssuer("");// 转出代币银关


        Payment payment = new Payment();
        payment.as(AccountID.Account, account);
        payment.as(AccountID.Destination, to);
        payment.setAmountInfo(amountInfo);
        payment.as(Amount.Fee, "20000");// 交易燃料费
        payment.sequence(new UInt32(1));// 转出地址序列号
        payment.flags(new UInt32(0));
        List<String> memos = new ArrayList<String>();// 交易备注
        memos.add("multisigned payment test");
        payment.addMemo(memos);
        SignedTransaction signedTx = payment.multiSign(testSecret, false);// 签名
        Assert.assertEquals(sign1, signedTx.txn.toJSON().toString());

        signedTx = signedTx.txn.multiSign(secret_ed, false);// 签名
        Assert.assertEquals(sign2, signedTx.txn.toJSON().toString());

    }

}