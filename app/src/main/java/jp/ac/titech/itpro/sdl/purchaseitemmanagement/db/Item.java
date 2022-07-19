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

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "ImagePath")
    public String path = "";

    public Item(int id,String name,int price,String author){
        this.id = id;
        this.name = name;
        this.price = price;
        this.author = author;
    }
    @Ignore
    public Item(int id,String name,int price,String author, String path){
        this.id = id;
        this.name = name;
        this.price = price;
        this.path = path;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", author='" + author + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
