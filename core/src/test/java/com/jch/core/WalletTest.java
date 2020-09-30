package com.jch.core;

import com.jch.core.btc.BWallet;
import com.jch.core.cypto.CipherException;
import com.jch.core.cypto.ECKeyPair;
import com.jch.core.cypto.Keys;
import com.jch.core.swtc.EDKeyPair;
import com.jch.core.swtc.JWallet;
import com.jch.core.swtc.K256KeyPair;
import com.jch.core.wallet.ChainTypes;
import com.jch.core.wallet.Wallet;
import com.jch.core.wallet.WalletFile;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.TransactionWitness;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bitcoinj.wallet.Protos;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.WalletTransaction;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Test;
import org.web3j.utils.Numeric;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static net.i2p.crypto.eddsa.Utils.bytesToHex;
import static net.i2p.crypto.eddsa.Utils.hexToBytes;
import static org.bitcoinj.core.Utils.HEX;
import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class WalletTest {
    @Test
    public void create_eth_wallet() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        // 随机创建钱包
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createLight(ChainTypes.ETH, "q12346", ecKeyPair);
        // 解密KeyStore
        ECKeyPair ecKeyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.ETH, "q12346", false, walletFile);
        Assert.assertEquals(ecKeyPair.getPrivateKey(), ecKeyPair1.getPrivateKey());
        Assert.assertEquals(ecKeyPair.getPublicKey(), ecKeyPair1.getPublicKey());
    }

    @Test
    public void create_eth_wallet1() throws CipherException {
        // 密钥创建钱包
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix("3dafb4ebb061ec56527beacf659b83c4c7ac794389caf8ea191f0b2870362706"));
        WalletFile walletFile = Wallet.createLight(ChainTypes.ETH, "q12346", ecKeyPair);
        Assert.assertEquals("2062154cd708d9b1d61c526628912b69d98a014c", walletFile.getAddress());

        ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray("3dafb4ebb061ec56527beacf659b83c4c7ac794389caf8ea191f0b2870362706"));
        walletFile = Wallet.createLight(ChainTypes.ETH, "q12346", ecKeyPair);
        Assert.assertEquals("2062154cd708d9b1d61c526628912b69d98a014c", walletFile.getAddress());
    }

    @Test
    public void create_moac_wallet() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        // 随机创建钱包
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        WalletFile walletFile = Wallet.createLight(ChainTypes.MOAC, "q12346", ecKeyPair);
        System.out.println(walletFile.getAddress());
        // 解密KeyStore
        ECKeyPair ecKeyPair1 = (ECKeyPair) Wallet.decrypt(ChainTypes.MOAC, "q12346", false, walletFile);
        Assert.assertEquals(ecKeyPair.getPrivateKey(), ecKeyPair1.getPrivateKey());
        Assert.assertEquals(ecKeyPair.getPublicKey(), ecKeyPair1.getPublicKey());
    }

    @Test
    public void create_moac_wallet1() throws CipherException {
        // 密钥创建钱包
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigIntNoPrefix("3a556aaf1a8e291d5fad2b0100a1295c7602e894b6af1cdc25849d853d2ff974"));
        WalletFile walletFile = Wallet.createLight(ChainTypes.MOAC, "q12346", ecKeyPair);
        Assert.assertEquals("481ffc8100efb193170d349144e0e4a1c28431a6", walletFile.getAddress());

        ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray("3a556aaf1a8e291d5fad2b0100a1295c7602e894b6af1cdc25849d853d2ff974"));
        walletFile = Wallet.createLight(ChainTypes.MOAC, "q12346", ecKeyPair);
        Assert.assertEquals("481ffc8100efb193170d349144e0e4a1c28431a6", walletFile.getAddress());
    }

    @Test
    public void create_swtc_wallet() throws CipherException {
        K256KeyPair keyPair = (K256KeyPair) JWallet.generate(false);
        System.out.println(keyPair.getAddress());
        System.out.println(keyPair.getSecret());
        System.out.println(keyPair.canonicalPubHex());
        System.out.println(keyPair.canonicalPriHex());
        WalletFile walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", keyPair);
        K256KeyPair k256KeyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", false, walletFile);
        Assert.assertEquals(keyPair.getAddress(), k256KeyPair.getAddress());
        Assert.assertEquals(keyPair.getSecret(), k256KeyPair.getSecret());

        K256KeyPair _keyPair = (K256KeyPair) JWallet.fromSecret(keyPair.getSecret(), false);
        Assert.assertEquals(keyPair.getAddress(), _keyPair.getAddress());
        Assert.assertEquals(keyPair.getSecret(), _keyPair.getSecret());
        Assert.assertEquals(keyPair.canonicalPubHex(), _keyPair.canonicalPubHex());
        Assert.assertEquals(keyPair.canonicalPriHex(), _keyPair.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair);
        k256KeyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", false, walletFile);
        Assert.assertEquals(_keyPair.getAddress(), k256KeyPair.getAddress());
        Assert.assertEquals(_keyPair.getSecret(), k256KeyPair.getSecret());

        _keyPair = (K256KeyPair) JWallet.fromSecret(keyPair.canonicalPriHex(), false);
        Assert.assertEquals(keyPair.getAddress(), _keyPair.getAddress());
        Assert.assertEquals(keyPair.canonicalPriHex(), _keyPair.getSecret());
        Assert.assertEquals(keyPair.canonicalPubHex(), _keyPair.canonicalPubHex());
        Assert.assertEquals(keyPair.canonicalPriHex(), _keyPair.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair);
        k256KeyPair = (K256KeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", false, walletFile);
        Assert.assertEquals(_keyPair.getAddress(), k256KeyPair.getAddress());
        Assert.assertEquals(_keyPair.getSecret(), k256KeyPair.getSecret());

        EDKeyPair keyPair1 = (EDKeyPair) JWallet.generate(true);
        System.out.println(keyPair1.getAddress());
        System.out.println(keyPair1.getSecret());
        System.out.println(keyPair1.canonicalPubHex());
        System.out.println(keyPair1.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", keyPair1);
        EDKeyPair edKeyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", true, walletFile);
        Assert.assertEquals(keyPair1.getAddress(), edKeyPair.getAddress());
        Assert.assertEquals(keyPair1.getSecret(), edKeyPair.getSecret());

        EDKeyPair _keyPair1 = (EDKeyPair) JWallet.fromSecret(keyPair1.getSecret(), true);
        Assert.assertEquals(keyPair1.getAddress(), _keyPair1.getAddress());
        Assert.assertEquals(keyPair1.getSecret(), _keyPair1.getSecret());
        Assert.assertEquals(keyPair1.canonicalPubHex(), _keyPair1.canonicalPubHex());
        Assert.assertEquals(keyPair1.canonicalPriHex(), _keyPair1.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair1);
        edKeyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", true, walletFile);
        Assert.assertEquals(_keyPair1.getAddress(), edKeyPair.getAddress());
        Assert.assertEquals(_keyPair1.getSecret(), edKeyPair.getSecret());

        _keyPair1 = (EDKeyPair) JWallet.fromSecret(keyPair1.canonicalPriHex(), true);
        Assert.assertEquals(keyPair1.getAddress(), _keyPair1.getAddress());
        Assert.assertEquals(keyPair1.canonicalPriHex(), _keyPair1.getSecret());
        Assert.assertEquals(keyPair1.canonicalPubHex(), _keyPair1.canonicalPubHex());
        Assert.assertEquals(keyPair1.canonicalPriHex(), _keyPair1.canonicalPriHex());
        walletFile = Wallet.createLight(ChainTypes.SWTC, "q12346", _keyPair1);
        edKeyPair = (EDKeyPair) Wallet.decrypt(ChainTypes.SWTC, "q12346", true, walletFile);
        Assert.assertEquals(_keyPair1.getAddress(), edKeyPair.getAddress());
        Assert.assertEquals(_keyPair1.getSecret(), edKeyPair.getSecret());
    }

    @Test
    public void create_btc_wallet() throws InsufficientMoneyException {
        org.bitcoinj.wallet.Wallet wallet = BWallet.generate();
        System.out.println(wallet.currentReceiveAddress());
        System.out.println(wallet.getActiveKeyChain().getMnemonicCode());
        System.out.println(wallet.currentReceiveKey().getPrivateKeyAsHex());
        System.out.println(wallet.currentReceiveKey().getPrivKey());
        System.out.println(wallet.currentReceiveKey().getPrivateKeyAsWiF(MainNetParams.get()));

//        ECKey ecKey = BWallet.fromPrivateKey("cStNxakH8X8NeDzckfnnrsdFvpcKTkBF7e3vmhFP9uSf58xGnKEH");
        ECKey ecKey = BWallet.fromPrivateKey("KxyDWgW6sqp3KTUZSbfN2p9YkpcLSCeDm6mDyLo8D3kygCJZNxDn");
        System.out.println("普通地址: " + LegacyAddress.fromKey(TestNet3Params.get(), ecKey));//
        System.out.println("隔离见证（原生）: " + BWallet.createSegwitAddress(ecKey));//
        System.out.println("隔离见证（兼容）: " + BWallet.createP2SH(ecKey));// 隔离见证（兼容）

        org.bitcoinj.wallet.Wallet wallet1 = BWallet.fromMen(wallet.getActiveKeyChain().getMnemonicCode());
        System.out.println(wallet1.currentReceiveAddress());

//        String to = "mpZN5LyChLoJaF9YL6fUAX5ChNd5D9xL3o";
//        String to = "bc1qvvkapd9jrxt6v7va9he8dmhkqle76rqsvtdlrd";
//        String to = "tb1q80cczamr9ylk0jvdx7chf6c7wn4gxed0my2ggx";
        String to = "2NEKsBdZCVSwAoe7qCSRZA8eT1PuRaB9JPE";
        Long amount = 10000L;
        Long utxoAmount = 12000L;
        Transaction transaction = new Transaction(TestNet3Params.get());

        // https://bitcoinfees.earn.com/api/v1/fees/recommended
        // fee
//        Long fee = (long) ((148 * 1 + 34 * 2 + 10) * 70);
//        // 转出output
////        transaction.addOutput(Coin.valueOf(amount), LegacyAddress.fromBase58(TestNet3Params.get(), to));
////        transaction.addOutput(Coin.valueOf(amount), SegwitAddress.fromKey(TestNet3Params.get(), ecKey));
        transaction.addOutput(Coin.valueOf(amount), ScriptBuilder.createP2SHOutputScript(LegacyAddress.fromBase58(TestNet3Params.get(), to).getHash()));
//        System.out.println(SegwitAddress.fromKey(TestNet3Params.get(), ecKey));
//        Long changeAmount = utxoAmount - (amount + fee);
//
//        // 余额判断
//        if (changeAmount < 0) {
//            System.out.println("utxo余额不足");
//            return;
//        }
//        // 找零
//        if (changeAmount > 0) {
//            Address address = LegacyAddress.fromBase58(TestNet3Params.get(), "mpZN5LyChLoJaF9YL6fUAX5ChNd5D9xL3o");
//            transaction.addOutput(Coin.valueOf(changeAmount), ScriptBuilder.createP2PKHOutputScript(address.getHash()));
//        }
//        System.out.println(Hex.toHexString(BWallet.createP2SH(ecKey).getHash()));
        // unspent utxo
        UTXO utxo = new UTXO(Sha256Hash.wrap("edfec87e0fee635d5a18e663414d18845cc13b38874938c767ec22cf8012134e"), Long.valueOf(0), Coin.valueOf(utxoAmount),
                1835616, false, new Script(Hex.decode("0014632dd0b4b21997a6799d2df276eef607f3ed0c10")));
//                1835616, false, new Script(Hex.decode(String.format("1976a914%s88ac", Hex.toHexString(BWallet.createP2SH(ecKey).getHash())))));
        System.out.println(transaction.getOutput(0));
//        System.out.println(transaction.getOutput(1).toString());
        System.out.println(utxo.getScript());

        Script scriptCode = new ScriptBuilder()
                .data(ScriptBuilder.createOutputScript(BWallet.createP2SH(ecKey)).getProgram()).build();

        // 输入未消费列表项
        TransactionOutPoint outPoint = new TransactionOutPoint(TestNet3Params.get(), utxo.getIndex(), utxo.getHash());
//        transaction.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
        System.out.println(outPoint.getIndex());
        System.out.println(outPoint.getHash());

        transaction.addInput(Sha256Hash.wrap("edfec87e0fee635d5a18e663414d18845cc13b38874938c767ec22cf8012134e"), Long.valueOf(0), new Script(Hex.decode("0014632dd0b4b21997a6799d2df276eef607f3ed0c10")));


        System.out.println(BWallet.createP2SH(ecKey).getOutputScriptType());
        System.out.println("222:" + Hex.toHexString(scriptCode.getProgram()));
        Script witnessScript = ScriptBuilder.createP2WPKHOutputScript(ecKey);
        System.out.println("sss:" + Hex.toHexString(witnessScript.getProgram()));

        TransactionSignature txSig = transaction.calculateWitnessSignature(0, ecKey,
                witnessScript, Coin.valueOf(utxoAmount),
                Transaction.SigHash.ALL, true);
        transaction.getInput(0).setSequenceNumber(4294967294L);
        transaction.getInput(0).setWitness(TransactionWitness.redeemP2WPKH(txSig, ecKey));
//        transaction.getInput(0).setScriptSig(ScriptBuilder.createEmpty());
        System.out.println(transaction.toString());

//        byte[] bytes = transaction.bitcoinSerialize();
        String hash = Hex.toHexString(transaction.bitcoinSerialize());

        System.out.println(hash);
        System.out.println("utxoAmount:" + utxoAmount);
        System.out.println("amount:" + amount);
//        System.out.println("changeAmount:" + changeAmount);
//        System.out.println("fee:" + fee);
//        System.out.println("changeAmount:" + (changeAmount + amount));
    }

    @Test
    public void create_btc_wallet1() throws InsufficientMoneyException {
        org.bitcoinj.wallet.Wallet wallet = BWallet.generate();
        System.out.println(wallet.currentReceiveAddress());
        System.out.println(wallet.getActiveKeyChain().getMnemonicCode());
        System.out.println(wallet.currentReceiveKey().getPrivateKeyAsHex());
        System.out.println(wallet.currentReceiveKey().getPrivKey());
        System.out.println(wallet.currentReceiveKey().getPrivateKeyAsWiF(MainNetParams.get()));

        ECKey ecKey = BWallet.fromPrivateKey("KxyDWgW6sqp3KTUZSbfN2p9YkpcLSCeDm6mDyLo8D3kygCJZNxDn");
        System.out.println("普通地址: " + LegacyAddress.fromKey(TestNet3Params.get(), ecKey));//
        System.out.println("隔离见证（原生）: " + BWallet.createSegwitAddress(ecKey));//
        System.out.println("隔离见证（兼容）: " + BWallet.createP2SH(ecKey));// 隔离见证（兼容）

        org.bitcoinj.wallet.Wallet wallet1 = BWallet.fromMen(wallet.getActiveKeyChain().getMnemonicCode());
        System.out.println(wallet1.currentReceiveAddress());

//        String to = "mpZN5LyChLoJaF9YL6fUAX5ChNd5D9xL3o";
//        String to = "bc1qvvkapd9jrxt6v7va9he8dmhkqle76rqsvtdlrd";
        String to = "tb1q80cczamr9ylk0jvdx7chf6c7wn4gxed0my2ggx";
        Long amount = 70000L;
        Long utxoAmount = 94000L;
        Transaction transaction = new Transaction(TestNet3Params.get());

        // https://bitcoinfees.earn.com/api/v1/fees/recommended
        // fee
        Long fee = (long) ((148 * 1 + 34 * 2 + 10) * 70);
        // 转出output
//        transaction.addOutput(Coin.valueOf(amount), LegacyAddress.fromBase58(TestNet3Params.get(), to));
//        transaction.addOutput(Coin.valueOf(amount), SegwitAddress.fromKey(TestNet3Params.get(), ecKey));
        transaction.addOutput(Coin.valueOf(amount), ScriptBuilder.createP2WPKHOutputScript(SegwitAddress.fromBech32(TestNet3Params.get(), to).getHash()));
        Long changeAmount = utxoAmount - (amount + fee);

        // 余额判断
        if (changeAmount < 0) {
            System.out.println("utxo余额不足");
            return;
        }
        // 找零
        if (changeAmount > 0) {
            Address address = LegacyAddress.fromBase58(TestNet3Params.get(), "mpZN5LyChLoJaF9YL6fUAX5ChNd5D9xL3o");
            transaction.addOutput(Coin.valueOf(changeAmount), ScriptBuilder.createP2PKHOutputScript(address.getHash()));
        }
        // unspent utxo
        UTXO utxo = new UTXO(Sha256Hash.wrap("85dfc584d7a7d3856f128ba71c9ce328993d9d7cba7fa1e2f25895eebd6caeb7"), Long.valueOf(0), Coin.valueOf(utxoAmount),
                1835616, false, new Script(Hex.decode("a914e73b5acb3525c52a38c1495b2a2588afb9f0106787")));
//                1835616, false, new Script(Hex.decode(String.format("1976a914%s88ac", Hex.toHexString(BWallet.createP2SH(ecKey).getHash())))));
        System.out.println(transaction.getOutput(0));
        System.out.println(transaction.getOutput(1).toString());
        System.out.println(utxo.getScript());
        // 输入未消费列表项
        TransactionOutPoint outPoint = new TransactionOutPoint(TestNet3Params.get(), utxo.getIndex(), utxo.getHash());
//        transaction.addSignedInput(outPoint, utxo.getScript(), ecKey, Transaction.SigHash.ALL, true);
        System.out.println(outPoint.getIndex());
        System.out.println(outPoint.getHash());

        transaction.addInput(Sha256Hash.wrap("85dfc584d7a7d3856f128ba71c9ce328993d9d7cba7fa1e2f25895eebd6caeb7"), Long.valueOf(0), new Script(Hex.decode("a914e73b5acb3525c52a38c1495b2a2588afb9f0106787")));

        Script witnessScript = ScriptBuilder.createP2WPKHOutputScript(ecKey);
        System.out.println("sss:" + Hex.toHexString(witnessScript.getPubKeyHash()));
//
//        assertEquals("64f3b0f4dd2bb3aa1ce8566d220cc74dda9df97d8490cc81d89d735c92e59fb6",
//                tx.hashForWitnessSignature(0, witnessScript, Coin.COIN.multiply(10), Transaction.SigHash.ALL, false)
//                        .toString());
        TransactionSignature txSig = transaction.calculateWitnessSignature(0, ecKey,
                witnessScript, Coin.valueOf(utxoAmount),
                Transaction.SigHash.ALL, false);
        transaction.getInput(0).setSequenceNumber(4294967294L);
        transaction.getInput(0).setWitness(TransactionWitness.redeemP2WPKH(txSig, ecKey));
//        transaction.getInput(0).
//                setScriptSig(new ScriptBuilder().data(redeemScript1.getProgram()).build());
        System.out.println(transaction.toString());

//        byte[] bytes = transaction.bitcoinSerialize();
        String hash = Hex.toHexString(transaction.bitcoinSerialize());

        System.out.println(hash);
        System.out.println("utxoAmount:" + utxoAmount);
        System.out.println("amount:" + amount);
        System.out.println("changeAmount:" + changeAmount);
        System.out.println("fee:" + fee);
        System.out.println("changeAmount:" + (changeAmount + amount));
        System.out.println(Coin.parseCoin("0.001"));
        System.out.println(Coin.parseCoin("984180"));
    }


    final static String eth_keyStore = "{\"chainType\":\"ETH\"," +
            "\"address\":\"2062154cd708d9b1d61c526628912b69d98a014c\"," +
            "\"dateTime\":\"2020-06-18 15-40-02\"," +
            "\"id\":\"abd019fe-7a1a-42c9-afda-78dbb922c7a5\"," +
            "\"version\":3," +
            "\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"faf2540da8f96649d0d735c104e4ba0168792f757f66155b350691c92a26652c\",\"cipherparams\":{\"iv\":\"d059832a8e04848c76d4631713c8cf69\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":4096,\"p\":6,\"r\":8,\"salt\":\"9aecf692a790efd9be47c21e11ba89260d6be25adccd429fdfbe16386d73d754\"},\"mac\":\"af82c7bd82d2bcf8cf3317bb577d9ee3942213d9799a11b214a69c7a3fd6d6a0\"}}";
}