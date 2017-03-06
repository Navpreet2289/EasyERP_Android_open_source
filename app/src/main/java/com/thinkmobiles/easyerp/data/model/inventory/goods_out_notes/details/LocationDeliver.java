package com.thinkmobiles.easyerp.data.model.inventory.goods_out_notes.details;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samson on 06.03.17.
 */

public class LocationDeliver implements Parcelable {

    @SerializedName("_id")
    public String id;
//    public CreatedEditedUserString editedBy;
//    public CreatedEditedUserString createdBy;
//    public String zone;
//    public String warehouse;
//    public String groupingD;
//    public String groupingC;
//    public String groupingB;
//    public String groupingA;
    public String name;


    public LocationDeliver() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
    }

    protected LocationDeliver(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
    }

    public static final Creator<LocationDeliver> CREATOR = new Creator<LocationDeliver>() {
        @Override
        public LocationDeliver createFromParcel(Parcel source) {
            return new LocationDeliver(source);
        }

        @Override
        public LocationDeliver[] newArray(int size) {
            return new LocationDeliver[size];
        }
    };
}
