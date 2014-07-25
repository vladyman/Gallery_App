package com.galleryapp.data.provider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.galleryapp.data.provider.GalleryDBContent.Channels;
import com.galleryapp.data.provider.GalleryDBContent.GalleryImages;

import java.util.ArrayList;

/**
 * This class was generated by the ContentProviderCodeGenerator project made by Foxykeep
 * <p/>
 * (More information available https://github.com/foxykeep/ContentProviderCodeGenerator)
 */
public final class GalleryDBProvider extends HttpBaseProvider {

    private static final String LOG_TAG = GalleryDBProvider.class.getSimpleName();

    /* package */ static final boolean ACTIVATE_ALL_LOGS = false;

    protected static final String DATABASE_NAME = "GalleryDBProvider.db";

    public static final String AUTHORITY = "com.galleryapp.provider.GalleryDBProvider";

    static {
        Uri.parse("content://" + AUTHORITY + "/integrityCheck");
    }

    // Version 1 : Creation of the database
    public static final int DATABASE_VERSION = 3;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private enum UriType {
        GALLERY_IMAGES(GalleryImages.TABLE_NAME, GalleryImages.TABLE_NAME, GalleryImages.TYPE_ELEM_TYPE),
        GALLERY_IMAGES_ID(GalleryImages.TABLE_NAME + "/#", GalleryImages.TABLE_NAME, GalleryImages.TYPE_DIR_TYPE),
        CHANNELS(Channels.TABLE_NAME, Channels.TABLE_NAME, Channels.TYPE_ELEM_TYPE),
        CHANNELS_ID(Channels.TABLE_NAME + "/#", Channels.TABLE_NAME, Channels.TYPE_DIR_TYPE);

        private String mTableName;
        private String mType;

        UriType(String matchPath, String tableName, String type) {
            mTableName = tableName;
            mType = type;
            sUriMatcher.addURI(AUTHORITY, matchPath, ordinal());
        }

        String getTableName() {
            return mTableName;
        }

        String getType() {
            return mType;
        }
    }

    static {
        // Ensures UriType is initialized
        UriType.values();
    }

    private static UriType matchUri(Uri uri) {
        int match = sUriMatcher.match(uri);
        if (match < 0) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return UriType.class.getEnumConstants()[match];
    }

    private SQLiteDatabase mDatabase;

    @SuppressWarnings("deprecation")
    public synchronized SQLiteDatabase getDatabase(Context context) {
        // Always return the cached database, if we've got one
        if (mDatabase == null || !mDatabase.isOpen()) {
            DatabaseHelper helper = new DatabaseHelper(context, DATABASE_NAME);
            mDatabase = helper.getWritableDatabase();
            if (mDatabase != null) {
                mDatabase.setLockingEnabled(true);
            }
        }

        return mDatabase;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context, String name) {
            super(context, name, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "Creating GalleryDBProvider database");

            // Create all tables here; each class has its own method
            GalleryImages.createTable(db);
            Channels.createTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            // Upgrade all tables here; each class has its own method
            GalleryImages.upgradeTable(db, oldVersion, newVersion);
            Channels.upgradeTable(db, oldVersion, newVersion);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);
        String id;

        if (ACTIVATE_ALL_LOGS) {
            Log.d(LOG_TAG, "delete: uri=" + uri + ", match is " + uriType.name());
        }

        int result = -1;

