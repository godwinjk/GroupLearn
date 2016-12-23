package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.CourseSearchViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.settings.BrowserActivity;
import com.grouplearn.project.bean.GLCourse;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 10:42 for GroupLearn application.
 */

public class CourseSearchRecyclerAdapter extends RecyclerView.Adapter<CourseSearchViewHolder> {
    OnRecyclerItemClickListener onRecyclerItemClickListener;
    ArrayList<GLCourse> courses = new ArrayList<>();

    @Override
    public CourseSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_item, null);
        return new CourseSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CourseSearchViewHolder holder, final int position) {
        final GLCourse course = courses.get(position);
        holder.tvCourseName.setText(course.getCourseName());
        holder.tvDescription.setText(course.getDefinition());
        holder.tvSiteAddress.setText(course.getUrl());
        holder.tvView.setVisibility(View.GONE);
        holder.llSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, course, holder.llSearchItem);
                }
            }
        });
        holder.tvSiteAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context mContext = holder.itemView.getContext();
                if (mContext != null) {
                    Intent i = new Intent(mContext, BrowserActivity.class);
                    i.putExtra("uri", course.getUrl());
                    mContext.startActivity(i);
                }
            }
        });

        final Context mContext = holder.itemView.getContext();

        String imageUri = course.getIconUrl();
        if (imageUri != null) {
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
//                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(new BitmapImageViewTarget(holder.ivCourseIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.ivCourseIcon.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            holder.ivCourseIcon.setImageResource(R.drawable.course_128);
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public ArrayList<GLCourse> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<GLCourse> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }
}
