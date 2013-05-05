package net.nctucs.lazchi.marco79423.accountbook;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener{

	private SectionsPagerAdapter _sectionsPagerAdapter;
	private ViewPager _viewPager;
	private Fragment[] _fragment; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_fragment = new Fragment[]{ new ExpenseFragment(), new StatisticsFragment() };
		_setViews();
		_setActionBar();
	}

	private void _setViews()
	{
		_viewPager = (ViewPager) findViewById(R.id.pager);	
	}
	
	private void _setActionBar()
	{
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		_sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		_viewPager.setAdapter(_sectionsPagerAdapter);
		_viewPager.setOnPageChangeListener(
			new ViewPager.SimpleOnPageChangeListener() 
			{
				@Override
				public void onPageSelected(int position) 
				{
					actionBar.setSelectedNavigationItem(position);
				}
			}
		);

		for (int i = 0; i < _sectionsPagerAdapter.getCount(); i++) 
		{
			actionBar.addTab(
				actionBar.newTab()
					.setText(_sectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this)
			);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
	{
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		if(tab.getPosition() == 1)
			((StatisticsFragment)_fragment[1]).update();
		_viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
	{
	
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) 
	{
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter 
	{
		public SectionsPagerAdapter(FragmentManager fm) 
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position) 
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			//Bundle args = new Bundle();
			
			return _fragment[position];
		}

		@Override
		public int getCount() 
		{
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			//Locale l = Locale.getDefault();
			switch (position) {
			case 0:	return getString(R.string.section_pay);
			case 1:	return getString(R.string.section_statistics);
			}
			return null;
		}
	}	
}
