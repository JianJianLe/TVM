package com.tvm.tvm.bean.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.tvm.tvm.bean.Setting;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SETTING".
*/
public class SettingDao extends AbstractDao<Setting, Long> {

    public static final String TABLENAME = "SETTING";

    /**
     * Properties of entity Setting.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ShopName = new Property(1, String.class, "shopName", false, "SHOP_NAME");
        public final static Property DeviceNo = new Property(2, String.class, "deviceNo", false, "DEVICE_NO");
        public final static Property Md5Key = new Property(3, String.class, "md5Key", false, "MD5_KEY");
        public final static Property SelectTimeOut = new Property(4, int.class, "selectTimeOut", false, "SELECT_TIME_OUT");
        public final static Property PayTimeOut = new Property(5, int.class, "payTimeOut", false, "PAY_TIME_OUT");
        public final static Property PrintTimeOut = new Property(6, int.class, "printTimeOut", false, "PRINT_TIME_OUT");
        public final static Property PrintQRCodeFlag = new Property(7, String.class, "printQRCodeFlag", false, "PRINT_QRCODE_FLAG");
        public final static Property PayDesc = new Property(8, String.class, "payDesc", false, "PAY_DESC");
        public final static Property PayDeviceID = new Property(9, String.class, "payDeviceID", false, "PAY_DEVICE_ID");
        public final static Property ShowMainViewFlag = new Property(10, String.class, "showMainViewFlag", false, "SHOW_MAIN_VIEW_FLAG");
        public final static Property InitalTicketNumber = new Property(11, int.class, "initalTicketNumber", false, "INITAL_TICKET_NUMBER");
    }


    public SettingDao(DaoConfig config) {
        super(config);
    }
    
    public SettingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SETTING\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SHOP_NAME\" TEXT," + // 1: shopName
                "\"DEVICE_NO\" TEXT," + // 2: deviceNo
                "\"MD5_KEY\" TEXT," + // 3: md5Key
                "\"SELECT_TIME_OUT\" INTEGER NOT NULL ," + // 4: selectTimeOut
                "\"PAY_TIME_OUT\" INTEGER NOT NULL ," + // 5: payTimeOut
                "\"PRINT_TIME_OUT\" INTEGER NOT NULL ," + // 6: printTimeOut
                "\"PRINT_QRCODE_FLAG\" TEXT," + // 7: printQRCodeFlag
                "\"PAY_DESC\" TEXT," + // 8: payDesc
                "\"PAY_DEVICE_ID\" TEXT," + // 9: payDeviceID
                "\"SHOW_MAIN_VIEW_FLAG\" TEXT," + // 10: showMainViewFlag
                "\"INITAL_TICKET_NUMBER\" INTEGER NOT NULL );"); // 11: initalTicketNumber
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SETTING\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Setting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String shopName = entity.getShopName();
        if (shopName != null) {
            stmt.bindString(2, shopName);
        }
 
        String deviceNo = entity.getDeviceNo();
        if (deviceNo != null) {
            stmt.bindString(3, deviceNo);
        }
 
        String md5Key = entity.getMd5Key();
        if (md5Key != null) {
            stmt.bindString(4, md5Key);
        }
        stmt.bindLong(5, entity.getSelectTimeOut());
        stmt.bindLong(6, entity.getPayTimeOut());
        stmt.bindLong(7, entity.getPrintTimeOut());
 
        String printQRCodeFlag = entity.getPrintQRCodeFlag();
        if (printQRCodeFlag != null) {
            stmt.bindString(8, printQRCodeFlag);
        }
 
        String payDesc = entity.getPayDesc();
        if (payDesc != null) {
            stmt.bindString(9, payDesc);
        }
 
        String payDeviceID = entity.getPayDeviceID();
        if (payDeviceID != null) {
            stmt.bindString(10, payDeviceID);
        }
 
        String showMainViewFlag = entity.getShowMainViewFlag();
        if (showMainViewFlag != null) {
            stmt.bindString(11, showMainViewFlag);
        }
        stmt.bindLong(12, entity.getInitalTicketNumber());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Setting entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String shopName = entity.getShopName();
        if (shopName != null) {
            stmt.bindString(2, shopName);
        }
 
        String deviceNo = entity.getDeviceNo();
        if (deviceNo != null) {
            stmt.bindString(3, deviceNo);
        }
 
        String md5Key = entity.getMd5Key();
        if (md5Key != null) {
            stmt.bindString(4, md5Key);
        }
        stmt.bindLong(5, entity.getSelectTimeOut());
        stmt.bindLong(6, entity.getPayTimeOut());
        stmt.bindLong(7, entity.getPrintTimeOut());
 
        String printQRCodeFlag = entity.getPrintQRCodeFlag();
        if (printQRCodeFlag != null) {
            stmt.bindString(8, printQRCodeFlag);
        }
 
        String payDesc = entity.getPayDesc();
        if (payDesc != null) {
            stmt.bindString(9, payDesc);
        }
 
        String payDeviceID = entity.getPayDeviceID();
        if (payDeviceID != null) {
            stmt.bindString(10, payDeviceID);
        }
 
        String showMainViewFlag = entity.getShowMainViewFlag();
        if (showMainViewFlag != null) {
            stmt.bindString(11, showMainViewFlag);
        }
        stmt.bindLong(12, entity.getInitalTicketNumber());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Setting readEntity(Cursor cursor, int offset) {
        Setting entity = new Setting( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // shopName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // deviceNo
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // md5Key
            cursor.getInt(offset + 4), // selectTimeOut
            cursor.getInt(offset + 5), // payTimeOut
            cursor.getInt(offset + 6), // printTimeOut
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // printQRCodeFlag
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // payDesc
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // payDeviceID
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // showMainViewFlag
            cursor.getInt(offset + 11) // initalTicketNumber
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Setting entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setShopName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDeviceNo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMd5Key(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSelectTimeOut(cursor.getInt(offset + 4));
        entity.setPayTimeOut(cursor.getInt(offset + 5));
        entity.setPrintTimeOut(cursor.getInt(offset + 6));
        entity.setPrintQRCodeFlag(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPayDesc(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPayDeviceID(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setShowMainViewFlag(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setInitalTicketNumber(cursor.getInt(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Setting entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Setting entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Setting entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
