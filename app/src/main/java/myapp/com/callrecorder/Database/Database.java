package myapp.com.callrecorder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jeff on 06-May-16.
 * <p/>
 * Our SQLlite database
 */
public class Database extends SQLiteOpenHelper {

    public static String NAME = "callRecorder";
    public static int VERSION = 1;


    // Customer Table
    private static final String TABLE_CUSTOMER = "registration";
    private static final String KEY_PRIMARY_USER_ID = "primary_user_id";
    private static final String KEY_CUSTOMER_ID = "customer_id";
    private static final String KEY_CUSTOMER_NAME = "customer_name";
    private static final String KEY_MOB_NO = "mob_no";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private static final String TABLE_CALLDETAILS = "call_details_table";
    private static final String KEY_CALLDETAILS_ID = "primary_calldetails_id";
    private static final String KEY_FILE_PATH = "filepath";
    private static final String KEY_CALLDETAILS_MOBNO = "calldetails_mobno";
    private static final String KEY_CALL_TYPE = "call_type";
    private static final String KEY_CALL_DURATION = "call_duration";
    private static final String KEY_CALL_DATETIME = "call_datetime";
    private static final String KEY_FILE_UPLOAD_STATUS = "fileupload_status";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PURPOSE = "purpose";
    private static final String KEY_COMMENTS = "comments";

    private static final String TABLE_MISSEDCALL_DETAILS = "misscall_details_table";
    private static final String KEY_MISSEDCALL_DETAILS_ID = "primary_misscalldetails_id";
    private static final String KEY_MISSEDCALL_DETAILS_NAME= "primary_misscalldetails_name";
    private static final String KEY_MISSEDCALL_DETAILS_MOBNO = "misscalldetails_mobno";
    private static final String KEY_MISSEDCALL_DATETIME = "misscall_datetime";



