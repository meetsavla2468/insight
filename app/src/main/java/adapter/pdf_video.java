package adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.education.pdf;
import com.example.education.video;

public class pdf_video extends FragmentStatePagerAdapter {
    String course;

    public pdf_video(@NonNull FragmentManager fm, String course) {
        super(fm);
        this.course = course;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("course", course);
        if (i == 0) {
            fragment = new pdf();
            fragment.setArguments(bundle);
            return fragment;
        } else {
            fragment = new video();
            fragment.setArguments(bundle);
            return fragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "PDF Section";
            default:
                return "VIDEOS";
        }
    }
}

