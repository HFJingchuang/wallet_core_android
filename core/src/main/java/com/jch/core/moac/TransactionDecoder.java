/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.jch.core.moac;

import com.jch.core.rlp.RlpDecoder;
import com.jch.core.rlp.RlpList;
import com.jch.core.rlp.RlpString;

import java.math.BigInteger;

import org.web3j.utils.Numeric;

public class TransactionDecoder {

    public static RawTransaction decode(final String hexTransaction) {
        final byte[] transaction = Numeric.hexStringToByteArray(hexTransaction);
        final RlpList rlpList = RlpDecoder.decode(transaction);
        final RlpList values = (RlpList) rlpList.getValues().get(0);
        final BigInteger nonce = ((RlpString) values.getValues().get(0)).asPositiveBigInteger();
        final BigInteger gasPrice = ((RlpString) values.getValues().get(1)).asPositiveBigInteger();
        final BigInteger gasLimit = ((RlpString) values.getValues().get(2)).asPositiveBigInteger();
        final String to = ((RlpString) values.getValues().get(3)).asString();
        final BigInteger value = ((RlpString) values.getValues().get(4)).asPositiveBigInteger();
        final String data = ((RlpString) values.getValues().get(5)).asString();
        BigInteger shardingFlag = ((RlpString) values.getValues().get(6)).asPositiveBigInteger();
        String via = ((RlpString) values.getValues().get(7)).asString();
        if (values.getValues().size() == 8
                || (values.getValues().size() == 10
                        && ((RlpString) values.getValues().get(9)).getBytes().length == 10)
                || (values.getValues().size() == 11
                        && ((RlpString) values.getValues().get(10)).getBytes().length == 10)) {
            // the 8th or 9nth element is the hex
            // representation of "restricted" for private transactions
            return RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data,shardingFlag,via);
        } else {
            final byte[] v = ((RlpString) values.getValues().get(8)).getBytes();
            final byte[] r =
                    Numeric.toBytesPadded(
                            Numeric.toBigInt(((RlpString) values.getValues().get(9)).getBytes()),
                            32);
            final byte[] s =
                    Numeric.toBytesPadded(
                            Numeric.toBigInt(((RlpString) values.getValues().get(10)).getBytes()),
                            32);
            final Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
            return new SignedRawTransaction(
                    nonce, gasPrice, gasLimit, to, value, data, signatureData,shardingFlag,via);
        }
    }
}
