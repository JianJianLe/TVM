package com.tvm.tvm.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.tvm.tvm.R;

import java.io.File;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 21:32
 */
public class TicketEditActivity extends BaseActivity {

    // 拍照成功，读取相册成功，裁减成功
    private final int  ALBUM_OK = 1, CAMERA_OK = 2,CUT_OK = 3;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_openCameral:

                //这里被注掉的，是在6.0中进行权限判断的，大家可以根据情况，自行加上
                /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123);
                    Log.e("Album","我没有权限啊");
                }else {
                    Log.e("Album","我有权限啊");
                }*/

                // 来自相机
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(cameraIntent, CAMERA_OK);// CAMERA_OK是用作判断返回结果的标识
                break;
            case R.id.menu_openAlbum:
                // 来自相册
                Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                /**
                 * 下面这句话，与其它方式写是一样的效果，如果：
                 * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                 * intent.setType(""image/*");设置数据类型
                 * 要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                 */
                albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(albumIntent, ALBUM_OK);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 裁剪图片方法实现
     * @param uri          图片uri
     * @param type         类别：相机，相册
     */
    public void clipPhoto(Uri uri, int type) {


        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);

        /**
         * 此处做一个判断
         * １，相机取到的照片，我们把它做放到了定义的目录下。就是file
         * ２，相册取到的照片，这里注意了，因为相册照片本身有一个位置，我们进行了裁剪后，要给一个裁剪后的位置，
         * 　　不然onActivityResult方法中，data一直是null
         */
        if(type==CAMERA_OK)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        }
        startActivityForResult(intent, CUT_OK);
    }

    /**
     * 保存裁剪之后的图片数据 将图片设置到imageview中
     *
     * @param picdata　　　　　　　　　　资源
     */
    private void setPicToView(Intent picdata) {

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picdata.getData()));
//            img_album.setImageBitmap(bitmap);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
