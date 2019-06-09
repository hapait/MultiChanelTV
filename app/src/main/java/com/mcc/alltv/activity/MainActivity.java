package com.mcc.alltv.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcc.alltv.Network.HttpParams;
import com.mcc.alltv.Network.RetrofitClient;
import com.mcc.alltv.R;
import com.mcc.alltv.adapter.ChannelAdapter;
import com.mcc.alltv.adapter.CustomSwipeAdapter;
import com.mcc.alltv.data.constants.AppConstant;
import com.mcc.alltv.data.sqllite.FavouriteDbController;
import com.mcc.alltv.listener.FavItemClickListener;
import com.mcc.alltv.listener.OnItemClickListener;
import com.mcc.alltv.model.CategoryList;
import com.mcc.alltv.model.Channel;
import com.mcc.alltv.model.ChannelList;
import com.mcc.alltv.utilities.ActivityUtils;
import com.mcc.alltv.utilities.AdUtils;
import com.mcc.alltv.utilities.AnalyticsUtils;
import com.mcc.alltv.utilities.ChannelUtils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    // variables
    private Context mContext;
    private Activity mActivity;
    private static final int TIMER_DURATION = 3000;
    private FavouriteDbController dbController;
    private ViewPager viewPager;
    private Channel channelData;

    public LayoutManager newsLinearLayoutManager, entertainmentLinearLayoutManager, sportsLinearLayoutManager, musicLinearLayoutManager, islamicLinearLayoutManager, cartoonLinearLayoutManager;

    //  all Adapter List
    private ChannelAdapter newsAdapter, entertainmentAdapter, sportsAdapter, musicAdapter, islamicAdapter, cartoonAdapter;
    //  all array List
    private ArrayList<Channel> newsChannelList, entertainmentChannelList, sportsChannelList, musicChannelList, islamicChannelList, cartoonChannelList;
    private ArrayList<Channel> liveTvList;
    private RecyclerView rvNews, rvEntertainmentTv, rvSportsTv, rvMusicTv, rvIslamicTv, rvCartoonTV;

    // Views
    private TextView tvNewsTitle, tvEntertainmentTitle, tvSportsTitle, tvMusicTitle, tvIslamicTitle, tvCartoonTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
        initView();
        initFunctionality();

        // load full screen ad
        AdUtils.getInstance(mContext).loadFullScreenAd(mActivity);
    }

    public void initVariable() {
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        liveTvList = new ArrayList<>();
        newsChannelList = new ArrayList<>();
        entertainmentChannelList = new ArrayList<>();
        sportsChannelList = new ArrayList<>();
        musicChannelList = new ArrayList<>();
        islamicChannelList = new ArrayList<>();
        cartoonChannelList = new ArrayList<>();
        dbController = new FavouriteDbController(mContext);

        // analytics event tiger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("ChannelList Activity");

    }
    /* All view */
    public void initView() {
        setContentView(R.layout.activity_main);

        /* Load navigation Drawer and Toolbar */
        initToolbar();
        initDrawer();
        initLoader();

        // News Tv views
        RelativeLayout lytNewsList = findViewById(R.id.lytNewsTvList);
        rvNews = lytNewsList.findViewById(R.id.homeRecyclerView);
        tvNewsTitle = lytNewsList.findViewById(R.id.tvAllChannelListTitle);

        // Entertainment tv views
        RelativeLayout lytEntertainmentTvList = findViewById(R.id.lytEntertainmentTvList);
        rvEntertainmentTv = lytEntertainmentTvList.findViewById(R.id.homeRecyclerView);
        tvEntertainmentTitle = lytEntertainmentTvList.findViewById(R.id.tvAllChannelListTitle);

        // Sports Tv views
        RelativeLayout lytSportsTvList = findViewById(R.id.lytSportsTvList);
        rvSportsTv = lytSportsTvList.findViewById(R.id.homeRecyclerView);
        tvSportsTitle = lytSportsTvList.findViewById(R.id.tvAllChannelListTitle);


        // Music Tv views
        RelativeLayout lytMusicTvList = findViewById(R.id.lytMusicTvList);
        rvMusicTv = lytMusicTvList.findViewById(R.id.homeRecyclerView);
        tvMusicTitle = lytMusicTvList.findViewById(R.id.tvAllChannelListTitle);

        // Islamic Tv views

        RelativeLayout lytIslamicTvList = findViewById(R.id.lytIslamicTvList);
        rvIslamicTv = lytIslamicTvList.findViewById(R.id.homeRecyclerView);
        tvIslamicTitle = lytIslamicTvList.findViewById(R.id.tvAllChannelListTitle);

        // Cartoon Tv views
        RelativeLayout lyCartonTvList = findViewById(R.id.lyCartonTvList);
        rvCartoonTV = lyCartonTvList.findViewById(R.id.homeRecyclerView);
        tvCartoonTitle = lyCartonTvList.findViewById(R.id.tvAllChannelListTitle);

        // Load tech News tv as sample category
        newsLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvNews.setLayoutManager(newsLinearLayoutManager);
        rvNews.setItemAnimator(new DefaultItemAnimator());

        // Load tech entertainment ChannelList tv as sample List
        entertainmentLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvEntertainmentTv.setLayoutManager(entertainmentLinearLayoutManager);
        rvEntertainmentTv.setItemAnimator(new DefaultItemAnimator());


        // Load tech sportsChannelList tv as sample category
        sportsLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvSportsTv.setLayoutManager(sportsLinearLayoutManager);
        rvSportsTv.setItemAnimator(new DefaultItemAnimator());

        // Load tech music tv as sample category
        musicLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMusicTv.setLayoutManager(musicLinearLayoutManager);
        rvMusicTv.setItemAnimator(new DefaultItemAnimator());

        // Load tech islamic tv as sample category
        islamicLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvIslamicTv.setLayoutManager(islamicLinearLayoutManager);
        rvIslamicTv.setItemAnimator(new DefaultItemAnimator());

        // Load tech cartoon tv as sample category
        cartoonLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCartoonTV.setLayoutManager(cartoonLinearLayoutManager);
        rvCartoonTV.setItemAnimator(new DefaultItemAnimator());
    }

    /* All functionality */
    public void initFunctionality() {
        loadCategories();
    }

    private void loadCategories(){

        RetrofitClient.getClient().getCategoryList(HttpParams.SHEET_ID, HttpParams.SHEET_NAME_CATEGORY).enqueue(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {
                AppConstant.ALL_CATEGORY_LIST.clear();
                AppConstant.ALL_CATEGORY_LIST.addAll(response.body().getCategory());
                loadChannelList();
            }

            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                Log.d("TimeTesting", "Second Req DetailsDataNotFound");
            }
        });
    }
    /*Load data live channel List */
    private void loadChannelList() {

        AppConstant.ALL_CHANNEL_LIST.clear();
        RetrofitClient.getClient().getChannelList(HttpParams.SHEET_ID, HttpParams.SHEET_NAME).enqueue(new Callback<ChannelList>() {
            @Override
            public void onResponse(Call<ChannelList> call, Response<ChannelList> response) {
                ArrayList<Channel> myData = new ArrayList<>();
                myData.addAll(response.body().getChannel());
                AppConstant.ALL_CHANNEL_LIST.addAll(myData);

                for (int i = 0; i <myData.size(); i++) {
                    channelData = myData.get(i);
                    if (channelData.getIsLive() == 1) {
                        liveTvList.add(channelData);
                    }
                }
                loadDataForAllCategory();
                hideLoader();
            }

            @Override
            public void onFailure(Call<ChannelList> call, Throwable t) {
                Log.d("TimeTesting", "Second Req DetailsDataNotFound");
            }
        });
    }

    public void loadDataForAllCategory(){
        loadViewPager();
        loadNewsList();
        loadEntertainmentList();
        loadSportstList();
        loadMusicList();
        loadIslamicList();
        loadCartoonList();
        initListener();
    }

    /*Load Json data news List */
    private void loadNewsList() {
        if (!newsChannelList.isEmpty()) {
            newsChannelList.clear();
        }
        newsChannelList.addAll(ChannelUtils.getTvChannelList( AppConstant.ALL_CHANNEL_LIST,AppConstant.CATEGORY_NEWS_ID));
        tvNewsTitle.setText(AppConstant.ALL_CATEGORY_LIST.get(AppConstant.CATEGORY_NEWS_ID).getCategoryName());
        newsAdapter = new ChannelAdapter(this, newsChannelList);
        rvNews.setAdapter(newsAdapter);
    }

       /*Load Json data Entertainment List */
    private void loadEntertainmentList() {
        if (!entertainmentChannelList.isEmpty()) {
            entertainmentChannelList.clear();
        }
        entertainmentChannelList.addAll(ChannelUtils.getTvChannelList(AppConstant.ALL_CHANNEL_LIST, AppConstant.CATEGORY_ENTERTAINMENT_ID));
        tvEntertainmentTitle.setText(AppConstant.ALL_CATEGORY_LIST.get(AppConstant.CATEGORY_ENTERTAINMENT_ID).getCategoryName());
        entertainmentAdapter = new ChannelAdapter(this, entertainmentChannelList);
        rvEntertainmentTv.setAdapter(entertainmentAdapter);
    }

   /*Load Json data Sports  List */
    private void loadSportstList() {
        if (!sportsChannelList.isEmpty()) {
            sportsChannelList.clear();
        }
        sportsChannelList.addAll(ChannelUtils.getTvChannelList(AppConstant.ALL_CHANNEL_LIST, AppConstant.CATEGORY_SPORTS_ID));
        tvSportsTitle.setText(AppConstant.ALL_CATEGORY_LIST.get(AppConstant.CATEGORY_SPORTS_ID ).getCategoryName());
        sportsAdapter = new ChannelAdapter(this, sportsChannelList);
        rvSportsTv.setAdapter(sportsAdapter);
    }

   /*Load Json data Music List */
    private void loadMusicList() {
        if (!musicChannelList.isEmpty()) {
            musicChannelList.clear();
        }
        musicChannelList.addAll(ChannelUtils.getTvChannelList( AppConstant.ALL_CHANNEL_LIST,AppConstant.CATEGORY_MUSIC_ID));
        tvMusicTitle.setText(AppConstant.ALL_CATEGORY_LIST.get(AppConstant.CATEGORY_MUSIC_ID).getCategoryName());
        musicAdapter = new ChannelAdapter(this, musicChannelList);
        rvMusicTv.setAdapter(musicAdapter);
    }

       /*Load Json data Islamic List */
    private void loadIslamicList() {
        if (!islamicChannelList.isEmpty()) {
            islamicChannelList.clear();
        }
        islamicChannelList.addAll(ChannelUtils.getTvChannelList(AppConstant.ALL_CHANNEL_LIST, AppConstant.CATEGORY_ISLAMIC_ID));
        tvIslamicTitle.setText(AppConstant.ALL_CATEGORY_LIST.get(AppConstant.CATEGORY_ISLAMIC_ID).getCategoryName());
        islamicAdapter = new ChannelAdapter(this, islamicChannelList);
        rvIslamicTv.setAdapter(islamicAdapter);
    }

     /*Load Json data Cartoon List */
    private void loadCartoonList() {
        if (!cartoonChannelList.isEmpty()) {
            cartoonChannelList.clear();
        }
        cartoonChannelList.addAll(ChannelUtils.getTvChannelList(AppConstant.ALL_CHANNEL_LIST, AppConstant.CATEGORY_CARTOON_ID));
        tvCartoonTitle.setText(AppConstant.ALL_CATEGORY_LIST.get(AppConstant.CATEGORY_CARTOON_ID).getCategoryName());
        cartoonAdapter = new ChannelAdapter(this, cartoonChannelList);
        rvCartoonTV.setAdapter(cartoonAdapter);
    }

    /* All Listener*/
    public void initListener() {

        //  News item OnClickListener
        newsAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (newsChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(newsChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, newsChannelList.get(position), newsChannelList);
                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, newsChannelList.get(position), newsChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {

                channelData = newsChannelList.get(position);
                if (dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {
                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    newsAdapter.notifyDataSetChanged();
                } else {
                    dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    newsAdapter.notifyDataSetChanged();
                }
            }

        });
        // Entertainment item OnClickListener
        entertainmentAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (entertainmentChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(entertainmentChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, entertainmentChannelList.get(position), entertainmentChannelList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, entertainmentChannelList.get(position), entertainmentChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                channelData = entertainmentChannelList.get(position);
                if (dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {

                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    entertainmentAdapter.notifyDataSetChanged();
                } else {
                    dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    entertainmentAdapter.notifyDataSetChanged();
                }
            }
        });
        //  Sports item OnClickListener
        sportsAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (sportsChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(sportsChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, sportsChannelList.get(position), sportsChannelList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, sportsChannelList.get(position), sportsChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                channelData = sportsChannelList.get(position);
                if (dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {

                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    sportsAdapter.notifyDataSetChanged();
                } else {
                    dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    sportsAdapter.notifyDataSetChanged();
                }

            }
        });
        //  Music item OnClickListener
        musicAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (musicChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(musicChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, musicChannelList.get(position), musicChannelList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, musicChannelList.get(position), musicChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                channelData = musicChannelList.get(position);
                if (dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {

                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    musicAdapter.notifyDataSetChanged();
                } else {
                    dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    musicAdapter.notifyDataSetChanged();
                }

            }
        });
        //  Islamic item OnClickListener
        islamicAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (islamicChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(islamicChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, islamicChannelList.get(position), islamicChannelList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, islamicChannelList.get(position), islamicChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                channelData = islamicChannelList.get(position);
                if (dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {

                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    islamicAdapter.notifyDataSetChanged();
                } else {
                    dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    islamicAdapter.notifyDataSetChanged();
                }
            }
        });
        //  Cartoon item OnClickListener
        cartoonAdapter.setItemClickListener(new FavItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {
                if (cartoonChannelList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(cartoonChannelList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, cartoonChannelList.get(position), cartoonChannelList);

                    } else {
                        ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, cartoonChannelList.get(position), cartoonChannelList);
                    }
                }
            }

            @Override
            public void onFavIconListener(View view, int position) {
                channelData = cartoonChannelList.get(position);
                if (dbController.isAlreadyFavourite(String.valueOf(channelData.getChannelId()))) {

                    dbController.deleteFavItem(String.valueOf(channelData.getChannelId()));
                    cartoonAdapter.notifyDataSetChanged();
                } else {
                    dbController.insertData(String.valueOf(channelData.getChannelId()), channelData.getChannelLogoUrl(), channelData.getChannelName(), channelData.getStreamUrl(), channelData.getIsLive());
                    cartoonAdapter.notifyDataSetChanged();
                }

            }
        });
    }


    /*Load view pager */
    private void loadViewPager() {
        final CustomSwipeAdapter swipeAdapter = new CustomSwipeAdapter(mContext, liveTvList);
        viewPager = findViewById(R.id.vpImageSlider);
        CircleIndicator indicator = findViewById(R.id.sliderIndicator);
        indicator.setViewPager(viewPager);
        viewPager.setAdapter(swipeAdapter);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                int setPosition = viewPager.getCurrentItem() + 1;
                if (setPosition == liveTvList.size()) {
                    setPosition = AppConstant.VALUE_ZERO;
                }
                viewPager.setCurrentItem(setPosition, true);
                swipeAdapter.notifyDataSetChanged();
            }
        };

        //  Auto animated timer
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, TIMER_DURATION, TIMER_DURATION);

        swipeAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemListener(View view, int position) {

                if (liveTvList.get(position).getStreamUrl() != null) {
                    if (isYouTubeUrl(liveTvList.get(position).getStreamUrl())) {
                        ActivityUtils.getInstance().invokeYoutubePlayerViewActivity(mActivity, liveTvList.get(position), liveTvList);
                    } else {
                         ActivityUtils.getInstance().invokeExoPlayerViewActivity(mActivity, liveTvList.get(position), liveTvList);
                    }
                }

            }
        });
    }

    private boolean isYouTubeUrl(String url) {
        if (url.contains(AppConstant.HTTP)) {
            return false;
        } else {
            return true;
        }
    }

    public void onResume() {
        super.onResume();
        if (!AppConstant.ALL_CHANNEL_LIST.isEmpty()) {
            loadDataForAllCategory();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
