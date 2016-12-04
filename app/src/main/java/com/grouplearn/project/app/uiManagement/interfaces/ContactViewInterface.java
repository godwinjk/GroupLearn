package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.models.GLContact;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 22:08 for Group Learn application.
 */
public interface ContactViewInterface {
    public void onGetAllContacts(ArrayList<GLContact> contactModels);

    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels);

    public void onGetContactsFinished(ArrayList<GLContact> contactModels);

    public void onGetContactsFailed(AppError error);

}
