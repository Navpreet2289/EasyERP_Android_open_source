package com.thinkmobiles.easyerp.data.model.inventory.goods_out_notes.details;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by samson on 06.03.17.
 */

public class OutNoteStatus implements Parcelable {

    public boolean shipped;
    public boolean picked;
    public boolean packed;
    public boolean printed;
    public String shippedOn;
    public String pickedOn;
    public String packedOn;
    public String printedOn;
    public String printedById;
    public String shippedById;
    public String packedById;
    public String pickedById;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.shipped ? (byte) 1 : (byte) 0);
        dest.writeByte(this.picked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.packed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.printed ? (byte) 1 : (byte) 0);
        dest.writeString(this.shippedOn);
        dest.writeString(this.pickedOn);
        dest.writeString(this.packedOn);
        dest.writeString(this.printedOn);
        dest.writeString(this.printedById);
        dest.writeString(this.shippedById);
        dest.writeString(this.packedById);
        dest.writeString(this.pickedById);
    }

    public OutNoteStatus() {
    }

    protected OutNoteStatus(Parcel in) {
        this.shipped = in.readByte() != 0;
        this.picked = in.readByte() != 0;
        this.packed = in.readByte() != 0;
        this.printed = in.readByte() != 0;
        this.shippedOn = in.readString();
        this.pickedOn = in.readString();
        this.packedOn = in.readString();
        this.printedOn = in.readString();
        this.printedById = in.readString();
        this.shippedById = in.readString();
        this.packedById = in.readString();
        this.pickedById = in.readString();
    }

    public static final Parcelable.Creator<OutNoteStatus> CREATOR = new Parcelable.Creator<OutNoteStatus>() {
        @Override
        public OutNoteStatus createFromParcel(Parcel source) {
            return new OutNoteStatus(source);
        }

        @Override
        public OutNoteStatus[] newArray(int size) {
            return new OutNoteStatus[size];
        }
    };
}
