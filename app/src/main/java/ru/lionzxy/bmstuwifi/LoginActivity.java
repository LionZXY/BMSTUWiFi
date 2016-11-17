package ru.lionzxy.bmstuwifi;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.securepreferences.SecurePreferences;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.lionzxy.bmstuwifi.utils.AuthAsyncTaskLoader;
import ru.lionzxy.bmstuwifi.utils.Notification;

/**
 * Created by lionzxy on 05.11.16.
 */
@EActivity(R.layout.login_activity)
public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {
    public static final String ACTION_RESAVE_PSWD = "RESAVE_PSWD";
    private ProgressDialog progressDialog;
    private Notification notification;
    @ViewById(R.id.editText_login)
    EditText editTextLogin;
    @ViewById(R.id.editText_password)
    EditText editTextPassword;
    @ViewById(R.id.lgn_btn)
    Button button;
    @ViewById(R.id.rememberPassword)
    CheckBox rememberPswd;

    @AfterViews
    protected void afterViews() {
        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_RESAVE_PSWD)) {
            rememberPswd.setVisibility(View.GONE);
            button.setText(R.string.login_button_save);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String login = editTextLogin.getText().toString();
                    final String password = editTextPassword.getText().toString();
                    App.get().getSharedPreferences().edit().putString("auth_user", login).putString("auth_pass", password).apply();

                    Toast.makeText(LoginActivity.this, R.string.sucsesful, Toast.LENGTH_LONG).show();
                    LoginActivity.this.finish();
                }
            });
        }
    }

    @Click(R.id.lgn_btn)
    public void onClick() {
        final String login = editTextLogin.getText().toString();
        final String password = editTextPassword.getText().toString();


        notification = new Notification(this)
                .setId(1)
                .setEnabled(true)
                .setTitle(getString(R.string.auth))
                .setIcon(R.drawable.logo_bmstu_white);

        notification.show();

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(R.string.auth);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.login_button_hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("auth_user", login);
        bundle.putString("auth_pass", password);
        getSupportLoaderManager().initLoader(0, bundle, this);
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader<Boolean> asyncTaskLoader = new AuthAsyncTaskLoader(this, null, notification, args);
        asyncTaskLoader.forceLoad();
        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        progressDialog.setMessage(getString(R.string.auth_finished));
        if (rememberPswd.isChecked() && data) {
            new SecurePreferences(LoginActivity.this).edit().putString("auth_user", editTextLogin.getText().toString())
                    .putString("auth_pass", editTextPassword.getText().toString()).apply();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        //TODO
    }

}
