package ru.lionzxy.bmstuwifi.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import ru.lionzxy.bmstuwifi.LoginActivity_;
import ru.lionzxy.bmstuwifi.R;
import ru.lionzxy.bmstuwifi.authentificator.AuthManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by lionzxy on 07.02.17.
 */

public class SSIDChoiseFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] SSIDs = AuthManager.getSSIDs().toArray(new String[AuthManager.getSSIDs().size()]);
        builder.setTitle(R.string.login_wifi_choise).setItems(SSIDs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), LoginActivity_.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);

                Bundle mBundle = new Bundle();
                mBundle.putString("wifi_ssid", SSIDs[which]);
                intent.putExtras(mBundle);

                if (getArguments() != null && getArguments().getString("action", null) != null)
                    intent.setAction(getArguments().getString("action", null));

                getActivity().startActivity(intent);
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
