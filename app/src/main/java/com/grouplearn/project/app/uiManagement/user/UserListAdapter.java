package com.grouplearn.project.app.uiManagement.user;

import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;

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
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        GLContact contact = contacts.get(position);
        String imageUri = contact.getIconUrl();
        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(holder.ivContactImage.getContext())
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(new BitmapImageViewTarget(holder.ivContactImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(holder.ivContactImage.getContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.ivContactImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            holder.ivContactImage.setImageResource(R.drawable.user_placeholder);
        }
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
    }

    public void setOnItemClickListener(OnRecyclerItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvCotactName;
        ImageView ivContactImage;
        LinearLayout llContactItem;

        public UserViewHolder(View v) {
            super(v);
            tvCotactName = (TextView) v.findViewById(R.id.tv_contact_name);
            ivContactImage = (ImageView) v.findViewById(R.id.iv_contact_image);
            llContactItem = (LinearLayout) v.findViewById(R.id.ll_contact_item);
        }
    }
}
