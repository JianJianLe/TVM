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
        public final static Property PayDeviceName = new Property(10, String.class, "payDeviceName", false, "PAY_DEVICE_NAME");
        public final static Property BillAcceptorName = new Property(11, String.class, "billAcceptorName", false, "BILL_ACCEPTOR_NAME");
        public final static Property BillType = new Property(12, String.class, "billType", false, "BILL_TYPE");
        public final static Property ShowMainViewFlag = new Property(13, String.class, "showMainViewFlag", false, "SHOW_MAIN_VIEW_FLAG");
        public final static Property ShowOrderNumberFlag = new Property(14, String.class, "showOrderNumberFlag", false, "SHOW_ORDER_NUMBER_FLAG");
        public final static Property InitalTicketNumber = new Property(15, int.class, "initalTicketNumber", false, "INITAL_TICKET_NUMBER");
        public final static Property BillTimesNumber = new Property(16, int.class, "billTimesNumber", false, "BILL_TIMES_NUMBER");
        public final static Property BillAcceptorCashAmountType = new Property(17, String.class, "billAcceptorCashAmountType", false, "BILL_ACCEPTOR_CASH_AMOUNT_TYPE");
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
                "\"PAY_DEVICE_NAME\" TEXT," + // 10: payDeviceName
                "\"BILL_ACCEPTOR_NAME\" TEXT," + // 11: billAcceptorName
                "\"BILL_TYPE\" TEXT," + // 12: billType
                "\"SHOW_MAIN_VIEW_FLAG\" TEXT," + // 13: showMainViewFlag
                "\"SHOW_ORDER_NUMBER_FLAG\" TEXT," + // 14: showOrderNumberFlag
                "\"INITAL_TICKET_NUMBER\" INTEGER NOT NULL ," + // 15: initalTicketNumber
                "\"BILL_TIMES_NUMBER\" INTEGER NOT NULL ," + // 16: billTimesNumber
                "\"BILL_ACCEPTOR_CASH_AMOUNT_TYPE\" TEXT);"); // 17: billAcceptorCashAmountType
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
 
        String payDeviceName = entity.getPayDeviceName();
        if (payDeviceName != null) {
            stmt.bindString(11, payDeviceName);
        }
 
        String billAcceptorName = entity.getBillAcceptorName();
        if (billAcceptorName != null) {
            stmt.bindString(12, billAcceptorName);
        }
 
        String billType = entity.getBillType();
        if (billType != null) {
            stmt.bindString(13, billType);
        }
 
        String showMainViewFlag = entity.getShowMainViewFlag();
        if (showMainViewFlag != null) {
            stmt.bindString(14, showMainViewFlag);
        }
 
        String showOrderNumberFlag = entity.getShowOrderNumberFlag();
        if (showOrderNumberFlag != null) {
            stmt.bindString(15, showOrderNumberFlag);
        }
        stmt.bindLong(16, entity.getInitalTicketNumber());
        stmt.bindLong(17, entity.getBillTimesNumber());
 
        String billAcceptorCashAmountType = entity.getBillAcceptorCashAmountType();
        if (billAcceptorCashAmountType != null) {
            stmt.bindString(18, billAcceptorCashAmountType);
        }
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
 
        String payDeviceName = entity.getPayDeviceName();
        if (payDeviceName != null) {
            stmt.bindString(11, payDeviceName);
        }
 
        String billAcceptorName = entity.getBillAcceptorName();
        if (billAcceptorName != null) {
            stmt.bindString(12, billAcceptorName);
        }
 
        String billType = entity.getBillType();
        if (billType != null) {
            stmt.bindString(13, billType);
        }
 
        String showMainViewFlag = entity.getShowMainViewFlag();
        if (showMainViewFlag != null) {
            stmt.bindString(14, showMainViewFlag);
        }
 
        String showOrderNumberFlag = entity.getShowOrderNumberFlag();
        if (showOrderNumberFlag != null) {
            stmt.bindString(15, showOrderNumberFlag);
        }
        stmt.bindLong(16, entity.getInitalTicketNumber());
        stmt.bindLong(17, entity.getBillTimesNumber());
 
        String billAcceptorCashAmountType = entity.getBillAcceptorCashAmountType();
        if (billAcceptorCashAmountType != null) {
            stmt.bindString(18, billAcceptorCashAmountType);
        }
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
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // payDeviceName
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // billAcceptorName
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // billType
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // showMainViewFlag
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // showOrderNumberFlag
            cursor.getInt(offset + 15), // initalTicketNumber
            cursor.getInt(offset + 16), // billTimesNumber
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17) // billAcceptorCashAmountType
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
        entity.setPayDeviceName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setBillAcceptorName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setBillType(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setShowMainViewFlag(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setShowOrderNumberFlag(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setInitalTicketNumber(cursor.getInt(offset + 15));
        entity.setBillTimesNumber(cursor.getInt(offset + 16));
        entity.setBillAcceptorCashAmountType(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
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
