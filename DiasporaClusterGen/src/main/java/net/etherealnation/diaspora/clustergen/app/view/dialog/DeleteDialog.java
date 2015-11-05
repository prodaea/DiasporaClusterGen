package net.etherealnation.diaspora.clustergen.app.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import net.etherealnation.diaspora.clustergen.app.R;

public class DeleteDialog extends DialogFragment {

    public static final String TAG = DeleteDialog.class.getSimpleName();
    private static final String ARG_SYSTEM_ID = "system_id";
    private static final String ARG_SYSTEM_NAME = "system_name";
    private DeleteDialogListener listener;

    public static DeleteDialog newInstance(final Fragment target, final long systemId, final String systemName) {
        DeleteDialog dialog = new DeleteDialog();
        Bundle args = new Bundle(2);

        args.putLong(ARG_SYSTEM_ID, systemId);
        args.putString(ARG_SYSTEM_NAME, systemName);

        dialog.setTargetFragment(target, 0);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listener = (DeleteDialogListener) getTargetFragment();
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        String systemName = getArguments().getString(ARG_SYSTEM_NAME);
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_delete_system_title, systemName))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.dialog_confirm_delete_system, systemName))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        listener.onDeleteConfirm(getArguments().getLong(ARG_SYSTEM_ID));
                        dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
