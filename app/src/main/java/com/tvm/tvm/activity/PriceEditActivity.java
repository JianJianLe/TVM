package com.tvm.tvm.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvm.tvm.R;
import com.tvm.tvm.application.AppApplication;
import com.tvm.tvm.bean.Price;
import com.tvm.tvm.bean.dao.DaoSession;
import com.tvm.tvm.bean.dao.PriceDao;
import com.tvm.tvm.util.BitmapUtils;
import com.tvm.tvm.util.FileUtil;
import com.tvm.tvm.util.constant.StringUtils;
import com.tvm.tvm.util.device.paydevice.PayDeviceUtil;
import com.tvm.tvm.util.view.ButtomDialogView;
import com.tvm.tvm.util.view.ToastUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * - @Description:  $desc$
 * - @Author:  Jat
 * - @Date:  2019/1/6
 * - @Time： 21:32
 */
public class PriceEditActivity extends BaseActivity {

    // 拍照成功，读取相册成功，裁减成功
    private final int  REQUEST_CODE_ALBUM = 1, REQUEST_CODE_TAKE_PHOTO = 2,REQUEST_CODE_CROUP_PHOTO = 3;

    private File file;

    private Bitmap bitmap;

    @BindView(R.id.ib_price_edit_back)
    ImageButton ib_price_edit_back;

    @BindView(R.id.iv_price_edit_icon)
    ImageView iv_price_edit_icon;

    @BindView(R.id.et_price_edit_title)
    EditText et_price_edit_title;

    @BindView(R.id.et_price_edit_price)
    EditText et_price_edit_price;

    @BindView(R.id.et_price_edit_desc)
    EditText et_price_edit_desc;

    @BindView(R.id.btn_price_edit_save)
    Button btn_price_edit_save;

    Long priceId = null;

    String mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";

    private DaoSession daoSession;

    Price price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_edit);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        priceId = intent.getLongExtra("priceId",-1l);
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ticket);
        //保证图片路径存在
        file = new File(mTempPhotoPath);
        file.delete();
        getData();
    }

    private void getData(){
        if (priceId != null){
            daoSession = AppApplication.getApplication().getDaoSession();
            price = daoSession.getPriceDao().queryBuilder().where(PriceDao.Properties.Id.eq(priceId)).unique();
            if (price != null){
                init(price);
            }
        }
    }

    private void init(Price price){
        et_price_edit_desc.setText(price.getDescription());
        et_price_edit_price.setText(String.valueOf(price.getPrice()));
        et_price_edit_title.setText(price.getTitle());
        bitmap = BitmapUtils.byte2Bitmap(price.getPic());
        iv_price_edit_icon.setImageBitmap(bitmap);
    }

    /**
     * 保存
     */
    private void save(){
        //检查对应值是否正确
        if (check()){
            savePrice();
            //保存成功返回列表
            ToastUtils.showText(this,StringUtils.SAVE_SUCCESS);
            this.finish();
        }
    }

    private void savePrice(){
        if (price !=null ){
            price.setTitle(et_price_edit_title.getText().toString().trim());
            price.setDescription(et_price_edit_desc.getText().toString().trim());
            price.setPrice(Double.valueOf(et_price_edit_price.getText().toString().trim()));
            price.setPic(BitmapUtils.bitmap2Byte(bitmap));
        }else {
            price = new Price();
            price.setTitle(et_price_edit_title.getText().toString().trim());
            price.setDescription(et_price_edit_desc.getText().toString().trim());
            price.setPrice(Double.valueOf(et_price_edit_price.getText().toString().trim()));
            price.setPic(BitmapUtils.bitmap2Byte(bitmap));
        }
        daoSession.getPriceDao().save(price);

        //上报本地通道信息到支付盒子
        PayDeviceUtil.getInstance().cmd_UploadParams();
    }

    /**
     * 检查对应column是否正确
     * @return
     */
    private boolean check(){

        //标题是否为空
        if ( TextUtils.isEmpty(et_price_edit_title.getText().toString().trim())){
            ToastUtils.showText(this,StringUtils.TITLE_NOT_NULL);
            return false;
        }
        //描述是否为空
        if ( TextUtils.isEmpty(et_price_edit_desc.getText().toString().trim())){
            ToastUtils.showText(this,StringUtils.DESC_NOT_NULL);
            return false;
        }
        //价格是否为空
        if ( TextUtils.isEmpty(et_price_edit_price.getText().toString().trim())){
            ToastUtils.showText(this,StringUtils.PRICE_NOT_NULL);
            return false;
        }
        Price dbPrice = daoSession.getPriceDao().queryBuilder().where(PriceDao.Properties.Title.eq(et_price_edit_title.getText().toString().trim()),PriceDao.Properties.IsDelete.eq(0)).unique();
        if (dbPrice!=null){
            if (price!=null && price.getTitle().equals(et_price_edit_title.getText().toString().trim())){

            }else {
                ToastUtils.showText(this,StringUtils.HAD_TITLE);
                return false;
            }
        }
        return true;
    }

    @OnClick({R.id.ib_price_edit_back,R.id.iv_price_edit_icon,R.id.btn_price_edit_save})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ib_price_edit_back:
                this.finish();
                break;
            case R.id.iv_price_edit_icon:
                showButtomDialog();
                break;
            case R.id.btn_price_edit_save:
                save();
                break;
        }
    }

    public void showButtomDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_icon_select,null);
        final ButtomDialogView buttomDialogView = new ButtomDialogView(this,view,false,false);
        buttomDialogView.show();
        TextView tv_dialog_icon_select_take_photo = view.findViewById(R.id.tv_dialog_icon_select_take_photo);
        tv_dialog_icon_select_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 来自相机
                buttomDialogView.dismiss();
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(takeIntent, REQUEST_CODE_TAKE_PHOTO);
            }
        });

        TextView tv_dialog_icon_select_choose_photo = view.findViewById(R.id.tv_dialog_icon_select_choose_photo);
        tv_dialog_icon_select_choose_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // "从相册选择"按钮被点击了
                buttomDialogView.dismiss();
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, REQUEST_CODE_ALBUM);
            }
        });

        //取消按钮
        TextView tv_dialog_icon_select_cancel = view.findViewById(R.id.tv_dialog_icon_select_cancel);
        tv_dialog_icon_select_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttomDialogView.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result code",resultCode+"");
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            startPhotoZoom(Uri.fromFile(file));
        } else if (requestCode == REQUEST_CODE_ALBUM) {
            //调用系统裁剪方法进行裁剪
            startPhotoZoom(data.getData());
        } else if (requestCode == REQUEST_CODE_CROUP_PHOTO) {
            //获取图片路径进行设置
            compressAndUploadAvatar(file.getPath());
        }
    }

    /**
     * 裁剪方法
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        // intent.putExtra("outputX", Constants.USER_AVATAR_MAX_SIZE);//图片输出大小,可以不需要
        //intent.putExtra("outputY", Constants.USER_AVATAR_MAX_SIZE);
        //注意这里的输出的是上面的文件路径的Uri格式，这样在才能获取图片
        intent.putExtra("output", Uri.fromFile(file));
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        startActivityForResult(intent, REQUEST_CODE_CROUP_PHOTO);
    }

    /**
     * 压缩图片
     */
    private void compressAndUploadAvatar(String fileSrc) {
        //压缩图片
        bitmap = FileUtil.getSmallBitmapFromFile(this, fileSrc);
        iv_price_edit_icon.setImageBitmap(BitmapUtils.file2Bitmap(file));
    }

}
