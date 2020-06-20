package com.jch.core.swtc.bean;

import android.os.Parcel;
import android.os.Parcelable;

// 金额实体（支付金额，关系金额）
public class AmountInfo implements Parcelable {
    // 货币数量
    private String value;
    // 货币种类，三到六个字母或20字节的自定义货币
    private String currency;
    // 货币发行方
    private String issuer;

    public AmountInfo() {
    }

    protected AmountInfo(Parcel in) {
        value = in.readString();
        currency = in.readString();
        issuer = in.readString();
    }

    public static final Creator<AmountInfo> CREATOR = new Creator<AmountInfo>() {
        @Override
        public AmountInfo createFromParcel(Parcel in) {
            return new AmountInfo(in);
        }

        @Override
        public AmountInfo[] newArray(int size) {
            return new AmountInfo[size];
        }
    };

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
        dest.writeString(currency);
        dest.writeString(issuer);
    }
}
