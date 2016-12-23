package ru.lionzxy.bmstuwifi.fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.lionzxy.bmstuwifi.R;

/**
 * Created by lionzxy on 13.11.16.
 */

public class AboutMeFragment extends DialogFragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(R.string.pref_about_contact);

        LinearLayout linearLayout = new LinearLayout(getActivity(), null);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linLayoutParam);

        View contactView = inflater.inflate(R.layout.fragment_aboutme, container, false);
        ((ImageView) contactView.findViewById(R.id.logoCompany)).setImageResource(R.drawable.logo_vk);
        ((TextView) contactView.findViewById(R.id.nameCompany)).setText(R.string.about_me_vk);
        ((TextView) contactView.findViewById(R.id.link)).setText(R.string.about_me_vk_link);
        contactView.setOnClickListener(this);
        linearLayout.addView(contactView);

        contactView = inflater.inflate(R.layout.fragment_aboutme, container, false);
        ((ImageView) contactView.findViewById(R.id.logoCompany)).setImageResource(R.drawable.logo_telegram);
        ((TextView) contactView.findViewById(R.id.nameCompany)).setText(R.string.about_me_telegram);
        ((TextView) contactView.findViewById(R.id.link)).setText(R.string.about_me_telegram_link);
        contactView.setOnClickListener(this);
        linearLayout.addView(contactView);

        contactView = inflater.inflate(R.layout.fragment_aboutme, container, false);
        ((ImageView) contactView.findViewById(R.id.logoCompany)).setImageResource(R.drawable.logo_4pda);
        ((TextView) contactView.findViewById(R.id.nameCompany)).setText(R.string.about_me_4pda);
        ((TextView) contactView.findViewById(R.id.link)).setText(R.string.about_me_4pda_link);
        contactView.setOnClickListener(this);
        linearLayout.addView(contactView);

        contactView = inflater.inflate(R.layout.fragment_aboutme, container, false);
        ((ImageView) contactView.findViewById(R.id.logoCompany)).setImageResource(R.drawable.logo_github);
        ((TextView) contactView.findViewById(R.id.nameCompany)).setText(R.string.about_me_github);
        ((TextView) contactView.findViewById(R.id.link)).setText(R.string.about_me_github_link);
        contactView.setOnClickListener(this);
        linearLayout.addView(contactView);

        contactView = inflater.inflate(R.layout.fragment_aboutme, container, false);
        ((ImageView) contactView.findViewById(R.id.logoCompany)).setImageResource(R.drawable.logo_email);
        ((TextView) contactView.findViewById(R.id.nameCompany)).setText(R.string.about_me_email);
        ((TextView) contactView.findViewById(R.id.link)).setText(R.string.about_me_email_link);
        contactView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.about_me_email_link)});
                try {
                    startActivity(Intent.createChooser(i, getString(R.string.about_me_email_send)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), getString(R.string.about_me_email_err), Toast.LENGTH_SHORT).show();
                }
            }
        });
        linearLayout.addView(contactView);

        return linearLayout;
    }

    @Override
    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((TextView) v.findViewById(R.id.link)).getText().toString()));
        getActivity().startActivity(browserIntent);
    }
}
