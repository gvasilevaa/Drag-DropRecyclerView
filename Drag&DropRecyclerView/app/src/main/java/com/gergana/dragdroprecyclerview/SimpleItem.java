package com.gergana.dragdroprecyclerview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gergana on 11/17/14.
 */
public class SimpleItem implements Parcelable{
    private int id;
    private String name;
    private Boolean selected;

    public SimpleItem(int id, String name, Boolean selected) {
        this.id = id;
        this.name = name;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
