package ru.lionzxy.bmstuwifi;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import ru.lionzxy.bmstuwifi.authentificator.AuthManager;
import ru.lionzxy.bmstuwifi.authentificator.IAuth;
import ru.lionzxy.bmstuwifi.utils.AuthAsyncTaskLoader;
import ru.lionzxy.bmstuwifi.utils.Notification;

/**
 * Created by lionzxy on 05.11.16.
 */
@EActivity(R.layout.login_activity)
public class LoginActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Boolean> {
    public static final String ACTION_RESAVE_PSWD = "ru.lionzxy.bmstuwifi.LoginActivity.RESAVE_PSWD";
    private ProgressDialog progressDialog = null;
    private AlertDialog wifiDialog = null;
    private Notification notification = null;
    private IAuth auth = null;
    @ViewById(R.id.editText_login)
    EditText editTextLogin;
    @ViewById(R.id.editText_password)
    EditText editTextPassword;
    @ViewById(R.id.textSSID)
    TextView wifiSSID;
    @ViewById(R.id.lgn_btn)
    Button button;
    @ViewById(R.id.rememberPassword)
    CheckBox rememberPswd;
    @ViewById(R.id.editText_password_visible)
    ImageView editTextPasswordVisible;

    @AfterViews
    protected void afterViews() {
        String SSID = getIntent().getExtras() == null ? null : getIntent().getExtras().getString("wifi_ssid", null);
        if (SSID == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.login_wifi_err)
                    .setTitle(R.string.login_err);
            builder.setNegativeButton(R.string.login_button_hide, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wifiDialog.dismiss();
                }
            });
            wifiDialog = builder.setCancelable(true).create();
            wifiDialog.show();
            auth = AuthManager.getCurrentAuth(this);
        } else {
            auth = AuthManager.getAuthForSSID(SSID);
            wifiSSID.setText(SSID);
        }

        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_RESAVE_PSWD)) {
            rememberPswd.setVisibility(View.GONE);
            button.setText(R.string.login_button_save);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String login = editTextLogin.getText().toString();
                    final String password = editTextPassword.getText().toString();
                    auth.setLogin(login).setPassword(password);

                    Toast.makeText(LoginActivity.this, R.string.sucsesful, Toast.LENGTH_LONG).show();
                    LoginActivity.this.finish();
                }
            });
        }

        editTextLogin.setText(auth.getLogin(""));
        editTextPassword.setText(auth.getPassword("").replaceAll(".", "*"));

        editTextLogin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && editTextPassword.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editTextPassword, InputMethodManager.SHOW_IMPLICIT);
                    return true;
                } else return false;
            }
        });
        editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    onClick();
                    return true;
                } else return false;
            }
        });
        editTextPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    editTextPasswordVisible.setVisibility(View.VISIBLE);
                else editTextPasswordVisible.setVisibility(View.GONE);
            }
        });

        editTextPasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    editTextPassword.setTransformationMethod(new SingleLineTransformationMethod());
                    editTextPasswordVisible.setImageResource(R.drawable.ic_visibility_white_24dp);
                } else {

                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                    editTextPasswordVisible.setImageResource(R.drawable.ic_visibility_off_white_24dp);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (notification != null)
            notification.hide();
        if (progressDialog != null)
            progressDialog.dismiss();
        if (wifiDialog != null)
            wifiDialog.dismiss();
    }

    @Click(R.id.lgn_btn)
    public void onClick() {
        String login = editTextLogin.getText().toString();
        String password = editTextPassword.getText().toString();

        if (password.equals(auth.getPassword("").replaceAll(".", "*")))
            password = auth.getPassword("");

        notification = new Notification(this)
                .setId(1)
                .setEnabled(true)
                .setTitle(getString(R.string.auth))
                .setIcon(R.drawable.ic_stat_logo);

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
        if (rememberPswd.isChecked()) {
            auth.setPassword(password);
            auth.setLogin(login);
        } //TODO

        Log.d("TAG", auth.getLogin("Нет"));
        Log.d("TAG", auth.getPassword("Нет"));

        Bundle bundle = new Bundle();
        bundle.putString("wifi_id", auth.getNameid());
        getSupportLoaderManager().initLoader(0, bundle, this);
    }


    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        AsyncTaskLoader<Boolean> asyncTaskLoader = new AuthAsyncTaskLoader(this, progressDialog, notification, args);
        asyncTaskLoader.forceLoad();
        return asyncTaskLoader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (rememberPswd.isChecked() && data) {
            progressDialog.setMessage(getString(R.string.auth_finished));
            progressDialog.dismiss();
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {
        //TODO
    }

}
