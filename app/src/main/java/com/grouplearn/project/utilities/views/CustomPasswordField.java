package com.grouplearn.project.utilities.views;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 14-09-2016 14:20 for Group Learn application.
 */
public class CustomPasswordField extends RelativeLayout {


    private boolean mIsShowingPassword;
    private boolean mEnabled;
    private boolean mShowButton;
    private String mHint;
    private int mTextColorHint;
    private int mImeOptions = -1;
    /**
     * EditText component
     */
    private TextInputLayout editText;

    /**
     * Button that clears the EditText contents
     */
    private ImageButton btnShowPassword;

    /**
     * Additional listener fired when cleared
     */
    private OnClickListener onClickClearListener;

    public CustomPasswordField(Context context) {
        super(context);
        init(null);
    }

    public CustomPasswordField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public CustomPasswordField(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Initialize view
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {

        //inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.wise_password_text, this, true);

        //pass attributes to EditText, make clearable
        editText = (TextInputLayout) findViewById(R.id.et_password);
        mEnabled = true;
        mShowButton = true;

        if (mEnabled) {
            editText.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (mShowButton) {
                        if (s.length() > 0)
                            btnShowPassword.setVisibility(RelativeLayout.VISIBLE);
                        else
                            btnShowPassword.setVisibility(RelativeLayout.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            editText.setEnabled(false);
        }

        //build clear button
        btnShowPassword = (ImageButton) findViewById(R.id.button_clear);
        btnShowPassword.setVisibility(RelativeLayout.INVISIBLE);
        btnShowPassword.setOnTouchListener(mOnTouchListener);
        if (!TextUtils.isEmpty(mHint)) {
            editText.setHint(mHint);
        }
        editText.getEditText().setHintTextColor(mTextColorHint);
        if (mImeOptions > -1) {
            editText.getEditText().setImeOptions(mImeOptions);
        }
    }

    /**
     * Expose the edit text
     */
    public EditText getEditText() {
        return editText.getEditText();
    }

    public TextInputLayout getTextinputlayout() {
        return editText;
    }

    /**
     * Get value
     *
     * @return text
     */
    public String getText() {
        return editText.getEditText().getText().toString();
    }

    /**
     * Set value
     *
     * @param text
     */
    public void setText(String text) {
        editText.getEditText().setText(text);
    }

    /**
     * Set OnClickListener, making EditText unfocusable
     *
     * @param listener
     */
    @Override
    public void setOnClickListener(OnClickListener listener) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(listener);
    }

    /**
     * Set listener to be fired after EditText is cleared
     *
     * @param listener
     */
    public void setOnClearListener(OnClickListener listener) {
        onClickClearListener = listener;
    }


    private int mPreviousInputType;

    public void showPassword() {
        mIsShowingPassword = false;
        setInputType(mPreviousInputType, true);
        mPreviousInputType = -1;
        if (null != mOnPasswordDisplayListener) {
            mOnPasswordDisplayListener.onPasswordShow();
        }
    }

    public void hidePassword() {
        mPreviousInputType = editText.getEditText().getInputType();
        mIsShowingPassword = true;
        setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, true);
        if (null != mOnPasswordDisplayListener) {
            mOnPasswordDisplayListener.onPasswordHide();
        }
    }

    public interface OnPasswordDisplayListener {
        public void onPasswordShow();

        public void onPasswordHide();
    }

    OnPasswordDisplayListener mOnPasswordDisplayListener;

    public void setOnPasswordDisplayListener(OnPasswordDisplayListener listener) {
        mOnPasswordDisplayListener = listener;
    }

    OnTouchListener mOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mIsShowingPassword) {
                        btnShowPassword.setBackgroundResource(R.drawable.eye_visible);
                        showPassword();
                    } else {
                        btnShowPassword.setBackgroundResource(R.drawable.eye_hide);
                        hidePassword();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }

            return false;
        }
    };

    private void setInputType(int inputType, boolean keepState) {
        int selectionStart = -1;
        int selectionEnd = -1;
        if (keepState) {
            selectionStart = editText.getEditText().getSelectionStart();
            selectionEnd = editText.getEditText().getSelectionEnd();
        }
        editText.getEditText().setInputType(inputType);
        if (keepState) {
            editText.getEditText().setSelection(selectionStart, selectionEnd);
        }
    }

}



