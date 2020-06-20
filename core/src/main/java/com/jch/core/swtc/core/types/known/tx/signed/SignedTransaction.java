package com.jch.core.swtc.core.types.known.tx.signed;

import com.jch.core.cypto.IKeyPair;
import com.jch.core.exceptions.PrivateKeyFormatException;
import com.jch.core.swtc.JWallet;
import com.jch.core.swtc.Seed;
import com.jch.core.swtc.core.coretypes.Amount;
import com.jch.core.swtc.core.coretypes.Blob;
import com.jch.core.swtc.core.coretypes.STObject;
import com.jch.core.swtc.core.coretypes.hash.HalfSha512;
import com.jch.core.swtc.core.coretypes.hash.Hash256;
import com.jch.core.swtc.core.coretypes.hash.prefixes.HashPrefix;
import com.jch.core.swtc.core.coretypes.uint.UInt32;
import com.jch.core.swtc.core.serialized.BytesList;
import com.jch.core.swtc.core.serialized.MultiSink;
import com.jch.core.swtc.core.serialized.enums.TransactionType;
import com.jch.core.swtc.core.types.known.tx.Transaction;

import java.util.Arrays;

public class SignedTransaction {
    private SignedTransaction(Transaction of) {
        // TODO: is this just over kill ?
        txn = (Transaction) STObject.translate.fromBytes(of.toBytes());
    }

    // This will eventually be private
    @Deprecated
    public SignedTransaction() {
    }

    public Transaction txn;
    public Hash256 hash;

    public byte[] signingData;
    public byte[] previousSigningData;
    public String tx_blob;

    public void sign(String base58Secret, boolean isED25519) {
//        if (JWallet.isValidSecret(base58Secret, isED25519)) {
//            sign(Seed.fromBase58(base58Secret).keyPair());
//        } else {
//            Seed seed = new Seed();
//            if (base58Secret.length() == 66) {
//                if (base58Secret.toUpperCase().startsWith("ED")) {
//                    seed.setEd25519();
//                }
//                base58Secret = base58Secret.substring(2);
//            } else {
//                throw new PrivateKeyFormatException("the length of private key must be 66");
//            }
//            sign(seed.fromPrivateKey(base58Secret));
//        }
        sign(JWallet.fromSecret(base58Secret, isED25519));
    }

    public static SignedTransaction fromTx(Transaction tx) {
        return new SignedTransaction(tx);
    }

    public void sign(IKeyPair keyPair) {
        prepare(keyPair, null, null, null);
    }

    public void prepare(IKeyPair keyPair,
                        Amount fee,
                        UInt32 Sequence,
                        UInt32 lastLedgerSequence) {

        Blob pubKey = new Blob(keyPair.canonicalPubBytes());

        // This won't always be specified
        if (lastLedgerSequence != null) {
            txn.put(UInt32.LastLedgerSequence, lastLedgerSequence);
        }
        if (Sequence != null) {
            txn.put(UInt32.Sequence, Sequence);
        }
        if (fee != null) {
            txn.put(Amount.Fee, fee);
        }

        txn.signingPubKey(pubKey);


        if (Transaction.CANONICAL_FLAG_DEPLOYED) {
            txn.setCanonicalSignatureFlag();
        }

        txn.checkFormat();
        signingData = txn.signingData();
        //System.out.println("------------");
        //System.out.println(JsonUtils.toJsonString(txn));
        if (previousSigningData != null && Arrays.equals(signingData, previousSigningData)) {
            return;
        }
        try {
            txn.txnSignature(new Blob(keyPair.signMessage(signingData)));

            BytesList blob = new BytesList();
            HalfSha512 id = HalfSha512.prefixed256(HashPrefix.transactionID);
            txn.toBytesSink(new MultiSink(blob, id));
            tx_blob = blob.bytesHex();
            hash = id.finish();
        } catch (Exception e) {
            // electric paranoia
            previousSigningData = null;
            throw new RuntimeException(e);
        } /*else {*/
        previousSigningData = signingData;
        // }
    }

    public TransactionType transactionType() {
        return txn.transactionType();
    }
}
