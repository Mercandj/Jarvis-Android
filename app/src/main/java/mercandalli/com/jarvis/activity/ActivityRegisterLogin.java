/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import mercandalli.com.jarvis.R;
import mercandalli.com.jarvis.fragment.InscriptionFragment;
import mercandalli.com.jarvis.fragment.LoginFragment;
import mercandalli.com.jarvis.view.PagerSlidingTabStrip;

public class ActivityRegisterLogin extends Application {

    private final int NB_FRAGMENT = 2;
    private int INIT_FRAGMENT = 1;
    public Fragment listFragment[] = new Fragment[NB_FRAGMENT];
    private ViewPager mViewPager;
    private RegisterLoginPagerAdapter mPagerAdapter;
    private PagerSlidingTabStrip tabs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.view_register_login);
		super.onCreate(savedInstanceState);

        mPagerAdapter = new RegisterLoginPagerAdapter(this.getFragmentManager(), this);

        tabs = (PagerSlidingTabStrip) this.findViewById(R.id.tabs);
        mViewPager = (ViewPager) this.findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                ActivityRegisterLogin.this.invalidateOptionsMenu();
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }
            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        mViewPager.setOffscreenPageLimit(this.NB_FRAGMENT - 1);

        if(this.getConfig().getUserUsername()==null || this.getConfig().getUserPassword()==null)
            this.INIT_FRAGMENT = 0;
        else if(this.getConfig().getUserUsername().equals("") || this.getConfig().getUserPassword().equals(""))
            this.INIT_FRAGMENT = 0;

        mViewPager.setCurrentItem(this.INIT_FRAGMENT);

        tabs.setViewPager(mViewPager);
        tabs.setIndicatorColor(getResources().getColor(R.color.white));

        if(this.getConfig().isAutoConncetion() && this.getConfig().getUrlServer()!=null && this.getConfig().getUserUsername()!=null && this.getConfig().getUserPassword()!=null)
            if(!this.getConfig().getUserUsername().equals("") && !this.getConfig().getUserPassword().equals("") && this.getConfig().getUserId() != -1)
        	    connectionSucceed();

        ((ImageView) this.findViewById(R.id.signin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listFragment[getCurrentFragmentIndex()] != null) {
                    if(listFragment[getCurrentFragmentIndex()] instanceof InscriptionFragment) {
                        ((InscriptionFragment)listFragment[getCurrentFragmentIndex()]).clickSignIn();
                    }
                    else if(listFragment[getCurrentFragmentIndex()] instanceof LoginFragment) {
                        ((LoginFragment)listFragment[getCurrentFragmentIndex()]).clickSignIn();
                    }
                }
            }
        });
	}
	
	public void connectionSucceed() {
		Intent intent = new Intent(this, ActivityMain.class);
		this.startActivity(intent);
		this.overridePendingTransition(R.anim.left_in, R.anim.left_out);
		this.finish();
	}

	@Override
	public void updateAdapters() {
		
	}

	@Override
	public void refreshAdapters() {
		
	}

    public int getCurrentFragmentIndex() {
        return mViewPager.getCurrentItem();
    }

    public class RegisterLoginPagerAdapter extends FragmentPagerAdapter {
        Application app;

        public RegisterLoginPagerAdapter(FragmentManager fm, Application app) {
            super(fm);
            this.app = app;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            switch(i) {
                case 0:		fragment = new InscriptionFragment();  	break;
                case 1:		fragment = new LoginFragment(); 	break;
                default:	fragment = new InscriptionFragment();	break;
            }
            listFragment[i] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            return NB_FRAGMENT;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            String title = "null";
            switch(i) {
                case 0:		title = "REGISTER";		break;
                case 1:		title = "LOGIN";		break;
            }
            return title;
        }
    }
}
