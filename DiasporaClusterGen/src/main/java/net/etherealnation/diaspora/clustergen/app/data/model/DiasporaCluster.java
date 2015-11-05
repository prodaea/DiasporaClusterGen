package net.etherealnation.diaspora.clustergen.app.data.model;

import android.database.Cursor;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;

public class DiasporaCluster {
    private long id;
    private String name;

    public DiasporaCluster(final Cursor cursor) {
        populateModel(cursor);
    }

    private void populateModel(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(ClusterGenContract.ClusterColumns.ID));
        name = cursor.getString(cursor.getColumnIndex(ClusterGenContract.ClusterColumns.NAME));
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
