package com.example.test.app.member.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.example.test.app.member.R;
import com.example.test.app.member.utils.FileUtil;
import com.example.test.app.member.widget.SelectPicPopupWindow;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MemberInfoActivity extends Activity implements View.OnClickListener {

    private static final String IMAGE_FILE_NAME = "/shine/avatar.png";
    private static final int REQUESTCODE_PICK = 101;
    private static final int REQUESTCODE_TAKE = 102;


    private SelectPicPopupWindow mSelectPicWindow;
    private File mTempFile;
    private String avatarPath = "";
    @Bind(R.id.edit_name)
    EditText mEditName;
    @Bind(R.id.edit_phone)
    EditText mEditPhone;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.imgview_head)
    ImageView mImgHead;


    //用户反馈自定义参数
    private Map<String, String> mapFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);
        ButterKnife.bind(this);
        init();

    }

    private void init() {
        //新建图片文件
        avatarPath = Environment.getExternalStorageDirectory() + IMAGE_FILE_NAME;
        mTempFile = new File(avatarPath);

    }

    private void userFeedback() {
        if (mapFeedback == null) {
            mapFeedback = new HashMap<String, String>();
            mapFeedback.put("themeColor", "#2b3237");
            mapFeedback.put("enableAudio", "1");
        }
        FeedbackAPI.openFeedbackActivity(this);
        //如果发生错误，请查看logcat日志
        FeedbackAPI.setUICustomInfo(mapFeedback);
    }

    @OnClick({
            R.id.btn_next,
            R.id.imgview_head
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                userFeedback();
                //startActivity(new Intent(MemberInfoActivity.this, MemberAccoutActivity.class));
                break;
            case R.id.imgview_head:
                mSelectPicWindow = new SelectPicPopupWindow(MemberInfoActivity.this, MemberInfoActivity.this);
                mSelectPicWindow.showAtLocation(findViewById(R.id.layout_base),
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.takePhotoBtn:
                takePhoto();
                break;
            case R.id.pickPhotoBtn:
                pickPhoto();
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //用户没有进行有效的设置操作，返回
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        switch (requestCode) {
            case REQUESTCODE_TAKE:
                // 检查图片旋转问题
                if (FileUtil.checkRotationPhoto(mTempFile.getAbsolutePath())) {
                    Uri uri = Uri.fromFile(mTempFile);
                    startPhotoZoom(uri, uri);
                }
                break;
            case REQUESTCODE_PICK:
                try {
                    Uri destUri = Uri.fromFile(mTempFile);
                    startPhotoZoom(data.getData(), destUri);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                break;
            case UCrop.REQUEST_CROP:
                mSelectPicWindow.dismiss();
                setImageToHeadView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    /***
     * 拍照
     */

    private void takePhoto() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri = Uri.fromFile(mTempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQUESTCODE_TAKE);
            } catch (Exception e) {
                e.printStackTrace();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else {
            Toast.makeText(MemberInfoActivity.this, "内存错误", Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUESTCODE_PICK);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            mImgHead.setImageBitmap(photo);
        }
    }


    private void startPhotoZoom(Uri sourceUri, Uri destUri) {

        if (sourceUri == null) {
            return;
        } else {
            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(ActivityCompat.getColor(this, R.color.toolbar_background_color));
            options.setStatusBarColor(ActivityCompat.getColor(this, R.color.toolbar_background_color));
            UCrop.of(sourceUri, destUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(236, 236)
                    .withOptions(options)
                    .start(this);
//            Intent intent = new Intent("com.android.camera.action.CROP");
//            intent.setDataAndType(sourceUri, "image/*");
//            android.util.Log.i("seven", "sourceUri:" + sourceUri);
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("outputX", 300);
//            intent.putExtra("outputY", 300);
//            intent.putExtra("return-data", true);
//            startActivityForResult(intent, UCrop.REQUEST_CROP);
        }

    }


}