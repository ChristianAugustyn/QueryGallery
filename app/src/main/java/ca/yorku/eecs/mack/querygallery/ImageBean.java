package ca.yorku.eecs.mack.querygallery;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ImageBean {
    @PrimaryKey @NonNull
    public String id;

    @ColumnInfo(name = "tags")
    public String tags;
}
