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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
//Sold Adapter Class that save or load data in Recycler View
public class SoldAdapter extends RecyclerView.Adapter<SoldAdapter.MyViewHolder> {


    ArrayList<BuyerSalerModel> arrayList;
    Context context;

    public SoldAdapter(ArrayList<BuyerSalerModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sold_history, parent, false);


        return new SoldAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BuyerSalerModel user = arrayList.get(position);
        holder.dateTimeH.setText(user.getDateTime());
        holder.addressH.setText(user.getBuyerAddres());
        holder.priceH.setText(user.getPrice());
        holder.mealNameH.setText(user.getMealName());
        holder.plateSizeH.setText(user.getPlates());
        holder.buyerNameH.setText(user.getBuyerName());

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

        TextView dateTimeH, mealNameH, buyerNameH, priceH, addressH, plateSizeH;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgHistory = itemView.findViewById(R.id.soldImage);
            dateTimeH = itemView.findViewById(R.id.soldtime);
            mealNameH = itemView.findViewById(R.id.soldMealName);
            buyerNameH = itemView.findViewById(R.id.soldBuyerName);
            priceH = itemView.findViewById(R.id.soldInPrice);
            addressH = itemView.findViewById(R.id.soldAddress);
            plateSizeH = itemView.findViewById(R.id.soldPlateSize);


        }
    }

}
