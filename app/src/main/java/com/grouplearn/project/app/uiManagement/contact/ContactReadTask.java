package com.grouplearn.project.app.uiManagement.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.bean.GLContact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Godwin Joseph on 14-09-2016 14:51 for Group Learn application.
 */
public class ContactReadTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ContactReadTask";
    Context mContext;
    ArrayList<GLContact> contactList = new ArrayList<>();
    ContactViewInterface contactViewInterface;

    public ContactReadTask(Context mContext) {
        this.mContext = mContext;
    }

    public void setContactViewInterface(ContactViewInterface contactViewInterface) {
        this.contactViewInterface = contactViewInterface;
    }

    @Override
    protected Void doInBackground(Void... params) {
        contactList = readContacts(contactViewInterface);
        return null;
    }

    public ArrayList<GLContact> readContacts(final ContactViewInterface contactViewInterface) {
        final ArrayList<GLContact> contactList = new ArrayList<>();
        int count = 0;
        ContentResolver cr = mContext.getContentResolver();
        String sort = ContactsContract.Contacts.DISPLAY_NAME + " ASC";
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sort);
        String phone = null;
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String imageUri = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    GLContact contactModel = new GLContact();
                    contactModel.setContactName(name);
//                    System.out.println(count + "\t\t    name : " + name + ", ID : " + id);

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactModel.setContactNumber(formatPhoneNumber(phone));
//                        System.out.println("phone" + phone);
                        contactModel.setContactId(id);
                        contactModel.setContactImage(getContactImage(imageUri));
                    }

                    pCur.close();
                    contactList.add(contactModel);
                    new ContactDbHelper(mContext).addContact(contactModel);
                    sort(contactList);
                    count++;
                    if (count % 10 == 0) {
                        ArrayList<GLContact> contactModels = new ArrayList<>();
                        contactModels = (ArrayList<GLContact>) contactList.clone();
                        if (contactViewInterface != null)
                            contactViewInterface.onGetAllContactsFromDb(contactModels);
                    }
                }
            }
            cur.close();
            if (contactViewInterface != null)
                contactViewInterface.onGetContactsFinished(contactList);
        }
        return contactList;
    }

    private Bitmap getContactImage(String imageUri) {
        Bitmap bitmap = null;
        if (imageUri != null) {
//            System.out.println(Uri.parse(imageUri));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber != null) {
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replaceAll("\\(", "");
            phoneNumber = phoneNumber.replaceAll("\\)", "");
            phoneNumber = phoneNumber.replaceAll("\\+", "");
            if (phoneNumber.length() > 10) {
                phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
            }
        }
//        Log.d(TAG, phoneNumber);
        return phoneNumber;
    }

    private void sort(ArrayList<GLContact> contactList) {
        Collections.sort(contactList, new Comparator<GLContact>() {
            @Override
            public int compare(GLContact lhs, GLContact rhs) {
                return lhs.getContactName().compareToIgnoreCase(rhs.getContactName());
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (contactViewInterface != null) {
            contactViewInterface.onGetAllContactsFromDb(contactList);
        }
    }
}
