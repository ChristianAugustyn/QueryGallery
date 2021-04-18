package ca.yorku.eecs.mack.democamera71179;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//idk wtf this shit does, something about creating
@Database(entities = {ImageBean.class}, version = 1, exportSchema = false)
public abstract class TagDB extends RoomDatabase {
    public abstract ImageDAO imageDAO();
}

