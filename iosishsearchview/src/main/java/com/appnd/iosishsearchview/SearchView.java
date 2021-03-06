package com.appnd.iosishsearchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchView extends RelativeLayout implements View.OnClickListener,
        TextWatcher, TextView.OnEditorActionListener {

    public static final int CANCEL_MARGIN_LEFT = 2;
    public static final int CANCEL_MARGIN_RIGHT = 4;
    public static final int MARGIN_SEARCHBOX = 4;
    private Context mContext;

    private FrameLayout mSearchContainer;
    private ImageView mSearchIcon;
    private TextView mCancelSearch;
    private EditText mSearchText;
    private View mViewContainer;

    private List<OnQueryTextListener> queryListeners;
    private OnSearchOpenedListener openedListener;

    public SearchView(Context context) {
        super(context);
        initSearchView(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSearchView(context, attrs);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSearchView(context, attrs);
    }

    @TargetApi(21)
    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSearchView(context, attrs);
    }

    @Override
    public void onClick(View v) {
        if (mSearchContainer.isClickable()) {
            showSearch();
        } else {
            hideSearch();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        for (OnQueryTextListener listener : queryListeners) {
            listener.onQueryTextChanged(s.toString());
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            for (OnQueryTextListener listener : queryListeners) {
                listener.onQueryTextSubmitted(mSearchText.getText().toString());
            }
            return true;
        }
        return false;
    }

    private void initSearchView(Context context, AttributeSet attrs) {
        mContext = context;
        inflate(getContext(), R.layout.search_view, this);

        //get references to searchView views
        mViewContainer = findViewById(R.id.search_container);
        mSearchContainer = (FrameLayout) findViewById(R.id.search_rectangle);
        mSearchIcon = (ImageView) findViewById(R.id.search_icon);
        mCancelSearch = (TextView) findViewById(R.id.cancel_search);
        mSearchText = (EditText) findViewById(R.id.text_search);

        mSearchText.addTextChangedListener(this);
        mSearchText.setOnEditorActionListener(this);

        mSearchContainer.setOnClickListener(this);
        mCancelSearch.setOnClickListener(this);

        queryListeners = new ArrayList<>();

        applyAttrs(context, attrs);
    }

    private void applyAttrs(Context context, AttributeSet attrs) {
        TypedArray attributeArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.SearchView);

        Drawable background = attributeArray.getDrawable(R.styleable.SearchView_android_background);
        if (background != null)
            mSearchContainer.setBackground(background);

        Drawable searchIcon = attributeArray.getDrawable(R.styleable.SearchView_search_icon);
        if (searchIcon != null)
            mSearchIcon.setImageDrawable(searchIcon);

        String font = attributeArray.getString(R.styleable.SearchView_font);
        if (font != null && !"".equals(font)){
            setTypeface(font);
        }

        attributeArray.recycle();
    }

    public void addOnQueryTextListener(OnQueryTextListener listener) {
        queryListeners.add(listener);
    }

    public void removeOnQueryTextListener(OnQueryTextListener listener) {
        queryListeners.remove(listener);
    }

    public void setOpenedListener(OnSearchOpenedListener listener) {
        openedListener = listener;
    }

    public void show() {
        UiHelper.slideIn(mViewContainer);
    }

    public void hide() {
        UiHelper.slideToTop(mViewContainer);
    }



    private void hideSearch() {
        mCancelSearch.setClickable(false);
        mSearchText.setClickable(false);
        mSearchText.animate()
                .alpha(0)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mSearchIcon.animate().translationX(0)
                                .setDuration(400)
                                .start();
                        mCancelSearch.animate().alpha(0).setDuration(400).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mCancelSearch.setVisibility(View.GONE);
                                mSearchContainer.setClickable(true);
                                if (openedListener != null) {
                                    openedListener.closed();
                                }
                            }
                        }).start();

                        mSearchText.setVisibility(View.INVISIBLE);

                    }
                }).start();
    }

    private void showSearch() {
        //disable click to prevent errors while animating
        mSearchContainer.setClickable(false);
        //the animation starts when the cancel textview is drawn (so we add a listener on layout changes)
        mCancelSearch.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //first we translate the searchIcon to its new position (which is on the left side of the searchbox)
                mSearchIcon.animate()
                        .translationX(-mSearchContainer.getWidth() / 2 +
                                mSearchIcon.getWidth() / 2 + mCancelSearch.getWidth() / 2 +
                                UiHelper.DpToPixels(MARGIN_SEARCHBOX * 2 + CANCEL_MARGIN_RIGHT + CANCEL_MARGIN_LEFT, mContext))
                        .setDuration(400)
                        .withStartAction(new Runnable() {
                            @Override
                            public void run() {
                                mCancelSearch.animate().alpha(1).setDuration(400).start();
                            }
                        }).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mSearchText.animate().alpha(1).setDuration(300).start();
                        mSearchText.setVisibility(View.VISIBLE);
                        mSearchText.setClickable(true);
                        mSearchContainer.requestFocus();

                        mCancelSearch.setClickable(true);
                        if (openedListener != null) {
                            openedListener.opened();
                        }
                    }
                }).start();

                mCancelSearch.removeOnLayoutChangeListener(this);
            }
        });


        mCancelSearch.setVisibility(View.VISIBLE);

    }

    private void setTypeface(String fontString){

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), fontString);

        if (typeface != null) {
            mSearchText.setTypeface(typeface);
            mCancelSearch.setTypeface(typeface);
        }
    }

    public interface OnQueryTextListener {
        void onQueryTextChanged(String query);

        void onQueryTextSubmitted(String query);
    }

    public interface OnSearchOpenedListener {
        void opened();

        void closed();
    }
}
