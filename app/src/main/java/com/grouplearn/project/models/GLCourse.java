package com.grouplearn.project.models;

/**
 * Created by WiSilica on 03-12-2016 22:17 for GroupLearn application.
 */

public class GLCourse extends BaseModel {
    private long courseId;
    private String courseName;
    private String courseIconId;
    private long courseUserId;
    private String definition;
    private String contactDetails;
    private String url;
    private String groupName;
    private long groupId;
    private int courseStatus;

    public int getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(int courseStatus) {
        this.courseStatus = courseStatus;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseIconId() {
        return courseIconId;
    }

    public void setCourseIconId(String courseIconId) {
        this.courseIconId = courseIconId;
    }

    public long getCourseUserId() {
        return courseUserId;
    }

    public void setCourseUserId(long courseUserId) {
        this.courseUserId = courseUserId;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
