package ca.yorku.eecs.mack.querygallery;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ImageDAO_Impl implements ImageDAO {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ImageBean> __insertionAdapterOfImageBean;

  private final EntityDeletionOrUpdateAdapter<ImageBean> __updateAdapterOfImageBean;

  public ImageDAO_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfImageBean = new EntityInsertionAdapter<ImageBean>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `ImageBean` (`id`,`tags`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ImageBean value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
        if (value.tags == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.tags);
        }
      }
    };
    this.__updateAdapterOfImageBean = new EntityDeletionOrUpdateAdapter<ImageBean>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `ImageBean` SET `id` = ?,`tags` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ImageBean value) {
        if (value.id == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.id);
        }
        if (value.tags == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.tags);
        }
        if (value.id == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.id);
        }
      }
    };
  }

  @Override
  public void insert(final ImageBean imageBean) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfImageBean.insert(imageBean);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateImageTags(final ImageBean imageBean) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfImageBean.handle(imageBean);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<ImageBean> getAll() {
    final String _sql = "select * from imagebean";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
      final List<ImageBean> _result = new ArrayList<ImageBean>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ImageBean _item;
        _item = new ImageBean();
        _item.id = _cursor.getString(_cursorIndexOfId);
        _item.tags = _cursor.getString(_cursorIndexOfTags);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ImageBean> getImagesByTag(final String tag) {
    final String _sql = "select * from imagebean where tags like '%' || ? || '%' ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (tag == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, tag);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
      final List<ImageBean> _result = new ArrayList<ImageBean>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ImageBean _item;
        _item = new ImageBean();
        _item.id = _cursor.getString(_cursorIndexOfId);
        _item.tags = _cursor.getString(_cursorIndexOfTags);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ImageBean getImageById(final String id) {
    final String _sql = "select * from imagebean where id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
      final ImageBean _result;
      if(_cursor.moveToFirst()) {
        _result = new ImageBean();
        _result.id = _cursor.getString(_cursorIndexOfId);
        _result.tags = _cursor.getString(_cursorIndexOfTags);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
