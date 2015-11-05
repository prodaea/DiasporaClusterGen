package net.etherealnation.diaspora.clustergen.app.data.sql.contract;

public class ClusterContractHelper extends ContractHelper {

    private static final String DATABASE_CREATE = "create table "
                                                  + ClusterGenContract.CLUSTER_TABLE
                                                  + "("
                                                  + ClusterGenContract.ClusterColumns.ID + " integer primary key autoincrement, "
                                                  + ClusterGenContract.ClusterColumns.NAME + " text not null"
                                                  + ");";

    private static final String DATABASE_UPGRADE = "drop table if exists " + ClusterGenContract.CLUSTER_TABLE;


    @Override
    public String getTable() {
        return ClusterGenContract.CLUSTER_TABLE;
    }

    @Override
    public String[] getAvailableColumns() {
        return new String[] {
                ClusterGenContract.ClusterColumns.ID,
                ClusterGenContract.ClusterColumns.NAME
        };
    }

    @Override
    public String getCreateStatement() {
        return DATABASE_CREATE;
    }

    @Override
    protected String getUpgradeStatement(final int oldVersion, final int newVersion) {
        return DATABASE_UPGRADE;
    }
}
