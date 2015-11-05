package net.etherealnation.diaspora.clustergen.app.view.system;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.sql.ClusterGenContentProvider;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemContractHelper;

public abstract class BaseSystemFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String ARG_CLUSTER_URI = "cluster_uri";
    protected static final String ARG_CLUSTER_NAME = "system_name";
    protected static final String ARG_CURRENT_SYSTEM_URI = "current_system_uri";

    private static final int CLUSTER_SYSTEMS_LOADER = 0;

    private static final SystemContractHelper SYSTEM_HELPER = (SystemContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_TABLE);
    private static final ClusterContractHelper CLUSTER_HELPER = (ClusterContractHelper) ContractFactory.getHelper(ClusterGenContract.CLUSTER_TABLE);

    private Uri clusterUri;
    private Uri currentSystemUri;
    private SystemPagerAdapter systemPagerAdapter;
    private View progressView;
    private View noSystemsView;
    private ViewPager viewPager;
    private int currentPage;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CLUSTER_SYSTEMS_LOADER, null, this);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        activity.setTitle(getArguments().getString(ARG_CLUSTER_NAME));
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //TODO: also set and check from savedInstanceState
        Bundle args = getArguments();
        clusterUri = args.getParcelable(ARG_CLUSTER_URI);
        currentSystemUri = args.getParcelable(ARG_CURRENT_SYSTEM_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_system_base, container, false);
        systemPagerAdapter = new SystemPagerAdapter(getChildFragmentManager(), getIsEditable());
        if (rootView != null) {
            progressView = rootView.findViewById(R.id.progress);

            noSystemsView = rootView.findViewById(R.id.add_system);
            noSystemsView.setOnClickListener(getEmptyViewClickListener());

            viewPager = (ViewPager) rootView.findViewById(R.id.pager);
            viewPager.setAdapter(systemPagerAdapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(final int position, final float positionOffset,
                                           final int positionOffsetPixels) {
                    //stub
                }

                @Override
                public void onPageSelected(final int position) {
                    currentPage = position;
                    currentSystemUri = ClusterGenContract.getSystemUri(
                            systemPagerAdapter.getPageSystemId(currentPage));
                }

                @Override
                public void onPageScrollStateChanged(final int state) {
                    //stub
                }
            });
        }

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        if (clusterUri == null) {
            clusterUri = getArguments().getParcelable(ARG_CLUSTER_URI);
        }
        Uri clusterSystemUri = null;
        if (clusterUri != null) {
            clusterSystemUri = ClusterGenContract.getClusterSystemJoinUri(ContentUris.parseId(clusterUri));
        }

        return new CursorLoader(getActivity(), clusterSystemUri,
                                ClusterGenContentProvider.getJoinedProjection(CLUSTER_HELPER, SYSTEM_HELPER), null,
                                null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (data.getCount() > 0) {
            data.moveToFirst();

            String systemName = data.getString(data.getColumnIndex(SYSTEM_HELPER.getJoinedColumn(ClusterGenContract.SystemColumns.NAME)));
            if (!TextUtils.isEmpty(systemName)) {
                systemPagerAdapter.swapCursor(data);
                movePagerToCurrentSystem();
            } else {
                systemPagerAdapter.swapCursor(null);
                showEmptyState(true);
            }

            progressView.setVisibility(View.GONE);
        } else {
            systemPagerAdapter.swapCursor(null);
        }
        showEmptyState(systemPagerAdapter.getCount() == 0);
    }

    private void movePagerToCurrentSystem() {
        for (int i = 0; i < systemPagerAdapter.getCount(); i++) {
            if (currentSystemUri != null &&
                    systemPagerAdapter.getPageSystemId(i) ==
                            Long.parseLong(currentSystemUri.getLastPathSegment())) {
                viewPager.setCurrentItem(i);
            }
        }
    }

    private void showEmptyState(final boolean show) {
        if (show) {
            viewPager.setVisibility(View.GONE);
            noSystemsView.setVisibility(View.VISIBLE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            noSystemsView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        systemPagerAdapter.swapCursor(null);
    }

    protected abstract View.OnClickListener getEmptyViewClickListener();

    protected abstract boolean getIsEditable();

    public Uri getClusterUri() {
        return clusterUri;
    }

    public Uri getCurrentSystemUri() {
        return currentSystemUri;
    }

    public void setCurrentSystemUri(Uri currentSystemUri) {
        this.currentSystemUri = currentSystemUri;
        movePagerToCurrentSystem();
    }

    public SystemPagerAdapter getSystemPagerAdapter() {
        return systemPagerAdapter;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public String getClusterName() {
        return getArguments().getString(ARG_CLUSTER_NAME);
    }
}
