package ca.yorku.eecs.mack.democamera71179;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDAO {
    @Query("select * from imagebean")
    List<ImageBean> getAll();

    @Query("select * from imagebean where tags like :tag")
    List<ImageBean> getImagesByTag(String tag);


}
