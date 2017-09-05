package com.anxa.hapilabs.common.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
        protected static final String DATABASE_NAME = "anxahapicoach.db";
        
        protected static final int VERSION = 100;

        protected int oldVersion;
        protected int newVersion;

        private static DatabaseHelper mInstance = null;


        public static DatabaseHelper getInstance(Context ctx) {

                // Use the application context, which will ensure that you
                // don't accidentally leak an Activity's context.
                // See this article for more information: http://bit.ly/6LRzfx
                if (mInstance == null) {
                        mInstance = new DatabaseHelper(ctx.getApplicationContext());
                }
                return mInstance;
        }


        protected DatabaseHelper(Context appCtx) {
                super(appCtx, DATABASE_NAME, null, VERSION);
                this.getDatabase();
        }

        @Override
        public void finalize() {
                this.close();
                try {
                        super.finalize();
                } catch (Throwable e) {
                        e.printStackTrace();
                }
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                this.oldVersion = oldVersion;
                this.newVersion = newVersion;

                onCreate(db);
        }
        /**
         * Get the database instance
         * @return
         */
        public SQLiteDatabase getDatabase() {
                return this.getWritableDatabase();
        }
        /**
         * Check if this requires an upgrade
         * @return
         */
        public boolean requireUpgrade() {
                return (this.oldVersion != this.newVersion);
        }
}