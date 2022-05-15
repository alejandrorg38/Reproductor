package com.example.reproductor;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<CancionInfo> mContentList;

    public RecyclerAdapter(Context mContext, Activity mActivity, ArrayList<CancionInfo> mContentList) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mContentList = mContentList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(view, viewType);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewC;
        private ImageView iv_portadaC;
        private TextView tv_nCancionC, tv_artistaC;
        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            cardViewC = itemView.findViewById(R.id.cardViewC);
            iv_portadaC = itemView.findViewById(R.id.iv_portadaC);
            tv_nCancionC = itemView.findViewById(R.id.tv_nCancionC);
            tv_artistaC = itemView.findViewById(R.id.tv_artistaC);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder mainHolder, int position) {
        ViewHolder holder = (ViewHolder) mainHolder;
        final CancionInfo cancionInfo = mContentList.get(position);
        // setting data over views
        String imgUrl = cancionInfo.getPortadaUrl();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(holder.iv_portadaC);
        }
        holder.tv_nCancionC.setText(cancionInfo.getNombre());
        holder.tv_artistaC.setText(cancionInfo.getArtista());
    }
    @Override
    public int getItemCount() {
        if(mContentList!=null){
            return mContentList.size();
        }
        return 0;
    }
}
