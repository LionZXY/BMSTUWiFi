package ru.lionzxy.bmstuwifi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.lionzxy.bmstuwifi.R;

/**
 * Created by lionzxy on 27.11.16.
 */
public class BlueButton extends LinearLayout {
    public BlueButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.button, this);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ButtonAttr,
                0, 0);

        ((ImageView) findViewById(R.id.buttonImage)).setImageDrawable(a.getDrawable(R.styleable.ButtonAttr_imageSrc));
        ((TextView) findViewById(R.id.buttonText)).setText(a.getString(R.styleable.ButtonAttr_text));

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        findViewById(R.id.ll).setOnClickListener(l);
    }
}
