package com.wekly;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.wekly.Model.Escort;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListFragment extends Fragment {
    RadioButton all_rb, women_rb, men_rb, trans_rb;
    Button apply_filter_button;

    List<Escort> lstEscort;
    FirebaseRecyclerAdapter<Escort, ViewHolder> adapter;
    FirebaseRecyclerOptions<Escort> options;
    FirebaseDatabase mFirebaseDatabase;
    TextView dialog_escort_name, dialog_escort_price, dialog_escort_description ;
    CircleImageView dialog_escort_pic;

    DatabaseReference mRef;
    RecyclerView myrv;
    Query mQuery;
    boolean showAll = true;
    Dialog mDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_list, container, false);
        myrv = v.findViewById(R.id.recyclerview_id);
        all_rb = v.findViewById(R.id.all_rb);
        women_rb = v.findViewById(R.id.women_rb);
        men_rb = v.findViewById(R.id.men_rb);
        trans_rb = v.findViewById(R.id.trans_rb);
        apply_filter_button = v.findViewById(R.id.button_apply_filter);
        myrv.setHasFixedSize(true);
        myrv.setItemViewCacheSize(20);
        myrv.setDrawingCacheEnabled(true);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference().child("escort");
        options = new FirebaseRecyclerOptions.Builder<Escort>().setQuery(mRef, Escort.class).build();

        apply_filter_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (all_rb.isChecked()) {
                    options = new FirebaseRecyclerOptions.Builder<Escort>().setQuery(mRef, Escort.class).build();
                    setView(options);

                } else if (women_rb.isChecked()) {
                    mQuery = mRef.orderByChild("sex").equalTo("Mujer");

                    options = new FirebaseRecyclerOptions.Builder<Escort>().setQuery(mQuery, Escort.class).build();
                    setView(options);
                } else if (men_rb.isChecked()) {
                    mQuery = mRef.orderByChild("sex").equalTo("Hombre");

                    options = new FirebaseRecyclerOptions.Builder<Escort>().setQuery(mQuery, Escort.class).build();
                    setView(options);
                } else if (trans_rb.isChecked()) {
                    mQuery = mRef.orderByChild("sex").equalTo("Trans");
                    options = new FirebaseRecyclerOptions.Builder<Escort>().setQuery(mQuery, Escort.class).build();
                    setView(options);


                }

            }
        });


        setView(options);
        return v;

    }

    public void setView(FirebaseRecyclerOptions<Escort> options) {

        adapter = new FirebaseRecyclerAdapter<Escort, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull final Escort escort) {
                viewHolder.setDetails(getContext(), escort.getName(), escort.getPrice(), escort.getImage(), escort.getHeight(),
                        escort.getWeight(), escort.getAge(), escort.getDescription(), escort.getRace());


                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialog = new Dialog(getContext());
                                NumberFormat format = DecimalFormat.getInstance();
                                format.setRoundingMode(RoundingMode.FLOOR);
                                format.setMinimumFractionDigits(0);
                                mDialog.setContentView(R.layout.dialog_details);


                                dialog_escort_description = mDialog.findViewById(R.id.dialog_escort_description);
                                dialog_escort_price = mDialog.findViewById(R.id.dialog_escort_price);
                                dialog_escort_name =mDialog.findViewById(R.id.dialog_escort_name);
                                dialog_escort_pic=mDialog.findViewById(R.id.dialog_escort_image);
                                dialog_escort_name.setText(escort.getName());
                                dialog_escort_description.setText(escort.getDescription());
                                dialog_escort_price.setText("$ "+ format.format(escort.getPrice())+"");
                                Picasso.with(getContext()).load(escort.getImage()).into(dialog_escort_pic);

                                mDialog.show();


                            }
                        });


            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;


                LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
                view = mInflater.inflate(R.layout.card_item, parent, false);



                return new ViewHolder(view);
            }
        };
        RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getActivity(), lstEscort);
        myAdapter.setHasStableIds(true);
        adapter.startListening();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        myrv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        myrv.setAdapter(adapter);

    }
}
