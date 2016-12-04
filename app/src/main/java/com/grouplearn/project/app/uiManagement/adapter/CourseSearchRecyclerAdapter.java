package com.grouplearn.project.app.uiManagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.CourseSearchViewHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.models.GLCourse;

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
    }
}
