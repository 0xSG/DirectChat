package ap1.testbox.sooryagangarajk.com.perplechat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by sooryagangarajk on 09/08/17.
 */

public class CustomAdapterArrayAdapter extends ArrayAdapter {

    List<Ingredient> ingredientsList;
    public CustomAdapterArrayAdapter(Context context, List<Ingredient> list)
    {
        super(context,0,list);
        ingredientsList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater mInflater;
            mInflater = LayoutInflater.from(getContext());
            convertView = mInflater.inflate(R.layout.row,parent,false);
// inflate custom layout called row
            holder = new ViewHolder();
            holder.tv =(TextView) convertView.findViewById(R.id.textView1);
// initialize textview
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        Ingredient in = (Ingredient)ingredientsList.get(position);
        holder.tv.setText(in.name);
        // set the name to the text;

        return convertView;

    }

    static class ViewHolder
    {

        TextView tv;
    }
}