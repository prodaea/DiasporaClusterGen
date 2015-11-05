package net.etherealnation.diaspora.clustergen.app.view.system;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemContractHelper;

/**
 * If need to abstract, see http://tumble.mlcastle.net/post/25875136857/bridging-cursorloaders-and-viewpagers-on-android
 */
public class SystemPagerAdapter extends FragmentStatePagerAdapter {
    private static final SystemContractHelper SYSTEM_HELPER = (SystemContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_TABLE);

    private Cursor cursor;
    private boolean editable;

    public SystemPagerAdapter(final FragmentManager fm, final boolean editable) {
        super(fm);

        this.editable = editable;
    }

    @Override
    public Fragment getItem(final int position) {
        if (cursor == null) {
            return null;
        }

        cursor.moveToPosition(position);
        if (editable) {
            return SystemPageEditFragment.newInstance(getPageSystemId(position), (String) getPageTitle(position));
        }
        return SystemPageViewFragment.newInstance(getPageSystemId(position), (String) getPageTitle(position));
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndex(SYSTEM_HELPER.getJoinedColumn(ClusterGenContract.SystemColumns.NAME)));
    }

    public void swapCursor(final Cursor cursor) {
        if (this.cursor == cursor) {
            return;
        }

        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public long getPageSystemId(final int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndex(SYSTEM_HELPER.getJoinedColumn(ClusterGenContract.SystemColumns.ID)));
    }
}