package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import androidx.appcompat.app.AppCompatActivity;

import jp.ac.titech.itpro.sdl.purchaseitemmanagement.databinding.ActivityMainBinding;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.AppDatabase;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.AppDatabaseSingleton;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.Item;
import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.ItemDao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        Button bt = findViewById(R.id.bt_submit_test);
        bt.setOnClickListener(new SubmitButtonClickListener(db));
        mBinding.fabItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MA","item add button clicked");
                Intent intent = new Intent(getApplication(),PurchaseItemAddActivity.class);
                startActivity(intent);
            }
        });
    }

    private class SubmitButtonClickListener implements View.OnClickListener{
        private AppDatabase db;
        @Override
        public void onClick(View view){
            Log.d("debug","button touched");
            /*
            ItemDao dao = db.itemDao();
            dao.insert(new Item(1,"hogehoge",500));
            //ItemDao dao = db.itemDao();
            List<Item> items = dao.getAll();
            items.forEach(item -> {
                Log.d("debug",item.name);
            });
             */
            TextView tv_id = findViewById(R.id.et_ID);
            TextView tv_name = findViewById(R.id.et_name);
            TextView tv_price = findViewById(R.id.et_price);
            TextView tv_output = findViewById(R.id.tv_output);
            String id = tv_id.getText().toString();
            String name = tv_name.getText().toString();
            int price = Integer.parseInt(tv_price.getText().toString());
            new AsyncExportProgress(db,id,name,price,tv_output).execute();
        }
        private SubmitButtonClickListener(AppDatabase db){
            this.db = db;
        }
    }

    private static class AsyncExportProgress {

        AppDatabase db;
        private String id;
        private String name;
        private int price;
        TextView output;

        private AsyncExportProgress(AppDatabase db,String id,String name,int price,TextView tv){
            this.db = db;
            this.id = id;
            this.name = name;
            this.price = price;
            this.output = tv;
        }


        private class AsyncRunnable implements Runnable {

            private String strResult;

            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                // ここにバックグラウンド処理を書く
                ItemDao dao = db.itemDao();
                dao.insert(new Item(id,name,price));
                List<Item> items = dao.getAll();
                StringBuilder sb = new StringBuilder();
                items.forEach(item -> {
                    Log.d("debug",item.name);
                    sb.append("id:" +item.id + ",name:" + item.name + ",price:" + item.price + "\n" );

                });
                strResult = sb.toString();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(strResult);
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

        void onPostExecute(String result) {
            // バックグランド処理終了後の処理をここに記述します
            // 例） プログレスダイアログ終了
            output.setText(result);
        }
    }

}