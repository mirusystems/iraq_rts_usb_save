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


@Database(entities = {UsbListEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String TAG = "AppDatabase";
    public abstract UsbSaveDao usbSaveDao();
    private static AppDatabase instance = null;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.v(TAG, "migrate: ");

            database.execSQL("CREATE TABLE `PS_INFO_NEW` (`GOV_ID` INTEGER, `GOV_NAME` TEXT, `ED_ID` INTEGER, `ED_NAME` TEXT, `VRC_ID` INTEGER, `VRC_NAME` TEXT, `PC_ID` INTEGER, `PC_NAME` TEXT, `PS_ID` INTEGER, `PSO` INTEGER, `VVD` INTEGER, `PCOS` INTEGER, `RTS` INTEGER, PRIMARY KEY(`PS_ID`))");

            database.execSQL("INSERT INTO PS_INFO_NEW (GOV_ID, GOV_NAME, ED_ID, ED_NAME, VRC_ID, VRC_NAME, PC_ID, PC_NAME, PS_ID, PSO, VVD, PCOS, RTS) SELECT GOV_ID, GOV_NAME, ED_ID, ED_NAME, VRC_ID, VRC_NAME, PC_ID, PC_NAME, PS_ID, PSO, VVD, PCOS, RTS FROM PS_INFO");

            // 기존 PS_INFO 테이블 삭제
            database.execSQL("DROP TABLE PS_INFO");

            // 새 테이블 이름을 PS_INFO로 변경
            database.execSQL("ALTER TABLE PS_INFO_NEW RENAME TO PS_INFO");

//            database.execSQL("ALTER TABLE `PS_INFO` RENAME TO `PS_INFO_OLD`;");
//            database.execSQL("CREATE TABLE `PS_INFO` (`ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,`idGov` TEXT,`nameGov` TEXT,`idED` TEXT,`nameED` TEXT,`nameSubED` TEXT,`idFDC` TEXT,`nameFDC` TEXT,`idVRC` TEXT,`nameVRC` TEXT,`iddPC` TEXT,`namePC` TEXT,`addrPC` TEXT,`_voters` TEXT,`nPS` TEXT);");
//            database.execSQL("INSERT INTO `PS_INFO` (`ID`)");
//            database.execSQL("ALTER TABLE `PS_INFO` ADD COLUMN mark INTEGER");
        }
    };

    private static AppDatabase buildDatabase(Context context) {
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

        return Room.databaseBuilder(context, AppDatabase.class, Manager.DB_PATH)
//                .fallbackToDestructiveMigration()
//                .addMigrations(MIGRATION_1_2)
                .addCallback(callback)
                .allowMainThreadQueries()
                .build();
    }
}
