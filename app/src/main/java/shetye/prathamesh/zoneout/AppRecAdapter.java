package shetye.prathamesh.zoneout;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by p.shetye on 3/13/15.
 */
public class AppRecAdapter extends
        RecyclerView.Adapter<AppRecAdapter.ViewHolder> {
    List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
    Context mContext;
    private SparseBooleanArray mSelectedItems;

    AppRecAdapter(Context context, List<ResolveInfo> objects) {
        mContext = context;
        mApps = objects;
        mSelectedItems = new SparseBooleanArray();
    }

    @Override
    public AppRecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_icon, parent,
                false);
        v.setSelected(false);
        ViewHolder vh = new ViewHolder(v,mContext);
        return vh;
    }

    @Override
    public void onBindViewHolder(AppRecAdapter.ViewHolder viewHolder, int position) {
        viewHolder.appIcon.setImageDrawable(mApps.get(position).loadIcon(mContext.getPackageManager()));
        viewHolder.appName.setText(mApps.get(position).loadLabel(mContext.getPackageManager()));
        viewHolder.itemView.setActivated(mSelectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView appName;
        public ImageView appIcon;
        private Context mContext;

        public ViewHolder(View view, Context context) {
            super(view);
            mContext = context;
            appName = (TextView) view.findViewById(R.id.appText);
            appIcon = (ImageView) view.findViewById(R.id.appIcon);
        }
    }

    public void toggleSelection(int pos) {
        if (mSelectedItems.get(pos, false)) {
            mSelectedItems.delete(pos);
        }
        else {
            mSelectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }

    public Set<String> getSelectedItemsAsSet() {
        Set<String> items = new HashSet<>();
        for (int i = 0; i < mSelectedItems.size(); i++) {
            items.add(mApps.get(mSelectedItems.keyAt(i)).activityInfo.packageName);
        }
        return items;
    }

    @Override
    public long getItemId(int position) {
        return 142857 + position;
    }
}
