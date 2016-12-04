package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.InvitationViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.GLRequest;

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
    public void onBindViewHolder(InvitationViewHolder holder, final int position) {
        final GLRequest model = invitationList.get(position);
        holder.tvName.setText(model.getGroupName());
        holder.tvMessage.setText(model.getDefinition());

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
