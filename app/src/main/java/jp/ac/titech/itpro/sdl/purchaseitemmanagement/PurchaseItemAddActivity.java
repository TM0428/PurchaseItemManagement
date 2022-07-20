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
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import jp.ac.titech.itpro.sdl.purchaseitemmanagement.databinding.ActivityPurchaseItemAddBinding;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.AppDatabase;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.AppDatabaseSingleton;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.Item;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.ItemDao;

public class PurchaseItemAddActivity extends AppCompatActivity {
    private Uri photoImage = null;
    private String currentPhotoPath;
    private ActivityPurchaseItemAddBinding mBinding;

    @SuppressLint("WrongConstant")
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
               if(result.getResultCode() == Activity.RESULT_OK){
                   if(result.getData() != null){
                       Log.d("PIAA", String.valueOf(result.getData().getData()));
                       currentPhotoPath = String.valueOf(result.getData().getData());
                       final int takeFlags = result.getData().getFlags()& (Intent.FLAG_GRANT_READ_URI_PERMISSION
                               | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                       photoImage = result.getData().getData();
                       try {
                           getContentResolver().takePersistableUriPermission(photoImage, takeFlags);
                       }
                       catch (SecurityException e){
                           e.printStackTrace();
                       }
                       mBinding.ivSampleImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                       showPhoto();
                   }
               }
            });

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    //photoImage = (Bitmap) result.getData().getExtras().get("data");
                    //showPhoto();

                    mBinding.ivSampleImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    showPhoto();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPurchaseItemAddBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        mBinding.btSelimg.setOnClickListener(v -> {
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
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            activityResultLauncher.launch(intent);
        });
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
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoImage);

                PackageManager manager = getPackageManager();
                @SuppressLint("QueryPermissionsNeeded")
                List<ResolveInfo> activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!activities.isEmpty()) {
                    Log.d("PIAA","photo button push");
                    launcher.launch(intent);
                } else {
                    Toast.makeText(PurchaseItemAddActivity.this, R.string.toast_no_activities, Toast.LENGTH_LONG).show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        mBinding.btSubmit.setOnClickListener(new View.OnClickListener() {
            // private AppDatabase db;
            @Override
            public void onClick(View v) {
                //getContentResolver().takePersistableUriPermission(photoImage,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Log.d("PIAA","item add button push");
                AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
                String name = mBinding.etAddName.getText().toString();
                int price = Integer.parseInt(mBinding.etAddPrice.getText().toString());
                String tag = mBinding.etAddTag.getText().toString();
                // String id = createId();
                new AsyncExportProgress(db,0,name,price,tag,photoImage.toString()).execute();
            }
        });
    }

    private void showPhoto() {
        if (photoImage == null) {
            return;
        }
        mBinding.ivSampleImage.setImageURI(photoImage);
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

    /*
    private String createId(){
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

     */



    private class AsyncExportProgress {
        AppDatabase db;
        private int id;
        private String name;
        private int price;
        private String tag;
        private String path = "";

        private AsyncExportProgress(AppDatabase db,int id,String name,int price,String tag, String path){
            this.db = db;
            this.id = id;
            this.name = name;
            this.price = price;
            this.tag = tag;
            this.path = path;
        }


        private class AsyncRunnable implements Runnable {

            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                // ここにバックグラウンド処理を書く
                ItemDao dao = db.itemDao();
                Log.d("PIAA","DB access.");
                Item tmp = new Item(id,name,price,tag,path);
                Log.d("PIAA",tmp.toString());

                dao.insert(new Item(id,name,price,tag,path));
                List<Item> items = dao.getAll();
                Log.d("PIAA",String.valueOf(items.size()));
                items.forEach(item -> {
                    Log.d("PIAA",item.name);

                });
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute();
                    }
                });
            }
        }

        void onPreExecute() {
            // ここに前処理を記述します
            // 例） プログレスダイアログ表示
        }

        void execute() {
            onPreExecute();
            ExecutorService executorService  = Executors.newSingleThreadExecutor();
            executorService.submit(new AsyncRunnable());
        }

        void onPostExecute() {
            // バックグランド処理終了後の処理をここに記述します
            // 例） プログレスダイアログ終了
            Log.d("PIAA","DB item add.");
            setResult(RESULT_OK);
            finish();
        }
    }

}