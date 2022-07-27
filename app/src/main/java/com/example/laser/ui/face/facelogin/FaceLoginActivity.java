package com.example.laser.ui.face.facelogin;


import static com.example.laser.ui.face.facelogin.FaceEngine.FACERECOGNIZER;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laser.R;


public class FaceLoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facelogin);
        FaceEngine.init();
        setClickListener(R.id.bt_initfacedata);
        setClickListener(R.id.bt_facelogin);
    }

    private void setClickListener(int id){
        findViewById(id).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_initfacedata:
                //录入人脸信息
                gotoActivity(FaceInitActivity.class);
                break;
            case R.id.bt_facelogin:
                //人脸登录
                gotoActivity(LoginActivity.class);
                break;
        }
    }
    private void gotoActivity(Class clazz){
        startActivity(new Intent(this,clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空人脸数据
        if(FACERECOGNIZER!=null){
            FACERECOGNIZER.Clear();
//            FACERECOGNIZER.dispose();
        }
        //用了一个引擎后就不需要释放了
//        if(POINTDETECTOR!=null){
//            POINTDETECTOR.dispose();
//        }
//        if(FACEDETECTOR!=null){
//            FACEDETECTOR.dispose();
//        }
    }
}
