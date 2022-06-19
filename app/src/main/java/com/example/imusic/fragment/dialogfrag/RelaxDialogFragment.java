package com.example.imusic.fragment.dialogfrag;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.example.imusic.R;
import com.example.imusic.adapter.SplashPagerAdapter;
import com.example.imusic.util.DialogUtil;
import com.example.imusic.util.SpUtil;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/5/30 13:27
 *
 * @author Stran
 */
public class RelaxDialogFragment
        extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LinearLayout.inflate(getActivity(), R.layout.relax_dialog_fragment, null);
        initView(view);
        return DialogUtil.getDialogFag(getActivity(), view);
    }

    private void initView(View view) {
        ViewPager girlsViewPager = view.findViewById(R.id.vp_girls);
        SplashPagerAdapter splashPagerAdapter = new SplashPagerAdapter(SpUtil.getPicUrlFlag(getActivity(),false));
        girlsViewPager.setAdapter(splashPagerAdapter);
//        splashPagerAdapter.setZoomViewClickListener(this::dismiss);
    }


    public static RelaxDialogFragment newInstance() {
        return new RelaxDialogFragment();
    }


}
