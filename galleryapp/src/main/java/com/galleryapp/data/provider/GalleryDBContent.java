package com.galleryapp.data.provider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.galleryapp.data.provider.util.ColumnMetadata;

/**
 * This class was generated by the ContentProviderCodeGenerator project made by Foxykeep
 * <p/>
 * (More information available https://github.com/foxykeep/ContentProviderCodeGenerator)
 */
public abstract class GalleryDBContent {

    public static final Uri CONTENT_URI = Uri.parse("content://" + GalleryDBProvider.AUTHORITY);

    private GalleryDBContent() {
    }

    /**
     * Created in version 1
     */
    public static final class GalleryImages extends GalleryDBContent {

        private static final String LOG_TAG = GalleryImages.class.getSimpleName();

        public static final String TABLE_NAME = "galleryImages";
        public static final String TYPE_ELEM_TYPE = "vnd.android.cursor.item/gallerydb-galleryimages";
        public static final String TYPE_DIR_TYPE = "vnd.android.cursor.dir/gallerydb-galleryimages";

        public static final Uri CONTENT_URI = Uri.parse(GalleryDBContent.CONTENT_URI + "/" + TABLE_NAME);

        public static enum Columns implements ColumnMetadata {
            ID(BaseColumns._ID, "integer"),
            IMAGE_NAME("imageName", "text"),
            IMAGE_PATH("imagePath", "text"),
            THUMB_PATH("thumbPath", "text"),
            IMAGE_TITLE("imageTitle", "text"),
            IMAGE_NOTES("imageNotes", "text"),
            CREATE_DATE("createDate", "text"),
            IS_SYNCED("isSynced", "integer");

            private final String mName;
            private final String mType;

            private Columns(String name, String type) {
                mName = name;
                mType = type;
            }

            @Override
            public int getIndex() {
                return ordinal();
            }

            @Override
            public String getName() {
                return mName;
            }

            @Override
            public String getType() {
                return mType;
            }
        }

        public static final String[] PROJECTION = new String[]{
                Columns.ID.getName(),
                Columns.IMAGE_NAME.getName(),
                Columns.IMAGE_PATH.getName(),
                Columns.THUMB_PATH.getName(),
                Columns.IMAGE_TITLE.getName(),
                Columns.IMAGE_NOTES.getName(),
                Columns.CREATE_DATE.getName(),
                Columns.IS_SYNCED.getName()
        };

        private GalleryImages() {
            // No private constructor
        }

        public static void createTable(SQLiteDatabase db) {
            if (GalleryDBProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "GalleryImages | createTable start");
            }
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    Columns.ID.getName() + " " + Columns.ID.getType() + ", " +
                    Columns.IMAGE_NAME.getName() + " " + Columns.IMAGE_NAME.getType() + ", " +
                    Columns.IMAGE_PATH.getName() + " " + Columns.IMAGE_PATH.getType() + ", " +
                    Columns.THUMB_PATH.getName() + " " + Columns.THUMB_PATH.getType() + ", " +
                    Columns.IMAGE_TITLE.getName() + " " + Columns.IMAGE_TITLE.getType() + ", " +
                    Columns.IMAGE_NOTES.getName() + " " + Columns.IMAGE_NOTES.getType() + ", " +
                    Columns.CREATE_DATE.getName() + " " + Columns.CREATE_DATE.getType() + ", " +
                    Columns.IS_SYNCED.getName() + " " + Columns.IS_SYNCED.getType() +
                    ", PRIMARY KEY (" + Columns.ID.getName() + ")" + ");");

            db.execSQL("CREATE INDEX galleryImages_imageName on " + TABLE_NAME + "(" + Columns.IMAGE_NAME.getName() + ");");
            db.execSQL("CREATE INDEX galleryImages_imagePath on " + TABLE_NAME + "(" + Columns.IMAGE_PATH.getName() + ");");
            db.execSQL("CREATE INDEX galleryImages_thumbPath on " + TABLE_NAME + "(" + Columns.THUMB_PATH.getName() + ");");
            db.execSQL("CREATE INDEX galleryImages_imageTitle on " + TABLE_NAME + "(" + Columns.IMAGE_TITLE.getName() + ");");
            db.execSQL("CREATE INDEX galleryImages_imageNotes on " + TABLE_NAME + "(" + Columns.IMAGE_NOTES.getName() + ");");
            db.execSQL("CREATE INDEX galleryImages_createDate on " + TABLE_NAME + "(" + Columns.CREATE_DATE.getName() + ");");
            if (GalleryDBProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "GalleryImages | createTable end");
            }
        }

        // Version 1 : Creation of the table
        public static void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (GalleryDBProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "GalleryImages | upgradeTable start");
            }

            if (oldVersion < 1) {
                Log.i(LOG_TAG, "Upgrading from version " + oldVersion + " to " + newVersion
                        + ", data will be lost!");

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
                createTable(db);
                return;
            }


            if (oldVersion != newVersion) {
                throw new IllegalStateException("Error upgrading the database to version "
                        + newVersion);
            }

            if (GalleryDBProvider.ACTIVATE_ALL_LOGS) {
                Log.d(LOG_TAG, "GalleryImages | upgradeTable end");
            }
        }

        static String getBulkInsertString() {
            return new StringBuilder("INSERT INTO ")
                    .append(TABLE_NAME).append(" ( ")
                    .append(Columns.ID.getName()).append(", ")
                    .append(Columns.IMAGE_NAME.getName()).append(", ")
                    .append(Columns.IMAGE_PATH.getName()).append(", ")
                    .append(Columns.THUMB_PATH.getName()).append(", ")
                    .append(Columns.IMAGE_TITLE.getName()).append(", ")
                    .append(Columns.IMAGE_NOTES.getName()).append(", ")
                    .append(Columns.CREATE_DATE.getName()).append(", ")
                    .append(Columns.IS_SYNCED.getName())
                    .append(" ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)").toString();
        }

        static void bindValuesInBulkInsert(SQLiteStatement stmt, ContentValues values) {
            int i = 1;
            String value;
            stmt.bindLong(i++, values.getAsLong(Columns.ID.getName()));
            value = values.getAsString(Columns.IMAGE_NAME.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.IMAGE_PATH.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.THUMB_PATH.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.IMAGE_TITLE.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.IMAGE_NOTES.getName());
            stmt.bindString(i++, value != null ? value : "");
            value = values.getAsString(Columns.CREATE_DATE.getName());
            stmt.bindString(i++, value != null ? value : "");
            stmt.bindLong(i++, values.getAsLong(Columns.IS_SYNCED.getName()));
        }
    }
}

