package ca.yorku.eecs.mack.democamera71179;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDAO {
    @Query("select * from imagebean")
    List<ImageBean> getAll();

    @Query("select * from imagebean where tags like '%' || :tag || '%' ")
    List<ImageBean> getImagesByTag(String tag);

    @Query("select * from imagebean where id = :id")
    ImageBean getImageById(String id);

    @Insert
        //for brand new images in tth DB
    void insert(ImageBean imageBean);

    @Update
    void updateImageTags(ImageBean imageBean);

}
