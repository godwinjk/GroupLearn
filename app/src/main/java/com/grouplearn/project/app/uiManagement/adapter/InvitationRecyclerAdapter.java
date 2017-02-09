package com.grouplearn.project.app.uiManagement.adapter;

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
import com.grouplearn.project.app.uiManagement.adapter.holder.InvitationViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-10-2016 21:27 for GroupLearn application.
 */

public class InvitationRecyclerAdapter extends RecyclerView.Adapter<InvitationViewHolder> {
    Context mContext;
    ArrayList<GLRequest> invitationList = new ArrayList<>();
    OnRecyclerItemClickListener onRecyclerItemClickListener;

    public InvitationRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public InvitationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invitation_list_item, null);
        return new InvitationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final InvitationViewHolder holder, final int position) {
        final GLRequest model = invitationList.get(position);
        holder.tvName.setText(model.getGroupName());
        holder.tvMessage.setText("Invited by " + model.getUserDisplayName());

        holder.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, model, view);
                }
            }
        });
        holder.tvIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, model, view);
                }
            }
        });
        String imageUri = model.getIconUrl();
        if (!TextUtils.isEmpty(imageUri)) {
            final Context mContext = holder.itemView.getContext();
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(holder.ivGroupIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.ivGroupIcon.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            holder.ivGroupIcon.setImageResource(R.drawable.man_prof_128);
        }
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    public ArrayList<GLRequest> getInvitationList() {
        return invitationList;
    }

    public void setInvitationList(ArrayList<GLRequest> invitationList) {
        this.invitationList = invitationList;
        notifyDataSetChanged();
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }
}
