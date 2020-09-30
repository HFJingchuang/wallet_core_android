package com.jch.core.swtc.core.types.known.tx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jch.core.cypto.IKeyPair;
import com.jch.core.swtc.core.coretypes.AccountID;
import com.jch.core.swtc.core.coretypes.Amount;
import com.jch.core.swtc.core.coretypes.Blob;
import com.jch.core.swtc.core.coretypes.STArray;
import com.jch.core.swtc.core.coretypes.STObject;
import com.jch.core.swtc.core.coretypes.hash.HalfSha512;
import com.jch.core.swtc.core.coretypes.hash.Hash256;
import com.jch.core.swtc.core.coretypes.hash.prefixes.HashPrefix;
import com.jch.core.swtc.core.coretypes.uint.UInt16;
import com.jch.core.swtc.core.coretypes.uint.UInt32;
import com.jch.core.swtc.core.enums.TransactionFlag;
import com.jch.core.swtc.core.fields.Field;
import com.jch.core.swtc.core.fields.STArrayField;
import com.jch.core.swtc.core.formats.TxFormat;
import com.jch.core.swtc.core.serialized.BytesList;
import com.jch.core.swtc.core.serialized.enums.TransactionType;
import com.jch.core.swtc.core.types.known.tx.signed.SignedTransaction;
import com.jch.core.utils.HashUtils;

import org.web3j.utils.Numeric;

import java.util.List;

import static org.web3j.compat.Compat.UTF_8;

public class Transaction extends STObject {
    public static final boolean CANONICAL_FLAG_DEPLOYED = true;
    public static final UInt32 CANONICAL_SIGNATURE = new UInt32(TransactionFlag.FullyCanonicalSig);

    public Transaction(TransactionType type) {
        setFormat(TxFormat.formats.get(type));
        put(Field.TransactionType, type);
    }

    public SignedTransaction sign(String secret, boolean isED25519) {
        SignedTransaction signed = SignedTransaction.fromTx(this);
        signed.sign(secret, isED25519);
        return signed;
    }

    public SignedTransaction multiSign(String secret, boolean isED25519) {
        SignedTransaction signed = SignedTransaction.fromTx(this);
        signed.multiSign(secret, isED25519);
        return signed;
    }

    public SignedTransaction sign(IKeyPair keyPair) {
        SignedTransaction signed = SignedTransaction.fromTx(this);
        signed.sign(keyPair);
        return signed;
    }

    public TransactionType transactionType() {
        return transactionType(this);
    }

    public Hash256 signingHash() {
        HalfSha512 signing = HalfSha512.prefixed256(HashPrefix.txSign);
        toBytesSink(signing, new FieldFilter() {
            @Override
            public boolean evaluate(Field a) {
                return a.isSigningField();
            }
        });
        return signing.finish();
    }

    public byte[] signingData() {
        BytesList bl = new BytesList();
        bl.add(HashPrefix.txSign.bytes);
        toBytesSink(bl, new FieldFilter() {
            @Override
            public boolean evaluate(Field a) {
                return a.isSigningField();
            }
        });
        return bl.bytes();
    }

    public byte[] multiSigningData() {
        BytesList bl = new BytesList();
        bl.add(HashPrefix.transactionMultiSig.bytes);
        toBytesSink(bl, new FieldFilter() {
            @Override
            public boolean evaluate(Field a) {
                return a.isSigningField();
            }
        });
        return bl.bytes();
    }

    public void setCanonicalSignatureFlag() {
        UInt32 flags = get(UInt32.Flags);
        if (flags == null) {
            flags = CANONICAL_SIGNATURE;
        } else {
            flags = flags.or(CANONICAL_SIGNATURE);
        }
        put(UInt32.Flags, flags);
    }

    public UInt32 flags() {
        return get(UInt32.Flags);
    }

    public UInt32 sourceTag() {
        return get(UInt32.SourceTag);
    }

