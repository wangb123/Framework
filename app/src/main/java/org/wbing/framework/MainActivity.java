package org.wbing.framework;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mogujie.security.EncryptUtils;
import com.mogujie.security.MGSoTool;

import org.wbing.base.utils.ApplicationContextGetter;
import org.wbing.base.utils.EncryptUtil;
import org.wbing.base.utils.LogUtils;
import org.wbing.base.utils.ScreenTools;
import org.wbing.base.view.PagerSlidingTabStrip;
import org.wbing.base.view.refreshlayout.RefreshLayout;
import org.wbing.base.view.refreshlayout.impl.PullScrollableLayout;

public class MainActivity extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener {
    PullScrollableLayout pullScrollableLayout;
    PagerSlidingTabStrip mTabView;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationContextGetter.instance().setApplicationContext(getApplication());

        pullScrollableLayout = (PullScrollableLayout) findViewById(R.id.sl_layout);
        mTabView = (PagerSlidingTabStrip)findViewById(R.id.tab);
        mViewPager = (ViewPager) findViewById(R.id.content);
        pullScrollableLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onPullDown(float y) {
                Log.e("onPullDown ", String.valueOf(y));
            }

            @Override
            public void onRefresh() {
                getFrag(mViewPager.getCurrentItem()).refreshData();
//                pullScrollableLayout.showEmptyView();

            }

            @Override
            public void onRefreshOver(Object obj) {
//                Log.e("onRefreshOver ",String.valueOf(obj.getClass().getName()));
            }
        });

        mTabView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        mTabView.setBackgroundColor(Color.WHITE);
        mTabView.setTextColor(Color.parseColor("#BBBBBB"));
        mTabView.setSelectedTabTextColor(Color.parseColor("#1D1D1F"));
        mTabView.setIndicatorColor(Color.parseColor("#1D1D1F"));
        mTabView.setTextSize(ScreenTools.instance().dip2px(16f));
        mTabView.setTabPaddingLeftRight(10);
        mTabView.setIndicatorHeight(ScreenTools.instance().dip2px(3));
        mTabView.setIndicatorWidth(ScreenTools.instance().dip2px(32));
        mTabView.setShouldExpand(true);
        mTabView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                IPageListFragment frag = getFrag(position);
                if (frag != null && pullScrollableLayout != null) {
                    pullScrollableLayout.getRefreshView().getHelper().setCurrentScrollableContainer(frag.getListView());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return BlankFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "title"+position;
            }
        });
        mTabView.setViewPager(mViewPager);
        mTabView.getOnPageChangeListener().onPageSelected(mViewPager.getCurrentItem());



//
//        pullScrollableLayout.getRefreshView().setExtralY(0);
//        pullScrollableLayout.getRefreshView().getHelper().setCurrentScrollableContainer(findViewById(R.id.content));
//
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content);
//
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(new RecyclerView.Adapter<Holder>() {
//
//            @Override
//            public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//                return new Holder(new TextView(parent.getContext()));
//            }
//
//            @Override
//            public void onBindViewHolder(Holder holder, int position) {
//                holder.fill(position);
//                Log.e("tag", position + "");
//            }
//
//            @Override
//            public int getItemCount() {
//                return 1000;
//            }
//        });
    }


    public IPageListFragment getFrag(int position) {
        IPageListFragment frag = (IPageListFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(mViewPager.getId(), position));
        return frag;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public void onFragmentRefreshComplete(Intent intent) {
        pullScrollableLayout.refreshOver(null);
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView textView;

        public Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            itemView.setPadding(20, 20, 20, 20);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void fill(int pos) {
            textView.setText("position: " + pos);
        }
    }

}
