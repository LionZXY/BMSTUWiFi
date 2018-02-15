package ru.lionzxy.bmstuwifi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ru.lionzxy.bmstuwifi.utils.Notify;
import ru.lionzxy.bmstuwifi.utils.logs.Logger;

/**
 * Created by lionzxy on 07.11.16.
 */
public class AppPreferenceActivity extends AppCompatActivity {
    private static final String TAG = "PreferenceActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new AuthPreferenceFragment()).commit();
        Logger.getLogger().log(TAG, Logger.Level.DEBUG, "Инициализация активити с настройками");
    }

    public static class AuthPreferenceFragment extends PreferenceFragment {
        private ProgressDialog progressDialog = null;
        private Notify notification = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_main);
            PreferenceCategory auth_cat = ((PreferenceCategory) getPreferenceScreen().getPreference(1));

            Preference forgotLoginAndPassword = new Preference(getActivity());
            forgotLoginAndPassword.setTitle(R.string.pref_auth_login_forgetLogin);
            forgotLoginAndPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    App.get().getSharedPreferences().edit().remove("auth_user").remove("auth_pass").apply();
                    Toast.makeText(getActivity(), getString(R.string.pref_auth_login_forgetLogin_toast), Toast.LENGTH_LONG).show();
                    return true;
                }
            });
            auth_cat.addPreference(forgotLoginAndPassword);

            Preference logout = new Preference(getActivity());
            logout.setTitle(R.string.auth_logout);
            auth_cat.addPreference(logout);
        }

        @Override
        public void onPause() {
            super.onPause();
            if (notification != null)
                notification.hide();
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

}
