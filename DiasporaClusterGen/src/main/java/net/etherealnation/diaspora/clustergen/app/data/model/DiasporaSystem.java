package net.etherealnation.diaspora.clustergen.app.data.model;

import android.database.Cursor;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;

public class DiasporaSystem {

    private long id;
    private long clusterId;
    private String name;
    private int technology;
    private int environment;
    private int resources;

    public DiasporaSystem(final Cursor cursor) {
        populateModel(cursor);
    }

    private void populateModel(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(ClusterGenContract.SystemColumns.ID));
        clusterId = cursor.getLong(cursor.getColumnIndex(ClusterGenContract.SystemColumns.PARENT_CLUSTER_ID));
        name = cursor.getString(cursor.getColumnIndex(ClusterGenContract.SystemColumns.NAME));
        technology = cursor.getInt(cursor.getColumnIndex(ClusterGenContract.SystemColumns.TECHNOLOGY));
        environment = cursor.getInt(cursor.getColumnIndex(ClusterGenContract.SystemColumns.ENVIRONMENT));
        resources = cursor.getInt(cursor.getColumnIndex(ClusterGenContract.SystemColumns.RESOURCES));
    }

    public long getId() {
        return id;
    }

    public long getClusterId() {
        return clusterId;
    }

    public String getName() {
        return name;
    }

    public int getTechnology() {
        return technology;
    }

    public int getEnvironment() {
        return environment;
    }

    public int getResources() {
        return resources;
    }

}
