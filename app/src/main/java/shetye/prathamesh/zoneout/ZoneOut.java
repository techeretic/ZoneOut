package shetye.prathamesh.zoneout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;


public class ZoneOut extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton mFAddButton = null;
    private PackageManager mPm;
    private List<ResolveInfo> mPkgAppsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zone_out_home);

        mPm = this.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mPkgAppsList = mPm.queryIntentActivities(mainIntent, 0);
        mRecyclerView = (RecyclerView) findViewById(R.id.appRecView);

        //displayApps();

        mFAddButton = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.abc_ic_commit_search_api_mtrl_alpha))
                .withButtonColor(getResources().getColor(R.color.accent_material_light))
                .withGravity(Gravity.BOTTOM | Gravity.END).withMargins(0, 0, 15, 15).create();

        mFAddButton.setVisibility(View.INVISIBLE);


        mFAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
/*
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (mFAddButton.getVisibility() == View.INVISIBLE) {
                            NoteAnimator.animateFAB(getApplicationContext(), mFAddButton, NoteAnimator.IN,
                                    NoteAnimator.BOTTOM);
                            //NoteAnimator.animateFAB(getApplicationContext(), mFAddButton, NoteAnimator.OUT, NoteAnimator.BOTTOM);
                        }
                        CardView cv = (CardView) findViewById(R.id.card_view);
                        cv.setBackgroundColor(getResources().getColor(R.color.accent_material_light));
                    }
                }));
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayApps();
    }

    private void displayApps() {
        mAdapter = new AppRecAdapter(this, mPkgAppsList);
        mRecyclerView.setAdapter(mAdapter);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
    }
}
