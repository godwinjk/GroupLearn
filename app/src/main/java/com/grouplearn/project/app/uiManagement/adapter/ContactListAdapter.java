package com.grouplearn.project.app.uiManagement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.adapter.holder.ContactItemHolder;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.bean.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 19:56 for Group Learn application.
 */
public class ContactListAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = "ContactListAdapter";
    Context mContext;
    LayoutInflater inflater;
    ArrayList<GLContact> mContactModels = new ArrayList<>();
    ArrayList<GLContact> filterContacts = new ArrayList<>();
    SearchFilter searchFilter;
    String filterText;
    OnRecyclerItemClickListener onRecyclerItemClickListener;
    int type = 0;

    public ContactListAdapter(Context mContext, int type) {
        this.mContext = mContext;
        this.type = type;
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        searchFilter = new SearchFilter();
    }

    public void setContactList(ArrayList<GLContact> contactModels) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ContactItemHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_contact_item, null);
            holder = new ContactItemHolder();
            holder.tvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            holder.tvContactNumber = (TextView) convertView.findViewById(R.id.tv_contact_number);
            holder.tvInvite = (TextView) convertView.findViewById(R.id.tv_invite);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_contact_image);
            holder.llContactItem = (LinearLayout) convertView.findViewById(R.id.ll_contact_item);
            convertView.setTag(holder);
        } else
            holder = (ContactItemHolder) convertView.getTag();

        final GLContact model = (GLContact) getItem(position);

        String imageUri = model.getIconUrl();
        if (imageUri != null) {
            Glide.with(mContext)
                    .load(imageUri)
                    .asBitmap()
                    .centerCrop()
                    .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                    .into(new BitmapImageViewTarget(holder.ivIcon) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            holder.ivIcon.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
        if (type == 0 && model.getStatus() == 1) {
            holder.tvInvite.setVisibility(View.INVISIBLE);
        } else if (type == 1 && model.getStatus() == 1) {
            holder.tvInvite.setVisibility(View.VISIBLE);
        } else if (type == 0 && model.getStatus() == 0) {
            holder.tvInvite.setVisibility(View.VISIBLE);
        }

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
                if (onRecyclerItemClickListener != null) {
                    onRecyclerItemClickListener.onItemClicked(position, model, v);
                }
            }
        });
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getContactId() != null) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClicked(position, model, v);
                    }
                }
            }
        });
        holder.llContactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getContactUniqueId() > 0) {
                    if (onRecyclerItemClickListener != null) {
                        onRecyclerItemClickListener.onItemClicked(position, model, v);
                    }
                }
            }
        });
        return convertView;
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
            final ArrayList<GLContact> list = doMySearch(filterString);
            results.values = list;
            results.count = list.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterContacts = (ArrayList<GLContact>) results.values;
            notifyDataSetChanged();
        }

        private ArrayList<GLContact> doMySearch(String query) {
            ArrayList<GLContact> contactModels = new ArrayList<>();               //Temporary arrayList of storing devices.
            for (GLContact contactModel : mContactModels) {
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

    public OnRecyclerItemClickListener getOnRecyclerItemClickListener() {
        return onRecyclerItemClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }
}
