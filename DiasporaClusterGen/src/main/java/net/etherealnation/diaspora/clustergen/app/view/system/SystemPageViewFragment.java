package net.etherealnation.diaspora.clustergen.app.view.system;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystem;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystemAspect;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;

public class SystemPageViewFragment extends BaseSystemPageFragment {

    private TextView techStatField;
    private TextView environmentStatField;
    private TextView resourcesStatField;
    private ViewGroup aspectsContainer;

    public static SystemPageViewFragment newInstance(long systemId, String name) {
        SystemPageViewFragment fragment = new SystemPageViewFragment();
        Bundle args = new Bundle(3);
        Uri systemUri = ClusterGenContract.getSystemUri(systemId);
        args.putLong(ARG_SYSTEM_ID, systemId);
        args.putParcelable(ARG_SYSTEM_URI, systemUri);
        args.putString(ARG_SYSTEM_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_system_page, container, false);

        if (rootView != null) {
            techStatField = (TextView) rootView.findViewById(R.id.field_technology);
            environmentStatField = (TextView) rootView.findViewById(R.id.field_environment);
            resourcesStatField = (TextView) rootView.findViewById(R.id.field_resources);

            aspectsContainer = (ViewGroup) rootView.findViewById(R.id.aspects_container);
        }

        return rootView;
    }

    @Override
    protected void updateViews() {
        DiasporaSystem system = getSystem();
        if (system != null) {
            techStatField.setText(String.valueOf(system.getTechnology()));
            environmentStatField.setText(String.valueOf(system.getEnvironment()));
            resourcesStatField.setText(String.valueOf(system.getResources()));
        }

        aspectsContainer.removeAllViews();
        for (DiasporaSystemAspect aspect : getAspects()) {
            View aspectView = View.inflate(getActivity(), R.layout.view_system_aspect_item, null);

            TextView aspectText = (TextView) aspectView.findViewById(R.id.text_system_aspect);
            aspectText.setText(aspect.getText());

            aspectsContainer.addView(aspectView);
        }
    }
}