    String CREATE_CALL_RECORDS_TABLE = "CREATE TABLE records(_id INTEGER PRIMARY KEY, phone_number TEXT, outgoing INTEGER, start_date_time INTEGER, end_date_time INTEGER, path_to_recording TEXT, keep INTEGER DEFAULT 0, backup_state INTEGER DEFAULT 0 )";
    public static String CALL_RECORDS_TABLE = "records";
    public static String CALL_RECORDS_TABLE_ID = "_id"; // only because of https://developer.android.com/reference/android/widget/CursorAdapter.html
    public static String CALL_RECORDS_TABLE_PHONE_NUMBER = "phone_number";
    public static String CALL_RECORDS_TABLE_OUTGOING = "outgoing";
    public static String CALL_RECORDS_TABLE_START_DATE = "start_date_time";
    public static String CALL_RECORDS_TABLE_END_DATE = "end_date_time";
    public static String CALL_RECORDS_TABLE_RECORDING_PATH = "path_to_recording";
    public static String CALL_RECORDS_TABLE_KEEP = "keep";
    public static String CALL_RECORDS_BACKUP_STATE = "backup_state";


    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context.getApplicationContext());
        }
        return instance;
    }

    public Database(Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_CALL_RECORDS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMER + "("
                + KEY_PRIMARY_USER_ID + " INTEGER PRIMARY KEY,"
                + KEY_CUSTOMER_ID + " INTEGER,"
                + KEY_ACCESS_TOKEN + " TEXT,"
                + KEY_CUSTOMER_NAME + " TEXT,"
                + KEY_MOB_NO + " TEXT" + ")";

        String CREATE_CALL_DETAILS_TABLE = "CREATE TABLE " + TABLE_CALLDETAILS + "("
                + KEY_CALLDETAILS_ID + " INTEGER PRIMARY KEY,"
                + KEY_FILE_PATH + " TEXT,"
                + KEY_CALLDETAILS_MOBNO + " TEXT,"
                + KEY_CALL_TYPE + " TEXT,"
                + KEY_CALL_DURATION + " TEXT,"
                + KEY_CALL_DATETIME + " TEXT,"
                + KEY_FILE_UPLOAD_STATUS + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PURPOSE + " TEXT,"
                + KEY_COMMENTS + " TEXT" + ")";

        String CREATE_MISSED_CALL_DETAILS_TABLE = "CREATE TABLE " + TABLE_MISSEDCALL_DETAILS + "("
                + KEY_MISSEDCALL_DETAILS_ID + " INTEGER PRIMARY KEY,"
                + KEY_MISSEDCALL_DETAILS_NAME + " TEXT,"
                + KEY_MISSEDCALL_DETAILS_MOBNO + " TEXT,"
                + KEY_MISSEDCALL_DATETIME + " TEXT" + ")";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CALL_DETAILS_TABLE);
        db.execSQL(CREATE_MISSED_CALL_DETAILS_TABLE);

    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        onCreate(db);
    }


    private CallLog getCallLogFrom(Cursor cursor) {
        CallLog phoneCall = new CallLog();
        phoneCall.isNew = false;

        // String[] columnNames = cursor.getColumnNames();

        int index = cursor.getColumnIndex(CALL_RECORDS_TABLE_ID);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_ID, cursor.getInt(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_PHONE_NUMBER);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_PHONE_NUMBER, cursor.getString(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_OUTGOING);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_OUTGOING, cursor.getInt(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_START_DATE);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_START_DATE, cursor.getLong(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_END_DATE);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_END_DATE, cursor.getLong(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_RECORDING_PATH);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_RECORDING_PATH, cursor.getString(index));

        index = cursor.getColumnIndex(CALL_RECORDS_TABLE_KEEP);
        phoneCall.getContent().put(CALL_RECORDS_TABLE_KEEP, cursor.getInt(index));

        index = cursor.getColumnIndex(CALL_RECORDS_BACKUP_STATE);
        phoneCall.getContent().put(CALL_RECORDS_BACKUP_STATE, cursor.getInt(index));

        return phoneCall;
    }


    public synchronized ArrayList<CallLog> getAllCalls() {
        ArrayList<CallLog> array_list = new ArrayList<CallLog>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + Database.CALL_RECORDS_TABLE, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                CallLog phoneCall = getCallLogFrom(cursor);
                array_list.add(phoneCall);
                cursor.moveToNext();
            }
            return array_list;
        } finally {
            db.close();
        }
    }


    public synchronized ArrayList<CallLog> getAllCalls(boolean outgoing) {
        ArrayList<CallLog> array_list = new ArrayList<CallLog>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery("select * from " + Database.CALL_RECORDS_TABLE + " where " + Database.CALL_RECORDS_TABLE_OUTGOING + "=" + (outgoing ? "1" : "0"), null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                CallLog phoneCall = getCallLogFrom(cursor);
                array_list.add(phoneCall);
                cursor.moveToNext();
            }
            return array_list;
        } finally {
            db.close();
        }
    }

    public synchronized boolean addCall(CallLog phoneCall) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (phoneCall.isNew) {
                long rowId = db.insert(Database.CALL_RECORDS_TABLE, null, phoneCall.getContent());
                // rowID is and Alias for _ID  see: http://www.sqlite.org/autoinc.html
                phoneCall.getContent().put(Database.CALL_RECORDS_TABLE_ID, rowId);
            } else {
                db.update(Database.CALL_RECORDS_TABLE, phoneCall.getContent(), CALL_RECORDS_TABLE_ID + "=" + phoneCall.getId(), null);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public synchronized boolean updateCall(CallLog phoneCall) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.update(Database.CALL_RECORDS_TABLE, phoneCall.getContent(), "id = ?", new String[]{Integer.toString(phoneCall.getId())});
            return true;
        } finally {
            db.close();
        }
    }

    public synchronized int count() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            int numRows = (int) DatabaseUtils.queryNumEntries(db, Database.CALL_RECORDS_TABLE);
            return numRows;
        } finally {
            db.close();
        }
    }

    public synchronized CallLog getCall(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + Database.CALL_RECORDS_TABLE + " where " + Database.CALL_RECORDS_TABLE_ID + "=" + id, null);
            if (!cursor.moveToFirst()) return null; // does not exist
            return getCallLogFrom(cursor);
        } finally {
            db.close();
        }
    }

    public synchronized void removeCall(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + Database.CALL_RECORDS_TABLE + " where " + Database.CALL_RECORDS_TABLE_ID + "=" + id, null);
            if (!cursor.moveToFirst()) return; // doesn't exist
            CallLog call = getCallLogFrom(cursor);
            String path = call.getPathToRecording();
            try {
                if (null != path)
                    new File(path).delete();
            } catch (Exception e) {

            }
            db.execSQL("Delete from " + Database.CALL_RECORDS_TABLE + " where " + Database.CALL_RECORDS_TABLE_ID + "=" + id);
        } finally {
            db.close();
        }
    }


    public synchronized void removeAllCalls(boolean includeKept) {
        final ArrayList<CallLog> allCalls = getAllCalls();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            for (CallLog call : allCalls) {
                if (includeKept || !call.isKept()) {
                    try {
                        new File(call.getPathToRecording()).delete();
                    } catch (Exception e) {
                    }
                    try {
                        db.execSQL("Delete from " + Database.CALL_RECORDS_TABLE + " where " + Database.CALL_RECORDS_TABLE_ID + "=" + call.getId());
                    } catch (Exception e) {
                    }
                }
            }
            // db.delete(Database.CALL_RECORDS_TABLE, null, null);
        } finally {
            db.close();
        }
    }


    // ############################# User table ###########################
    // code to add User
    public void addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOMER_ID, customer.getCust_id());
        values.put(KEY_ACCESS_TOKEN, customer.getAccess_token());
        values.put(KEY_CUSTOMER_NAME, customer.getCust_name());
        values.put(KEY_MOB_NO, customer.getMob_no());

        // Inserting Row
        db.insert(TABLE_CUSTOMER, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public Customer getCustomerDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

        }
        Customer customer = new Customer(
                cursor.getInt(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4));
        return customer;
    }

    // code to get all Users in a list view
    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<Customer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CUSTOMER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Customer customer = new Customer();

                customer.setCust_id(cursor.getInt(1));
                customer.setAccess_token(cursor.getString(2));
                customer.setCust_name(cursor.getString(3));
                customer.setMob_no(cursor.getString(4));

                customerList.add(customer);
            } while (cursor.moveToNext());
        }

        // return contact list
        return customerList;
    }

