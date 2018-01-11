package fr.emse.com.environmentcontrol;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import fr.emse.com.environmentcontrol.Model.RoomContextRule;

/**
 * Created by Valentin on 28/07/2017.
 */

public class ResultAdapter extends ArrayAdapter<RoomContextRule> {


    public ResultAdapter(Context context, List list){
        super(context,0,list);
        Log.i("ResultAdapter","Construc OK");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_result, parent, false);
        }

        RowViewHolder viewHolder = (RowViewHolder) convertView.getTag();
        if (viewHolder == null){
            viewHolder = new RowViewHolder();
            viewHolder.rowTitle = (TextView) convertView.findViewById(R.id.rowTitle);
            viewHolder.rowLeftTitle = (TextView) convertView.findViewById(R.id.rowLeftTitle);
            viewHolder.rowHeader = (TextView) convertView.findViewById(R.id.rowHeader);
            viewHolder.rowDescription = (TextView) convertView.findViewById(R.id.rowDescription);

            convertView.setTag(viewHolder);
        }


        RoomContextRule rule = getItem(position);


            viewHolder.rowTitle.setText( rule.getName() );

            viewHolder.rowDescription.setText(rule.getDescription());




        return convertView;
    }


    public class RowViewHolder {
        public TextView rowTitle;
        public TextView rowLeftTitle;
        public TextView rowHeader;
        public TextView rowDescription;
    }


}
