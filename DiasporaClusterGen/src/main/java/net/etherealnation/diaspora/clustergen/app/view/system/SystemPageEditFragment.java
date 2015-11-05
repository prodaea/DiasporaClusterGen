package net.etherealnation.diaspora.clustergen.app.view.system;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import net.etherealnation.diaspora.clustergen.app.MainActivity;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystemAspect;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;
import net.etherealnation.diaspora.clustergen.app.util.Fate;
import net.etherealnation.diaspora.clustergen.app.view.dialog.AddSystemAspectDialog;
import net.etherealnation.diaspora.clustergen.app.view.dialog.DeleteDialogListener;
import net.etherealnation.diaspora.clustergen.app.view.dialog.DeleteSystemAspectDialog;
import net.etherealnation.diaspora.clustergen.app.view.dialog.EditSystemAspectDialog;

public class SystemPageEditFragment extends BaseSystemPageFragment implements AddSystemAspectDialog.AddSystemAspectDialogListener,
                                                        EditSystemAspectDialog.EditSystemAspectDialogListener,
                                                        DeleteDialogListener {

    private View rootView;
    private EditText techStatField;
    private EditText environmentStatField;
    private EditText resourcesStatField;
    private ViewGroup aspectsContainer;

    public static SystemPageEditFragment newInstance(long systemId, String name) {
        SystemPageEditFragment fragment = new SystemPageEditFragment();
        Bundle args = new Bundle(3);
        Uri systemUri = ClusterGenContract.getSystemUri(systemId);
        args.putLong(ARG_SYSTEM_ID, systemId);
        args.putParcelable(ARG_SYSTEM_URI, systemUri);
        args.putString(ARG_SYSTEM_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_system_page, container, false);
        if (rootView != null) {
            final Button rollTechnologyButton = (Button) rootView.findViewById(R.id.button_roll_technology);
            techStatField = (EditText) rootView.findViewById(R.id.field_technology);
            bindRollerAndField(rollTechnologyButton, techStatField);

            final Button rollEnvironmentButton = (Button) rootView.findViewById(R.id.button_roll_environment);
            environmentStatField = (EditText) rootView.findViewById(R.id.field_environment);
            bindRollerAndField(rollEnvironmentButton, environmentStatField);

            final Button rollResourcesButton = (Button) rootView.findViewById(R.id.button_roll_resources);
            resourcesStatField = (EditText) rootView.findViewById(R.id.field_resources);
            bindRollerAndField(rollResourcesButton, resourcesStatField);

            final ImageButton addAspectButton = (ImageButton) rootView.findViewById(R.id.button_add_aspect);
            addAspectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    createAspect();
                }
            });

            aspectsContainer = (ViewGroup) rootView.findViewById(R.id.aspects_container);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        saveSystemStats();
    }

    private void createAspect() {
        AddSystemAspectDialog dialog = AddSystemAspectDialog.newInstance(this, getString(R.string.dialog_title_add_system_aspect, getSystem().getName()));
        dialog.show(getFragmentManager(), AddSystemAspectDialog.TAG);
    }

    @Override
    protected void updateViews() {
        if (rootView != null && getSystem() != null) {
            final int techStat = getSystem().getTechnology();
            if (techStat != ClusterGenContract.SYSTEM_DEFAULT_STAT) {
                techStatField.setText(String.valueOf(techStat));
            }
            final int environmentStat = getSystem().getEnvironment();
            if (environmentStat != ClusterGenContract.SYSTEM_DEFAULT_STAT) {
                environmentStatField.setText(String.valueOf(environmentStat));
            }
            final int resourceStat = getSystem().getResources();
            if (resourceStat != ClusterGenContract.SYSTEM_DEFAULT_STAT) {
                resourcesStatField.setText(String.valueOf(resourceStat));
            }
        }

        aspectsContainer.removeAllViews();
        for (DiasporaSystemAspect aspect : getAspects()) {
            View aspectView = View.inflate(getActivity(), R.layout.view_system_aspect_item, null);

            TextView aspectText = (TextView) aspectView.findViewById(R.id.text_system_aspect);
            aspectText.setText(aspect.getText());

            ImageButton deleteAspectButton = (ImageButton) aspectView.findViewById(R.id.button_delete_system_aspect);
            deleteAspectButton.setVisibility(View.VISIBLE);
            deleteAspectButton.setOnClickListener(new DeleteSystemAspectOnClickListener(aspect));

            ImageButton editAspectButton = (ImageButton) aspectView.findViewById(R.id.button_edit_system_aspect);
            editAspectButton.setVisibility(View.VISIBLE);
            editAspectButton.setOnClickListener(new EditSystemAspectOnClickListener(aspect));

            aspectsContainer.addView(aspectView);
        }
    }

    private void bindRollerAndField(final Button button, final EditText field) {
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                // stub
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                // stub
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() > 0) {
                    boolean error = false;
                    int i = ClusterGenContract.SYSTEM_DEFAULT_STAT;
                    if (s.length() > 1 || !s.toString().equals("-")) {
                        try {
                            i = Integer.parseInt(s.toString());
                        } catch (NumberFormatException exception) {
                            error = true;
                        }

                        if ((i > 4 || i < -4) && !error) {
                            error = true;
                        }
                    } else if (!s.toString().equals("-")) {
                        error = true;
                    }

                    if (error) {
                        field.setText("");
                        field.setError(getString(R.string.text_invalid_fate),
                                       getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int roll = Fate.roll(0);
                field.setText(Integer.toString(roll));
            }
        });
    }

    private void saveSystemStats() {
        Uri systemUri = getArguments().getParcelable(ARG_SYSTEM_URI);

        final Editable techStatText = techStatField.getText();
        final Editable environmentStatText = environmentStatField.getText();
        final Editable resourceStatText = resourcesStatField.getText();

        int techStat = ClusterGenContract.SYSTEM_DEFAULT_STAT;
        int environmentStat = ClusterGenContract.SYSTEM_DEFAULT_STAT;
        int resourceStat = ClusterGenContract.SYSTEM_DEFAULT_STAT;

        if (techStatText != null && !TextUtils.isEmpty(techStatText.toString())) {
            techStat = Integer.parseInt(techStatText.toString());
        }

        if (environmentStatText != null && !TextUtils.isEmpty(environmentStatText.toString())) {
            environmentStat = Integer.parseInt(environmentStatText.toString());
        }

        if (resourceStatText != null && !TextUtils.isEmpty(resourceStatText.toString())) {
            resourceStat = Integer.parseInt(resourceStatText.toString());
        }

        ((MainActivity) getActivity()).updateSystem(systemUri, techStat, environmentStat, resourceStat);
    }

    @Override
    public void onCreateAspectConfirm(final String aspect) {
        ((MainActivity) getActivity()).insertSystemAspect(aspect, getSystem().getId());
    }

    @Override
    public void onDeleteConfirm(final long id) {
        ((MainActivity) getActivity()).deleteSystemAspect(id);
    }

    @Override
    public void onEditSystemAspect(final long id, final String text) {
        ((MainActivity) getActivity()).updateSystemAspect(id, text);
    }

    private class DeleteSystemAspectOnClickListener implements View.OnClickListener {
        private final DiasporaSystemAspect aspect;

        public DeleteSystemAspectOnClickListener(final DiasporaSystemAspect aspect) {
            this.aspect = aspect;
        }

        @Override
        public void onClick(final View v) {
            DeleteSystemAspectDialog dialog = DeleteSystemAspectDialog.newInstance(SystemPageEditFragment.this, aspect);
            dialog.show(getFragmentManager(), DeleteSystemAspectDialog.TAG);
        }
    }

    private class EditSystemAspectOnClickListener implements View.OnClickListener {
        private final DiasporaSystemAspect aspect;

        public EditSystemAspectOnClickListener(final DiasporaSystemAspect aspect) {
            this.aspect = aspect;
        }

        @Override
        public void onClick(final View v) {
            EditSystemAspectDialog dialog = EditSystemAspectDialog.newInstance(SystemPageEditFragment.this, aspect, getString(R.string.dialog_title_edit_system_aspect, getSystem().getName()));
            dialog.show(getFragmentManager(), EditSystemAspectDialog.TAG);
        }
    }
}
