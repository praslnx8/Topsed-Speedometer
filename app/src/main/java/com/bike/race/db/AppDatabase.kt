package com.bike.race.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bike.race.db.dao.DriveDao
import com.bike.race.db.dbModels.DriveEntity
import com.bike.race.db.dbModels.DrivePathItemEntity
import com.bike.race.db.typeConverters.DataTypeConverter

@Database(
    entities = [DriveEntity::class, DrivePathItemEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun driveDao(): DriveDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private val migration12 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'DRIVE' ADD COLUMN 'IS_MOCK_DRIVE' INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val migration23 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS DRIVE_PATH_ITEM (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `drive_id` INTEGER NOT NULL, `lat` REAL NOT NULL, `lon` REAL NOT NULL, `speed` REAL NOT NULL, `time` INTEGER NOT NULL, `duration` INTEGER NOT NULL, `distance` INTEGER NOT NULL, `acceleration` REAL NOT NULL, `sequence` INTEGER NOT NULL, `next_lat` REAL, `next_lon` REAL)")
            }
        }

        private val migration34 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE DRIVE_PATH")
            }
        }

        private val migration45 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'DRIVE' ADD COLUMN 'PAUSE_TIME' INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val migration56 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'DRIVE' ADD COLUMN 'TAG' TEXT DEFAULT NULL")
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "topsedv1.db")
                .addMigrations(migration12)
                .addMigrations(migration23)
                .addMigrations(migration34)
                .addMigrations(migration45)
                .addMigrations(migration56)
                .build()
    }


}