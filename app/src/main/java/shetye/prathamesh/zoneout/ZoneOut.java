package shetye.prathamesh.zoneout;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ZoneOut extends ActionBarActivity implements ActionMode.Callback{

    private RecyclerView mRecyclerView;
    private Button mTimeBtn;
    private Button mDateBtn;
    private AppRecAdapter mAdapter;
    private FloatingActionButton mFAddButton = null;
    private PackageManager mPm;
    private List<ResolveInfo> mPkgAppsList;
    private Context mContext;
    private String [] mTimeSpinnerContent;
    private String [] mDateSpinnerContent;
    private int mSelectedDay;
    private int mSelectedMonth;
    private int mSelectedYear;
    private int mSelectedHours;
    private int mSelectedMinutes;
    private boolean mPastDateSelected;
    private SharedPreferences mPrefs;
    private final int mTagKey = 100;
    private final String mTimeTag = "TIME";
    private final String mDateTag = "DATE";
    public static final String SHARED_PREF_APP_DATA = "APP_DATA";
    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zone_out_home);

        mContext = this;
        mPrefs = getSharedPreferences(SHARED_PREF_APP_DATA, MODE_PRIVATE);
        fetchApps();

        mTimeSpinnerContent = getResources().getStringArray(R.array.Time_Spinner_Options);
        mDateSpinnerContent = getResources().getStringArray(R.array.Date_Spinner_Options);
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
                                    createZoneOutDialog(mContext);
                                }
                            });
                        }
                        myToggleSelection(position);
                    }
                }));

    }

    private void createZoneOutDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.datetimepicker_dialog);
        dialog.setTitle("Zone Out until??");
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mTimeBtn = (Button) dialog.findViewById(R.id.btntimeset);
        mDateBtn = (Button) dialog.findViewById(R.id.btndateset);
        final Time dtNow = new Time();
        dtNow.setToNow();
        mSelectedHours = dtNow.hour;
        mSelectedMinutes = dtNow.minute;
        mSelectedYear = dtNow.year;
        mSelectedMonth = dtNow.month;
        mSelectedDay = dtNow.monthDay;
        updateBtnText(dtNow, false);
        //updateBtnText(dtNow, true);
        Time selectedTime = new Time();
        selectedTime.set(0, mSelectedMinutes, mSelectedHours, mSelectedDay, mSelectedMonth, mSelectedYear);
        if (Time.compare(selectedTime, dtNow) <= 0) {
            mPastDateSelected = true;
        } else {
            mPastDateSelected = false;
        }
        mTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Time selectedTime = new Time();
                        mSelectedHours = hourOfDay;
                        mSelectedMinutes = minute;
                        selectedTime.set(0, mSelectedMinutes, mSelectedHours, mSelectedDay, mSelectedMonth, mSelectedYear);
                        if (Time.compare(selectedTime, dtNow) <= 0) {
                            mPastDateSelected = true;
                        } else {
                            mPastDateSelected = false;
                        }
                        updateBtnText(selectedTime, true);
                    }
                }, mSelectedHours, mSelectedMinutes, false);
                timePickerDialog.show();
            }
        });
        mDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                        Time selectedTime = new Time();
                        mSelectedYear = yy;
                        mSelectedMonth = mm;
                        mSelectedDay = dd;
                        selectedTime.set(0, mSelectedMinutes, mSelectedHours, mSelectedDay, mSelectedMonth, mSelectedYear);
                        if (Time.compare(selectedTime, dtNow) <= 0) {
                            mPastDateSelected = true;
                        } else {
                            mPastDateSelected = false;
                        }
                        updateBtnText(selectedTime, false);
                    }
                }, mSelectedYear, mSelectedMonth, mSelectedDay);
                datePickerDialog.show();
            }
        });
        dialog.show();
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
        if (mAdapter.getSelectedItemCount() == 0) {
            hideFAB();
        }
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
        if (mFAddButton != null) {
            hideFAB();
            mAdapter.clearSelections();
        } else {
            super.onBackPressed();
        }
    }

    private void fetchApps() {
        mPm = this.getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mPkgAppsList = mPm.queryIntentActivities(mainIntent, 0);
        Log.d("ZoneOut", "IN fetchApps");
        for (ResolveInfo reInfo : mPkgAppsList) {
            Log.d("ZoneOut", "reInfo.activityInfo.packageName = " + reInfo.activityInfo.packageName);
        }
    }

    private void hideFAB() {
        if (mFAddButton != null) {
            NoteAnimator.animateFAB(getApplicationContext(), mFAddButton, NoteAnimator.OUT,
                    NoteAnimator.BOTTOM);
            this.mFAddButton.setVisibility(View.INVISIBLE);
            this.mFAddButton = null;
        }
    }

    private void updateBtnText(Time time, boolean setTime) {
        if (setTime) {
            if (mTimeBtn != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                mTimeBtn.setText(sdf.format(new Date(time.toMillis(false))));
                if (mPastDateSelected) {
                    mTimeBtn.setTextColor(Color.RED);
                } else {
                    mTimeBtn.setTextColor(Color.GRAY);
                }
            }
        } else {
            if (mDateBtn != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("cccc, MMMM dd");
                mDateBtn.setText(sdf.format(new Date(time.toMillis(false))));
                if (mPastDateSelected) {
                    mDateBtn.setTextColor(Color.RED);
                } else {
                    mDateBtn.setTextColor(Color.GRAY);
                }
            }
        }
    }
}
