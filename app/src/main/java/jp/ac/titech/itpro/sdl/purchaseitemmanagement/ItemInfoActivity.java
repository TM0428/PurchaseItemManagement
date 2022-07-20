package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.ac.titech.itpro.sdl.purchaseitemmanagement.databinding.ActivityItemInfoBinding;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.AppDatabase;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.AppDatabaseSingleton;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.Item;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.ItemDao;

public class ItemInfoActivity extends AppCompatActivity {
    private static final String ARG_PARAM1 = "ID";
    private int id;

    private ActivityItemInfoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityItemInfoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        id = Integer.parseInt(getIntent().getStringExtra(ARG_PARAM1));
        new AsyncExportProgress(db, id, mBinding.ivIteminfoimage, mBinding.tvIteminfoName, mBinding.tvIteminfoPrice, mBinding.tvIteminfoAuthor, this).execute();

    }


    public void setData(Item item){
        mBinding.tvIteminfoName.setText(item.name);
        mBinding.tvIteminfoAuthor.setText(item.tag);
        mBinding.tvIteminfoPrice.setText(String.valueOf(item.price) + "円");
        // Glide.with(this).load(item.path).into(mBinding.ivIteminfoimage);
        mBinding.ivIteminfoimage.setImage(ImageSource.uri(item.path));
        /*
        mBinding.ivIteminfoimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubsamplingScaleImageView imageViewEnlarged = new SubsamplingScaleImageView(getApplicationContext());
                imageViewEnlarged.setImage(ImageSource.uri(item.path));
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setView(imageViewEnlarged);
                builder.setNegativeButton("close", null);
                builder.show();
            }
        });

         */
        Log.d("IIA","Data set");
    }

    private class AsyncExportProgress {

        AppDatabase db;
        int id;
        SubsamplingScaleImageView iv;
        TextView name;
        TextView price;
        TextView author;
        Item item;
        private Context context;

        private AsyncExportProgress(AppDatabase db, int id, SubsamplingScaleImageView iv, TextView name, TextView price, TextView author, Context context){
            this.db = db;
            this.id = id;
            this.iv = iv;
            this.name = name;
            this.price = price;
            this.author = author;
            this.context = context;
        }


        private class AsyncRunnable implements Runnable {

            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                // ここにバックグラウンド処理を書く
                ItemDao dao = db.itemDao();
                List<Item> items = dao.getById(id);
                item = items.get(0);
                Log.d("IIA",item.toString());
                //iv.setImageURI(Uri.parse(item.path));
                //name.setText(item.name);
                //price.setText(String.valueOf(item.price) + "円");
                //author.setText(item.author + "作");
                handler.post(() -> onPostExecute(item));

            }
        }

        void onPreExecute() {
            // ここに前処理を記述します
            // 例） プログレスダイアログ表示
            Log.d("IIA","item get by DB");
            Log.d("IIA",String.valueOf(id));
        }

        void execute() {
            onPreExecute();
            ExecutorService executorService  = Executors.newSingleThreadExecutor();
            executorService.submit(new AsyncRunnable());
        }

        void onPostExecute(Item item) {
            // バックグランド処理終了後の処理をここに記述します
            // 例） プログレスダイアログ終了
            Log.d("IIA","DB exit.");
            setData(item);
        }
    }
}