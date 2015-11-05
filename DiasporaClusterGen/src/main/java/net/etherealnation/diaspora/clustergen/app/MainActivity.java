package net.etherealnation.diaspora.clustergen.app;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.view.cluster.ClusterListFragment;
import net.etherealnation.diaspora.clustergen.app.view.system.SystemEditFragment;
import net.etherealnation.diaspora.clustergen.app.view.system.SystemViewFragment;


public class MainActivity extends ActionBarActivity {

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.container, ClusterListFragment.newInstance())
                       .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void setTitle(final CharSequence title) {
        super.setTitle(title);

        mTitle = title;
        restoreActionBar();
    }

    public void editCluster(
            final Uri clusterUri,
            final String clusterName,
            final Uri currentSystemUri
    ) {
        final Fragment fragment = SystemEditFragment.newInstance(
                clusterUri,
                clusterName,
                currentSystemUri);
        swap(fragment, SystemEditFragment.TAG);
    }

    public void viewCluster(final Uri clusterUri, final String clusterName, Uri currentSystemUri) {
        final Fragment fragment = SystemViewFragment.newInstance(
                clusterUri,
                clusterName,
                currentSystemUri);
        swap(fragment, SystemViewFragment.TAG);
    }

    public void createCluster(final String name) {
        Uri clusterUri = insertCluster(name);
        final Fragment fragment = SystemEditFragment.newInstance(clusterUri, name, null);
        dive(fragment, SystemEditFragment.TAG);
    }

    private void swap(final Fragment fragment, final String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        dive(fragment, tag);
    }

    private void dive(final Fragment fragment, final String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                       .replace(R.id.container, fragment)
                       .addToBackStack(tag)
                       .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Uri insertCluster(final String name) {
        String clusterName = name.trim();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClusterGenContract.ClusterColumns.NAME, clusterName);

        return getContentResolver().insert(ClusterGenContract.CLUSTER_URI, contentValues);
    }

    public Uri insertSystem(final String name, final long clusterId) {
        String systemName = name.trim();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClusterGenContract.SystemColumns.NAME, systemName);
        contentValues.put(ClusterGenContract.SystemColumns.PARENT_CLUSTER_ID, clusterId);

        return getContentResolver().insert(ClusterGenContract.SYSTEM_URI, contentValues);
    }

    public void deleteSystem(final long systemId) {
        getContentResolver().delete(ClusterGenContract.getSystemUri(systemId), null, null);
    }

    public void updateSystem(final Uri systemUri, final int techStat, final int environmentStat, final int resourceStat) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClusterGenContract.SystemColumns.TECHNOLOGY, techStat);
        contentValues.put(ClusterGenContract.SystemColumns.ENVIRONMENT, environmentStat);
        contentValues.put(ClusterGenContract.SystemColumns.RESOURCES, resourceStat);

        getContentResolver().update(systemUri, contentValues, null, null);
    }

    public Uri insertSystemAspect(final String aspect, final long parentId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClusterGenContract.SystemAspectColumns.TEXT, aspect);
        contentValues.put(ClusterGenContract.SystemAspectColumns.PARENT_SYSTEM_ID, parentId);

        return getContentResolver().insert(ClusterGenContract.SYSTEM_ASPECT_URI, contentValues);
    }

    public void deleteSystemAspect(final long id) {
        getContentResolver().delete(ClusterGenContract.getSystemAspectUri(id), null, null);
    }

    public void updateSystemAspect(final long id, final String text) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ClusterGenContract.SystemAspectColumns.TEXT, text);

        getContentResolver().update(ClusterGenContract.getSystemAspectUri(id), contentValues, null, null);
    }

    public void deleteCluster(final long id) {
        getContentResolver().delete(ClusterGenContract.getClusterByIdUri(id), null, null);
    }
}
