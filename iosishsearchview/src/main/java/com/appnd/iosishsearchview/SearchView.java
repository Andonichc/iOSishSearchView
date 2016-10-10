package com.appnd.iosishsearchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchView extends RelativeLayout implements View.OnClickListener,
        TextWatcher, TextView.OnEditorActionListener {

    private Context mContext;

    private RelativeLayout mSearch;
    private ImageView mSearchIcon;
    private TextView mCancelSearch;
    private EditText mSearchText;
    private View mSearchContainer;

    private List<OnQueryTextListener> queryListeners;
    private OnSearchOpenedListener openedListener;

    public SearchView(Context context) {
        super(context);
        initSearchView(context);
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSearchView(context);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSearchView(context);
    }

    @TargetApi(21)
    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSearchView(context);
    }

    @Override
    public void onClick(View v) {
        if (mSearch.isClickable()) {
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

    public void addOnQueryTextListener(OnQueryTextListener listener) {
        queryListeners.add(listener);
    }

    public void removeOnQueryTextListener(OnQueryTextListener listener) {
        queryListeners.remove(listener);
    }

    public void setOpenedListener (OnSearchOpenedListener listener) {
        openedListener = listener;
    }

    public void show() {
        UiHelper.slideIn(mSearchContainer);
    }

    public void hide() {
        UiHelper.slideToTop(mSearchContainer);
    }

    private void initSearchView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.search_view, this, false);

        //get references to searchView views
        mSearchContainer = view.findViewById(R.id.search_container);
        mSearch = (RelativeLayout) view.findViewById(R.id.search_rectangle);
        mSearchIcon = (ImageView) view.findViewById(R.id.search_icon);
        mCancelSearch = (TextView) view.findViewById(R.id.cancel_search);
        mSearchText = (EditText) view.findViewById(R.id.text_search);

        mSearchText.addTextChangedListener(this);
        mSearchText.setOnEditorActionListener(this);

        mSearch.setOnClickListener(this);
        mCancelSearch.setOnClickListener(this);

        addView(view);

        queryListeners = new ArrayList<>();
    }

    private void hideSearch() {
        //UiHelper.hideKeyBoard(mContext);
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
                                mSearch.setClickable(true);
                                openedListener.closed();
                            }
                        }).start();

                        mSearchText.setVisibility(View.INVISIBLE);

                    }
                }).start();
    }

    private void showSearch() {
        mSearch.setClickable(false);
        mSearchIcon.animate()
                .translationX(0 - mSearch.getWidth() / 2 +
                        mSearchIcon.getWidth() / 2 + UiHelper.DpToPixels(8 + 4 + 78 / 2, mContext))
                .setDuration(400)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mCancelSearch.setVisibility(View.VISIBLE);
                        mCancelSearch.animate().alpha(1).setDuration(400).start();
                    }
                }).withEndAction(new Runnable() {
            @Override
            public void run() {
                mSearchText.animate().alpha(1).setDuration(300).start();
                mSearchText.setVisibility(View.VISIBLE);
                mSearchText.setClickable(true);
                mSearch.requestFocus();
                //UiHelper.showKeyboard(getActivity());
                mCancelSearch.setClickable(true);
                openedListener.opened();
            }
        }).start();
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
