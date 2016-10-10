package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.ContactItemHolder;
import com.grouplearn.project.models.ContactModel;
import com.grouplearn.project.utilities.Log;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 19:56 for Group Learn application.
 */
public class ContactListAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "ContactListAdapter";
    Context mContext;
    LayoutInflater inflater;
    ArrayList<ContactModel> mContactModels = new ArrayList<>();
    ArrayList<ContactModel> filterContacts = new ArrayList<>();
    SearchFilter searchFilter;
    String filterText;

    public ContactListAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchFilter = new SearchFilter();
    }

    public void setContactList(ArrayList<ContactModel> contactModels) {
        mContactModels = contactModels;
        filterContacts = contactModels;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filterContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return filterContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContactItemHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_contact_item, null);
            holder = new ContactItemHolder();
            holder.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.tvContactNumber = (TextView) convertView.findViewById(R.id.tv_contact_number);
            holder.tvInvite = (TextView) convertView.findViewById(R.id.tv_invite);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_contact_image);
            convertView.setTag(holder);
        } else
            holder = (ContactItemHolder) convertView.getTag();

        final ContactModel model = (ContactModel) getItem(position);

        holder.tvContactName.setText(model.getContactName());
        holder.tvContactNumber.setText(model.getContactNumber());
        holder.ivIcon.setBackground(getBackgroundDrawable(mContext, position));
        if (model.getContactImage() != null) {
            Bitmap bitmap = model.getContactImage();
            holder.ivIcon.setImageBitmap(bitmap);
        } else {
            holder.ivIcon.setImageResource(R.drawable.contact_white);
        }
        holder.tvInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSms(model.getContactNumber());
            }
        });
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getContactId() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(model.getContactId()));
                    intent.setData(uri);
                    mContext.startActivity(intent);
                }
            }
        });
        return convertView;
    }

    private void sendSms(String phoneNumber) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Install New GroupLearn", null, null);

        } catch (Exception ex) {
            Log.e(TAG, "FAILED TO SEND MESSAGE, FAILED TO SEND MESSAGE");
            ex.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            filterText = filterString;
            FilterResults results = new FilterResults();
            final ArrayList<ContactModel> list = doMySearch(filterString);
            results.values = list;
            results.count = list.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterContacts = (ArrayList<ContactModel>) results.values;
            notifyDataSetChanged();
        }

        private ArrayList<ContactModel> doMySearch(String query) {
            ArrayList<ContactModel> contactModels = new ArrayList<>();               //Temporary arrayList of storing devices.
            for (ContactModel contactModel : mContactModels) {
                String zoneName = contactModel.getContactName().toLowerCase();
                if (zoneName.contains(query)) {
                    contactModels.add(contactModel);
                }
            }
            return contactModels;
        }

    }

    private Drawable getBackgroundDrawable(Context context, int position) {
        ShapeDrawable biggerCircle = new ShapeDrawable(new OvalShape());
        biggerCircle.setIntrinsicHeight(100);
        biggerCircle.setIntrinsicWidth(100);
        biggerCircle.setBounds(new Rect(0, 0, 60, 60));
        biggerCircle.getPaint().setColor(getRandomColor(context, position));
        return biggerCircle;
    }

    private int getRandomColor(Context context, int position) {
        position = Math.abs(position);
        int[] colorSchema = new int[]{R.color.pale_rose, R.color.blue, R.color.green, R.color.purple, R.color.majenta, R.color.light_green, R.color.yellow, R.color.pale_red};
        return context.getResources().getColor((colorSchema[position % colorSchema.length]));
    }
}
