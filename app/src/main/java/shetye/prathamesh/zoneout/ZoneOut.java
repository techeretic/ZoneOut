package shetye.prathamesh.zoneout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;


public class ZoneOut extends ActionBarActivity implements ActionMode.Callback{

    private RecyclerView mRecyclerView;
    private AppRecAdapter mAdapter;
    private FloatingActionButton mFAddButton = null;
    private PackageManager mPm;
    private List<ResolveInfo> mPkgAppsList;
    private Context mContext;
    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zone_out_home);

        mContext = this;
        mPm = this.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mPkgAppsList = mPm.queryIntentActivities(mainIntent, 0);
        mRecyclerView = (RecyclerView) findViewById(R.id.appRecView);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(mRecyclerView, this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        view.setSelected(true);
                        if (mFAddButton == null) {
                            mFAddButton = new FloatingActionButton.Builder(ZoneOut.this)
                                    .withDrawable(getResources().getDrawable(R.drawable.abc_ic_commit_search_api_mtrl_alpha))
                                    .withButtonColor(getResources().getColor(R.color.accent_material_light))
                                    .withGravity(Gravity.BOTTOM | Gravity.END).withMargins(0, 0, 15, 15).create();
                            NoteAnimator.animateFAB(getApplicationContext(), mFAddButton, NoteAnimator.IN,
                                    NoteAnimator.BOTTOM);
                            mFAddButton.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }
                        myToggleSelection(position);
                    }
                }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayApps();
    }

    private void displayApps() {
        mAdapter = new AppRecAdapter(this, mPkgAppsList);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
    }

    private void myToggleSelection(int idx) {
        mAdapter.toggleSelection(idx);
//        mActionMode.setTitle("Zone these out");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_zone_out, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.mActionMode = null;
        mAdapter.clearSelections();
    }

    @Override
    public void onBackPressed() {
        this.mFAddButton = null;
        mAdapter.clearSelections();
        super.onBackPressed();
    }
}
