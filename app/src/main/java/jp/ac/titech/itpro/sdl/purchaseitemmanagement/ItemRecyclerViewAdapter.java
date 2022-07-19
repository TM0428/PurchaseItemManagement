package jp.ac.titech.itpro.sdl.purchaseitemmanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import jp.ac.titech.itpro.sdl.purchaseitemmanagement.db.Item;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private List<Item> items;
    private Context context;

    public ItemRecyclerViewAdapter(List<Item> items, Context context) {
        this.items = items; this.context = context;
    }

    public void setItem(List<Item> items){
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_itemrecycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mitem = items.get(position);
        holder.mNameView.setText(items.get(position).name);
        holder.mPriceView.setText(String.valueOf(items.get(position).price) + "円");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RV", "item clicked");
                int position = holder.getAdapterPosition();
                onItemClick(view,position,items.get(position));
            }
        });
        Log.d("RV",items.get(position).path);
        if(items.get(position).path != ""){

            // holder.mThumView.setImageURI(Uri.parse(items.get(position).path));
            Glide.with(context).load(items.get(position).path).into(holder.mThumView);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    /**
     * if you want to make a method(click), you override here.
     * @param view
     * @param position
     * @param itemData
     */
    void onItemClick(View view,int position, Item itemData){}

    class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mNameView;
        public final TextView mPriceView;
        public final ImageView mThumView;
        public Item mitem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.tv_itemname);
            mPriceView = (TextView) view.findViewById(R.id.tv_itemprice);
            mThumView = (ImageView) view.findViewById(R.id.iv_thumbnail);
        }
    }
}
