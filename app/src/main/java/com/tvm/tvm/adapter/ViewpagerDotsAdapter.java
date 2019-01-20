package com.tvm.tvm.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

public class ViewpagerDotsAdapter extends PagerAdapter {
	
	private List<ImageView> imageViews = new ArrayList<ImageView>();

	public ViewpagerDotsAdapter(Context context,List<ImageView> imageViews) {
		// TODO Auto-generated constructor stub
		this.imageViews = imageViews;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	
	@Override
	public Object instantiateItem(View v, int position) {
		// TODO Auto-generated method stub
		View view = imageViews.get(position);
		ViewPager viewPager = (ViewPager) v;
		viewPager.addView(view);
		return imageViews.get(position);
	}
 
	@Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View)object);
    }
	

}
