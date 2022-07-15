package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private static final String ARG_PARAM1 = "ID";
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_SEARCH_WORLD = "search_word";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    public List<Item> items = new ArrayList<>();
    private AppDatabase db;
    private ItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        db = AppDatabaseSingleton.getInstance(getApplicationContext());
        mBinding.fabItemAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MA","item add button clicked");
                Intent intent = new Intent(getApplication(),PurchaseItemAddActivity.class);
                startActivityForResult(intent,114514);
            }
        });
        new AsyncExportProgress(db).execute();

        if (mColumnCount <= 1) {
            mBinding.rvItemlist.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mBinding.rvItemlist.setLayoutManager(new GridLayoutManager(this, mColumnCount));
        }
        adapter = new ItemRecyclerViewAdapter(this.items){
            // クリック時、サークル詳細ページに飛べるように変更
            @Override
            void onItemClick(View view, int position, Item itemData) {
                // TODO: アイテム詳細ページへ遷移
                Log.d("MA","item clicked");
                Intent intent = new Intent(getApplication(), ItemInfoActivity.class);
                intent.putExtra(ARG_PARAM1, itemData.id);
                startActivity(intent);
            }
        };
        mBinding.rvItemlist.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Log.d("MA","Activity Returened.");
            new AsyncExportProgress(db).execute();
        }
    }

    public void setItem(List<Item> items){
        items.forEach(item -> {
            Log.d("MA_items", String.valueOf(item.price));
            //Uri uri = Uri.parse(item.path);
            //getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        });
        this.items.clear();
        this.items.addAll(items);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        if(searchItem != null){
            SearchView sv = (SearchView) searchItem.getActionView();
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d("MA_search",query);
                    new AsyncExportProgressForSearch(db,query).execute();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    new AsyncExportProgressForSearch(db,newText).execute();
                    return false;
                }
            });
        }
        return true;
    }




    private class AsyncExportProgress {

        AppDatabase db;
        //TextView output;
        private AsyncExportProgress(AppDatabase db){
            this.db = db;
        }
        /*
        private AsyncExportProgress(AppDatabase db,TextView tv){
            this.db = db;
            //this.output = tv;
        }
        */
        private class AsyncRunnable implements Runnable {

            private String strResult;

            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                // ここにバックグラウンド処理を書く
                ItemDao dao = db.itemDao();
                List<Item> items_db = dao.getAll();
                StringBuilder sb = new StringBuilder();
                items_db.forEach(item -> {
                    Log.d("MA_DB",item.name);
                    sb.append(item.toString());

                });
                strResult = sb.toString();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(strResult,items_db);
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

        void onPostExecute(String result, List<Item> items) {
            // バックグランド処理終了後の処理をここに記述します
            // 例） プログレスダイアログ終了
            // output.setText(result);
            setItem(items);
        }
    }

    private class AsyncExportProgressForSearch {

        AppDatabase db;
        String query;
        //TextView output;
        private AsyncExportProgressForSearch(AppDatabase db,String query){
            this.db = db;
            this.query = query;
        }
        /*
        private AsyncExportProgress(AppDatabase db,TextView tv){
            this.db = db;
            //this.output = tv;
        }
        */
        private class AsyncRunnable implements Runnable {

            private String strResult;

            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                // ここにバックグラウンド処理を書く
                ItemDao dao = db.itemDao();
                List<Item> items_db = dao.findItemByName(query);
                StringBuilder sb = new StringBuilder();
                /*
                items_db.forEach(item -> {
                    Log.d("MA_DB",item.name);
                    sb.append(item.toString());

                });
                strResult = sb.toString();

                 */
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(items_db);
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

        void onPostExecute(List<Item> items) {
            // バックグランド処理終了後の処理をここに記述します
            // 例） プログレスダイアログ終了
            // output.setText(result);
            setItem(items);
        }
    }

}