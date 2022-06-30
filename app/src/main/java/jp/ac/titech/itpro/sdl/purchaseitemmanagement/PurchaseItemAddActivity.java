package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jp.ac.titech.itpro.sdl.purchaseitemmanagement.databinding.ActivityPurchaseItemAddBinding;

public class PurchaseItemAddActivity extends AppCompatActivity {
    private Uri photoImage = null;
    private String currentPhotoPath;
    private ActivityPurchaseItemAddBinding mBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPurchaseItemAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.btCamera.setOnClickListener(v -> {
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