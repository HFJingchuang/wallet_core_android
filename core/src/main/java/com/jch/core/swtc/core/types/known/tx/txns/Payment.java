package com.jch.core.swtc.core.types.known.tx.txns;

import android.os.RemoteException;

import com.jch.core.swtc.bean.AmountInfo;
import com.jch.core.swtc.core.coretypes.AccountID;
import com.jch.core.swtc.core.coretypes.Amount;
import com.jch.core.swtc.core.coretypes.Currency;
import com.jch.core.swtc.core.coretypes.PathSet;
import com.jch.core.swtc.core.coretypes.hash.Hash256;
import com.jch.core.swtc.core.coretypes.uint.UInt32;
import com.jch.core.swtc.core.fields.Field;
import com.jch.core.swtc.core.runtime.Value;
import com.jch.core.swtc.core.serialized.enums.TransactionType;
import com.jch.core.swtc.core.types.known.tx.Transaction;

import java.math.BigDecimal;

public class Payment extends Transaction {
    public Payment() {
        super(TransactionType.Payment);
    }


    public UInt32 destinationTag() {
        return get(UInt32.DestinationTag);
    }

    public Hash256 invoiceID() {
        return get(Hash256.InvoiceID);
    }

    public Amount amount() {
        return get(Amount.Amount);
    }

    public Amount sendMax() {
        return get(Amount.SendMax);
    }

    public AccountID destination() {
        return get(AccountID.Destination);
    }

    public PathSet paths() {
        return get(PathSet.Paths);
    }

    public void destinationTag(UInt32 val) {
        put(Field.DestinationTag, val);
    }

    public void invoiceID(Hash256 val) {
        put(Field.InvoiceID, val);
    }

    public void amount(Amount val) {
        put(Field.Amount, val);
    }

    public void sendMax(Amount val) {
        put(Field.SendMax, val);
    }

    public void destination(AccountID val) {
        put(Field.Destination, val);
    }

    public void paths(PathSet val) {
        put(Field.Paths, val);
    }

    public void setAmountInfo(AmountInfo amountInfo) {
        Object o;
        try {
            o = toAmount(amountInfo);
            if (Value.typeOf(o) == Value.STRING) {
                this.as(Amount.Amount, o);
            } else {
                BigDecimal temp = new BigDecimal(amountInfo.getValue());
                Amount amount = new Amount(temp, Currency.fromString(amountInfo.getCurrency()),
                        AccountID.fromAddress(amountInfo.getIssuer()));
                this.as(Amount.Amount, amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据金额对象内容返回信息 货币单位SWT转基本单位
     *
     * @param amount 金额对象
     * @return
     */
    public static Object toAmount(AmountInfo amount) throws Exception {
        String value = amount.getValue();
        BigDecimal temp = new BigDecimal(value);
        BigDecimal max_value = new BigDecimal("100000000000");
        String currency = amount.getCurrency();
        if (!value.isEmpty() && temp.compareTo(max_value) > 0) {
            throw new RemoteException("invalid amount: amount's maximum value is 100000000000");
        }
        if (currency.equals("SWT")) {
            BigDecimal exchange_rate = new BigDecimal("1000000.00");
            BigDecimal rs = temp.multiply(exchange_rate);
            return String.valueOf(rs.longValue());
        }
        return amount;
    }
}
