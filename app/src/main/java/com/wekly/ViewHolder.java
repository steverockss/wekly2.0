package com.wekly;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;




class ViewHolder extends RecyclerView.ViewHolder {


    View mview;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        mview = itemView;


    }
    public void setDetails(Context mContext, String name, int price, String image, int height, int weight, int age, String description, String sex){
        TextView escort_name, escort_height, escort_weight, escort_price, escort_age, escort_sex;
        ImageView escort_pic;
        //zitem_escort =  itemView.findViewById(R.id.escort_item);
        escort_height = itemView.findViewById(R.id.escort_height);
        escort_age = itemView.findViewById(R.id.escort_age);
        //  escort_race = itemView.findViewById(R.id.escort_race);
        escort_price = itemView.findViewById(R.id.escort_price);
        escort_weight = itemView.findViewById(R.id.escort_weight);
        escort_name = itemView.findViewById(R.id.escort_name);
        escort_pic = itemView.findViewById(R.id.escort_image);
        escort_sex = itemView.findViewById(R.id.escort_sex);
        NumberFormat format = DecimalFormat.getInstance();
        format.setRoundingMode(RoundingMode.FLOOR);
        format.setMinimumFractionDigits(0);
        escort_height.setText((height)+" cm");
        escort_age.setText((age+" años"));
        escort_sex.setText(sex);
        escort_price.setText("$ "+ format.format(price)+"");
        escort_weight.setText(weight+" kg");
        escort_name.setText(name);
        Picasso.with(mContext).load(image).into(escort_pic);



    }


}


