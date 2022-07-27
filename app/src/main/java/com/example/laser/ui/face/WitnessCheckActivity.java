package com.example.laser.ui.face;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.example.laser.R;
import com.seeta.sdk.FaceDetector2;
import com.seeta.sdk.FaceRecognizer2;
import com.seeta.sdk.PointDetector2;
import com.seeta.sdk.SeetaImageData;
import com.seeta.sdk.SeetaPointF;
import com.seeta.sdk.SeetaRect;

import java.io.File;
import java.nio.ByteBuffer;

public class WitnessCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_witness_check);


        String path1="/sdcard/DCIM/Camera/IMG_20220726_155559.jpg";
        String path2="/sdcard/DCIM/Camera/IMG_20220727_092030.jpg";
        File file = new File(path1);
        if (file.exists()){
            Log.e("TAG", "file：存在");
        }else {
            Log.e("TAG", "file：no");
        }
        File file2 = new File(path2);

        SeetaImageData image =ConvertToSeetaImageData(file.getPath());
        SeetaImageData image2 =ConvertToSeetaImageData(file2.getPath());

        // 初始化人脸检测器2
        FaceDetector2 faceDetector = new FaceDetector2(Environment.getExternalStorageDirectory()+ File.separator+"seetaface"+File.separator+"SeetaFaceDetector2.0.ats");
        //使用“Detect”接口检测图像中的人脸位置
        SeetaRect[] seetaRects = faceDetector.Detect(image);//make sure SeetaImageData.data in BGR format

        //初始化PointDetector2，使用“Detect”接口检测目标人脸的landmarks
        //初始化PointDetector2
        PointDetector2 pointDetector = new PointDetector2(path2);


        // 使用“Detect”接口检测目标人脸的地标
        SeetaPointF[] landmarks = pointDetector.Detect(image, seetaRects[0]);//if seetaRects not empty，seetaRects[0] is maximum face

        //初始化FaceRecognizer2，进行人脸登记、人脸识别或人脸比对
        // 初始化人脸识别器2
        FaceRecognizer2 faceRecognizer = new FaceRecognizer2(path2);

        //人脸登记
        int registedFaceIndex = faceRecognizer.Register(image, landmarks);

        //人脸识别
        float[] similarity = {1};//save the most similar face similarity value
        int targetIndex = faceRecognizer.Recognize(image, landmarks, similarity);//targetIndex is the index of the most similar face in database
        Log.e("TAG", "onCreate: targetIndex"+ targetIndex);
        //当前人脸与数据库中注册人脸的相似度
        float[] crossSimilarities = faceRecognizer.RecognizeEx(image, landmarks);//crossSimilarities's length is the returned value of MaxRegisterIndex()
        Log.e("TAG", "onCreate: crossSimilarities"+ crossSimilarities);

        // 比较两张脸
        float similarity2 = faceRecognizer.Compare(image, landmarks, image2, landmarks);

        Log.e("TAG", "onCreate: similarity2"+ similarity2);

    }


    public SeetaImageData ConvertToSeetaImageData(String imgPath) {
        Log.e("TAG", "imgPath"+ imgPath);
        Bitmap bmp = BitmapFactory.decodeFile(imgPath);

        Bitmap bmp_src = bmp.copy(Bitmap.Config.ARGB_8888, true); // true is RGBA
        SeetaImageData imageData = new SeetaImageData(bmp_src.getWidth(), bmp_src.getHeight(), 3);
        imageData.data = getPixelsBGR(bmp_src);

        return imageData;
    }

    public byte[] getPixelsBGR(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[(temp.length/4) * 3]; // Allocate for BGR

        // Copy pixels into place
        for (int i = 0; i < temp.length/4; i++) {

            pixels[i * 3] = temp[i * 4 + 2];        //B
            pixels[i * 3 + 1] = temp[i * 4 + 1];    //G
            pixels[i * 3 + 2] = temp[i * 4 ];       //R

        }

        return pixels;
    }
}