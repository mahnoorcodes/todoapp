package com.example.to_doapp.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.to_doapp.Model.ToDoModel;
import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {
    private SQLiteDatabase todo_db;
    private static final String DB_Name = "todo_database";
    private static final String Table_Name = "todo_table";
    private static final String ID_Col = "id";
    private static final String Task_Col = "task";
    private static final String Status_Col = "status";
    public DBHandler(@Nullable Context context) {
        super(context, DB_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase todo_db) {
        todo_db.execSQL("CREATE TABLE IF NOT EXISTS " + Table_Name + "(id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT, status INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase todo_db, int oldVersion, int newVersion) {
        todo_db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(todo_db);
    }
    public void insertTask(ToDoModel model) {
        todo_db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Task_Col, model.getTask());
        values.put(Status_Col, 0);

        todo_db.insert(Table_Name, null, values);
    }

    public void updateTask(int id, String task){
        todo_db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Task_Col , task);
        todo_db.update(Table_Name , values, ID_Col + "=?", new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id , int status){
        todo_db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Status_Col, status);
        todo_db.update(Table_Name , values , ID_Col + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id){
        todo_db = this.getWritableDatabase();
        todo_db.delete(Table_Name, ID_Col + "=?", new String[]{String.valueOf(id)});
    }

    public List<ToDoModel> getAllTasks() {
        todo_db = this.getWritableDatabase();
        List<ToDoModel> modelList = new ArrayList<>();
        Cursor cursor = null;

        try {
            todo_db.beginTransaction();
            cursor = todo_db.query(Table_Name, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {

                int idIndex = cursor.getColumnIndex(ID_Col);
                int taskIndex = cursor.getColumnIndex(Task_Col);
                int statusIndex = cursor.getColumnIndex(Status_Col);

                // Ensure indices are valid
                if (idIndex == -1 || taskIndex == -1 || statusIndex == -1) {
                    throw new IllegalArgumentException("One or more column names are invalid in the database schema.");
                }

                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cursor.getInt(idIndex));
                    task.setTask(cursor.getString(taskIndex));
                    task.setStatus(cursor.getInt(statusIndex));
                    modelList.add(task);
                } while (cursor.moveToNext());
            }
            todo_db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            todo_db.endTransaction();
        }
        return modelList;
    }
}
