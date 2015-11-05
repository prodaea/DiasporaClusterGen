package net.etherealnation.diaspora.clustergen.app.view.system;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import net.etherealnation.diaspora.clustergen.app.MainActivity;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.view.dialog.CreateDialog;
import net.etherealnation.diaspora.clustergen.app.view.dialog.CreateDialogListener;
import net.etherealnation.diaspora.clustergen.app.view.dialog.DeleteDialog;
import net.etherealnation.diaspora.clustergen.app.view.dialog.DeleteDialogListener;

public class SystemEditFragment extends BaseSystemFragment implements CreateDialogListener,
                                                                      DeleteDialogListener {
    public static final String TAG = SystemEditFragment.class.getSimpleName();

    private static final String ARG_CREATE_SYSTEM = "create_system";

    public static SystemEditFragment newInstance(final Uri clusterUri, final String clusterName, final Uri currentSystemUri) {
        SystemEditFragment fragment = new SystemEditFragment();
        Bundle args = new Bundle(2);
        args.putParcelable(ARG_CLUSTER_URI, clusterUri);
        args.putString(ARG_CLUSTER_NAME, clusterName);
        args.putParcelable(ARG_CURRENT_SYSTEM_URI, currentSystemUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().getParcelable(ARG_CURRENT_SYSTEM_URI) == null) {
            createSystem();
        }
    }

    @Override
    protected boolean getIsEditable() {
        return true;
    }

    @Override
    protected View.OnClickListener getEmptyViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                createSystem();
            }
        };
    }

    private void createSystem() {
        CreateDialog dialog = CreateDialog.newInstance(this, R.string.dialog_title_create_system);
        dialog.show(getFragmentManager(), CreateDialog.TAG);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.system_edit, menu);
    }

    @Override
    public void onCreateConfirm(final String name) {
        setCurrentSystemUri(
                ((MainActivity) getActivity())
                        .insertSystem(name, ContentUris.parseId(getClusterUri())));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_system:
                createSystem();
                return true;
            case R.id.menu_delete_system:
                deleteSystem();
                return true;
            case R.id.menu_save_cluster:
                ((MainActivity) getActivity()).viewCluster(
                        getClusterUri(),
                        getClusterName(),
                        getCurrentSystemUri());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteSystem() {
        final SystemPagerAdapter adapter = getSystemPagerAdapter();
        final int currentPage = getCurrentPage();
        long systemId = adapter.getPageSystemId(currentPage);
        if (systemId >= 0) {
            String systemName = (String) adapter.getPageTitle(currentPage);
            DeleteDialog dialog = DeleteDialog.newInstance(this, systemId, systemName);
            dialog.show(getFragmentManager(), DeleteDialog.TAG);
        }
    }

    @Override
    public void onDeleteConfirm(final long id) {
        ((MainActivity) getActivity()).deleteSystem(id);
        ((MainActivity) getActivity()).viewCluster(
                getClusterUri(),
                getClusterName(),
                null);
    }
}