//    //// code to update call details
//    public int UpdateCallDetails(int CustomerId,String fileName ,String CallDetailsMobNo,String CallType,String CallDuration,String CallDateTime,String AudioFileUploadStatus) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_FILE_NAME,fileName);
//        values.put(KEY_CALLDETAILS_MOBNO, CallDetailsMobNo);
//        values.put(KEY_CALL_TYPE,CallType);
//        values.put(KEY_CALL_DURATION, CallDuration);
//        values.put(KEY_CALL_DATETIME,CallDateTime);
//        values.put(KEY_FILE_UPLOAD_STATUS, AudioFileUploadStatus);
//
//
//        // updating row
//        return db.update(TABLE_CUSTOMER, values, KEY_CUSTOMER_ID + " = ?",
//                new String[]{String.valueOf(CustomerId)});
//    }


    // ############################# addCallDetails table ###########################
    // code to add User
    public long addCallDetails(CallDetails calldetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FILE_PATH, calldetails.getFile_path());
        values.put(KEY_CALLDETAILS_MOBNO, calldetails.getCalldetails_mobno());
        values.put(KEY_CALL_TYPE, calldetails.getCall_type());
        values.put(KEY_CALL_DURATION, calldetails.getCall_duration());
        values.put(KEY_CALL_DATETIME, calldetails.getCall_datetime());
        values.put(KEY_FILE_UPLOAD_STATUS, calldetails.getFile_upload_status());

        // Inserting Row
     long id= db.insert(TABLE_CALLDETAILS, null, values);
        //2nd argument is String containing nullColumnHack


        db.close();
        return id;// Closing database connection
    }

    // code to update call details
    public int UpdateCallDetails(long id,String name ,String address,String purpose,String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME,name);
        values.put(KEY_ADDRESS, address);
        values.put(KEY_PURPOSE,purpose);
        values.put(KEY_COMMENTS, comments);
        // updating row
        return db.update(TABLE_CALLDETAILS, values, KEY_CALLDETAILS_ID + " = ?",
                new String[]{String.valueOf(id)});
    }


    public CallDetails getCallDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CALLDETAILS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

        }
        CallDetails calldetails = new CallDetails(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));
        return calldetails;
    }

    // code to get all Users in a list view
    public List<CallDetails> getAllCustomersCalldetails() {
        List<CallDetails> calldetailsList = new ArrayList<CallDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALLDETAILS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CallDetails calldetails = new CallDetails();
                calldetails.setId(cursor.getInt(0));
                calldetails.setFile_path(cursor.getString(1));
                calldetails.setCalldetails_mobno(cursor.getString(2));
                calldetails.setCall_type(cursor.getString(3));
                calldetails.setCall_duration(cursor.getString(4));
                calldetails.setCall_datetime(cursor.getString(5));
                calldetails.setFile_upload_status(cursor.getString(6));
                calldetails.setName(cursor.getString(7));
                calldetails.setAddress(cursor.getString(8));
                calldetails.setPurpose(cursor.getString(9));
                calldetails.setComments(cursor.getString(10));
                calldetailsList.add(calldetails);
            } while (cursor.moveToNext());
        }

        // return contact list
        return calldetailsList;
    }

    // code to add Notification
    public void deleteCallDetails(int id) {
        long rowDeleted = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_CALLDETAILS + " WHERE " + KEY_CALLDETAILS_ID + "='" + id + "'");
            // Closing database connection
        } catch (Exception e) {
            Log.i("row Delete Exception", e.toString());
        }
        db.close();
    }





    // ############################# addMissCallDetails table ###########################
    // code to add User
    public long addMissCallDetails(MissCallDetails misscalldetails) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MISSEDCALL_DETAILS_NAME, misscalldetails.getMisscallDetailName());
        values.put(KEY_MISSEDCALL_DETAILS_MOBNO, misscalldetails.getMisscallDetailMobno());
        values.put(KEY_MISSEDCALL_DATETIME, misscalldetails.getMisscallDateTime());

        // Inserting Row
        long id= db.insert(TABLE_MISSEDCALL_DETAILS, null, values);
        //2nd argument is String containing nullColumnHack


        db.close();
        return id;// Closing database connection
    }
    // code to get all Users in a list view
    public List<MissCallDetails> getAllMissCalldetails() {
        List<MissCallDetails> misscalldetailsList = new ArrayList<MissCallDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MISSEDCALL_DETAILS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MissCallDetails misscalldetails = new MissCallDetails();
                misscalldetails.setId(cursor.getInt(0));
                misscalldetails.setMisscallDetailName(cursor.getString(1));
                misscalldetails.setMisscallDetailMobno(cursor.getString(2));
                misscalldetails.setMisscallDateTime(cursor.getString(3));
                misscalldetailsList.add(misscalldetails);
            } while (cursor.moveToNext());
        }

        // return contact list
        return misscalldetailsList;
    }

}