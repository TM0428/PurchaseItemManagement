package jp.ac.titech.itpro.sdl.purchaseitemmanagement.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Item {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "price")
    public int price;

    @ColumnInfo(name = "tag")
    public String tag;

    @ColumnInfo(name = "ImagePath")
    public String path = "";

    public Item(int id,String name,int price,String tag){
        this.id = id;
        this.name = name;
        this.price = price;
        this.tag = tag;
    }
    @Ignore
    public Item(int id,String name,int price,String tag, String path){
        this.id = id;
        this.name = name;
        this.price = price;
        this.path = path;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", tag='" + tag + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
