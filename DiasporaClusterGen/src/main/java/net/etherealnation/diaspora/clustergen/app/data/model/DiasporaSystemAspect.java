package net.etherealnation.diaspora.clustergen.app.data.model;

import android.database.Cursor;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemAspectContractHelper;

public class DiasporaSystemAspect {

    private static final SystemAspectContractHelper SYSTEM_HELPER = (SystemAspectContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_ASPECT_TABLE);

    private long id;
    private long systemId;
    private String text;

    public DiasporaSystemAspect(final Cursor cursor) {
        // TODO: i don't particularly like this pattern. the model should be dumber, how would they know that they are being created from a joined query?
        if (cursor.getColumnIndex(ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID) < 0) {
            populateJoinedModel(cursor);
        } else {
            populateModel(cursor);
        }
    }

    private void populateJoinedModel(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(SYSTEM_HELPER.getJoinedColumn(ClusterGenContract.SystemAspectColumns.ID)));
        systemId = cursor.getLong(cursor.getColumnIndex(SYSTEM_HELPER.getJoinedColumn(ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID)));
        text = cursor.getString(cursor.getColumnIndex(SYSTEM_HELPER.getJoinedColumn(ClusterGenContract.SystemAspectColumns.TEXT)));
    }

    private void populateModel(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(ClusterGenContract.SystemAspectColumns.ID));
        systemId = cursor.getLong(cursor.getColumnIndex(ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID));
        text = cursor.getString(cursor.getColumnIndex(ClusterGenContract.SystemAspectColumns.TEXT));
    }

    public long getId() {
        return id;
    }

    public long getSystemId() {
        return systemId;
    }

    public String getText() {
        return text;
    }

}
