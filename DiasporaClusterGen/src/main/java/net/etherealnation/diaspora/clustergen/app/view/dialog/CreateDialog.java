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
import android.widget.EditText;
import android.widget.TextView;
import net.etherealnation.diaspora.clustergen.app.R;

public class CreateDialog extends DialogFragment implements TextView.OnEditorActionListener {

    public static final String TAG = CreateDialog.class.getSimpleName();
    private static final String ARG_TITLE = "title";
    private CreateDialogListener listener;
    private EditText nameField;

    public static CreateDialog newInstance(final Fragment target, final int titleResourceId) {
        CreateDialog dialog = new CreateDialog();
        Bundle args = new Bundle(1);

        args.putInt(ARG_TITLE, titleResourceId);

        dialog.setTargetFragment(target, 0);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (CreateDialogListener) getTargetFragment();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final View customView = View.inflate(getActivity(), R.layout.dialog_create_named_item, null);
        nameField = (EditText) customView.findViewById(R.id.field_name);
        nameField.requestFocus();
        nameField.setOnEditorActionListener(this);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getArguments().getInt(ARG_TITLE))
                .setView(customView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        if (nameField.getText() != null) {
                            final String newName = nameField.getText().toString();
                            //TODO: generate random name if blank
                            //TODO: automagic number of systems (4 to 10)
                            if (!TextUtils.isEmpty(newName)) {
                                listener.onCreateConfirm(newName);
                                dismiss();
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }

    @Override
    public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE && nameField.getText() != null) {
            final String newName = nameField.getText().toString();
            //TODO: generate random name if blank
            //TODO: automagic number of systems (4 to 10)
            if (!TextUtils.isEmpty(newName)) {
                listener.onCreateConfirm(newName);
                dismiss();
                return true;
            }
        }
        return false;
    }
}
