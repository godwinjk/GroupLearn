package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.adapter.holder.CourseSearchViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.settings.BrowserActivity;
import com.grouplearn.project.bean.GLCourse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by WiSilica on 04-12-2016 10:42 for GroupLearn application.
 */

public class CourseSearchRecyclerAdapter extends RecyclerView.Adapter<CourseSearchViewHolder> {
    OnRecyclerItemClickListener onRecyclerItemClickListener;
    ArrayList<GLCourse> courses = new ArrayList<>();
    private long myUserId;
    private boolean showBadge = false;

    public CourseSearchRecyclerAdapter(Context mContext, boolean showBadge) {
        this.showBadge = showBadge;
        myUserId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);
    }

    @Override
    public CourseSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_course_item, null);
        return new CourseSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CourseSearchViewHolder holder, final int position) {
        final GLCourse course = courses.get(position);
        if (course.isMine()) {
            holder.tvView.setText("Edit");
        }
        if (myUserId == course.getCourseUserId()) {
            holder.tvBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvBadge.setVisibility(View.GONE);
        }
        holder.tvCourseName.setText(course.getCourseName());
        holder.tvDescription.setText(course.getDefinition());
        holder.tvSiteAddress.setText(course.getUrl());
        holder.tvView.setVisibility(View.VISIBLE);

        holder.llSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, course, holder.llSearchItem);
                }
            }
        });
        holder.tvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, course, holder.tvView);
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
//                    mContext.startActivity(i);
                }
            }
        });

        final Context mContext = holder.itemView.getContext();

        String imageUri = course.getIconUrl();
        if (!TextUtils.isEmpty(imageUri)) {
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.course_128)
                    .thumbnail(.2f)
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
        if (showBadge || !course.isMine()) {
            holder.tvBadge.setVisibility(View.GONE);
        }
        holder.tvView.setVisibility(View.GONE);
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

    public void setCourses(ArrayList<GLCourse> listData) {
        if (this.courses.size() <= 0) {
            this.courses = (ArrayList<GLCourse>) listData.clone();
            notifyDataSetChanged();
            return;
        }

        for (Iterator<GLCourse> iterator = listData.iterator(); iterator.hasNext(); ) {
            GLCourse tempGroup = iterator.next();
            for (GLCourse group : courses) {
                if (tempGroup.getCourseId() == group.getCourseId()) {
                    group.setIconUrl(tempGroup.getIconUrl());
                    iterator.remove();
                }
            }
        }
        this.courses.addAll(listData);
        notifyDataSetChanged();
    }

    public void clear() {
        courses.clear();
        notifyDataSetChanged();
    }

    public void remove(GLCourse course) {
        for (Iterator<GLCourse> iterator = courses.iterator(); iterator.hasNext(); ) {
            GLCourse glCourse = iterator.next();
            if (course.getCourseId() == glCourse.getCourseId()) {
                iterator.remove();
            }
        }
        notifyDataSetChanged();
    }

    public void clearAndInsert() {
        ArrayList<GLCourse> notMineList = new ArrayList<>();
        ListIterator<GLCourse> groupListIterator = courses.listIterator();
        for (; groupListIterator.hasNext(); ) {
            GLCourse course = groupListIterator.next();
            if (!course.isMine()) {
                notMineList.add(course);
                groupListIterator.remove();
            }
        }
        notifyDataSetChanged();
        setCourses(notMineList);
    }
}
