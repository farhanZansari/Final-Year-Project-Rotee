package com.example.fireauthlog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
// Purchased History Class Adapter
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<BuyerSalerModel> arrayList;
    Context context;

    public MyAdapter(ArrayList<BuyerSalerModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.displayhistory, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        BuyerSalerModel user = arrayList.get(position);
        holder.dateTimeH.setText(user.getDateTime());
        holder.mealNameH.setText(user.getMealName());
        holder.addressH.setText(user.getSalerAddres());
        holder.priceH.setText(user.getPrice());
        holder.plateSizeH.setText(user.getPlates());
        holder.sellerNameH.setText(user.getSalerName());
        byte[] imageBytes = android.util.Base64.decode(user.getPicture(), android.util.Base64.DEFAULT);

        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.imgHistory.setImageBitmap(decodedImage);


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgHistory;

        TextView dateTimeH, mealNameH, sellerNameH, priceH, addressH, plateSizeH, deliveryStatusH;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgHistory = itemView.findViewById(R.id.historyImage);
            dateTimeH = itemView.findViewById(R.id.datetime);
            mealNameH = itemView.findViewById(R.id.historyMealName);
            sellerNameH = itemView.findViewById(R.id.historySellerName);
            priceH = itemView.findViewById(R.id.historyPrice);
            addressH = itemView.findViewById(R.id.historyAddress);
            plateSizeH = itemView.findViewById(R.id.historyDelivery);
        }
    }

}
