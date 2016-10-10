package com.appnd.iosishsearchview;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SearchView extends RelativeLayout implements View.OnClickListener{

    private Context mContext;

    private RelativeLayout mSearch;
    private ImageView mSearchIcon;
    private TextView mCancelSearch;
    private EditText mSearchText;
    private View mSearchContainer;

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

    private void initSearchView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.search_view, this, false);

        //get references to searchView views
        mSearchContainer = view.findViewById(R.id.search_container);
        mSearch = (RelativeLayout) view.findViewById(R.id.search_rectangle);
        mSearchIcon = (ImageView) view.findViewById(R.id.search_icon);
        mCancelSearch = (TextView) view.findViewById(R.id.cancel_search);
        mSearchText = (EditText) view.findViewById(R.id.text_search);

        mSearch.setOnClickListener(this);
        mCancelSearch.setOnClickListener(this);

        addView(view);
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
            }
        }).start();
    }
}
