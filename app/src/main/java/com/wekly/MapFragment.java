package com.wekly;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import com.android.volley.Request;

import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wekly.Model.Escort;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    private GoogleMap mMap;

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    Polyline polyline;
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private final int LOCATION_PERMISSION_CODE = 1;
    private boolean mLocationPermissionGranted = false;
    private static final int DEFAULT_ZOOM = 15;
    protected Location lastLocation = new Location("watever");
    public static List<List<HashMap<String, String>>> routes = new ArrayList<>();
    public Context context = getActivity();
    TextView dialog_escort_name, dialog_escort_price, dialog_escort_description ;
    CircleImageView dialog_escort_pic;
    Button dialog_escort_hire_button;
    private EditText mSearch;
    private String uInput;
    private RequestQueue requestQueue;
    public double lat = 0;
    public double lng = 0;
    public double latitude = 0;
    public  double longitude = 0;
    public String dir = "";
    Dialog mDialog;

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
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    lng = Double.parseDouble(String.valueOf(dataSnapshot.child("lng").getValue()));
                    lat = Double.parseDouble(String.valueOf(dataSnapshot.child("lat").getValue()));
                    String name = String.valueOf(dataSnapshot.child("name").getValue());
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.heart);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 80, 80, false);
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title(name));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(final Marker marker) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                            Query query = reference.child("escort").orderByChild("name").equalTo(marker.getTitle());
                            query.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    NumberFormat format = DecimalFormat.getInstance();
                                    format.setRoundingMode(RoundingMode.FLOOR);
                                    format.setMinimumFractionDigits(0);
                                    mDialog = new Dialog(getContext());
                                    mDialog.setContentView(R.layout.dialog_details);
                                    dialog_escort_description = mDialog.findViewById(R.id.dialog_escort_description);
                                    dialog_escort_price = mDialog.findViewById(R.id.dialog_escort_price);
                                    dialog_escort_name =mDialog.findViewById(R.id.dialog_escort_name);
                                    dialog_escort_pic=mDialog.findViewById(R.id.dialog_escort_image);
                                    dialog_escort_hire_button = mDialog.findViewById(R.id.dialog_button_hire);
                                    dialog_escort_name.setText(""+dataSnapshot.child("name").getValue());
                                    dialog_escort_description.setText(""+dataSnapshot.child("description").getValue());
                                    dialog_escort_price.setText("$ "+ dataSnapshot.child("price").getValue()+"");
                                    Picasso.with(getContext()).load(""+dataSnapshot.child("image").getValue()).into(dialog_escort_pic);
                                    mDialog.show();
                                    dialog_escort_hire_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                           // Toast.makeText(getContext(), "Contratada", Toast.LENGTH_SHORT).show();
                                            if(latitude != 0 && longitude !=0){
                                                webServiceObtenerRuta(latitude, longitude, marker.getPosition().latitude, marker.getPosition().longitude);
                                            }
                                            mDialog.dismiss();



                                        }
                                    });


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











                            return true;
                        }
                    });

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
        getLocationPermission();

        CameraUpdate point = CameraUpdateFactory.newLatLng(new LatLng(4.5994879, -74.0694694));

// moves camera to coordinates
        mMap.moveCamera(point);

        mMap.setMinZoomPreference(9.0f);
        mMap.setMaxZoomPreference(18.0f);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);


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
                             latitude = mLastKnownLocation.getLatitude();
                             longitude = mLastKnownLocation.getLongitude();
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
            if (mLocationPermissionGranted) {
                Log.d("Exception: %s", "Funopma");
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                Log.d("Exception: %s", "nel");
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
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

    private void webServiceObtenerRuta(double latitudInicial, double longitudInicial, double latitudFinal, double longitudFinal) {

        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyBM2mnz1mpG9_sYh-JPlJP8zqnCXaf8_i8";
        Log.d("Response",""+latitudInicial+""+longitudInicial+""+latitudFinal+""+longitudFinal);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            routes.add(path);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
                LatLng center = null;
                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                for(int i=0;i<routes.size();i++){
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Obteniendo el detalle de la ruta
                    List<HashMap<String, String>> path = routes.get(i);

                    // Obteniendo todos los puntos y/o coordenadas de la ruta
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        if (center == null) {
                            //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                            center = new LatLng(lat, lng);
                        }
                        points.add(position);
                    }
                    // Agregamos todos los puntos en la ruta al objeto LineOptions
                    lineOptions.addAll(points);
                    //Definimos el grosor de las Polilíneas
                    lineOptions.width(5);
                    //Definimos el color de la Polilíneas
                    lineOptions.color(Color.rgb(174,47,66));
                }
                 if(polyline!= null){
                     polyline.remove();
                 }
                 polyline = mMap.addPolyline(lineOptions);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        requestQueue.add(request);


    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
