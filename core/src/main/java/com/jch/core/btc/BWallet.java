package com.jch.core.btc;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.core.SegwitAddress;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.bouncycastle.util.encoders.Hex;

import java.util.List;

public class BWallet {

    /**
     * 随机生成钱包地址
     *
     * @return
     */
    public static Wallet generate() {
        return Wallet.createDeterministic(MainNetParams.get(), Script.ScriptType.P2PKH);
    }

    /**
     * 根据密钥生成Wallet
     *
     * @param privateKey
     * @return
     */
    public static ECKey fromPrivateKey(String privateKey) {
        ECKey key;
        if (privateKey.length() == 51 || privateKey.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(MainNetParams.get(), privateKey);
            key = dumpedPrivateKey.getKey();
        } else {
            key = ECKey.fromPrivate(Hex.decode(privateKey), true);
        }
        return key;
    }

    /**
     * 根据助记词生成Wallet
     *
     * @param mnemonicCode
     * @return
     */
    public static Wallet fromMen(List<String> mnemonicCode) {
        DeterministicSeed seed = new DeterministicSeed(mnemonicCode, null, "", System.currentTimeMillis());
        return Wallet.fromSeed(MainNetParams.get(), seed, Script.ScriptType.P2PKH);
    }

    /**
     * 生成隔离见证地（原生）
     *
     * @param ecKey
     * @return
     */
    public static Address createSegwitAddress(ECKey ecKey) {
        return SegwitAddress.fromKey(MainNetParams.get(), ecKey);
    }

    /**
     * 生成隔离见证地址（兼容）
     *
     * @param ecKey
     * @return
     */
    public static Address createP2SH(ECKey ecKey) {
        Script script = ScriptBuilder.createP2WPKHOutputScript(ecKey);
        String redeemScript = String.format("0014%s", Hex.toHexString(ScriptPattern.extractHashFromP2WH(script)));
        return LegacyAddress.fromScriptHash(MainNetParams.get(), Utils.sha256hash160(Hex.decode(redeemScript)));
    }
}
