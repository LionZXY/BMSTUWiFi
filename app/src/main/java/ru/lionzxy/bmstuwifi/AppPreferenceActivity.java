package ru.lionzxy.bmstuwifi;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import ru.lionzxy.bmstuwifi.fragments.AboutMeFragment;

/**
 * Created by lionzxy on 07.11.16.
 */
public class AppPreferenceActivity extends FragmentActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AuthPreferenceFragment()).commit();


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, new Bundle());
    }

    public static class AuthPreferenceFragment extends PreferenceFragment {
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

            Preference about = new Preference(getActivity());
            about.setTitle(R.string.pref_about_about);
            about_cat.addPreference(about);
        }
    }
}
