package com.fifed.inputlayoutviews.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Service;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fifed.inputlayoutviews.R;
import com.fifed.inputlayoutviews.listeners.OnChangeFocusListener;
import com.fifed.inputlayoutviews.listeners.OnChangeTextListener;
import com.fifed.inputlayoutviews.listeners.OnChangeValidStateListener;
import com.fifed.inputlayoutviews.listeners.OnErrorListener;
import com.fifed.inputlayoutviews.utils.validators.ValidatorEmptyText;
import com.fifed.inputlayoutviews.utils.validators.core.TextValidator;
import com.fifed.inputlayoutviews.utils.validators.core.ValidatorResponse;

import java.util.ArrayList;

/**
 * Created by Fedir on 07.12.2016.
 */

public class Inputlayout extends RelativeLayout implements View.OnFocusChangeListener, TextWatcher, Animator.AnimatorListener {
    private EditText editText;
    private TextView tvHint, tvError;
    private ArrayList<TextValidator> finishingValidatorList = new ArrayList<>();
    private ArrayList<TextValidator> runtimeValidatorList = new ArrayList<>();
    private OnChangeValidStateListener onChangeValidStateListener;
    private OnChangeFocusListener onChangeFocusListener;
    private OnChangeTextListener onChangeTextListener;
    private OnErrorListener onErrorListener;
    private boolean srarted, hadFocus, isError, isValid = true;;
    private InputMethodManager imm;
    private int floatingDistance;
    private static int inputType;
    private final int ANIMATION_DURATION = 250;
    @DrawableRes
    private int etDefaultBackground, etErrorBackground, etFocusedBackground, etFocusedErrorBackground;
    private TypedArray attributes;


    public Inputlayout(Context context) {
        super(context);
        initInputLayout();
    }

