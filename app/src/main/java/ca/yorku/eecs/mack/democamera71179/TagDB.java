package ca.yorku.eecs.mack.democamera71179;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ImageBean.class}, version = 1)
public abstract class TagDB extends RoomDatabase {
    public abstract ImageDAO imageDAO();
}

