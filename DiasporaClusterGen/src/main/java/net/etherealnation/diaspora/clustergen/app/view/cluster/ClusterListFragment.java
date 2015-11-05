package net.etherealnation.diaspora.clustergen.app.view.cluster;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import net.etherealnation.diaspora.clustergen.app.MainActivity;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.adapter.ClustersCursorAdapter;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaCluster;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.view.dialog.CreateDialog;
import net.etherealnation.diaspora.clustergen.app.view.dialog.CreateDialogListener;
import net.etherealnation.diaspora.clustergen.app.view.dialog.DeleteDialog;
import net.etherealnation.diaspora.clustergen.app.view.dialog.DeleteDialogListener;

public class ClusterListFragment extends ListFragment implements CreateDialogListener,
                                                                 DeleteDialogListener,
                                                                 LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CLUSTER_LIST_LOADER = 0;
    private static final ClusterContractHelper CLUSTER_HELPER = (ClusterContractHelper) ContractFactory.getHelper(ClusterGenContract.CLUSTER_TABLE);
    private View progressView;
    private View noClustersView;

    public ClusterListFragment() {}

    public static ClusterListFragment newInstance() {
        return new ClusterListFragment();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CLUSTER_LIST_LOADER, null, this);
        registerForContextMenu(getListView());
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ClustersCursorAdapter adapter = new ClustersCursorAdapter(getActivity(), null, false);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.title_cluster_list));

        View rootView = inflater.inflate(R.layout.fragment_cluster_list, container, false);

        if (rootView != null) {
            progressView = rootView.findViewById(R.id.progress);
            noClustersView = rootView.findViewById(R.id.add_cluster);
            noClustersView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    createCluster();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        ((MainActivity) getActivity()).viewCluster(
                ClusterGenContract.getClusterByIdUri(id),
                getClusterName(position),
                null);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(getClusterName(info.position));
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.cluster_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemId = item.getItemId();
        if (info != null) {
            String clusterName = getClusterName(info.position);
            Uri clusterUri = ClusterGenContract.getClusterByIdUri(info.id);
            switch (menuItemId) {
                case R.id.context_edit_cluster:
                    ((MainActivity) getActivity()).editCluster(clusterUri, clusterName, null);
                    break;
                case R.id.context_delete_cluster:
                    DeleteDialog dialog = DeleteDialog.newInstance(this, info.id, clusterName);
                    dialog.show(getFragmentManager(), DeleteDialog.TAG);
                    break;
            }
        }
        return true;
    }

    private String getClusterName(final int position) {
        ClustersCursorAdapter adapter = (ClustersCursorAdapter) getListAdapter();
        DiasporaCluster cluster = adapter.getItem(position);
        String clusterName = null;
        if (cluster != null) {
            clusterName = cluster.getName();
        }
        return clusterName;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.cluster, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_cluster:
                createCluster();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createCluster() {
        CreateDialog dialog = CreateDialog.newInstance(ClusterListFragment.this, R.string.dialog_title_create_cluster);
        dialog.show(getFragmentManager(), CreateDialog.TAG);
    }

    @Override
    public void onCreateConfirm(final String name) {
        ((MainActivity) getActivity()).createCluster(name);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new CursorLoader(getActivity(), ClusterGenContract.CLUSTER_URI, CLUSTER_HELPER.getAvailableColumns(),
                                null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        final ClustersCursorAdapter adapter = (ClustersCursorAdapter) getListAdapter();
        adapter.changeCursor(data);
        progressView.setVisibility(View.GONE);
        showEmptyState(adapter.getCount() == 0);
    }

    private void showEmptyState(final boolean show) {
        if (show) {
            noClustersView.setVisibility(View.VISIBLE);
            getListView().setVisibility(View.GONE);
        } else {
            noClustersView.setVisibility(View.GONE);
            getListView().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        ((ClustersCursorAdapter) getListAdapter()).changeCursor(null);
    }

    @Override
    public void onDeleteConfirm(final long id) {
        ((MainActivity) getActivity()).deleteCluster(id);
    }
}
