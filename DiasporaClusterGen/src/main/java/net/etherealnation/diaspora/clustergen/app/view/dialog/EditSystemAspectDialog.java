package net.etherealnation.diaspora.clustergen.app.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystemAspect;

public class EditSystemAspectDialog extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = EditSystemAspectDialog.class.getSimpleName();
    private static final String ARG_ID = "id";
    private static final String ARG_TEXT = "text";
    private static final String ARG_TITLE = "title";
    private EditSystemAspectDialogListener listener;
    private EditText aspectField;

    public static EditSystemAspectDialog newInstance(final Fragment target, final DiasporaSystemAspect aspect,
                                                     final String title) {
        EditSystemAspectDialog dialog = new EditSystemAspectDialog();
        Bundle args = new Bundle(3);

        args.putLong(ARG_ID, aspect.getId());
        args.putString(ARG_TEXT, aspect.getText());
        args.putString(ARG_TITLE, title);

        dialog.setTargetFragment(target, 0);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (EditSystemAspectDialogListener) getTargetFragment();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        View customView = View.inflate(getActivity(), R.layout.dialog_create_update_system_aspect, null);
        aspectField = (EditText) customView.findViewById(R.id.field_aspect);
        aspectField.setText(getArguments().getString(ARG_TEXT));
        aspectField.requestFocus();
        aspectField.setOnEditorActionListener(this);

        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getString(ARG_TITLE))
                .setView(customView)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (positiveButton != null) {
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (aspectField.getText() != null) {
                                final String aspect = aspectField.getText().toString();

                                if (!TextUtils.isEmpty(aspect)) {
                                    if (!TextUtils.equals(aspect, getArguments().getString(ARG_TEXT))) {
                                        listener.onEditSystemAspect(getArguments().getLong(ARG_ID), aspect);
                                    }
                                    dismiss();
                                } else if (TextUtils.isEmpty(aspect)) {
                                    aspectField.setError(getString(R.string.dialog_error_add_edit_system_aspect),
                                                         getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                                }
                            }
                        }
                    });
                }
            }
        });

        return alertDialog;
    }

    @Override
    public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && aspectField.getText() != null) {
            final String aspect = aspectField.getText().toString();
            if (!TextUtils.isEmpty(aspect)) {
                if (!TextUtils.equals(aspect, getArguments().getString(ARG_TEXT))) {
                    listener.onEditSystemAspect(getArguments().getLong(ARG_ID), aspect);
                }
                dismiss();
            } else {
                v.setError(getString(R.string.dialog_error_add_edit_system_aspect),
                           getResources().getDrawable(android.R.drawable.ic_dialog_alert));
            }
            return true;
        }
        return false;
    }

    public interface EditSystemAspectDialogListener {
        void onEditSystemAspect(long id, String text);
    }
}