        switch (uriType) {
            case GALLERY_IMAGES_ID:
            case CHANNELS_ID:
                id = uri.getPathSegments().get(1);
                result = db.delete(uriType.getTableName(), whereWithId(selection),
                        addIdToSelectionArgs(id, selectionArgs));
                break;
            case GALLERY_IMAGES:
            case CHANNELS:
                result = db.delete(uriType.getTableName(), selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        return matchUri(uri).getType();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);
        long id;

        if (ACTIVATE_ALL_LOGS) {
            Log.d(LOG_TAG, "insert: uri=" + uri + ", match is " + uriType.name());
        }

        Uri resultUri;

        switch (uriType) {
            case GALLERY_IMAGES:
            case CHANNELS:
                id = db.insert(uriType.getTableName(), "foo", values);
                resultUri = id == -1 ? null : ContentUris.withAppendedId(uri, id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify with the base uri, not the new uri (nobody is watching a new
        // record)
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
//        Uri uri = operations.get(0).getUri();
        SQLiteDatabase db = getDatabase(getContext());
        db.beginTransaction();
        try {
            int numOperations = operations.size();
            ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
                db.yieldIfContendedSafely();
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
//            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);

        if (ACTIVATE_ALL_LOGS) {
            Log.d(LOG_TAG, "bulkInsert: uri=" + uri + ", match is " + uriType.name());
        }

        int numberInserted = 0;
        SQLiteStatement insertStmt;

        db.beginTransaction();
        try {
            switch (uriType) {
                case GALLERY_IMAGES:
                    insertStmt = db.compileStatement(GalleryImages.getBulkInsertString());
                    for (ContentValues value : values) {
                        GalleryImages.bindValuesInBulkInsert(insertStmt, value);
                        insertStmt.execute();
                        insertStmt.clearBindings();
                    }
                    insertStmt.close();
                    db.setTransactionSuccessful();
                    numberInserted = values.length;

                    if (ACTIVATE_ALL_LOGS) {
                        Log.d(LOG_TAG, "bulkInsert: uri=" + uri + " | nb inserts : " + numberInserted);
                    }
                    break;
                case CHANNELS:
                    insertStmt = db.compileStatement(Channels.getBulkInsertString());
                    for (ContentValues value : values) {
                        Channels.bindValuesInBulkInsert(insertStmt, value);
                        insertStmt.execute();
                        insertStmt.clearBindings();
                    }
                    insertStmt.close();
                    db.setTransactionSuccessful();
                    numberInserted = values.length;

                    if (ACTIVATE_ALL_LOGS) {
                        Log.d(LOG_TAG, "bulkInsert: uri=" + uri + " | nb inserts : " + numberInserted);
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } finally {
            db.endTransaction();
        }

        // Notify with the base uri, not the new uri (nobody is watching a new
        // record)
        context.getContentResolver().notifyChange(uri, null);
        return numberInserted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor c = null;
        UriType uriType = matchUri(uri);
        Context context = getContext();
        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);
        String id;

        if (ACTIVATE_ALL_LOGS) {
            Log.d(LOG_TAG, "query: uri=" + uri + ", match is " + uriType.name());
        }

        switch (uriType) {
            case GALLERY_IMAGES_ID:
            case CHANNELS_ID:
                id = uri.getPathSegments().get(1);
                c = db.query(uriType.getTableName(), projection, whereWithId(selection),
                        addIdToSelectionArgs(id, selectionArgs), null, null, sortOrder);
                break;
            case GALLERY_IMAGES:
            case CHANNELS:
                c = db.query(uriType.getTableName(), projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
        }

        if ((c != null) && !isTemporary()) {
//            TODO: Remove unused code
            c.setNotificationUri(getContext().getContentResolver(), uri);
//            if (uriType.getTableName().equals(GalleryImages.TABLE_NAME)){
//                checkForSync(c, uri);
//            }else {
//                c.setNotificationUri(getContext().getContentResolver(), uri);
//            }
        }
        return c;
    }

    private String whereWithId(String selection) {
        StringBuilder sb = new StringBuilder(256);
        sb.append(BaseColumns._ID);
        sb.append(" = ?");
        if (selection != null) {
            sb.append(" AND (");
            sb.append(selection);
            sb.append(')');
        }
        return sb.toString();
    }

    private String[] addIdToSelectionArgs(String id, String[] selectionArgs) {

        if (selectionArgs == null) {
            return new String[]{id};
        }

        int length = selectionArgs.length;
        String[] newSelectionArgs = new String[length + 1];
        newSelectionArgs[0] = id;
        System.arraycopy(selectionArgs, 0, newSelectionArgs, 1, length);
        return newSelectionArgs;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        UriType uriType = matchUri(uri);
        Context context = getContext();

        // Pick the correct database for this operation
        SQLiteDatabase db = getDatabase(context);

        if (ACTIVATE_ALL_LOGS) {
            Log.d(LOG_TAG, "update: uri=" + uri + ", match is " + uriType.name());
        }

        int result = -1;

        switch (uriType) {
            case GALLERY_IMAGES_ID:
            case CHANNELS_ID:
                String id = uri.getPathSegments().get(1);
                result = db.update(uriType.getTableName(), values, whereWithId(selection),
                        addIdToSelectionArgs(id, selectionArgs));
                break;
            case GALLERY_IMAGES:
            case CHANNELS:
                result = db.update(uriType.getTableName(), values, selection, selectionArgs);
                break;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public boolean onCreate() {
        return true;
    }
}
