package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.RequestViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.GLRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 05-08-2016 10:35 for Group Learn application.
 */
public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestViewHolder> {
    ArrayList<GLRequest> requestModels = new ArrayList<>();
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
        final GLRequest requestModel = requestModels.get(position);
        if (getItemViewType(position) == 1) {

        } else {
            holder.tvName.setText(requestModel.getUserName());
            holder.tvMessage.setText(requestModel.getGroupName());
            holder.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClicked(position, requestModel, holder.tvAccept);
                    }
                }
            });
            holder.tvIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClicked(position, requestModel, holder.tvIgnore);
                    }
                }
            });
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

    public ArrayList<GLRequest> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<GLRequest> requestModels) {
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
