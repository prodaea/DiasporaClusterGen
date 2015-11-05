package net.etherealnation.diaspora.clustergen.app.data.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import net.etherealnation.diaspora.clustergen.app.R;
import net.etherealnation.diaspora.clustergen.app.data.model.DiasporaCluster;
import net.etherealnation.diaspora.clustergen.app.data.sql.contract.ClusterGenContract;

public class ClustersCursorAdapter extends CursorAdapter {

    public ClustersCursorAdapter(final Context context, final Cursor c, final boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        View clusterItemView = LayoutInflater.from(context).inflate(R.layout.view_cluster_item, parent, false);

        if (clusterItemView != null) {
            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) clusterItemView.findViewById(R.id.name);

            clusterItemView.setTag(holder);
        }

        return clusterItemView;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(ClusterGenContract.ClusterColumns.NAME));

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(name);
    }

    @Override
    public DiasporaCluster getItem(final int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return new DiasporaCluster(cursor);
    }

    private class ViewHolder {
        TextView name;
    }
}
