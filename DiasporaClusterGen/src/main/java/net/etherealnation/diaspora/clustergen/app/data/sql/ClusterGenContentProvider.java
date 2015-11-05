package net.etherealnation.diaspora.clustergen.app.data.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemAspectContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemContractHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterGenContentProvider extends ContentProvider {

    private static final int CLUSTERS = 10;
    private static final int CLUSTER_ID = 20;
    private static final int CLUSTER_SYSTEM_JOIN = 30;
    private static final int SYSTEMS = 40;
    private static final int SYSTEM_ID = 50;
    private static final int SYSTEM_ASPECT_JOIN = 55;
    private static final int SYSTEM_ASPECTS = 60;
    private static final int SYSTEM_ASPECT_ID = 70;

    private static final ClusterContractHelper CLUSTER_HELPER = (ClusterContractHelper) ContractFactory.getHelper(ClusterGenContract.CLUSTER_TABLE);
    private static final SystemContractHelper SYSTEM_HELPER = (SystemContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_TABLE);
    private static final SystemAspectContractHelper SYSTEM_ASPECT_HELPER = (SystemAspectContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_ASPECT_TABLE);

    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final Map<String, String> clusterSystemProjectionMap = getOuterJoinProjectionMap(CLUSTER_HELPER,
                                                                                                    SYSTEM_HELPER);
    private static final String clusterSystemJoinedTables = getJoinedTables(CLUSTER_HELPER, SYSTEM_HELPER,
                                                                            ClusterGenContract.ClusterColumns.ID,
                                                                            ClusterGenContract.SystemColumns.PARENT_CLUSTER_ID);

    private static final Map<String, String> systemAspectProjectionMap = getOuterJoinProjectionMap(SYSTEM_HELPER,
                                                                                                   SYSTEM_ASPECT_HELPER);
    private static final String systemAspectJoinedTables = getJoinedTables(SYSTEM_HELPER, SYSTEM_ASPECT_HELPER,
                                                                           ClusterGenContract.SystemColumns.ID,
                                                                           ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID);

    private ClusterGenDatabaseHelper dbHelper;

    static {
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.CLUSTER_TABLE, CLUSTERS);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.CLUSTER_TABLE + "/#", CLUSTER_ID);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.CLUSTER_TABLE + "/#/" + ClusterGenContract.SYSTEM_TABLE, CLUSTER_SYSTEM_JOIN);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.SYSTEM_TABLE, SYSTEMS);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.SYSTEM_TABLE + "/#", SYSTEM_ID);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.SYSTEM_ASPECT_TABLE, SYSTEM_ASPECTS);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.SYSTEM_ASPECT_TABLE + "/#", SYSTEM_ASPECT_ID);
        uriMatcher.addURI(ClusterGenContract.AUTHORITY, ClusterGenContract.SYSTEM_TABLE + "/#/" + ClusterGenContract.SYSTEM_ASPECT_TABLE, SYSTEM_ASPECT_JOIN);
    }

    private static Map<String, String> getOuterJoinProjectionMap(final ContractHelper innerHelper, final ContractHelper outerHelper) {
        HashMap<String, String> map = new HashMap<String, String>();
        String[] clusterProjection = innerHelper.getAvailableColumns();
        for (String column : clusterProjection) {
            String qualifiedColumn = innerHelper.getTable() + "." + column;
            map.put(qualifiedColumn, qualifiedColumn + " as " + column);
        }

        String[] systemProjection = outerHelper.getAvailableColumns();
        for (String column : systemProjection) {
            String qualifiedColumn = outerHelper.getTable() + "." + column;
            String alias = qualifiedColumn.replace(".", "_");
            map.put(qualifiedColumn, qualifiedColumn + " as " + alias);
        }

        return map;
    }

    private static String getJoinedTables(final ContractHelper innerHelper, final ContractHelper outerHelper,
                                          final String innerColumnId, final String outerColumnId) {
        return innerHelper.getTable()
                + " left outer join "
                + outerHelper.getTable()
                + " on ("
                + outerHelper.getQualifiedColumn(outerColumnId)
                + " = "
                + innerHelper.getQualifiedColumn(innerColumnId)
                + ")";
    }

    public static String[] getJoinedProjection(final ContractHelper innerHelper, final ContractHelper outerHelper) {
        String innerProjection[] = innerHelper.getQualifiedColumns();
        String outerProjection[] = outerHelper.getQualifiedColumns();
        int innerProjectionLength = innerProjection.length;
        int outerProjectionLength = outerProjection.length;

        String projection[] = new String[innerProjectionLength + outerProjectionLength];
        System.arraycopy(innerProjection, 0, projection, 0, innerProjectionLength);
        System.arraycopy(outerProjection, 0, projection, innerProjectionLength, outerProjectionLength);
        return projection;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ClusterGenDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        final String id;
        switch (uriMatcher.match(uri)) {
            case CLUSTER_ID:
                builder.appendWhere(ClusterGenContract.ClusterColumns.ID + "=" + uri.getLastPathSegment());
            case CLUSTERS:
                builder.setTables(ClusterGenContract.CLUSTER_TABLE);
                break;
            case CLUSTER_SYSTEM_JOIN:
                id = getJoinedIdFromUri(uri);
                builder.setTables(clusterSystemJoinedTables);
                builder.setProjectionMap(clusterSystemProjectionMap);
                builder.appendWhere(ClusterGenContract.CLUSTER_TABLE + "." + ClusterGenContract.ClusterColumns.ID + "="
                                    + id);
                break;
            case SYSTEM_ID:
                builder.appendWhere(ClusterGenContract.SystemColumns.ID + "=" + uri.getLastPathSegment());
            case SYSTEMS:
                builder.setTables(ClusterGenContract.SYSTEM_TABLE);
                break;
            case SYSTEM_ASPECT_ID:
                builder.appendWhere(ClusterGenContract.ClusterColumns.ID + "=" + uri.getLastPathSegment());
            case SYSTEM_ASPECTS:
                builder.setTables(ClusterGenContract.SYSTEM_ASPECT_TABLE);
                break;
            case SYSTEM_ASPECT_JOIN:
                id = getJoinedIdFromUri(uri);
                builder.setTables(systemAspectJoinedTables);
                builder.setProjectionMap(systemAspectProjectionMap);
                builder.appendWhere(ClusterGenContract.SYSTEM_TABLE + "." + ClusterGenContract.SystemColumns.ID + "="
                                    + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            final Context context = getContext();
            if (cursor != null && context != null) {
                cursor.setNotificationUri(context.getContentResolver(), ClusterGenContract.AUTHORITY_URI);
            }
        }

        return cursor;
    }

    private String getJoinedIdFromUri(final Uri uri) {
        final List<String> pathSegments;
        pathSegments = uri.getPathSegments();
        String id;
        if (uri.getPathSegments() != null) {
            id = pathSegments.get(1);
        } else {
            throw new IllegalArgumentException("Null path segments in uri: " + uri);
        }
        return id;
    }

    @Override
    public String getType(final Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CLUSTERS:
                return ClusterGenContract.URI_TYPE_CLUSTER_DIR;
            case CLUSTER_ID:
                return ClusterGenContract.URI_TYPE_CLUSTER_ITEM;
            case CLUSTER_SYSTEM_JOIN:
            case SYSTEMS:
                return ClusterGenContract.URI_TYPE_SYSTEM_DIR;
            case SYSTEM_ID:
                return ClusterGenContract.URI_TYPE_SYSTEM_ITEM;
            case SYSTEM_ASPECT_JOIN:
            case SYSTEM_ASPECTS:
                return ClusterGenContract.URI_TYPE_SYSTEM_ASPECT_DIR;
            case SYSTEM_ASPECT_ID:
                return ClusterGenContract.URI_TYPE_SYSTEM_ASPECT_ITEM;
        }
        return null;
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = 0;

        if (db != null && getContext() != null) {
            switch (uriMatcher.match(uri)) {
                case CLUSTERS:
                    id = db.insert(ClusterGenContract.CLUSTER_TABLE, null, values);
                    break;
                case SYSTEMS:
                    id = db.insert(ClusterGenContract.SYSTEM_TABLE, null, values);
                    break;
                case SYSTEM_ASPECTS:
                    id = db.insert(ClusterGenContract.SYSTEM_ASPECT_TABLE, null, values);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ClusterGenContract.getClusterByIdUri(id);
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted = 0;

        String id;
        if (db != null && getContext() != null) {
            switch (uriMatcher.match(uri)) {
                case CLUSTERS:
                    rowsDeleted = db.delete(ClusterGenContract.CLUSTER_TABLE, selection, selectionArgs);
                    break;
                case CLUSTER_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(ClusterGenContract.CLUSTER_TABLE,
                                                ClusterGenContract.ClusterColumns.ID + "=" + id,
                                                selectionArgs);
                    } else {
                        rowsDeleted = db.delete(ClusterGenContract.CLUSTER_TABLE,
                                                ClusterGenContract.ClusterColumns.ID + "=" + id + " and " + selection,
                                                selectionArgs);
                    }
                    break;
                case SYSTEMS:
                    rowsDeleted = db.delete(ClusterGenContract.SYSTEM_TABLE, selection, selectionArgs);
                    break;
                case SYSTEM_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(ClusterGenContract.SYSTEM_TABLE,
                                                ClusterGenContract.SystemColumns.ID + "=" + id,
                                                selectionArgs);
                    } else {
                        rowsDeleted = db.delete(ClusterGenContract.SYSTEM_TABLE,
                                                ClusterGenContract.SystemColumns.ID + "=" + id + " and " + selection,
                                                selectionArgs);
                    }
                    break;
                case SYSTEM_ASPECTS:
                    rowsDeleted = db.delete(ClusterGenContract.SYSTEM_ASPECT_TABLE, selection, selectionArgs);
                    break;
                case SYSTEM_ASPECT_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = db.delete(ClusterGenContract.SYSTEM_ASPECT_TABLE,
                                                ClusterGenContract.SystemAspectColumns.ID + "=" + id,
                                                selectionArgs);
                    } else {
                        rowsDeleted = db.delete(ClusterGenContract.SYSTEM_ASPECT_TABLE,
                                                ClusterGenContract.SystemAspectColumns.ID + "=" + id + " and " + selection,
                                                selectionArgs);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown uri: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        String id;
        if (db != null && getContext() != null) {
            switch (uriMatcher.match(uri)) {
                case CLUSTERS:
                    rowsUpdated = db.update(ClusterGenContract.CLUSTER_TABLE, values, selection, selectionArgs);
                    break;
                case CLUSTER_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(ClusterGenContract.CLUSTER_TABLE, values,
                                                ClusterGenContract.ClusterColumns.ID + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(ClusterGenContract.CLUSTER_TABLE, values,
                                                ClusterGenContract.ClusterColumns.ID + "=" + id + " and " + selection,
                                                selectionArgs);
                    }
                    break;
                case SYSTEMS:
                    rowsUpdated = db.update(ClusterGenContract.SYSTEM_TABLE, values, selection, selectionArgs);
                    break;
                case SYSTEM_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(ClusterGenContract.SYSTEM_TABLE, values,
                                                ClusterGenContract.SystemColumns.ID + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(ClusterGenContract.SYSTEM_TABLE, values,
                                                ClusterGenContract.SystemColumns.ID + "=" + id + " and " + selection,
                                                selectionArgs);
                    }
                    break;
                case SYSTEM_ASPECTS:
                    rowsUpdated = db.update(ClusterGenContract.SYSTEM_ASPECT_TABLE, values, selection, selectionArgs);
                    break;
                case SYSTEM_ASPECT_ID:
                    id = uri.getLastPathSegment();
                    if (TextUtils.isEmpty(selection)) {
                        rowsUpdated = db.update(ClusterGenContract.SYSTEM_ASPECT_TABLE, values,
                                                ClusterGenContract.SystemAspectColumns.ID + "=" + id, null);
                    } else {
                        rowsUpdated = db.update(ClusterGenContract.SYSTEM_ASPECT_TABLE, values,
                                                ClusterGenContract.SystemAspectColumns.ID + "=" + id + " and " + selection,
                                                selectionArgs);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown uri: " + uri);
            }

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
