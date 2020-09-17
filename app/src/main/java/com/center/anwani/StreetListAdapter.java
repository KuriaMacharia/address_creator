package com.center.anwani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class StreetListAdapter extends ArrayAdapter<Street> {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Street> listAll;
    public StreetListAdapter(Context context ){
        super(context, 0);
        mContext=context;
        inflater = LayoutInflater.from(mContext);
        this.listAll=new ArrayList<Street>();
        this.listAll.addAll(ViewActivity.listAllStreet);

    }

    public class ViewHolder {
        TextView name;
        TextView latTxt;
        TextView lonTxt;
        TextView verifyTxt;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (listAll.get(position).getVerificationstatus().contentEquals("Verified")) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getCount() {
        return ViewActivity.listAllStreet.size();
    }

    @Override
    public Street getItem(int position) {
        return ViewActivity.listAllStreet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final ViewHolder holder;
        int type = getItemViewType(position);

        if (convertView == null)
            if (type == 0) {
                convertView = inflater.inflate(R.layout.address_item, null);
            } else {
                convertView = inflater.inflate(R.layout.address_item_verified, null);
            }

        if (type == 0) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.address_item, null);

            holder.name = (TextView) convertView.findViewById(R.id.txt_address_number);
            holder.latTxt = (TextView) convertView.findViewById(R.id.txt_latitude_item);
            holder.lonTxt = (TextView) convertView.findViewById(R.id.txt_longitude_item);
            holder.verifyTxt = (TextView) convertView.findViewById(R.id.txt_verification_status_item);
            convertView.setTag(holder);

            holder.name.setText(ViewActivity.listAllStreet.get(position).getRoad());
            holder.latTxt.setText(ViewActivity.listAllStreet.get(position).getEntrylatitude());
            holder.lonTxt.setText(ViewActivity.listAllStreet.get(position).getEntrylongitude());
            holder.verifyTxt.setText(ViewActivity.listAllStreet.get(position).getVerificationstatus());

        }else{
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.address_item_verified, null);

            holder.name = (TextView) convertView.findViewById(R.id.txt_address_number);
            holder.latTxt = (TextView) convertView.findViewById(R.id.txt_latitude_item);
            holder.lonTxt = (TextView) convertView.findViewById(R.id.txt_longitude_item);
            holder.verifyTxt = (TextView) convertView.findViewById(R.id.txt_verification_status_item);
            convertView.setTag(holder);

            holder.name.setText(ViewActivity.listAllStreet.get(position).getRoad());
            holder.latTxt.setText(ViewActivity.listAllStreet.get(position).getEntrylatitude());
            holder.lonTxt.setText(ViewActivity.listAllStreet.get(position).getEntrylongitude());
            holder.verifyTxt.setText(ViewActivity.listAllStreet.get(position).getVerificationstatus());
        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        ViewActivity.listAllStreet.clear();
        if (charText.length() == 0) {
            ViewActivity.listAllStreet.addAll(listAll);
        } else {
            for (Street wp : listAll) {
                if (wp.getRoad().toLowerCase(Locale.getDefault()).contains(charText)) {
                    ViewActivity.listAllStreet.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