    public UInt32 sequence() {
        return get(UInt32.Sequence);
    }

    public UInt32 lastLedgerSequence() {
        return get(UInt32.LastLedgerSequence);
    }

    public UInt32 operationLimit() {
        return get(UInt32.OperationLimit);
    }

    public Hash256 previousTxnID() {
        return get(Hash256.PreviousTxnID);
    }

    public Hash256 accountTxnID() {
        return get(Hash256.AccountTxnID);
    }

    public Amount fee() {
        return get(Amount.Fee);
    }

    public Blob signingPubKey() {
        return get(Blob.SigningPubKey);
    }

    public Blob txnSignature() {
        return get(Blob.TxnSignature);
    }

    public AccountID account() {
        return get(AccountID.Account);
    }

    public void transactionType(UInt16 val) {
        put(Field.TransactionType, val);
    }

    public void flags(UInt32 val) {
        put(Field.Flags, val);
    }

    public void sourceTag(UInt32 val) {
        put(Field.SourceTag, val);
    }

    public void sequence(UInt32 val) {
        put(Field.Sequence, val);
    }

    public void lastLedgerSequence(UInt32 val) {
        put(Field.LastLedgerSequence, val);
    }

    public void operationLimit(UInt32 val) {
        put(Field.OperationLimit, val);
    }

    public void previousTxnID(Hash256 val) {
        put(Field.PreviousTxnID, val);
    }

    public void accountTxnID(Hash256 val) {
        put(Field.AccountTxnID, val);
    }

    public void fee(Amount val) {
        put(Field.Fee, val);
    }

    public void signingPubKey(Blob val) {
        put(Field.SigningPubKey, val);
    }

    public void txnSignature(Blob val) {
        put(Field.TxnSignature, val);
    }

    public void account(AccountID val) {
        put(Field.Account, val);
    }

    // 新增方法
    public void memos(STArray val) {
        put(Field.Memos, val);
    }

    public JSONArray signers() {
        if (get(Field.Signers) == null) {
            return new JSONArray();
        }
        return ((STArray) get(Field.Signers)).toJSONArray();
    }

    public void signers(STArray val) {
        put(Field.Signers, val);
    }


    /**
     * 添加备注信息
     *
     * @param memos
     */
    public void addMemo(List<String> memos) {
        JSONArray memosArray = new JSONArray();
        if (memos != null) {
            for (String memo : memos) {
                JSONObject memoObj = new JSONObject();
                JSONObject memoData = new JSONObject();
                memoData.put("MemoData", Numeric.toHexStringNoPrefix(memo.getBytes(UTF_8)).toUpperCase());
                memoObj.put("Memo", memoData);
                memosArray.add(memoObj);
            }
        }
        put(STArray.Memos, STArray.translate.fromJSONArray(memosArray));
    }

    /**
     * 添加签名钱包
     *
     * @param account
     * @param signingPubKey
     */
    public void addSigner(String account, String signingPubKey, String txnSignature) {
        JSONArray memosArray = signers();
        JSONObject signer = new JSONObject();
        JSONObject signerData = new JSONObject();
        signerData.put("Account", account);
        signerData.put("SigningPubKey", signingPubKey);
        signerData.put("TxnSignature", txnSignature);
        signer.put("Signer", signerData);
        memosArray.add(signer);
        put(Field.Signers, STArray.translate.fromJSONArray(memosArray));
    }

    /**
     * 添加签名钱包
     *
     * @param signers
     */
    public void addSigner(JSONArray signers) {
        put(STArray.Signers, STArray.translate.fromJSONArray(signers));
    }


    public Hash256 hash() {
        return get(Hash256.hash);
    }

    public AccountID signingKey() {
        byte[] pubKey = HashUtils.SHA256_RIPEMD160(signingPubKey().toBytes());
        return AccountID.fromAddressBytes(pubKey);
    }
}
