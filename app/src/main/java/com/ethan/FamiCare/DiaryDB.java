package com.ethan.FamiCare;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.Date;

public class DiaryDB {
//1
    private SQLiteDatabase db = null;

    private static final String DB_Name = "DiaryDB";//資料庫
    private static final int DB_Version = 1;

    private final static String TB_NAME = "DiaryTB";//資料表 有DATE,TITLE,CONTENT
    private final static String DATE = "date";
    private final static String TITLE = "title";
    private final static String CONTENT = "content";
    private final static String CREATE_TABLE = "CREATE TABLE " + TB_NAME + " (" +
            DATE + " DATE PRIMARY KEY," +
            TITLE + " VARCHAR(50)," +
            CONTENT + " VARCHAR(500) )";
    private Context context;

    //    DiaryDB的建構式
    public DiaryDB(Context context) {
        this.context = context;
    }

    //     建立open()方法，資料庫存執行開啟資料庫，尚未存在則建立資料庫
    public void open() throws SQLException {
        try {
            //        建立資料庫並指定權限
            db = context.openOrCreateDatabase("DiaryDB.db", Context.MODE_PRIVATE, null);
            //        建立表格
            db.execSQL(CREATE_TABLE);

        } catch (Exception e) {

            Toast.makeText(context, "DiaryDB.db 已建立", Toast.LENGTH_LONG).show();
        }

    }

    //    建立新增、修改(更新)、刪除，資料操作
    //    execSQL完整輸入SQL語法實現，資料操作

    //    新增
    public void insert(Date date, String title, String content) {
        String insert_text =
                "INSERT INTO " + TB_NAME + "( " + DATE + "," + TITLE + "," + CONTENT + ") " +
                        "values ('" + date + "','" + title + "','" + content + "')";
        db.execSQL(insert_text);
    }

    //    修改(更新)
    public void update(Date date, String title, String content) {
        String update_text =
                " UPDATE " + TB_NAME +
                        " SET " + TITLE + "='" + title + "'," + CONTENT + "='" + content +
                        "' WHERE " + DATE + "=" + date;
        db.execSQL(update_text);
    }

    //    刪除
    public void delete(Date date) {
        String delete_text =
                "DELETE FROM " + TB_NAME +
                        " WHERE " + DATE + "=" + date;
        db.execSQL(delete_text);

    }

    //    查詢單筆資料
    public Cursor select(Date date) {
        String select_text =
                "SELECT * FROM " + TB_NAME +
                        " WHERE " + DATE + "=" + date;
        Cursor cursor = db.rawQuery(select_text, null);
        return cursor;
    }

    public String selectTitle(Date date) {
        String select_date =
                "SELECT ISNULL(DATE, 尚未設置標題) FROM " + TB_NAME +
                        " WHERE " + DATE + "=" + date;
        String DBdate = "" + db.rawQuery(select_date, null);

        String title = "尚未設置標題";
        if (!DBdate.equals(title)) {
            String select_text =
                    "SELECT TITLE FROM " + TB_NAME +
                            " WHERE " + DATE + "=" + date;
            title = "" + db.rawQuery(select_text, null);
        }
        return title;
    }

    //    查詢所有資料
    public Cursor select_all() {
        String select_text = "SELECT * FROM " + TB_NAME;
        Cursor cursor = db.rawQuery(select_text, null);
        return cursor;
    }
}