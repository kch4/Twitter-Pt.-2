package fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by kathu228 on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Home", "Mentions"};
    private Context context;
    public HomeTimelineFragment homeTimelineFragment;
    public MentionsTimelineFragment mentionsTimelineFragment;
    public TweetsPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
        homeTimelineFragment = new HomeTimelineFragment();
        mentionsTimelineFragment = new MentionsTimelineFragment();
    }
    // return the total # of fragment

    @Override
    public int getCount() {
        return 2;
    }

    // return the fragment to use depending on the position

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return homeTimelineFragment;
        } else if (position == 1){
            return mentionsTimelineFragment;
        } else {
            return null;
        }
    }

    // return title

    @Override
    public CharSequence getPageTitle(int position) {
        // generate title based on item position
//        return tabTitles[position];
        return null;

    }
}
