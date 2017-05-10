package com.rssreader.adapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

    private int prevTotal = 0;
    private boolean loading = true;
    private int currPage = 0;
    private LinearLayoutManager linearLayoutManager;

    protected EndlessOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int visibleThreshold = 0;

        if (loading) {
            if (totalItemCount > prevTotal) {
                loading = false;
                prevTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItemPosition + visibleThreshold)) {
            currPage++;
            onLoadMore(currPage);
            loading = true;
        }
    }

    public abstract void onLoadMore(int currPage);
}