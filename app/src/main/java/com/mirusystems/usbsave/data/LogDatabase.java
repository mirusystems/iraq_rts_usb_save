package com.mirusystems.usbsave.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mirusystems.usbsave.Manager;


@Database(entities = {LogData.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class LogDatabase extends RoomDatabase {
    private static final String TAG = "LogDatabase";

    public abstract LogDao logDao();

    private static LogDatabase instance = null;

    public static LogDatabase getInstance(Context context) {
        if (instance == null) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.v(TAG, "migrate: ");
//            database.execSQL("ALTER TABLE `PS_INFO` RENAME TO `PS_INFO_OLD`;");
//            database.execSQL("CREATE TABLE `PS_INFO` (`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,`idGov` TEXT,`nameGov` TEXT,`idED` TEXT,`nameED` TEXT,`nameSubED` TEXT,`idFDC` TEXT,`nameFDC` TEXT,`idVRC` TEXT,`nameVRC` TEXT,`iddPC` TEXT,`namePC` TEXT,`addrPC` TEXT,`_voters` TEXT,`nPS` TEXT);");
//            database.execSQL("INSERT INTO `PS_INFO` (`ID`)");
//            database.execSQL("ALTER TABLE `PS_INFO` ADD COLUMN mark INTEGER");
        }
    };

    private static LogDatabase buildDatabase(Context context) {
        Log.v(TAG, "buildDatabase: E");
        Callback callback = new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                Log.v(TAG, "onCreate: ");
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
                Log.v(TAG, "onOpen: getVersion = " + db.getVersion());
//                db.execSQL("CREATE TABLE IF NOT EXISTS result_log (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, idGov TEXT, idED TEXT, iddPC TEXT, mark INTEGER)");
            }

            @Override
            public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                super.onDestructiveMigration(db);
                Log.v(TAG, "onDestructiveMigration: getVersion = " + db.getVersion());
            }
        };

        return Room.databaseBuilder(context, LogDatabase.class, Manager.LOG_PATH)
                .allowMainThreadQueries()
                .addCallback(callback)
                .build();
    }
}
