package com.jch.core.swtc.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Transactions implements Parcelable {

    private Integer date;// 时间戳
    private String hash;// 交易hash
    private String type;// 交易类型
    private String fee;// 手续费
    private String result;// 交易结果
    private List<Memo> memos;// 备注
    private String counterparty;// 交易对家
    private AmountInfo amount;// 交易金额对象
    private AmountInfo spent;//
    private String offertype;

    private AmountInfo gets;//
    private AmountInfo pays;//
    private Integer seq;//
    private Integer offerseq;//
    private String relationtype;
    private List params;
    private boolean isactive;
    private String method;
    private String payload;
    private String destination;
    private JSONArray effects;// 交易效果

    public Transactions() {
    }

    protected Transactions(Parcel in) {
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readInt();
        }
        hash = in.readString();
        type = in.readString();
        fee = in.readString();
        result = in.readString();
        memos = new ArrayList<>();
        in.readTypedList(memos, Memo.CREATOR);
        counterparty = in.readString();
        amount = in.readParcelable(AmountInfo.class.getClassLoader());
        spent = in.readParcelable(AmountInfo.class.getClassLoader());
        offertype = in.readString();
        gets = in.readParcelable(AmountInfo.class.getClassLoader());
        pays = in.readParcelable(AmountInfo.class.getClassLoader());
        if (in.readByte() == 0) {
            seq = null;
        } else {
            seq = in.readInt();
        }
        if (in.readByte() == 0) {
            offerseq = null;
        } else {
            offerseq = in.readInt();
        }
        relationtype = in.readString();
        isactive = in.readByte() != 0;
        method = in.readString();
        payload = in.readString();
        destination = in.readString();
        effects = JSONArray.parseArray(in.readString());
    }

    public static final Creator<Transactions> CREATOR = new Creator<Transactions>() {
        @Override
        public Transactions createFromParcel(Parcel in) {
            return new Transactions(in);
        }

        @Override
        public Transactions[] newArray(int size) {
            return new Transactions[size];
        }
    };

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public AmountInfo getAmount() {
        return amount;
    }

    public void setAmount(AmountInfo amount) {
        this.amount = amount;
    }

    public JSONArray getEffects() {
        return effects;
    }

    public void setEffects(JSONArray effects) {
        this.effects = effects;
    }

    public AmountInfo getSpent() {
        return spent;
    }

    public void setSpent(AmountInfo spent) {
        this.spent = spent;
    }

    public String getOffertype() {
        return offertype;
    }

    public void setOffertype(String offertype) {
        this.offertype = offertype;
    }

    public AmountInfo getGets() {
        return gets;
    }

    public void setGets(AmountInfo gets) {
        this.gets = gets;
    }

    public AmountInfo getPays() {
        return pays;
    }

    public void setPays(AmountInfo pays) {
        this.pays = pays;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getOfferseq() {
        return offerseq;
    }

    public void setOfferseq(Integer offerseq) {
        this.offerseq = offerseq;
    }

    public String getRelationtype() {
        return relationtype;
    }

    public void setRelationtype(String relationtype) {
        this.relationtype = relationtype;
    }

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (date == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(date);
        }
        dest.writeString(hash);
        dest.writeString(type);
        dest.writeString(fee);
        dest.writeString(result);
        dest.writeTypedList(memos);
        dest.writeString(counterparty);
        dest.writeParcelable(amount, 0);
        dest.writeParcelable(spent, 0);
        dest.writeString(offertype);
        dest.writeParcelable(gets, 0);
        dest.writeParcelable(pays, 0);
        if (seq == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(seq);
        }
        if (offerseq == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(offerseq);
        }
        dest.writeString(relationtype);
        dest.writeByte((byte) (isactive ? 1 : 0));
        dest.writeString(method);
        dest.writeString(payload);
        dest.writeString(destination);
        dest.writeString(effects.toJSONString());
    }
}
