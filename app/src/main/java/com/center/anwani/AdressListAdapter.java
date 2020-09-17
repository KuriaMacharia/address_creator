package com.center.anwani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class AdressListAdapter extends ArrayAdapter<Address> {
    Context mContext;
    LayoutInflater inflater;
    private ArrayList<Address> listAll;
    public AdressListAdapter(Context context ){
        super(context, 0);
        mContext=context;
        inflater = LayoutInflater.from(mContext);
        this.listAll=new ArrayList<Address>();
        this.listAll.addAll(ViewActivity.listAllAddress);
    }

    public class ViewHolder {
        TextView name;
        TextView latTxt;
        TextView lonTxt;
        TextView addressname;
        TextView type;
        TextView verification;
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
        return ViewActivity.listAllAddress.size();
    }

    @Override
    public Address getItem(int position) {
        return ViewActivity.listAllAddress.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final ViewHolder holder;
        //View vi = convertView;
        int type = getItemViewType(position);

        if (convertView == null)
            if (type == 0) {
                convertView = inflater.inflate(R.layout.address_item, null);
            } else {
                convertView = inflater.inflate(R.layout.address_item_verified, null);
            }

        if (type == 0) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txt_address_number);
            holder.latTxt=(TextView) convertView.findViewById(R.id.txt_latitude_item);
            holder.lonTxt= (TextView) convertView.findViewById(R.id.txt_longitude_item);

            holder.addressname = (TextView) convertView.findViewById(R.id.txt_address_name_item);
            holder.type=(TextView) convertView.findViewById(R.id.txt_address_type_item);
            holder.verification= (TextView) convertView.findViewById(R.id.txt_verification_status_item);
            convertView.setTag(holder);

            holder.name.setText(ViewActivity.listAllAddress.get(position).getFulladdress());
            holder.latTxt.setText(ViewActivity.listAllAddress.get(position).getLatitude());
            holder.lonTxt.setText(ViewActivity.listAllAddress.get(position).getLongitude());
            holder.addressname.setText(ViewActivity.listAllAddress.get(position).getAddressname());
            holder.type.setText(ViewActivity.listAllAddress.get(position).getAddresstype());
            holder.verification.setText(ViewActivity.listAllAddress.get(position).getVerificationstatus());

        } else {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.txt_address_number);
            holder.latTxt=(TextView) convertView.findViewById(R.id.txt_latitude_item);
            holder.lonTxt= (TextView) convertView.findViewById(R.id.txt_longitude_item);

            holder.addressname = (TextView) convertView.findViewById(R.id.txt_address_name_item);
            holder.type=(TextView) convertView.findViewById(R.id.txt_address_type_item);
            holder.verification= (TextView) convertView.findViewById(R.id.txt_verification_status_item);
            convertView.setTag(holder);

            holder.name.setText(ViewActivity.listAllAddress.get(position).getFulladdress());
            holder.latTxt.setText(ViewActivity.listAllAddress.get(position).getLatitude());
            holder.lonTxt.setText(ViewActivity.listAllAddress.get(position).getLongitude());
            holder.addressname.setText(ViewActivity.listAllAddress.get(position).getAddressname());
            holder.type.setText(ViewActivity.listAllAddress.get(position).getAddresstype());
            holder.verification.setText(ViewActivity.listAllAddress.get(position).getVerificationstatus());

        }

        return convertView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        ViewActivity.listAllAddress.clear();
        if (charText.length() == 0) {
            ViewActivity.listAllAddress.addAll(listAll);
        } else {
            for (Address wp : listAll) {
                if (wp.getFulladdress().toLowerCase(Locale.getDefault()).contains(charText)) {
                    ViewActivity.listAllAddress.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
