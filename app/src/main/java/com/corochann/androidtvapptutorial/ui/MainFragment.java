package com.corochann.androidtvapptutorial.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.corochann.androidtvapptutorial.R;
import com.corochann.androidtvapptutorial.model.CustomListRow;
import com.corochann.androidtvapptutorial.model.IconHeaderItem;
import com.corochann.androidtvapptutorial.ui.presenter.CustomListRowPresenter;
import com.corochann.androidtvapptutorial.ui.presenter.IconHeaderItemPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFragment extends BrowseFragment {
    private static final String TAG = MainFragment.class.getSimpleName();

    /* Adapter and ListRows */
    private ArrayObjectAdapter mRowsAdapter = null;
    private ArrayList<CustomListRow> mGridItemListRows = new ArrayList<>();
    private ArrayList<IconHeaderItem> iconHeaderItems = new ArrayList<>();


    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private String[] categoryList = {"TV", "Xem Phim","Nghe Nhạc", "Youtube", "Ứng dụng khác" };

    /* Grid row item settings */
    private static final int GRID_ITEM_WIDTH = 300;
    private static final int GRID_ITEM_HEIGHT = 300;
    private static final String GRID_STRING_ERROR_FRAGMENT = "ErrorFragment";
    private static final String GRID_STRING_GUIDED_STEP_FRAGMENT = "GuidedStepFragment";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        /* Set up rows with light data. done in main thread. */
        loadRows();
        setRows();


        setupEventListeners();


    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());

        // Existence of this method make In-app search icon visible
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(getActivity(), SearchActivity.class);
               // startActivity(intent);
            }
        });
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            // each time the item is clicked, code inside here will be executed.
            ApplicationInfo applicationInfo = (ApplicationInfo) item;
            String packageName = applicationInfo.packageName;
            Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }

            }

    }



    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.videos_by_google_banner));
        setTitle("Hello Android TV!"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new IconHeaderItemPresenter();
            }
        });
    }

    /**
     * only load rows which can be prepared (executed in main thread) instantaneously.
     * UI update is done in {@link #setRows}
     */
    private void loadRows() {

        packageManager = getActivity().getPackageManager();
       // applist = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        for (int i =0 ; i< applist.size(); i++)
        {
            Log.i("viettung", applist.get(i).packageName);
        }

        for (int cat =0; cat < categoryList.length; cat++) {
           // IconHeaderItem gridItemPresenterHeader = new IconHeaderItem(cat, categoryList[cat], R.drawable.ic_add_white_48dp);
                iconHeaderItems.add(new IconHeaderItem(cat, categoryList[cat], R.drawable.ic_add_white_48dp));

        }
        //Map<String, ArrayObjectAdapter> arrayObjectAdapters = new HashMap<>();


        for(int header =0 ; header < iconHeaderItems.size(); header++ )
        {
            GridItemPresenter mGridPresenter = new GridItemPresenter();
            String headerName = iconHeaderItems.get(header).getName();
            ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
                if(gridRowAdapter != null && headerName.equals("TV")) {
                    for (int appIndex = 0; appIndex < categoryApp("tv").size(); appIndex++) {

                            gridRowAdapter.add(categoryApp("tv").get(appIndex));

                    }
                }
                else if(gridRowAdapter != null && headerName.equals("Xem Phim")) {
                for (int appIndex = 0; appIndex < categoryApp("film").size(); appIndex++) {
                    gridRowAdapter.add(categoryApp("film").get(appIndex));
                    }

                 }
                else if(gridRowAdapter != null && headerName.equals("Nghe Nhạc")) {
                    for (int appIndex = 0; appIndex < categoryApp("music").size(); appIndex++) {
                        gridRowAdapter.add(categoryApp("music").get(appIndex));
                    }

                }
                else if(gridRowAdapter != null && headerName.equals("Youtube")) {
                    for (int appIndex = 0; appIndex < categoryApp("youtube").size(); appIndex++) {
                        gridRowAdapter.add(categoryApp("youtube").get(appIndex));
                    }

                }
                else  {
                    for (int appIndex = 0; appIndex < categoryApp("").size(); appIndex++) {
                        gridRowAdapter.add(categoryApp("").get(appIndex));
                    }

                }
            CustomListRow mGridItemListRow = new CustomListRow(iconHeaderItems.get(header), gridRowAdapter);
                if(mGridItemListRow!= null) {
                    mGridItemListRows.add(mGridItemListRow);
                }
        }

        /*****GridItemPresenter*******/

    }
        //catergory apps
        private  ArrayList<ApplicationInfo> categoryApp(String category)
        {
                ArrayList<ApplicationInfo> applicationInfos = new ArrayList<>();

                for (int appIndex = 0; appIndex < applist.size(); appIndex++) {

                        if(applist.get(appIndex).packageName.contains("htvonlinetv") ||applist.get(appIndex).packageName.contains("fptplay") || applist.get(appIndex).packageName.contains("mytvnet") ) {
                            {
                                if(category.equals("tv"))
                                applicationInfos.add(applist.get(appIndex));
                            }
                        }


                     else   if(applist.get(appIndex).packageName.contains("hayhaytv")) {
                            if (category.equals("film"))
                            applicationInfos.add(applist.get(appIndex));

                    }

                     else   if(applist.get(appIndex).packageName.contains("zing.mp3")) {
                            if (category.equals("music"))
                            applicationInfos.add(applist.get(appIndex));

                    }

                     else   if(applist.get(appIndex).packageName.contains("youtube")) {
                            if (category.equals("youtube"))
                            applicationInfos.add(applist.get(appIndex));

                    }
                    else
                    {
                        if (category.equals(""))

                            applicationInfos.add(applist.get(appIndex));

                    }

                }
            return applicationInfos;
        }
    /**
     * Updates UI after loading Row done.
     */
    private void setRows() {
        mRowsAdapter = new ArrayObjectAdapter(new CustomListRowPresenter()); // Initialize

        if(mGridItemListRows != null) {
            for (CustomListRow customListRow : mGridItemListRows) {
                if(customListRow!=null)
                mRowsAdapter.add(customListRow);
            }
        }
        /* Set */
        setAdapter(mRowsAdapter);

    }

    /**
     * from AOSP sample source code
     * GridItemPresenter class. Show TextView with item type String.
     */
    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            //LinearLayOut Setup
            LinearLayout linearLayout= new LinearLayout(parent.getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            linearLayout.setFocusable(true);
            linearLayout.setFocusableInTouchMode(true);

            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 250));
            imageView.setId(R.id.imageID);
            linearLayout.addView(imageView);

            TextView view = new TextView(getActivity());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50));
            view.setId(R.id.textID);
            view.setBackgroundColor(getResources().getColor(R.color.fastlane_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
           // view.setPadding(0,5,5,5);
            linearLayout.addView(view);
            return new ViewHolder(linearLayout);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            LinearLayout linearLayout = (LinearLayout) viewHolder.view;
            ApplicationInfo applicationInfo = (ApplicationInfo) item;
            TextView textView = (TextView) linearLayout.findViewById(R.id.textID);
            textView.setText(applicationInfo.loadLabel(packageManager));
            ImageView imageView  = (ImageView) linearLayout.findViewById(R.id.imageID);
            imageView.setImageDrawable(applicationInfo.loadIcon(packageManager));
            Toast.makeText(getActivity(), item.toString(), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

        ArrayList<ApplicationInfo> apps = new ArrayList<>();

        for(ApplicationInfo info : list) {
            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    apps.add(info);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return apps;
    }


}
