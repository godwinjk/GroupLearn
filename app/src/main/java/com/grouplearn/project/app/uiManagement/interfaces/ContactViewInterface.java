package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by Godwin on 07-06-2016 22:08 for Group Learn application 20:17 for GroupLearn.
 * @author : Godwin Joseph Kurinjikattu
 */
public interface ContactViewInterface {
    public void onGetAllContactsFromCloud(ArrayList<GLContact> contactModels);

    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels);

    public void onGetContactsFailed(AppError error);
}
