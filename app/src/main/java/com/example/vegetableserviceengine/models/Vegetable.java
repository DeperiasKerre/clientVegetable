package com.example.vegetableserviceengine.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Vegetable implements Parcelable {
    private Integer id;
    private String name;
    private Integer quantity;
    private Double price;

    public Vegetable() { }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String describeVegetable () {
        return "id: " + String.valueOf(this.id) + ", name: " + this.name + ", quantity: " + String.valueOf(this.quantity) + ", price: " + String.valueOf(this.price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.quantity);
        dest.writeDouble(this.price);
    }

    protected Vegetable(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.quantity = in.readInt();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<Vegetable> CREATOR = new Parcelable.Creator<Vegetable>() {
        @Override
        public Vegetable createFromParcel(Parcel source) {
            return new Vegetable(source);
        }

        @Override
        public Vegetable[] newArray(int size) {
            return new Vegetable[size];
        }
    };
}
