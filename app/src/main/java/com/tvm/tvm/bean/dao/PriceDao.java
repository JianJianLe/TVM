package com.tvm.tvm.bean.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.tvm.tvm.bean.Price;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PRICE".
*/
public class PriceDao extends AbstractDao<Price, Long> {

    public static final String TABLENAME = "PRICE";

    /**
     * Properties of entity Price.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Pic = new Property(1, byte[].class, "pic", false, "PIC");
        public final static Property Price = new Property(2, double.class, "price", false, "PRICE");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Description = new Property(4, String.class, "description", false, "DESCRIPTION");
        public final static Property IsDelete = new Property(5, int.class, "isDelete", false, "IS_DELETE");
    }


    public PriceDao(DaoConfig config) {
        super(config);
    }
    
    public PriceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PRICE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PIC\" BLOB," + // 1: pic
                "\"PRICE\" REAL NOT NULL ," + // 2: price
                "\"TITLE\" TEXT," + // 3: title
                "\"DESCRIPTION\" TEXT," + // 4: description
                "\"IS_DELETE\" INTEGER NOT NULL );"); // 5: isDelete
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PRICE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Price entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        byte[] pic = entity.getPic();
        if (pic != null) {
            stmt.bindBlob(2, pic);
        }
        stmt.bindDouble(3, entity.getPrice());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(5, description);
        }
        stmt.bindLong(6, entity.getIsDelete());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Price entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        byte[] pic = entity.getPic();
        if (pic != null) {
            stmt.bindBlob(2, pic);
        }
        stmt.bindDouble(3, entity.getPrice());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(5, description);
        }
        stmt.bindLong(6, entity.getIsDelete());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Price readEntity(Cursor cursor, int offset) {
        Price entity = new Price( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1), // pic
            cursor.getDouble(offset + 2), // price
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // description
            cursor.getInt(offset + 5) // isDelete
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Price entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPic(cursor.isNull(offset + 1) ? null : cursor.getBlob(offset + 1));
        entity.setPrice(cursor.getDouble(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDescription(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setIsDelete(cursor.getInt(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Price entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Price entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Price entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}