package com.grouplearn.project.app.uiManagement.user;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.utilities.views.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by Godwin on 16-04-2017 13:35 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    ArrayList<GLContact> contacts = new ArrayList<>();
    OnRecyclerItemClickListener onItemClickListener;

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_item, null);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, final int position) {
        final GLContact contact = contacts.get(position);
        String imageUri = contact.getIconUrl();
        String text = contact.getContactName();
        if (!TextUtils.isEmpty(text) && text.length() > 16) {
            text = text.substring(0, 16) + "...";
        }
        String interests = getStringArray("Interests : ", contact.getInterests());
        String skills = getStringArray("Skills : ", contact.getSkills());
        if (!TextUtils.isEmpty(interests)) {
            holder.tvContactInterests.setVisibility(View.VISIBLE);
            holder.tvContactInterests.setText(interests);
        } else {
            holder.tvContactInterests.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(skills)) {
            holder.tvContactSkills.setVisibility(View.VISIBLE);
            holder.tvContactSkills.setText(skills);
        } else {
            holder.tvContactSkills.setVisibility(View.GONE);
        }

        holder.tvContactInterests.setVisibility(View.GONE);
        holder.tvContactSkills.setVisibility(View.GONE);

        holder.tvCotactName.setText(text);
        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(holder.ivContactImage.getContext())
                    .load(imageUri)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(holder.ivContactImage);
        } else {
            Glide.with(holder.ivContactImage.getContext())
                    .load(R.drawable.user_admin)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.ivContactImage);
        }
        holder.llContactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(position, contact, 1, v);
                }
            }
        });
    }

    private String getStringArray(String key, ArrayList<GLInterest> array) {
        StringBuilder builder = new StringBuilder();
        if (array != null && array.size() > 0) {
            builder.append(key);
            for (GLInterest interest : array) {
                builder.append(interest.getInterestName());
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return contacts == null ? 0 : contacts.size();
    }

    public ArrayList<GLContact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<GLContact> contacts) {
        this.contacts = contacts;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvCotactName, tvContactInterests, tvContactSkills;
        RoundedImageView ivContactImage;
        LinearLayout llContactItem;

        public UserViewHolder(View v) {
            super(v);
            tvCotactName = (TextView) v.findViewById(R.id.tv_contact_name);
            tvContactInterests = (TextView) v.findViewById(R.id.tv_contact_interests);
            tvContactSkills = (TextView) v.findViewById(R.id.tv_contact_skills);
            ivContactImage = (RoundedImageView) v.findViewById(R.id.iv_user_profile);
            llContactItem = (LinearLayout) v.findViewById(R.id.ll_contact_item);
        }
    }
}
