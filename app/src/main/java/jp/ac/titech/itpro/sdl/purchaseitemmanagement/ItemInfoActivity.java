package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
    private String id;

    private ActivityItemInfoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityItemInfoBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        id = getIntent().getStringExtra(ARG_PARAM1);
        new AsyncExportProgress(db, id, mBinding.ivIteminfoimage, mBinding.tvIteminfoName, mBinding.tvIteminfoPrice, mBinding.tvIteminfoAuthor).execute();
    }


    private static class AsyncExportProgress {

        AppDatabase db;
        String id;
        ImageView iv;
        TextView name;
        TextView price;
        TextView author;
        Item item;

        private AsyncExportProgress(AppDatabase db, String id, ImageView iv, TextView name, TextView price, TextView author){
            this.db = db;
            this.id = id;
            this.iv = iv;
            this.name = name;
            this.price = price;
            this.author = author;
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
                iv.setImageURI(Uri.parse(item.path));
                name.setText(item.name);
                price.setText(String.valueOf(item.price) + "円");
                author.setText(item.author + "作");
                handler.post(() -> onPostExecute());

            }
        }

        void onPreExecute() {
            // ここに前処理を記述します
            // 例） プログレスダイアログ表示
            Log.d("IIA","item get by DB");
            Log.d("IIA",id);
        }

        void execute() {
            onPreExecute();
            ExecutorService executorService  = Executors.newSingleThreadExecutor();
            executorService.submit(new AsyncRunnable());
        }

        void onPostExecute() {
            // バックグランド処理終了後の処理をここに記述します
            // 例） プログレスダイアログ終了
        }
    }
}