    public Inputlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInputLayout();
        setAttributes(attrs);

    }

    public Inputlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInputLayout();
        setAttributes(attrs);
    }

    private void initInputLayout() {

    }

    private void setAttributes(AttributeSet attrs) {
        attributes = getContext().obtainStyledAttributes(attrs, R.styleable.Inputlayout);
    }

    private void initAttributtes() {
        setHintText();
        setHintMargins();
        setErrorMargins();
        setHintErrorTextSize();
        setHintErrorTextColor();
        adjustFloatingDistance();
        setBackgroundsForEditText();
        setPossibilityBeEmptyForEditText();
        attributes.recycle();
    }

    private void setHintText() {
        String text = attributes.getString(R.styleable.Inputlayout_hint_text);
        if (text != null && text.isEmpty()) {
            tvHint.setText(editText.getHint());
            editText.setHint("");
        } else {
            tvHint.setText(text);
        }
    }

    private void setHintMargins() {
        int marginTop = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_hint_margin_top, 0));
        int marginBottom = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_hint_margin_botom, 0));
        int marginLeft = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_hint_margin_left, 0));
        int marginRight = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_hint_margin_right, 0));
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tvHint.getLayoutParams();
        p.setMargins(p.leftMargin + marginLeft, p.topMargin + marginTop, p.rightMargin + marginRight, p.bottomMargin + marginBottom);
    }

    private void setErrorMargins() {
        int marginTop = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_error_margin_top, 0));
        int marginBottom = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_error_margin_bottom, 0));
        int marginLeft = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_error_margin_left, 0));
        int marginRight = (attributes.getDimensionPixelSize(R.styleable.Inputlayout_error_margin_right, 0));
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tvError.getLayoutParams();
        p.setMargins(p.leftMargin + marginLeft, p.topMargin + marginTop, p.rightMargin + marginRight, p.bottomMargin + marginBottom);
    }

    private void setHintErrorTextSize() {
        int hintSize = attributes.getDimensionPixelSize(R.styleable.Inputlayout_hint_text_size, 0);
        int errorSize = attributes.getDimensionPixelSize(R.styleable.Inputlayout_error_text_size, 0);
        int defaultHintSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18, getResources().getDisplayMetrics()));
        int defaultErrorSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
        tvHint.setTextSize(TypedValue.COMPLEX_UNIT_PX, hintSize == 0 ? defaultHintSize : hintSize);
        tvError.setTextSize(TypedValue.COMPLEX_UNIT_PX, errorSize == 0 ? defaultErrorSize : errorSize);
    }

    private void setHintErrorTextColor() {
        tvError.setTextColor(attributes.getColor(R.styleable.Inputlayout_error_default_color, Color.RED));
        tvHint.setTextColor(attributes.getColor(R.styleable.Inputlayout_hint_color, Color.BLACK));
    }

    private void adjustFloatingDistance() {
        floatingDistance = floatingDistance + (-attributes.getDimensionPixelSize(R.styleable.Inputlayout_adjust_hint_floating_distance, 0));
    }

    private void setBackgroundsForEditText() {
        etDefaultBackground = attributes.getResourceId(R.styleable.Inputlayout_background_default_edit_text, 0);
        if (etDefaultBackground != 0) {
            editText.setBackgroundResource(etDefaultBackground);
        }
        etErrorBackground = attributes.getResourceId(R.styleable.Inputlayout_background_error_edit_text, 0);
        etFocusedBackground = attributes.getResourceId(R.styleable.Inputlayout_background_focused_edit_text, 0);
        etFocusedErrorBackground = attributes.getResourceId(R.styleable.Inputlayout_background_focused_error_edit_text, 0);
    }

    private void setPossibilityBeEmptyForEditText() {
        String errorText = attributes.getString(R.styleable.Inputlayout_empty_error_text);
        if (errorText != null) {
            setEmtyTextValidator(errorText);
            isValid = false;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initEditText();
        initAttributtes();
    }

    private void initEditText() {
        if (getChildCount() > 0 && getChildAt(0) instanceof EditText) {
            editText = (EditText) getChildAt(0);
        } else throw new RuntimeException("InputLayout must contains one EditText");
        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);

        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, MeasureSpec.AT_MOST);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        editText.measure(widthMeasureSpec, heightMeasureSpec);
        initHintAndErrorTextView();

    }

    private void initHintAndErrorTextView() {

        RelativeLayout.LayoutParams errorParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(editText.getId() == View.NO_ID){
            editText.setId(editText.hashCode());
        }
        errorParams.addRule(BELOW, editText.getId());

        ViewGroup.LayoutParams textParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvHint = new TextView(getContext());
        tvHint.setLayoutParams(textParams);

        tvError = new TextView(getContext());
        tvError.setLayoutParams(textParams);

        addView(tvHint);
        addView(tvError, errorParams);


        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, MeasureSpec.AT_MOST);
        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        tvHint.measure(widthMeasureSpec, heightMeasureSpec);
        tvError.measure(widthMeasureSpec, heightMeasureSpec);


        floatingDistance = -(editText.getMeasuredHeight() / 2);

        int marginTopHint = editText.getMeasuredHeight() / 2;
        ViewGroup.MarginLayoutParams tvHintLayoutParams = (ViewGroup.MarginLayoutParams) tvHint.getLayoutParams();
        tvHintLayoutParams.setMargins(tvHintLayoutParams.leftMargin, marginTopHint, tvHintLayoutParams.rightMargin, tvHintLayoutParams.bottomMargin);


        int topMarginET = tvHint.getMeasuredHeight() / 2;
        ViewGroup.MarginLayoutParams etLayoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
        etLayoutParams.setMargins(etLayoutParams.leftMargin, topMarginET, etLayoutParams.rightMargin, etLayoutParams.bottomMargin);

        tvHint.setPivotX(0);
        tvError.setPivotX(0);
        tvError.animate().scaleY(0).scaleX(0).setDuration(0).setListener(this).start();

    }


    public boolean isErrorShowing() {
        return isError;
    }

    public void setErrorColor(@ColorInt int color) {
        tvError.setTextColor(color);
    }

    public void setFloatingHintColor(@ColorInt int color) {
        tvHint.setTextColor(color);
    }


    protected void checkEditText() {
        if (editText.getText().length() != 0) {
            tvHint.animate().translationY(floatingDistance)
                    .scaleX(0.7f).scaleY(0.7f).setDuration(ANIMATION_DURATION);
        } else if (!editText.hasFocus())
            tvHint.animate().translationY(0).scaleX(1).scaleY(1).setDuration(200);
    }

    public void setError(@StringRes int error) {
        setError(getResources().getString(error));
    }

    public void setError(@Nullable String error) {
        if (error == null) {
            if (isErrorShowing()) {
                isError = false;
                tvError.animate().scaleY(0).scaleX(0).setDuration(ANIMATION_DURATION).setListener(this).start();
            }

        } else {
            isError = true;
            tvError.setText(error);
            tvError.setVisibility(VISIBLE);
            tvError.animate().scaleY(1).scaleX(1).setListener(this).setDuration(ANIMATION_DURATION).start();
        }
        if(onErrorListener != null){
            onErrorListener.onError(error == null);
        }
        if (isError && hasFocus() && etFocusedErrorBackground != 0) {
            editText.setBackgroundResource(etFocusedErrorBackground);
        } else if (isError && etErrorBackground != 0) {
            editText.setBackgroundResource(etErrorBackground);
        } else if (hasFocus() && etFocusedBackground != 0) {
            editText.setBackgroundResource(etFocusedBackground);
        } else if (etDefaultBackground != 0) {
            editText.setBackgroundResource(etDefaultBackground);
        }
    }

    public void setOnChangeValidStateListener(OnChangeValidStateListener listener) {
        onChangeValidStateListener = listener;
    }

    public void setOnChangeFocusListener(OnChangeFocusListener listener) {
        this.onChangeFocusListener = listener;
    }

    public void setOnChangeTextListener(OnChangeTextListener listener) {
        this.onChangeTextListener = listener;
    }

    public void setEmtyTextValidator(String errorText) {
        addRuntimeValidator(new ValidatorEmptyText(errorText));
    }

    public void setFinishingValidator(TextValidator validator) {
        finishingValidatorList.clear();
        finishingValidatorList.add(validator);
    }

    public void addFinishingValidator(TextValidator validtor) {
        finishingValidatorList.add(validtor);
    }

    public void addRuntimeValidator(TextValidator validtor) {
        runtimeValidatorList.add(validtor);
        finishingValidatorList.add(validtor);
    }

    public boolean verifyFieldWithAllFinishingValidators() {
        for (int i = 0; i < finishingValidatorList.size(); i++) {
            ValidatorResponse response = finishingValidatorList.get(i).isValidText(editText.getText().toString(), getContext());
            setError(response.getError());
            if (!response.isValid()) return false;
        }
        return true;
    }

    private void verifyFieldWithRuntimeValidators() {
        for (int i = 0; i < runtimeValidatorList.size(); i++) {
            ValidatorResponse response = runtimeValidatorList.get(i).isValidText(editText.getText().toString(), getContext());
            setError(response.getError());
            if (!response.isValid()) break;
        }
    }

    private void checkValidState(){
        if(onChangeValidStateListener == null) {
            return;
        }
        boolean isValidTemp = isValid;
        for (int i = 0; i < finishingValidatorList.size(); i++) {
            ValidatorResponse response = finishingValidatorList.get(i).isValidText(editText.getText().toString(), getContext());
            if (!response.isValid()){
                isValid = false;
                break;
            } else {
                isValid = true;
            }
        }

        if(isValidTemp != isValid) {
            onChangeValidStateListener.onChangeValidState(isValid);
        }

    }

    public EditText getEditText() {
        return editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        editText.getInputType();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkEditText();
        setError(null);
        if (srarted && hadFocus) {
            verifyFieldWithRuntimeValidators();
            checkValidState();
        }
        if(onChangeTextListener != null){
            onChangeTextListener.onChangeText(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void initInputMethodManager() {
        imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
    }


    private boolean getVisibilityKeyBoard() {
        Rect r = new Rect();
        View mRootView = getRootView();
        mRootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = mRootView.getRootView().getHeight() - (r.bottom - r.top);
        float dp = heightDiff / getResources().getDisplayMetrics().density;
        return (dp > 200);
    }


    @Override
    protected void onDetachedFromWindow() {
        finishingValidatorList.clear();
        runtimeValidatorList.clear();
        hadFocus = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initInputMethodManager();
        checkEditText();
        srarted = true;
        editText.setInputType(editText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_TEXT_VARIATION_FILTER);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(onChangeFocusListener != null){
            onChangeFocusListener.onChangeFocus(hasFocus);
        }
        hadFocus = true;
        if (hasFocus && editText.getText().length() == 0) {
            if (!imm.isActive()) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }, 1);
            }
            tvHint.animate().translationY(floatingDistance).scaleX(0.7f).scaleY(0.7f).setDuration(ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            imm.showSoftInput(editText, 0);
                        }
                    });
        } else if (editText.getText().length() == 0 && !isError) {
            tvHint.animate().translationY(0).scaleX(1).scaleY(1).setDuration(ANIMATION_DURATION).start();
        } else if (editText.getText().length() == 0) {
            tvHint.animate().translationY(0).scaleX(1).scaleY(1).setDuration(ANIMATION_DURATION).start();
            if (etErrorBackground != 0) {
                editText.setBackgroundResource(etErrorBackground);
            }
        }
        if (hasFocus) {
            if (isError && etFocusedErrorBackground != 0) {
                editText.setBackgroundResource(etFocusedErrorBackground);
            } else if (etFocusedBackground != 0) {
                editText.setBackgroundResource(etFocusedBackground);
            }
        } else {
            inputType = editText.getInputType();
            if (isError && etErrorBackground != 0) {
                editText.setBackgroundResource(etErrorBackground);
            } else if (etDefaultBackground != 0) {
                editText.setBackgroundResource(etDefaultBackground);
            }
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (tvError.getVisibility() == VISIBLE && !isErrorShowing()) {
            tvError.setVisibility(GONE);
        } else if (isErrorShowing()) {
            tvError.setVisibility(VISIBLE);
        }

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
