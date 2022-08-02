package com.example.laser.ui.face.facelogin;


import static com.example.laser.ui.face.facelogin.FaceEngine.FACERECOGNIZER;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laser.R;
import com.example.laser.ui.MainActivity;
import com.example.laser.utils.BitmapUtils;
import com.example.laser.utils.GlideEngine;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;
import com.vondear.rxtool.RxLogTool;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class FaceLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FaceLoginActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facelogin);
        FaceEngine.init();
        setClickListener(R.id.bt_initfacedata);
        setClickListener(R.id.bt_facelogin);
        setClickListener(R.id.bt_facelogins);
    }

    private void setClickListener(int id) {
        findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_initfacedata:
                //录入人脸信息
                gotoActivity(FaceInitActivity.class);
                break;
            case R.id.bt_facelogin:
                //人脸登录
                gotoActivity(LoginActivity.class);
                break;
            case R.id.bt_facelogins:
                //导入数据
                getPicture();
                break;
        }
    }

    /**
     * 选择图片
     */
    private void getPicture() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .isPreviewImage(false)
                .isPreviewFullScreenMode(false)
                .setImageSpanCount(8)
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (int i = 0; i < result.size(); i++) {
                            Bitmap bitmap = BitmapFactory.decodeFile(result.get(i).getRealPath());

                            int finalI = i;
                            new Thread(() -> {
                                try {
                                    YuvImage image = new YuvImage(BitmapUtils.bmp2Yuv(bitmap), ImageFormat.NV21, bitmap.getWidth(), bitmap.getHeight(), null);
                                    if (image != null) {
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        image.compressToJpeg(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), 80, stream);
                                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                                        //纠正图像的旋转角度问题
                                        Matrix m = new Matrix();
                                        m.setRotate(0, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
                                        Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
                                        SeetaImageData RegistSeetaImageData = ConvertUtil.ConvertToSeetaImageData(bm);
                                        SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(RegistSeetaImageData);
                                        if (faceRects.length > 0) {
                                            //获取人脸区域（这里只有一个所以取0）
                                            SeetaRect faceRect = faceRects[0];
                                            SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(RegistSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
                                            FaceEngine.FACERECOGNIZER.Register(RegistSeetaImageData, seetaPoints);//将人脸注册到SeetaFace2数据库
                                            RxLogTool.e(TAG, "成功");
                                        } else {
                                            //如果检测不到人脸给予如下提示
                                            runOnUiThread(() -> {
                                                Toast.makeText(FaceLoginActivity.this, result.get(finalI).getFileName() + "：识别失败", Toast.LENGTH_SHORT).show();
                                            });

                                        }
                                    }
                                } catch (Exception ex) {

                                }

                            }
                            ).start();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });


    }

    private void gotoActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空人脸数据
        if (FACERECOGNIZER != null) {
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