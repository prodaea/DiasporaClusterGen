package net.etherealnation.diaspora.clustergen.app.data.sql.contract;

public class SystemAspectContractHelper extends ContractHelper {

    private static final String DATABASE_CREATE = "create table "
                                                  + ClusterGenContract.SYSTEM_ASPECT_TABLE
                                                  + "("
                                                  + ClusterGenContract.SystemAspectColumns.ID + " integer primary key autoincrement, "
                                                  + ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID + " integer, "
                                                  + ClusterGenContract.SystemAspectColumns.TEXT + " text not null, "
                                                  + "foreign key (" + ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID + ") references "
                                                  + ClusterGenContract.SYSTEM_TABLE + "(" + ClusterGenContract.SystemColumns.ID + "));";

    private static final String DATABASE_UPGRADE = "drop table if exists " + ClusterGenContract.SYSTEM_ASPECT_TABLE;

    @Override
    public String getTable() {
        return ClusterGenContract.SYSTEM_ASPECT_TABLE;
    }

    @Override
    public String[] getAvailableColumns() {
        return new String[]{
                ClusterGenContract.SystemAspectColumns.ID,
                ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID,
                ClusterGenContract.SystemAspectColumns.TEXT
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
