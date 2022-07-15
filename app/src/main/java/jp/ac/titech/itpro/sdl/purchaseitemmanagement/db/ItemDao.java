package jp.ac.titech.itpro.sdl.purchaseitemmanagement.db;


import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ItemDao {
    @Query("SELECT * FROM item")
    List<Item> getAll();

    @Query("SELECT * FROM item WHERE id LIKE (:id)")
    List<Item> getById(String id);

    @Query("SELECT * FROM item WHERE id IN (:ids)")
    List<Item> loadAllByIds(String[] ids);

    @Query("SELECT * FROM item WHERE name LIKE '%' || :query || '%'")
    List<Item> findItemByName(String query);

    @Insert
    void insertAll(Item... items);

    @Insert
    void insert(Item item);

    @Delete
    void delete(Item item);

}
