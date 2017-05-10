package com.rssreader.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rssreader.R;
import com.rssreader.rssinfo.RssObject;
import java.util.ArrayList;

public class RssAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RssObject> rssObjects;
    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;
    private final ItemClickListener rssItemClickListener;

    public RssAdapter(ArrayList<RssObject> rssObjects, ItemClickListener rssItemClickListener) {
        this.rssObjects = rssObjects;
        this.rssItemClickListener = rssItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
            return new TextViewHolder(view);
        } else {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.progress_item, viewGroup, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {

        if(viewHolder instanceof ProgressViewHolder){
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        } else{
             String strDesc = Html.fromHtml(rssObjects.get(i).description).toString();
            ((TextViewHolder)viewHolder).descTextView.setText(strDesc);
            ((TextViewHolder)viewHolder).cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rssItemClickListener.onItemClick(i, ((TextViewHolder)viewHolder).descTextView);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rssObjects.get(position).description.equals("progress") ? VIEW_PROG : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return rssObjects.size();
    }

    private class TextViewHolder extends RecyclerView.ViewHolder {
        private TextView descTextView;
        private CardView cardView;
         TextViewHolder(View view) {
            super(view);
            descTextView = (TextView) view.findViewById(R.id.descTextView);
            cardView = (CardView) view.findViewById(R.id.cardView);
        }
    }
    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
         ProgressBar progressBar;
         ProgressViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }
}