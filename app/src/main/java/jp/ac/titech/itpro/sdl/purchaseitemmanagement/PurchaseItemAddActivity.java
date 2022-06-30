package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.ac.titech.itpro.sdl.purchaseitemmanagement.databinding.ActivityPurchaseItemAddBinding;

public class PurchaseItemAddActivity extends AppCompatActivity {
    private Uri photoImage = null;
    private String currentPhotoPath;
    private ActivityPurchaseItemAddBinding mBinding;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
               if(result.getResultCode() == Activity.RESULT_OK){
                   if(result.getData() != null){
                       // 結果を受けとる
                       try {
                           BufferedInputStream inputStream = new BufferedInputStream(getContentResolver().openInputStream(result.getData().getData()));
                           Bitmap image = BitmapFactory.decodeStream(inputStream);
                           mBinding.ivSampleImage.setImageBitmap(image);
                           Log.d("PIAA", String.valueOf(image.getHeight()));
                           // mBinding.ivSampleImage.setMaxHeight(image.getHeight());
                           mBinding.ivSampleImage.setScaleType(ImageView.ScaleType.FIT_START);
                       } catch (FileNotFoundException e) {
                           e.printStackTrace();
                       }
                   }
               }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPurchaseItemAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btCamera.setOnClickListener(v -> {
            /*
            try {
                Log.d("TM","photo button push");
                File file = createImageFile();
                photoImage = FileProvider.getUriForFile(
                        PurchaseItemAddActivity.this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        file
                );
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // TODO: You should setup appropriate parameters for the intent
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoImage);
                startActivity(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }

             */
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(intent);
        });
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



}