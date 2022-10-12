//package com.example.laser.ui.face.facelogin;
//
//import android.annotation.SuppressLint;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
//import android.graphics.Matrix;
//import android.graphics.Rect;
//import android.graphics.YuvImage;
//import android.hardware.Camera;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.widget.Toast;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.laser.R;
//import com.seeta.sdk.SeetaImageData;
//import com.seeta.sdk.SeetaPointF;
//import com.seeta.sdk.SeetaRect;
//
//import java.io.ByteArrayOutputStream;
//
//public class FaceInitActivity extends AppCompatActivity {
//    private static final String TAG = "FaceInitActivity";
//    private FaceCameraView faceInitCameraView;
//    private boolean isScanning = false;
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Toast.makeText(FaceInitActivity.this, "信息录入成功", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    };
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login_face);
//        initView();
//    }
//    private void initView(){
//        faceInitCameraView = (FaceCameraView) findViewById(R.id.camera2);
//        faceInitCameraView.setPreviewCallback(new FaceCameraView.PreviewCallback() {
//            @Override
//            public void onPreview(final byte[] data, final Camera camera) {
//                if (FaceEngine.FACEDETECTOR != null && FaceEngine.FACERECOGNIZER != null && FaceEngine.POINTDETECTOR != null) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //识别中不处理其他帧数据
//                            if (!isScanning) {
//                                isScanning = true;
//                                try {
//                                    //获取Camera预览尺寸
//                                    Camera.Size size = camera.getParameters().getPreviewSize();
//                                    //将帧数据转为bitmap
//                                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
//                                    if (image != null) {
//                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                        image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
//                                        Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                                        //纠正图像的旋转角度问题
//                                        Matrix m = new Matrix();
//                                        m.setRotate(0, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
//                                        Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
//                                        SeetaImageData RegistSeetaImageData = ConvertUtil.ConvertToSeetaImageData(bm);
//                                        SeetaRect[] faceRects = FaceEngine.FACEDETECTOR.Detect(RegistSeetaImageData);
//                                        if (faceRects.length > 0) {
//                                            //获取人脸区域（这里只有一个所以取0）
//                                            SeetaRect faceRect = faceRects[0];
//                                            SeetaPointF[] seetaPoints = FaceEngine.POINTDETECTOR.Detect(RegistSeetaImageData, faceRect);//根据检测到的人脸进行特征点检测
//                                            FaceEngine.FACERECOGNIZER.Register(RegistSeetaImageData, seetaPoints);//将人脸注册到SeetaFace2数据库
//                                            handler.sendEmptyMessage(0);
//                                        } else {
//                                            //如果检测不到人脸给予如下提示
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
////                                                    Toast.makeText(FaceInitActivity.this, "请保持手机不要晃动", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                            isScanning = false;
//                                        }
//                                    }
//                                } catch (Exception ex) {
//                                    isScanning = false;
//                                }
//                            }
//                        }
//                    }
//                    ).start();
//                }
//            }
//        });
//    }
//}
