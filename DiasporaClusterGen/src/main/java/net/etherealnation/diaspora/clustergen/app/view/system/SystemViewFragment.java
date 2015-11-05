package net.etherealnation.diaspora.clustergen.app.view.system;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import net.etherealnation.diaspora.clustergen.app.MainActivity;
import net.etherealnation.diaspora.clustergen.app.R;

public class SystemViewFragment extends BaseSystemFragment {
    public static final String TAG = SystemViewFragment.class.getSimpleName();

    public static Fragment newInstance(
            final Uri clusterUri,
            final String clusterName,
            Uri currentSystemUri
    ) {
        SystemViewFragment fragment = new SystemViewFragment();
        Bundle args = new Bundle(2);
        args.putParcelable(ARG_CLUSTER_URI, clusterUri);
        args.putString(ARG_CLUSTER_NAME, clusterName);
        args.putParcelable(ARG_CURRENT_SYSTEM_URI, currentSystemUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.system_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_system:
                launchEditor(getCurrentSystemUri());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected View.OnClickListener getEmptyViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                launchEditor(getCurrentSystemUri());
            }
        };
    }

    private void launchEditor(Uri currentSystemUri) {
        ((MainActivity) getActivity()).editCluster(
                getClusterUri(),
                getClusterName(),
                currentSystemUri);
    }

    @Override
    protected boolean getIsEditable() {
        return false;
    }
}
