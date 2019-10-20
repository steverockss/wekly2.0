package com.wekly;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;
import com.wekly.Model.Escort;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>  {

    private Context mContext;
    private List<Escort> mData;
    Dialog mDialog;

    public RecyclerViewAdapter(Context mContext, List<Escort> mData) {
        this.mContext = mContext;
        this.mData = mData;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.card_item, parent, false);

        final MyViewHolder vHolder = new MyViewHolder(view);
        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_details);
        Button hire = mDialog.findViewById(R.id.dialog_button_hire);
        hire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Contratada", Toast.LENGTH_SHORT).show();
                mDialog.dismiss();
            }
        });
        vHolder.item_escort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Dialog init
                int position = vHolder.getAdapterPosition();
                TextView dialog_escort_name = mDialog.findViewById(R.id.dialog_escort_name);
                TextView dialog_escort_price = mDialog.findViewById(R.id.dialog_escort_price);
                ImageView dialog_escort_image = mDialog.findViewById(R.id.dialog_escort_image);
                dialog_escort_name.setText(mData.get(position).getName());
                NumberFormat format = DecimalFormat.getInstance();
                format.setRoundingMode(RoundingMode.FLOOR);
                format.setMinimumFractionDigits(0);
              //  String price = ("$ "+ format.format(mData.get(position).getPrice())+"");
                //dialog_escort_price.setText(price);
               // dialog_escort_image.setImageResource(mData.get(vHolder.getAdapterPosition()).getImage2());
                //Picasso.with(mContext).load(mData.get(position).getImage()).into(dialog_escort_image);


                mDialog.show();

            }
        });
        return  vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
/*
        //holder.escort_pic.setImageResource(mData.get(position).getImage2());
        Picasso.with(mContext).load(mData.get(position).getImage()).into(holder.escort_pic);
        holder.escort_name.setText(mData.get(position).getName());
        holder.escort_age.setText(String.valueOf(mData.get(position).getAge()));
        holder.escort_height.setText((mData.get(position).getHeight()+" cm"));
        holder.escort_weight.setText((mData.get(position).getWeight()+" kg"));
//        holder.escort_race.setText(String.valueOf(mData.get(position).getRace()));
        NumberFormat format = DecimalFormat.getInstance();
        format.setRoundingMode(RoundingMode.FLOOR);
        format.setMinimumFractionDigits(0);
        String price = ("$ "+ format.format(mData.get(position).getPrice())+"");

        holder.escort_price.setText(price);
        */


    }



    @Override
    public int getItemCount() {
        return mData.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout item_escort;
        TextView escort_name, escort_height, escort_weight, escort_price, escort_age, escort_race;
        ImageView escort_pic;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
           item_escort =  itemView.findViewById(R.id.escort_item);
            escort_height = itemView.findViewById(R.id.escort_height);
            escort_age = itemView.findViewById(R.id.escort_age);
          //  escort_race = itemView.findViewById(R.id.escort_race);
            escort_price = itemView.findViewById(R.id.escort_price);
            escort_weight = itemView.findViewById(R.id.escort_weight);
            escort_name = itemView.findViewById(R.id.escort_name);
            escort_pic = itemView.findViewById(R.id.escort_image);
        }
    }

}
