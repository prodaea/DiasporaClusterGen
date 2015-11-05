package net.etherealnation.diaspora.clustergen.app.data.sql.contract;

public class SystemContractHelper extends ContractHelper {

    private static final String DATABASE_CREATE = "create table "
                                                  + ClusterGenContract.SYSTEM_TABLE
                                                  + "("
                                                  + ClusterGenContract.SystemColumns.ID + " integer primary key autoincrement, "
                                                  + ClusterGenContract.SystemColumns.PARENT_CLUSTER_ID + " integer, "
                                                  + ClusterGenContract.SystemColumns.NAME + " text not null, "
                                                  + ClusterGenContract.SystemColumns.TECHNOLOGY + " integer default" + ClusterGenContract.SYSTEM_DEFAULT_STAT + ", "
                                                  + ClusterGenContract.SystemColumns.ENVIRONMENT + " integer default" + ClusterGenContract.SYSTEM_DEFAULT_STAT + ", "
                                                  + ClusterGenContract.SystemColumns.RESOURCES + " integer default" + ClusterGenContract.SYSTEM_DEFAULT_STAT + ", "
                                                  + "foreign key (" + ClusterGenContract.SystemColumns.PARENT_CLUSTER_ID + ") references "
                                                  + ClusterGenContract.CLUSTER_TABLE + "(" + ClusterGenContract.ClusterColumns.ID + "));";

    private static final String DATABASE_UPGRADE = "drop table if exists  " + ClusterGenContract.SYSTEM_TABLE;


    @Override
    public String getTable() {
        return ClusterGenContract.SYSTEM_TABLE;
    }

    @Override
    public String[] getAvailableColumns() {
        return new String[]{
                ClusterGenContract.SystemColumns.ID,
                ClusterGenContract.SystemColumns.PARENT_CLUSTER_ID,
                ClusterGenContract.SystemColumns.NAME,
                ClusterGenContract.SystemColumns.TECHNOLOGY,
                ClusterGenContract.SystemColumns.ENVIRONMENT,
                ClusterGenContract.SystemColumns.RESOURCES
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
