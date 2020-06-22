package com.latihanandroid.skripsitanpametode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.latihanandroid.skripsitanpametode.pojo.DailySchedule;
import com.latihanandroid.skripsitanpametode.pojo.RolesAndGoals;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME="dbdailyreminderapp_db";
    private static DatabaseHelper INSTANCE;
    private static final int DATABASE_VERSION=1;

    private static final String SQL_CREATE_TABLE_ROLE= String.format("CREATE TABLE %s ("
                    +"%s INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +"%s TEXT NOT NULL,"
                    +"%s TEXT NOT NULL)",
            RolesAndGoals.TABLE_NAME,
            RolesAndGoals.ID_COLUMN,
            RolesAndGoals.ROLES_COLUMN,
            RolesAndGoals.GOALS_COLUMN);
    private static final String SQL_CREATE_TABLE_DAILY_SCHEDULE= String.format(
            "CREATE TABLE %s "+//tablename
             "(%s INTEGER PRIMARY KEY AUTOINCREMENT,"+//id
             "%s INTEGER NOT NULL,"+ //hari
             "%s INTEGER NOT NULL,"+ //jenisAlarm
             "%s TEXT NOT NULL,"+ //waktu
             "%s TEXT NOT NULL,"+ //tempat
             "%s INTEGER NOT NULL)", //Aktivitas
            DailySchedule.TABLE_NAME,
            DailySchedule.ID_COLUMN,
            DailySchedule.HARI_COLUMN,
            DailySchedule.JENIS_ALARM_COLUMN,
            DailySchedule.WAKTU_COLUMN,
            DailySchedule.TEMPAT_COLUMN,
            DailySchedule.AKTIVITAS_COLUMN
            );
    private DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if (INSTANCE==null){
            synchronized (SQLiteOpenHelper.class){
                if (INSTANCE==null){
                    INSTANCE=new DatabaseHelper(context);
                }
            }
        }
        return INSTANCE;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ROLE);
        db.execSQL(SQL_CREATE_TABLE_DAILY_SCHEDULE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+RolesAndGoals.TABLE_NAME);
        onCreate(db);
    }

    public long insertRolesAndGoals(RolesAndGoals rolesAndGoals){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(RolesAndGoals.ROLES_COLUMN,rolesAndGoals.getRoles());
        contentValues.put(RolesAndGoals.GOALS_COLUMN,rolesAndGoals.getGoals());
        long id=database.insert(RolesAndGoals.TABLE_NAME,null,contentValues);
        database.close();
        return id;
    }

    public ArrayList<RolesAndGoals> readAllRolesAndGoals(){
//        new String[]{
//                RolesAndGoals.ID_COLUMN,
//                RolesAndGoals.ROLES_COLUMN,
//                RolesAndGoals.GOALS_COLUMN}
        ArrayList<RolesAndGoals> rolesAndGoalses=new ArrayList<>();
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.query(RolesAndGoals.TABLE_NAME,null,null,
                null,null,null,RolesAndGoals.ID_COLUMN);
        if (cursor.moveToFirst()){
            do{
                RolesAndGoals rolesAndGoals=new RolesAndGoals(
                        cursor.getInt(cursor.getColumnIndex(RolesAndGoals.ID_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(RolesAndGoals.ROLES_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(RolesAndGoals.GOALS_COLUMN))
                );
                rolesAndGoalses.add(rolesAndGoals);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return rolesAndGoalses;
    }
    public RolesAndGoals readRolesAndGoalsById(int id){
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor=database.query(RolesAndGoals.TABLE_NAME,null,RolesAndGoals.ID_COLUMN+" =? ",new String[]{
                String.valueOf(id)
        },null,null,null);
        RolesAndGoals rolesAndGoals=null;
        if (cursor.moveToFirst()){
            rolesAndGoals=new RolesAndGoals(
                    cursor.getInt(cursor.getColumnIndex(RolesAndGoals.ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(RolesAndGoals.ROLES_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(RolesAndGoals.GOALS_COLUMN))
            );
            cursor.close();
            database.close();
        }
        return rolesAndGoals;
    }
    public ArrayList<RolesAndGoals> findRolesAndGoals(String keyword){
        ArrayList<RolesAndGoals> rolesAndGoalses=new ArrayList<>();
        SQLiteDatabase database=this.getReadableDatabase();
        Cursor cursor= database.query(true,RolesAndGoals.TABLE_NAME,
                null,
                RolesAndGoals.ROLES_COLUMN+" LIKE ? OR "+ RolesAndGoals.GOALS_COLUMN+" LIKE ? ",
                new String[]{keyword+"%",keyword+"%"},
                null,
                null,
                RolesAndGoals.ID_COLUMN,
                null);
        if (cursor.moveToFirst()){
            do {
                RolesAndGoals rolesAndGoals=new RolesAndGoals(
                        cursor.getInt(cursor.getColumnIndex(RolesAndGoals.ID_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(RolesAndGoals.ROLES_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(RolesAndGoals.GOALS_COLUMN))
                );
                rolesAndGoalses.add(rolesAndGoals);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return rolesAndGoalses;
    }

    public long upadateRolesAndGoals(RolesAndGoals rolesAndGoals){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(RolesAndGoals.ROLES_COLUMN,rolesAndGoals.getRoles());
        contentValues.put(RolesAndGoals.GOALS_COLUMN,rolesAndGoals.getGoals());
        long changed=database.update(RolesAndGoals.TABLE_NAME,contentValues,RolesAndGoals.ID_COLUMN+" =?",new String[]{
                String.valueOf(rolesAndGoals.getId())
        });
        database.close();
        return changed;
    }
    public long deleteRolesAndGoals(RolesAndGoals rolesAndGoals){
        SQLiteDatabase database=this.getWritableDatabase();
        long deletedCount= database.delete(RolesAndGoals.TABLE_NAME,RolesAndGoals.ID_COLUMN+" =?",
                new String[]{String.valueOf(rolesAndGoals.getId())});
        database.close();
        return deletedCount;
    }

    public long insertDailySchedule(DailySchedule dailySchedule){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(DailySchedule.HARI_COLUMN,dailySchedule.getHari());
        contentValues.put(DailySchedule.JENIS_ALARM_COLUMN,dailySchedule.getJenisAlarmAsInt());
        contentValues.put(DailySchedule.WAKTU_COLUMN,dailySchedule.getWaktuAsString());
        contentValues.put(DailySchedule.TEMPAT_COLUMN,dailySchedule.getTempat());
        contentValues.put(DailySchedule.AKTIVITAS_COLUMN,dailySchedule.getAktivitas().getId());
        long id= database.insert(DailySchedule.TABLE_NAME,null,contentValues);
        database.close();
        return id;
    }
    public ArrayList<DailySchedule> readAllDailySchedule(int hari){
        ArrayList<DailySchedule> dailySchedules=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        Cursor cursor=database.query(DailySchedule.TABLE_NAME,null,DailySchedule.HARI_COLUMN+"=? ",new String[]{String.valueOf(hari)},null,
                null,DailySchedule.ID_COLUMN);
        if (cursor.moveToNext()){
            do {
                DailySchedule dailySchedule=new DailySchedule();
                dailySchedule.setId(cursor.getInt(cursor.getColumnIndex(DailySchedule.ID_COLUMN)));
                dailySchedule.setHari((short) cursor.getInt(cursor.getColumnIndex(DailySchedule.HARI_COLUMN)));
                dailySchedule.setJenisAlarmFromInt(cursor.getInt(cursor.getColumnIndex(DailySchedule.JENIS_ALARM_COLUMN)));
                dailySchedule.setWaktuFromString(cursor.getString(cursor.getColumnIndex(DailySchedule.WAKTU_COLUMN)));
                dailySchedule.setTempat(cursor.getString(cursor.getColumnIndex(DailySchedule.TEMPAT_COLUMN)));
                dailySchedule.setAktivitas(readRolesAndGoalsById(cursor.getInt(cursor.getColumnIndex(DailySchedule.AKTIVITAS_COLUMN))));
                dailySchedules.add(dailySchedule);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return dailySchedules;
    }
    public ArrayList<DailySchedule> readAllDailySchedule(){
        ArrayList<DailySchedule> dailySchedules=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        Cursor cursor=database.query(DailySchedule.TABLE_NAME,null,null,
                null,null,
                null,DailySchedule.ID_COLUMN);
        if (cursor.moveToNext()){
            do {
                DailySchedule dailySchedule=new DailySchedule();
                dailySchedule.setId(cursor.getInt(cursor.getColumnIndex(DailySchedule.ID_COLUMN)));
                dailySchedule.setHari((short) cursor.getInt(cursor.getColumnIndex(DailySchedule.HARI_COLUMN)));
                dailySchedule.setJenisAlarmFromInt(cursor.getInt(cursor.getColumnIndex(DailySchedule.JENIS_ALARM_COLUMN)));
                dailySchedule.setWaktuFromString(cursor.getString(cursor.getColumnIndex(DailySchedule.WAKTU_COLUMN)));
                dailySchedule.setTempat(cursor.getString(cursor.getColumnIndex(DailySchedule.TEMPAT_COLUMN)));
                dailySchedule.setAktivitas(readRolesAndGoalsById(cursor.getInt(cursor.getColumnIndex(DailySchedule.AKTIVITAS_COLUMN))));
                dailySchedules.add(dailySchedule);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return dailySchedules;
    }
    public ArrayList<DailySchedule> findDailySchedule(int hari,String keyword){
        ArrayList<DailySchedule> dailySchedules=new ArrayList<>();
        SQLiteDatabase database=getReadableDatabase();
        String sqlSelectTable= "SELECT "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.ID_COLUMN+", "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.HARI_COLUMN+", "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.JENIS_ALARM_COLUMN+", "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.WAKTU_COLUMN+", "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.TEMPAT_COLUMN+", "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.AKTIVITAS_COLUMN+", "+
                RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.ID_COLUMN+", "+
                RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.ROLES_COLUMN+", "+
                RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.GOALS_COLUMN+
                " FROM "+DailySchedule.TABLE_NAME +
                " INNER JOIN "+ RolesAndGoals.TABLE_NAME+
                " ON "+DailySchedule.TABLE_NAME+"."+DailySchedule.AKTIVITAS_COLUMN+"="+RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.ID_COLUMN
                +" WHERE "+DailySchedule.TABLE_NAME+"."+DailySchedule.HARI_COLUMN+"="+String.valueOf(hari)+" AND ( "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.WAKTU_COLUMN+" LIKE '"+keyword+"%' OR "+
                DailySchedule.TABLE_NAME+"."+DailySchedule.TEMPAT_COLUMN+" LIKE '"+keyword+"%' OR "+
                RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.ROLES_COLUMN+" LIKE '"+keyword+"%' OR "+
                RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.GOALS_COLUMN+" LIKE '"+keyword+"%'"+
                " ) "
                ;
        Cursor cursor=database.rawQuery(sqlSelectTable,null);
        if (cursor.moveToFirst()){
            do {
                DailySchedule dailySchedule=new DailySchedule();
                dailySchedule.setId(cursor.getInt(cursor.getColumnIndex(DailySchedule.TABLE_NAME+"."+DailySchedule.ID_COLUMN)));
                dailySchedule.setHari((short) cursor.getInt(cursor.getColumnIndex(DailySchedule.TABLE_NAME+"."+DailySchedule.HARI_COLUMN)));
                dailySchedule.setJenisAlarmFromInt(cursor.getInt(cursor.getColumnIndex(DailySchedule.TABLE_NAME+"."+DailySchedule.JENIS_ALARM_COLUMN)));
                dailySchedule.setWaktuFromString(cursor.getString(cursor.getColumnIndex(DailySchedule.TABLE_NAME+"."+DailySchedule.WAKTU_COLUMN)));
                dailySchedule.setTempat(cursor.getString(cursor.getColumnIndex(DailySchedule.TABLE_NAME+"."+DailySchedule.TEMPAT_COLUMN)));
                dailySchedule.setAktivitas(new RolesAndGoals(
                        cursor.getInt(cursor.getColumnIndex(RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.ID_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.ROLES_COLUMN)),
                        cursor.getString(cursor.getColumnIndex(RolesAndGoals.TABLE_NAME+"."+RolesAndGoals.GOALS_COLUMN))
                ));
                dailySchedules.add(dailySchedule);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return dailySchedules;
    }
    public long updateDailySchedule(DailySchedule dailySchedule){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put(DailySchedule.HARI_COLUMN,dailySchedule.getHari());
        contentValues.put(DailySchedule.JENIS_ALARM_COLUMN,dailySchedule.getJenisAlarmAsInt());
        contentValues.put(DailySchedule.WAKTU_COLUMN,dailySchedule.getWaktuAsString());
        contentValues.put(DailySchedule.TEMPAT_COLUMN,dailySchedule.getTempat());
        contentValues.put(DailySchedule.AKTIVITAS_COLUMN,dailySchedule.getAktivitas().getId());
        long editedCount= database.update(DailySchedule.TABLE_NAME,contentValues,DailySchedule.ID_COLUMN+"=? ",
                new String[]{String.valueOf(dailySchedule.getId())});
        database.close();
        return editedCount;
    }
    public long deleteDailySchedule(DailySchedule dailySchedule){
        SQLiteDatabase database=getWritableDatabase();
        long deletedCount=database.delete(DailySchedule.TABLE_NAME,DailySchedule.ID_COLUMN+"=? ",
                new String[]{String.valueOf(dailySchedule.getId())});
        database.close();
        return deletedCount;
    }
}
