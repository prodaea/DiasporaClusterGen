package net.etherealnation.diaspora.clustergen.app.data.sql.contract;

import android.net.Uri;

public class ClusterGenContract {
    //URI paths / tables
    public static final String CLUSTER_TABLE = "cluster";
    public static final String SYSTEM_TABLE = "system";
    public static final String SYSTEM_ASPECT_TABLE = "system_aspect";

    //URIs used
    public static final String AUTHORITY = "net.etherealnation.diaspora.clustergen.data";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CLUSTER_URI = Uri.withAppendedPath(AUTHORITY_URI, CLUSTER_TABLE);
    public static final Uri SYSTEM_URI = Uri.withAppendedPath(AUTHORITY_URI, SYSTEM_TABLE);
    public static final Uri SYSTEM_ASPECT_URI = Uri.withAppendedPath(AUTHORITY_URI, SYSTEM_ASPECT_TABLE);

    //URI types
    public static final String URI_TYPE_CLUSTER_ITEM = "vnd.android.cursor.item/vnd.etherealnation.cluster";
    public static final String URI_TYPE_SYSTEM_ITEM = "vnd.android.cursor.item/vnd.etherealnation.system";
    public static final String URI_TYPE_SYSTEM_ASPECT_ITEM = "vnd.android.cursor.item/vnd.etherealnation.aspect";
    public static final String URI_TYPE_CLUSTER_DIR = "vnd.android.cursor.dir/vnd.etherealnation.cluster";
    public static final String URI_TYPE_SYSTEM_DIR = "vnd.android.cursor.dir/vnd.etherealnation.system";
    public static final String URI_TYPE_SYSTEM_ASPECT_DIR = "vnd.android.cursor.dir/vnd.etherealnation.aspect";

    // defaults
    public static final int SYSTEM_DEFAULT_STAT = -10;
    
    public static Uri getClusterByIdUri(final long id) {
        return Uri.parse(CLUSTER_URI + "/" + id);
    }

    public static Uri getSystemUri(final long id) {
        return Uri.parse(SYSTEM_URI + "/" + id);
    }

    public static Uri getSystemAspectUri(final long id) {
        return Uri.parse(SYSTEM_ASPECT_URI + "/" + id);
    }

    public static Uri getClusterSystemJoinUri(final long id) {
        return Uri.withAppendedPath(CLUSTER_URI, id + "/" + SYSTEM_TABLE);
    }

    public static Uri getSystemAspectJoinUri(final long id) {
        return Uri.withAppendedPath(SYSTEM_URI, id + "/" + SYSTEM_ASPECT_TABLE);
    }


    public class ClusterColumns {
        public static final String ID = "_id";
        public static final String NAME = "name";
    }

    public class SystemColumns {
        public static final String ID = "_id";
        public static final String PARENT_CLUSTER_ID = "clusterId";
        public static final String NAME = "name";
        public static final String TECHNOLOGY = "technology";
        public static final String ENVIRONMENT = "environment";
        public static final String RESOURCES = "resources";
    }

    public class SystemAspectColumns {
        public static final String ID = "_id";
        public static final String PARENT_SYSTEM_ID = "systemId";
        public static final String TEXT = "text";
    }
}
