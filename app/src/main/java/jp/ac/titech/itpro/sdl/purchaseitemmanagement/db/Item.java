package jp.ac.titech.itpro.sdl.purchaseitemmanagement.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey
    @NonNull
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "price")
    public int price;

    public Item(String id,String name,int price){
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
