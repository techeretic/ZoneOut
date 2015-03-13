package shetye.prathamesh.zoneout;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p.shetye on 3/13/15.
 */
public class AppRecAdapter extends
        RecyclerView.Adapter<AppRecAdapter.ViewHolder> {
    List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
    Context mContext;

    AppRecAdapter(Context context, List<ResolveInfo> objects) {
        mContext = context;
        mApps = objects;
    }

    @Override
    public AppRecAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_icon, parent,
                false);
        ViewHolder vh = new ViewHolder(v,mContext);
        return vh;
    }

    @Override
    public void onBindViewHolder(AppRecAdapter.ViewHolder viewHolder, int position) {
        viewHolder.appIcon.setImageDrawable(mApps.get(position).loadIcon(mContext.getPackageManager()));
        viewHolder.appName.setText(mApps.get(position).loadLabel(mContext.getPackageManager()));
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView appName;

        public ImageView appIcon;

        private Context mContext;

        public ViewHolder(View view, Context context) {
            super(view);
            mContext = context;
            appName = (TextView) view.findViewById(R.id.appText);
            appIcon = (ImageView) view.findViewById(R.id.appIcon);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CardView cv = (CardView) v.findViewById(R.id.card_view);
                    cv.setBackgroundColor(mContext.getResources().getColor(R.color.accent_material_light));
                }
            });
        }
    }
}
