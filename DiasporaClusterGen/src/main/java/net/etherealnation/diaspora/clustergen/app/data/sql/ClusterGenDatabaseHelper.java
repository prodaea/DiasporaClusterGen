package net.etherealnation.diaspora.clustergen.app.data.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractHelper;

public class ClusterGenDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "clustergen.db";
    private static final int DATABASE_VERSION = 5;

    public ClusterGenDatabaseHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        //Create all your tables
        for (ContractHelper helper : ContractFactory.getAllHelpers()) {
            helper.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        //Update all your tables
        for (ContractHelper helper : ContractFactory.getAllHelpers()) {
            helper.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
