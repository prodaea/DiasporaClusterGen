package net.etherealnation.diaspora.clustergen.app.view.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaSystemAspect;

public class DeleteSystemAspectDialog extends DialogFragment {

    public static final String TAG = DeleteSystemAspectDialog.class.getSimpleName();
    private static final String ARG_ID = "id";
    private static final String ARG_TEXT = "text";
    private DeleteDialogListener listener;

    public static DeleteSystemAspectDialog newInstance(final Fragment target, final DiasporaSystemAspect aspect) {
        DeleteSystemAspectDialog dialog = new DeleteSystemAspectDialog();
        Bundle args = new Bundle(2);

        args.putLong(ARG_ID, aspect.getId());
        args.putString(ARG_TEXT, aspect.getText());

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
        String text = getArguments().getString(ARG_TEXT);
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_delete_system_aspect_title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(getString(R.string.dialog_confirm_delete_system_aspect, text))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        listener.onDeleteConfirm(getArguments().getLong(ARG_ID));
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
