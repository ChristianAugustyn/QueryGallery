package ca.yorku.eecs.mack.democamera71179;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ImageBean {
    @PrimaryKey
    public String id;

    @ColumnInfo(name = "tags")
    public String tags;
}
