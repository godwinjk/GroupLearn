package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.InterestViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLInterest;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 20:35 for GroupLearn application.
 */

public class InterestRecyclerAdapter extends RecyclerView.Adapter<InterestViewHolder> {
    ArrayList<GLInterest> interests = new ArrayList<>();
    OnRecyclerItemClickListener onRecyclerItemClickListener;
    private boolean type = false;

    @Override
    public InterestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_interest_item, null);
        return new InterestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InterestViewHolder holder, final int position) {
        final GLInterest interest = interests.get(position);
        holder.ivClose.setVisibility(type ? View.GONE : View.VISIBLE);
        holder.tvInterest.setText(interest.getInterestName());
        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, interest, holder.ivClose);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests, boolean type) {
        this.type = type;
        this.interests = interests;
        notifyDataSetChanged();
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }
}
