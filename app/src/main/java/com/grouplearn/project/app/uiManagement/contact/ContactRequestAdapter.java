package com.grouplearn.project.app.uiManagement.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.RequestViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin on 16-04-2017 15:58 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class ContactRequestAdapter extends RecyclerView.Adapter<RequestViewHolder> {
    ArrayList<GLContact> requestModels = new ArrayList<>();
    OnRecyclerItemClickListener onRecyclerItemClickListener;

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_request_group_item, null);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_request_item, null);
        }
        RequestViewHolder holder = new RequestViewHolder(v, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RequestViewHolder holder, final int position) {
        final GLContact requestModel = requestModels.get(position);
        if (getItemViewType(position) == 1) {

        } else {
            holder.tvName.setText(requestModel.getContactName());
            holder.tvMessage.setVisibility(View.GONE);
            holder.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClicked(position, requestModel,1, holder.tvAccept);
                    }
                }
            });
            holder.tvIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClicked(position, requestModel,1, holder.tvIgnore);
                    }
                }
            });
            String imageUri = requestModel.getIconUrl();
            if (!TextUtils.isEmpty(imageUri)) {
                final Context mContext = holder.itemView.getContext();
                Glide.with(mContext)
                        .load(imageUri)
                        .asBitmap()
                        .centerCrop()
                        .into(new BitmapImageViewTarget(holder.ivProfile) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.ivProfile.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            } else {
                holder.ivProfile.setImageResource(R.drawable.man_prof_128);
            }
        }
    }

    @Override
    public int getItemCount() {
        return requestModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public ArrayList<GLContact> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<GLContact> requestModels) {
        this.requestModels = requestModels;
        notifyDataSetChanged();
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }
}
