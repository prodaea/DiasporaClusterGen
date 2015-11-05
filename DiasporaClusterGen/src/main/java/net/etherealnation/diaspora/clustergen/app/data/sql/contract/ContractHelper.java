package net.etherealnation.diaspora.clustergen.app.data.sql.contract;

import android.database.sqlite.SQLiteDatabase;

public abstract class ContractHelper {

    public abstract String getTable();
    public abstract String[] getAvailableColumns();
    public abstract String getCreateStatement();
    protected abstract String getUpgradeStatement(final int oldVersion, final int newVersion);

    final public String[] getQualifiedColumns() {
        String[] qualifiedColumns = new String[getAvailableColumns().length];
        for (int i = 0; i < getAvailableColumns().length; i++) {
            qualifiedColumns[i] = getQualifiedColumn(getAvailableColumns()[i]);
        }

        return qualifiedColumns;
    }

    final public String getQualifiedColumn(final String columnId) {
        return getTable() + "." + columnId;
    }

    final public String getJoinedColumn(String columnId) {
        return getTable() + "_" + columnId;
    }

    final public void onCreate(SQLiteDatabase database) {
        database.execSQL(getCreateStatement());
    }

    final public void onUpgrade(SQLiteDatabase database, final int oldVersion, final int newVersion) {
        //TODO: this probably is a bad way to do this. (deletes all data)
        database.execSQL(getUpgradeStatement(oldVersion, newVersion));
        onCreate(database);
    }

}
