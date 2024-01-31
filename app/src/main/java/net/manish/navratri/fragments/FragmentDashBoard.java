package net.manish.navratri.fragments;

import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.manish.navratri.R;

import java.util.Calendar;

import cn.iwgang.countdownview.CountdownView;

public class FragmentDashBoard extends Fragment {

	private CountdownView mCvCountdownView;
	private RelativeLayout rl_days;
	private TextView textView_days, textView_comma;
	private MediaPlayer mediaPlayer;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
		mediaPlayer = MediaPlayer.create(requireActivity(), R.raw.krishna);
		mediaPlayer.start();
		mCvCountdownView = rootView.findViewById(R.id.countDownView);

		rl_days = rootView.findViewById(R.id.rl_days);
		textView_days = rootView.findViewById(R.id.textView_days);
		textView_comma = rootView.findViewById(R.id.textView_comma);
		textView_comma.setTypeface(null, Typeface.BOLD);

		textView_days.setTypeface(textView_days.getTypeface(), Typeface.BOLD);

		Calendar b = Calendar.getInstance();
		b.set(Calendar.DATE, 24);
		b.set(Calendar.MONTH, Calendar.AUGUST);
		b.set(Calendar.YEAR, b.get(Calendar.YEAR));
		b.set(Calendar.HOUR_OF_DAY, 0);
		b.set(Calendar.MINUTE, 0);
		b.set(Calendar.SECOND, 0);

		if(b.getTimeInMillis() < System.currentTimeMillis()) {
			b.set(Calendar.YEAR, b.get(Calendar.YEAR)+1);
		}

		if(Calendar.getInstance().get(Calendar.DATE) == 25 && b.get(Calendar.MONTH) == Calendar.DECEMBER) {
			mCvCountdownView.setVisibility(View.GONE);
			textView_comma.setVisibility(View.GONE);
			rl_days.setVisibility(View.GONE);
		} else {
			mCvCountdownView.start(b.getTimeInMillis() - System.currentTimeMillis());
			mCvCountdownView.setOnCountdownIntervalListener(1000, new CountdownView.OnCountdownIntervalListener() {
				@Override
				public void onInterval(CountdownView cv, long remainTime) {
					textView_days.setText(String.valueOf(mCvCountdownView.getDay()));
				}
			});
		}
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		if (mediaPlayer.isPlaying())
		{
			mediaPlayer.stop();
		}
		super.onDestroyView();
	}


	@Override
	public void onPause()
	{
		super.onPause();
		if (mediaPlayer.isPlaying())
		{
			mediaPlayer.stop();
		}

	}
}