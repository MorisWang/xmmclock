package xmm.elegant.org.xmmclock;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.anastr.flattimelib.CountDownTimerView;
import com.github.anastr.flattimelib.FlatClockView;
import com.github.anastr.flattimelib.HourGlassView;
import com.github.anastr.flattimelib.colors.Themes;
import com.github.anastr.flattimelib.intf.OnClockTick;
import com.github.anastr.flattimelib.intf.OnTimeFinish;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ClockActivity extends AppCompatActivity {

    private RelativeLayout mLayout;
    private FlatClockView mClockView;
    private HourGlassView mHourGlassView;
    private TextView mHourGlassTV;
    private TextView mYearTV;
    private TextView mDateTV;
    private TextView mCountTV;

    private static final int MSG_RESET_GLASS_TIME = 0x001;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_RESET_GLASS_TIME:
                    mHourGlassView.start(1000 * 10);
                    resetGlassText();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        initView();
    }

    private void initView() {
        mLayout = findViewById(R.id.clock_view_layout);
        mClockView = findViewById(R.id.clock_view);
        mHourGlassView = findViewById(R.id.hour_glass_view);
        mHourGlassTV = findViewById(R.id.hour_glass_text);
        mYearTV = findViewById(R.id.year_tv);
        mDateTV = findViewById(R.id.date_tv);
        mCountTV = findViewById(R.id.count_tv);
        setClockView();
        setTime();
        resetGlassText();
    }

    private void setTime(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mYearTV.setText(String.valueOf(year));

        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = getResources().getString(R.string.sunday);
                break;
            case 2:
                weekStr = getResources().getString(R.string.monday);
                break;
            case 3:
                weekStr = getResources().getString(R.string.tuesday);
                break;
            case 4:
                weekStr = getResources().getString(R.string.wednesday);
                break;
            case 5:
                weekStr = getResources().getString(R.string.thursday);
                break;
            case 6:
                weekStr = getResources().getString(R.string.friday);
                break;
            case 7:
                weekStr = getResources().getString(R.string.saturday);
                break;
        }
        mDateTV.setText(weekStr + "," + month + "月" + day + "日");

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = new Date();
            startDate = sdf.parse("2017-09-04");
            Date endDate = new Date();
            endDate.setTime(System.currentTimeMillis());
            int gapCount = getGapCount(startDate, endDate);
            String countStr = String.format(getResources().getString(R.string.count_time), gapCount);
            mCountTV.setText(countStr);
        } catch (Exception e) {

        }
    }

    //计算天数
    private int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));
    }

    private void setClockView() {
        if (isNight()) {
            mClockView.setTheme(Themes.DarkTheme);
            mClockView.setTimeToNow();
            mClockView.setOnClockTick(new OnClockTick() {
                @Override
                public void onTick() {

                }
            });
        } else {
            mLayout.setBackgroundColor(0xFFFFFFFF);
            mClockView.setTheme(Themes.LightTheme);
            mClockView.setHourIndicatorColor(0xFF1976d2);
            mClockView.setMinIndicatorColor(0xCC2196f3);
            mClockView.setSecIndicatorColor(0x7F03a9f4);
            mClockView.setBackgroundCircleColor(0x7Fbbdefb);
            mClockView.setBigMarkColor(0xFFFFFFFF);
            mClockView.setSmallMarkColor(0xFFFFFFFF);
            mClockView.setTimeToNow();
            mClockView.setOnClockTick(new OnClockTick() {
                @Override
                public void onTick() {

                }
            });

            mHourGlassView.start(1000 * 10);
            mHourGlassView.setOnTimeFinish(new OnTimeFinish() {
                @Override
                public void onFinish() {
                    mHourGlassView.flip();
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(MSG_RESET_GLASS_TIME, 1000);
                    }
                }
            });
        }
    }

    private boolean isNight() {
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        if (hour >= 22 || hour <= 7) {
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    private void resetGlassText(){
        int length = GreetingsConstant.GREETINGS_ARRAY.length;
        Random random = new Random();
        int randomNum = random.nextInt(length);
        String randomStr = GreetingsConstant.GREETINGS_ARRAY[randomNum];
        mHourGlassTV.setText(randomStr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_RESET_GLASS_TIME);
        }
    }
}
