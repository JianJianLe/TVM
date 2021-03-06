package com.tvm.tvm.bean.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.tvm.tvm.bean.BillSetting;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BILL_SETTING".
*/
public class BillSettingDao extends AbstractDao<BillSetting, Long> {

    public static final String TABLENAME = "BILL_SETTING";

    /**
     * Properties of entity BillSetting.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TemplateNum = new Property(1, String.class, "templateNum", false, "TEMPLATE_NUM");
        public final static Property TicketName = new Property(2, String.class, "ticketName", false, "TICKET_NAME");
        public final static Property TicketBody = new Property(3, String.class, "ticketBody", false, "TICKET_BODY");
        public final static Property CreateDate = new Property(4, java.util.Date.class, "createDate", false, "CREATE_DATE");
    }


    public BillSettingDao(DaoConfig config) {
        super(config);
    }
    
    public BillSettingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BILL_SETTING\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TEMPLATE_NUM\" TEXT," + // 1: templateNum
                "\"TICKET_NAME\" TEXT," + // 2: ticketName
                "\"TICKET_BODY\" TEXT," + // 3: ticketBody
                "\"CREATE_DATE\" INTEGER);"); // 4: createDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BILL_SETTING\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BillSetting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String templateNum = entity.getTemplateNum();
        if (templateNum != null) {
            stmt.bindString(2, templateNum);
        }
 
        String ticketName = entity.getTicketName();
        if (ticketName != null) {
            stmt.bindString(3, ticketName);
        }
 
        String ticketBody = entity.getTicketBody();
        if (ticketBody != null) {
            stmt.bindString(4, ticketBody);
        }
 
        java.util.Date createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(5, createDate.getTime());
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BillSetting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String templateNum = entity.getTemplateNum();
        if (templateNum != null) {
            stmt.bindString(2, templateNum);
        }
 
        String ticketName = entity.getTicketName();
        if (ticketName != null) {
            stmt.bindString(3, ticketName);
        }
 
        String ticketBody = entity.getTicketBody();
        if (ticketBody != null) {
            stmt.bindString(4, ticketBody);
        }
 
        java.util.Date createDate = entity.getCreateDate();
        if (createDate != null) {
            stmt.bindLong(5, createDate.getTime());
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public BillSetting readEntity(Cursor cursor, int offset) {
        BillSetting entity = new BillSetting( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // templateNum
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ticketName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ticketBody
            cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)) // createDate
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BillSetting entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTemplateNum(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTicketName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTicketBody(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCreateDate(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(BillSetting entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(BillSetting entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BillSetting entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
