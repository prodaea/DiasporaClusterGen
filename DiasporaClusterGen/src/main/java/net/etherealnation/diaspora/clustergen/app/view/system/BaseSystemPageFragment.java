package net.etherealnation.diaspora.clustergen.app.view.system;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystem;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystemAspect;
import net.etherealnation.diaspora.clustergen.app.data.sql.ClusterGenContentProvider;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractFactory;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemAspectContractHelper;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.SystemContractHelper;

import java.util.ArrayList;

public abstract class BaseSystemPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final String ARG_SYSTEM_ID = "system_id";
    protected static final String ARG_SYSTEM_URI = "system_uri";
    protected static final String ARG_SYSTEM_NAME = "system_name";

    private static final int SYSTEM_LOADER = 0;

    private static final SystemContractHelper SYSTEM_HELPER = (SystemContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_TABLE);
    private static final ContractHelper SYSTEM_ASPECT_HELPER = (SystemAspectContractHelper) ContractFactory.getHelper(ClusterGenContract.SYSTEM_ASPECT_TABLE);

    private DiasporaSystem system;
    private ArrayList<DiasporaSystemAspect> aspects;

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(SYSTEM_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        Uri systemUri = getArguments().getParcelable(ARG_SYSTEM_URI);
        Uri systemAspectUri = null;
        if (systemUri != null) {
            systemAspectUri = ClusterGenContract.getSystemAspectJoinUri(ContentUris.parseId(systemUri));
        }

        return new CursorLoader(getActivity(), systemAspectUri,
                                ClusterGenContentProvider.getJoinedProjection(SYSTEM_HELPER, SYSTEM_ASPECT_HELPER),
                                null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (data.getCount() > 0) {
            data.moveToFirst();
            system = new DiasporaSystem(data);
            aspects = new ArrayList<DiasporaSystemAspect>();
            for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {
                String aspectText = data.getString(data.getColumnIndex(SYSTEM_ASPECT_HELPER.getJoinedColumn(ClusterGenContract.SystemAspectColumns.TEXT)));
                if (!TextUtils.isEmpty(aspectText)) {
                    aspects.add(new DiasporaSystemAspect(data));
                }
            }
            updateViews();
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        system = null;
    }

    protected abstract void updateViews();

    protected DiasporaSystem getSystem() {
        return system;
    }

    protected ArrayList<DiasporaSystemAspect> getAspects() {
        return aspects;
    }
}
