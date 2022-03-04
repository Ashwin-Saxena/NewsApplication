package com.ashwinsaxena.newsapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;


public class DetailNewsFragment extends Fragment {

    public DetailNewsFragment() {
        // Required empty public constructor
    }

    public static DetailNewsFragment newInstance(ArrayList<DataModel> dataModelArrayList,
                                                 int curPos) {
        DetailNewsFragment detailNews = new DetailNewsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.TEMP_NEWS_LIST, dataModelArrayList);
        bundle.putInt(Constants.CUR_POS, curPos);
        detailNews.setArguments(bundle);
        return detailNews;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        ArrayList<DataModel> dataHolderList;
        int curPosition;
        if (bundle != null) {
            curPosition = bundle.getInt(Constants.CUR_POS);
            dataHolderList = bundle.getParcelableArrayList(Constants.TEMP_NEWS_LIST);
            ViewPager2 viewPager2 = view.findViewById(R.id.fragment_view_pager);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(dataHolderList, getContext());
            viewPager2.setAdapter(viewPagerAdapter);
            viewPager2.setCurrentItem(curPosition, false);
            //Not providing smooth transition because the target views can be at a far position
            // and transitioning from first view to that far one will be bad UX
            //In case of smooth scroll it should be done on secondary secondary thread and
            // then passed to message queue
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle bundle = getArguments();
        View view = getView();
        if (bundle != null && view != null) {
            ViewPager2 viewPager2 = view.findViewById(R.id.fragment_view_pager);
            int curPos = viewPager2.getCurrentItem();
            bundle.putInt(Constants.CUR_POS, curPos);
        }
    }
}
