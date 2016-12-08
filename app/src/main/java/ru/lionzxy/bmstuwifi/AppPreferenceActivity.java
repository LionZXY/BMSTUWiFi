package ru.lionzxy.bmstuwifi;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import ru.lionzxy.bmstuwifi.fragments.AboutMeFragment;
import ru.lionzxy.bmstuwifi.utils.Logger;
import ru.lionzxy.bmstuwifi.utils.LogoutAsyncTaskLoader;
import ru.lionzxy.bmstuwifi.utils.Notification;

/**
 * Created by lionzxy on 07.11.16.
 */
public class AppPreferenceActivity extends FragmentActivity {
    private static final String TAG = "PreferenceActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AuthPreferenceFragment()).commit();

        Logger.getLogger().log(TAG, Logger.Level.DEBUG, "Инициализация активити с настройками");
    }

    public static class AuthPreferenceFragment extends PreferenceFragment implements LoaderManager.LoaderCallbacks<Boolean> {
        private ProgressDialog progressDialog;
        private Notification notification;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.auth_preference);
            PreferenceCategory auth_cat = ((PreferenceCategory) getPreferenceScreen().getPreference(1));

            Preference changeLoginAndPassword = new Preference(getActivity());
            changeLoginAndPassword.setTitle(R.string.pref_auth_login_changeLogin);
            changeLoginAndPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LoginActivity_.class);
                    intent.setAction(LoginActivity.ACTION_RESAVE_PSWD);
                    startActivity(intent);
                    return true;
                }
            });

            auth_cat.addPreference(changeLoginAndPassword);

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

            Preference openLogin = new Preference(getActivity());
            openLogin.setTitle(R.string.pref_auth_login_open);
            openLogin.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), LoginActivity_.class);
                    startActivity(intent);
                    return true;
                }
            });
            auth_cat.addPreference(openLogin);

            Preference logout = new Preference(getActivity());
            logout.setTitle(R.string.auth_logout);
            logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Bundle bundle = new Bundle();
                    bundle.putString("logout_id", getPreferenceManager().getSharedPreferences().getString("logout_id", ""));
                    getLoaderManager().initLoader(1, bundle, AuthPreferenceFragment.this);
                    return true;
                }
            });
            auth_cat.addPreference(logout);

            PreferenceCategory about_cat = (PreferenceCategory) getPreferenceScreen().getPreference(2);
            Preference contactToAuthor = new Preference(getActivity());
            contactToAuthor.setTitle(R.string.pref_about_contact);
            contactToAuthor.setSummary(R.string.pref_about_contect_desct);
            contactToAuthor.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AboutMeFragment().show(getFragmentManager(), "dlg_aboutme");
                    return true;
                }
            });
            about_cat.addPreference(contactToAuthor);

            Preference logs = new Preference(getActivity());
            logs.setTitle(R.string.debug_name);
            logs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), DebugActivity_.class);
                    startActivity(intent);
                    return true;
                }
            });
            about_cat.addPreference(logs);

            Preference about = new Preference(getActivity());
            about.setTitle(R.string.pref_about_about);
            try {
                about.setSummary("Версия: " + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            about_cat.addPreference(about);
        }

        @Override
        public Loader<Boolean> onCreateLoader(int id, Bundle args) {
            notification = new Notification(getActivity())
                    .setId(1)
                    .setEnabled(true)
                    .setTitle(getString(R.string.auth_logout))
                    .setIcon(R.drawable.ic_stat_logo);

            notification.show();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(R.string.auth_logout);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.login_button_hide), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.dismiss();
                }
            });
            switch (id) {
                case 1: {
                    AsyncTaskLoader<Boolean> asyncTaskLoader = new LogoutAsyncTaskLoader(getActivity(), progressDialog, notification, args.getString("logout_id"));
                    asyncTaskLoader.forceLoad();
                    return asyncTaskLoader;
                }
                default:
                    return null;
            }

        }

        @Override
        public void onLoadFinished(android.content.Loader<Boolean> loader, Boolean data) {

        }

        @Override
        public void onLoaderReset(android.content.Loader<Boolean> loader) {

        }
    }

}
