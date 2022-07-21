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

import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Comparator;
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
    private boolean isexpand = false;
    private final String[] spinnerItems = {"選択無し","500円", "1000円", "5000円"};
    private String query = "";
    private int maxprice = 1000000000;
    private boolean tab_inc = false;

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
        adapter = new ItemRecyclerViewAdapter(this.items, this){
            // クリック時、サークル詳細ページに飛べるように変更
            @Override
            void onItemClick(View view, int position, Item itemData) {
                // TODO: アイテム詳細ページへ遷移
                Log.d("MA","item clicked");
                Intent intent = new Intent(getApplication(), ItemInfoActivity.class);
                intent.putExtra(ARG_PARAM1, String.valueOf(itemData.id));
                startActivity(intent);
            }
        };
        mBinding.rvItemlist.setAdapter(adapter);
        mBinding.cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isexpand){
                    mBinding.expandableLayout.collapse();
                }
                else{
                    mBinding.expandableLayout.expand();
                }
                isexpand = !isexpand;
            }
        });
        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = mBinding.loExsearch.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Spinner を取得
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムのテキストを取得
                String str = spinner.getSelectedItem().toString();
                switch (str){
                    case "500円":
                        maxprice = 500;
                        break;
                    case "1000円":
                        maxprice = 1000;
                        break;
                    case "5000円":
                        maxprice = 5000;
                        break;
                    case "選択無し":
                    default:
                        maxprice = 1000000000;
                }
                Log.d("MA",String.valueOf(maxprice));
            }

            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                // Spinner を取得
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムのテキストを取得
                String str = spinner.getSelectedItem().toString();
                switch (str){
                    case "500円":
                        maxprice = 500;
                        break;
                    case "1000円":
                        maxprice = 1000;
                        break;
                    case "5000円":
                        maxprice = 5000;
                        break;
                    case "選択無し":
                    default:
                        maxprice = 1000000000;
                }
                Log.d("MA",String.valueOf(maxprice));
                new AsyncExportProgressForSearch(db,query,tab_inc,maxprice).execute();
            }
        });
        Switch sw = mBinding.loExsearch.findViewById(R.id.sw_tag);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("MA",String.valueOf(isChecked));
                tab_inc = isChecked;
                new AsyncExportProgressForSearch(db,query,tab_inc,maxprice).execute();
            }
        });
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
                public boolean onQueryTextSubmit(String Text) {
                    Log.d("MA_search",Text);
                    query = Text;
                    new AsyncExportProgressForSearch(db,Text,tab_inc,maxprice).execute();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    query = newText;
                    new AsyncExportProgressForSearch(db,newText,tab_inc,maxprice).execute();
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
                Log.d("MA_DB","DB access");
                ItemDao dao = db.itemDao();
                List<Item> items_db = dao.getAll();
                Log.d("MA_DB",String.valueOf(items_db.size()));
                StringBuilder sb = new StringBuilder();
                items_db.forEach(item -> {
                    Log.d("MA_DB",item.name);
                    sb.append(item.toString());

                });
                items_db.sort(Comparator.comparing(Item::getName));
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
        boolean tag_inc;
        int price;
        //TextView output;
        private AsyncExportProgressForSearch(AppDatabase db,String query, boolean tag_inc, int price){
            this.db = db;
            this.query = query;
            this.tag_inc = tag_inc;
            this.price = price;
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
                List<Item> items_db;
                if(tag_inc){
                    items_db = dao.findItemByAll(query,price);
                }
                else{
                    items_db = dao.findItemByName(query,price);
                }
                /*
                StringBuilder sb = new StringBuilder();
                items_db.forEach(item -> {
                    Log.d("MA_DB",item.name);
                    sb.append(item.toString());

                });
                strResult = sb.toString();

                 */
                items_db.sort(Comparator.comparing(Item::getName));
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
            Log.d("MA_DB","DB access end");
            setItem(items);
        }
    }

}