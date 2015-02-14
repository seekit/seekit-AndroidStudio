package com.example.seekit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;


import com.example.seekit.FriendElement;
import com.example.seekit.R;

import java.util.List;

import contenedor.Usuario;

/**
 * Created by nicoB on 12/15/14.
 */
public class FriendListAdapter extends ArrayAdapter<Usuario> {
    protected Context mContext;
    protected List<Usuario> entradas;


    public FriendListAdapter(Context context, List<Usuario> entradas) {
        super(context, R.layout.friend_element_list, entradas);

        this.mContext = context;
        this.entradas = entradas;
    }

    @Override
    public int getCount() {
        return entradas.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;


        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_element_list, parent, false);

            holder = new ViewHolder();
            holder.nameLabel = (TextView) convertView.findViewById(R.id.friendName_list);
            holder.emailLabel = (TextView) convertView.findViewById(R.id.friendEmail_list);
            //holder.switchShare = (Switch) convertView.findViewById(R.id.switchShare);
            convertView.setTag(holder);

        } else{
          	holder = (ViewHolder)convertView.getTag();

        }

        Usuario friend = entradas.get(position);
        String friendName = friend.getNombre();
        String friendMail = friend.getMail();
        Boolean isShare = false;

        holder.nameLabel.setText(friendName);
        holder.emailLabel.setText(friendMail);
        //holder.switchShare.setChecked(isShare);


        return convertView;
    }


    private static class ViewHolder {
        TextView nameLabel;
        TextView emailLabel;
        Switch switchShare;

    }


}


