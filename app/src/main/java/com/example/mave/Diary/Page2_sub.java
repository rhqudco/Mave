package com.example.mave.Diary;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle; import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mave.PreferenceManager;
import com.example.mave.R;
import com.example.mave.repository.MemberRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalTime;

import static com.example.mave.Diary.Page2_sub_answer.count;

public class Page2_sub extends AppCompatActivity {

    private ListView listView;
    private ListViewAdapterForSub2 adapter;
    private Context mContext;

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_page2_sub);

        mContext = this;

        adapter = new ListViewAdapterForSub2(Page2_sub.this);
        listView.setAdapter(adapter);

//        adapter.addItem(); << 아답타에 아이템 넣는
//        adapter.notifyDataSetChanged(); << 아답타 새로고침 해주는 기능

        TextView textview = (TextView)findViewById(R.id.todayQuestion); // 오늘의 질문 버튼
        Button notibutton = (Button)findViewById(R.id.notifi);

        LocalTime settime = MemberRepository.getInstance().getQuestionTime(); // 설정시간
        LocalTime nowtime = LocalTime.now(); // 현재시간

        if(nowtime.isAfter(settime)) {
            NotificationSomethings(); // < 지우고 질문 받아오는 코드 넣으면 될 거 같슴다.
        }
        else
        {
            Log.i("123", String.valueOf(nowtime.isBefore(settime))); // 아니면 말고 코드 넣으면 될 거 같습니다.
        }

        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Page2_sub_answer.class);
                startActivity(intent);
            }
        });
      
        notibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationSomethings();
            }
        });

        FloatingActionButton FloatingButton = (FloatingActionButton)findViewById(R.id.customquestion);
        FloatingButton.setOnClickListener(new View.OnClickListener() { //플로팅버튼 눌렀을 때 이벤트
            @Override
            public void onClick(View v) {
                Create_Question dia = new Create_Question(Page2_sub.this);
                // 커스텀 다이얼로그 배경 투명
                dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dia.setDialogListener(new Create_Question.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String custom_question) {
                        Toast.makeText(getApplication(), custom_question, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNegativeClicked() {
                        //취소버튼 눌렀을 경우 구현될 코드 작성
                    }
                });
                dia.show();
            }
        });

    }

    public void NotificationSomethings() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, Page2_sub.class);
        /*notificationIntent.putExtra("notificationId", count); //전달할 값
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;*/
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.mave_logo)) //BitMap 이미지 요구
                .setContentTitle("다이어리에 질문이 도착했습니다!")
                .setContentText("알림바를 눌러 확인하고 답장 해보세요!")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.mave_logo2); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);
            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남
        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }
}


