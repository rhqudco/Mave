package com.example.mave.Diary;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mave.CreateRetrofit;
import com.example.mave.Dto.groupDto.CreateGroupRequest;
import com.example.mave.Dto.groupDto.CreateGroupResponse;
import com.example.mave.R;
import com.example.mave.repository.MemberRepository;
import com.example.mave.service.GroupRetrofitService;
import java.time.LocalTime;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Create_Diary extends Dialog implements View.OnClickListener {

    static final String TAG = "Mave";
    private Button positiveButton;
    private Button negativeButton;
    private EditText editName;
    private Context context;
    private CustomDialogListener customDialogListener;
    private String diaryName;
    TimePickerDialog timePickerDialog;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public Create_Diary(Context context) {
        super(context);
        this.context = context;
    }

    interface CustomDialogListener{
        void onPositiveClicked(String diary_name);
        void onNegativeClicked();
    }
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }
@Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_diary);
//init
        positiveButton = (Button)findViewById(R.id.btnPositive);
        negativeButton = (Button)findViewById(R.id.btnNegative);
        editName = (EditText)findViewById(R.id.editName);

        //?????? ?????? ????????? ??????
        positiveButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR);
        int mMinute = c.get(Calendar.MINUTE);
        switch (v.getId()){
            case R.id.btnPositive: //?????? ????????? ????????? ???
                //????????? ????????? EidtText?????? ????????? ?????? ??????
                diaryName = editName.getText().toString();
                //?????????????????? ????????? ???????????? ????????? ????????? ????????? Activity??? ??????

                customDialogListener.onPositiveClicked(diaryName);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override

                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    LocalTime questionTime = LocalTime.of(hourOfDay, minute);
                                    MemberRepository instance = MemberRepository.getInstance();
                                    instance.setQuestionTime(questionTime);
                                }
                                //Toast.makeText(getContext(), hourOfDay + "???" + minute + "???", Toast.LENGTH_SHORT).show();
                            }
                        },mHour, mMinute, false);
                timePickerDialog.show();
                dismiss();
                break;
            case R.id.btnNegative: //?????? ????????? ????????? ???
                cancel();
                break;
        }
        // ?????? ?????? api ??????!!
        GroupRetrofitService groupRetrofitService = CreateRetrofit.createRetrofit().create(GroupRetrofitService.class);
        String userId = MemberRepository.getInstance().getUserId();
        CreateGroupRequest request = new CreateGroupRequest(userId,diaryName);
        Call<CreateGroupResponse> call = groupRetrofitService.createGroup(request);

        call.enqueue(new Callback<CreateGroupResponse>() {
            @Override
            public void onResponse(Call<CreateGroupResponse> call, Response<CreateGroupResponse> response) {
                if (response.isSuccessful()) {
                    CreateGroupResponse body = response.body();
                    Log.d(TAG, "response ??????!!");
//                            textTest.setText(body.getUserId().toString());
                } else {
                    Log.d(TAG, "response ?????? ??????");

                }
            }

            @Override
            public void onFailure(Call<CreateGroupResponse> call, Throwable t) {
                Log.d(TAG, "onFailure => " + t.getMessage());
            }
        });
    }
}