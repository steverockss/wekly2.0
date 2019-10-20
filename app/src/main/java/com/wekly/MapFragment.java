package com.wekly;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wekly.Model.Escort;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final int LOCATION_PERMISSION_CODE = 1;
    private boolean mLocationPermissionGranted = false;
    private static final int DEFAULT_ZOOM = 15;
    protected Location lastLocation = new Location("watever");
    public Context context = getActivity();
    private EditText mSearch;
    private String uInput;
    private RequestQueue requestQueue;
    public double lat = 0;
    public double lng = 0;
    public String dir = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mSearch = v.findViewById(R.id.search_edit_text);
        mSearch.setMaxLines(1);
        mSearch.setInputType(InputType.TYPE_CLASS_TEXT);
        Log.d("Valio", "Si llega");
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map


                );
        mapFragment.getMapAsync(this);

        // Get a reference to your user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("escort");

// Attach a listener to read the data at your profile reference
        ref.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    lng = Double.parseDouble(String.valueOf(dataSnapshot.child("lng").getValue()));
                    lat = Double.parseDouble(String.valueOf(dataSnapshot.child("lat").getValue()));
                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.heart);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title(name));


                } catch (Exception e) {


                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMinZoomPreference(9.0f);
        mMap.setMaxZoomPreference(18.0f);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        search();

    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();


                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            double latitude = mLastKnownLocation.getLatitude();
                            double longitude = mLastKnownLocation.getLongitude();
                            LatLng local = new LatLng(latitude, longitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(latitude,
                                            longitude), DEFAULT_ZOOM));

                        } else {


                        }

                    }

                });

            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            Log.d("Exception: %s", "Intentoooo");
            if (mLocationPermissionGranted) {
                Log.d("Exception: %s", "Funopma");
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                Log.d("Exception: %s", "nel");
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    public void getLocationPermission() {
        //Si ya contamos con permisos simplemente seteamos la variable como true
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            requestLocationPermission();
        }

    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(getContext())
                    .setTitle("Permiso Necesario")
                    .setMessage("Wekly funciona mediante la ubicación")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
                            mLocationPermissionGranted = true;
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();


    }

    public void search() {

        mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            List<Address> address = null;

            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH
                        || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    uInput = mSearch.getText().toString();
                    Log.d("Info responde", uInput);
                    /*
                    Geocoder geocoder = new Geocoder(MapsActivity.this);


                        address = geocoder.getFromLocationName(uInput, 1);
                        double lat = address.get(0).getLatitude();
                        double lng = address.get(0).getLongitude();
                        */
                    requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    uInput = uInput.replace(' ', '+');

                    JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?address=" + uInput + "+bogota" + "&key=AIzaSyBM2mnz1mpG9_sYh-JPlJP8zqnCXaf8_i8", new JSONObject(),

                            new com.android.volley.Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Error", "Peticion mela");
                                    try {
                                        lat = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                                                .getDouble("lat");
                                        lng = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                                                .getDouble("lng");
                                        dir = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(

                                                new LatLng(lat,
                                                        lng), 17));

                                    } catch (JSONException e) {
                                        e.printStackTrace();

                                    }
                                }
                            }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {


                        }
                    });


                    requestQueue.add(request);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(final Marker marker) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Selección de ubicación")
                                    .setMessage("¿Estas seguro de querer seleccionar esta ubicación? \n" + dir)


                                    .setPositiveButton("SI!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getContext(), "Ubicación guardada exitosamente", Toast.LENGTH_SHORT).show();
                                            Intent intent = getActivity().getIntent();
                                            intent.putExtra("address", String.valueOf(dir));
                                            intent.putExtra("latitude", String.valueOf(lat));
                                            intent.putExtra("longitude", String.valueOf(lng));
                                            //  setResult(RESULT_OK, intent);
                                            Log.d("Info response map", uInput + " - - " + lastLocation.getLatitude() + " - - " + lastLocation.getLongitude());
                                            //finish();

                                        }
                                    })
                                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create().show();

                            return false;

                        }
                    });


                }

                return false;
            }
        });
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}
