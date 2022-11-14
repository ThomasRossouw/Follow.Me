package com.example.heremapss;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.Color;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.GeoPolyline;
import com.here.sdk.core.Point2D;
import com.here.sdk.core.Rectangle2D;
import com.here.sdk.core.Size2D;
import com.here.sdk.core.engine.SDKNativeEngine;
import com.here.sdk.core.engine.SDKOptions;
import com.here.sdk.core.errors.InstantiationErrorException;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapview.LocationIndicator;
import com.here.sdk.mapview.MapCamera;
import com.here.sdk.mapview.MapError;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapMeasure;
import com.here.sdk.mapview.MapPolyline;
import com.here.sdk.mapview.MapScene;
import com.here.sdk.mapview.MapScheme;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapViewBase;
import com.here.sdk.mapview.PickMapContentResult;
import com.here.sdk.routing.CalculateRouteCallback;
import com.here.sdk.routing.CarOptions;
import com.here.sdk.routing.Maneuver;
import com.here.sdk.routing.ManeuverAction;
import com.here.sdk.routing.Route;
import com.here.sdk.routing.RoutingEngine;
import com.here.sdk.routing.RoutingError;
import com.here.sdk.routing.Section;
import com.here.sdk.routing.SectionNotice;
import com.here.sdk.routing.Waypoint;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private Context context;
    private CurrentLocation gpsTracker;
    //private TextView tvLatitude,tvLongitude;

    public double longit;
    public double lat;

    //object of the routing class
    private RoutingExample routingExample;
    private RoutingExample4 routingExample4;
    private RoutingExample3 routingExample3;

    public FloatingActionButton mod;
    public FloatingActionButton pop;
    public FloatingActionButton hist;
    public FloatingActionButton sos;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeHERESDK();

        //typecasting buttons
        mod = findViewById(R.id.btn1);
        pop = findViewById(R.id.btn2);
        hist = findViewById(R.id.btn3);
        sos = findViewById(R.id.SOS);

        // Get a MapView instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        //tvLatitude = (TextView)findViewById(R.id.latitude);
        //tvLongitude = (TextView)findViewById(R.id.longitude);
        loadMapScene();
        //getting permissions
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Toast.makeText(MainActivity.this, ""+lat+longit, Toast.LENGTH_SHORT).show();


// initialize navigation View
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.home);
        // perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePage.class));
                        overridePendingTransition(0,0);
                        return true;


                }
                return false;
            }
        });

    }
    public void SOS(View view){
        Intent SOSIntent  = new Intent(MainActivity.this,SOS.class);
        startActivity(SOSIntent);
    }

    //this handles the options menu for here maps functionality

    private void initializeHERESDK() {
        // Set your credentials for the HERE SDK.
        String accessKeyID = "3_h_Ezmr49RjaFl6lMqKrg";
        String accessKeySecret = "dG6iplNrL5xPAT13UGJqbLgyFNUhrmfDnhmvrJBsCzS-_vrfIqFtcjaDuqtkbUes4jd9xOO5qk_mJl-p0-yjvQ";
        SDKOptions options = new SDKOptions(accessKeyID, accessKeySecret);
        try {
            Context context = this;
            SDKNativeEngine.makeSharedInstance(context, options);
        } catch (InstantiationErrorException e) {
            throw new RuntimeException("Initialization of HERE SDK failed: " + e.error.name());
        }
    }

    private void loadMapScene() {
        // Load a scene from the HERE SDK to render the map with a map scheme.
        mapView.getMapScene().loadScene(MapScheme.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapError mapError) {
                gpsTracker = new CurrentLocation(MainActivity.this);
                if(gpsTracker.canGetLocation()){
                    longit = gpsTracker.getLongitude();
                    lat = gpsTracker.getLatitude();
                    //  tvLatitude.setText(String.valueOf(longit));
                    // tvLongitude.setText(String.valueOf(lat));
                }else{
                    gpsTracker.showSettingsAlert();
                }

                if (mapError == null) {
                    double distanceInMeters = 25 * 10;
                    MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);
                    routingExample = new RoutingExample(MainActivity.this, mapView);
                    routingExample4 = new RoutingExample4(MainActivity.this, mapView);
                    routingExample3 = new RoutingExample3(MainActivity.this, mapView);

                    mapView.getCamera().lookAt(
                            new GeoCoordinates(lat, longit), mapMeasureZoom);
                } else {
                    Log.d(TAG, "Loading map failed: mapError: " + mapError.name());
                }
            }
        });
    }
    private void disposeHERESDK() {
        // Free HERE SDK resources before the application shuts down.
        // Usually, this should be called only on application termination.
        // Afterwards, the HERE SDK is no longer usable unless it is initialized again.
        SDKNativeEngine sdkNativeEngine = SDKNativeEngine.getSharedInstance();
        if (sdkNativeEngine != null) {
            sdkNativeEngine.dispose();
            // For safety reasons, we explicitly set the shared instance to null to avoid situations,
            // where a disposed instance is accidentally reused.
            SDKNativeEngine.setSharedInstance(null);
        }
    }


    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        disposeHERESDK();
        super.onDestroy();
    }
    public void addRouteButtonClicked(View view) {
        routingExample.addRoute();
    }

    public void addWaypointsButtonClicked(View view) {
        //routingExample.addWaypoints();
        routingExample4.addRoute4();
    }

    public void clearMapButtonClicked(View view) {
        //routingExample.clearMap();
        routingExample3.addRoute3();
    }

    public void clearALL(View view) {
        routingExample.clearMap();
        routingExample3.clearMap();
        routingExample4.clearMap();
    }


    public class CurrentLocation extends Service implements LocationListener {

        private Context mContext;

        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public CurrentLocation(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    if (isNetworkEnabled) {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }

                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            //check the network permission
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                            }
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         * */

        public void stopUsingGPS(){
            if(locationManager != null){
                // locationManager.removeUpdates(com.example.task2here.CurrentLocation.class);
            }
        }

        /**
         * Function to get latitude
         * */

        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */

        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/wifi enabled
         * @return boolean
         * */

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        /**
         * Function to show settings alert dialog
         * On pressing Settings button will lauch Settings Options
         * */

        public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }


    }

    public class RoutingExample {
        private  final String TAG = com.example.heremapss.MainActivity.class.getName();
        private final Context context;
        private final MapView mapView;
        private final List<MapMarker> mapMarkerList = new ArrayList<>();
        private final List<MapPolyline> mapPolylines = new ArrayList<>();
        private final RoutingEngine routingEngine;
        private GeoCoordinates startGeoCoordinates;
        public GeoCoordinates destinationGeoCoordinates;
        private CurrentLocation gpsTracker;
        private double lat;
        private double longit;

        //call destination co ordinates
        // in the main activity class
        //once the user clicks a button the co ordinates will change







        //Co ordinates for popular, modern, historical landmarks









        public RoutingExample(Context context, MapView mapView) {
            this.context = context;
            this.mapView = mapView;

            gpsTracker = new CurrentLocation(context);
            if(gpsTracker.canGetLocation()){
                longit = gpsTracker.getLongitude();
                lat = gpsTracker.getLatitude();
                //  tvLatitude.setText(String.valueOf(longit));
                // tvLongitude.setText(String.valueOf(lat));
            }else{
                gpsTracker.showSettingsAlert();
            }
            MapCamera camera = mapView.getCamera();
            double distanceInMeters = 1000 * 10;
            MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);
            camera.lookAt(new GeoCoordinates(lat, longit), mapMeasureZoom);

            try {
                routingEngine = new RoutingEngine();
            } catch (InstantiationErrorException e) {
                throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
            }
        }

        public void addRoute() {


            startGeoCoordinates = new GeoCoordinates(lat,longit);
            //going to try and use if statements
            destinationGeoCoordinates = new GeoCoordinates(-33.8076, 18.3712);
            Waypoint startWaypoint = new Waypoint(startGeoCoordinates);
            Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);

            List<Waypoint> waypoints =
                    new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                Route route = routes.get(0);
                                showRouteDetails(route);
                                showRouteOnMap(route);
                                logRouteSectionDetails(route);
                                logRouteViolations(route);
                            } else {
                                showDialog("Error while calculating a route:", routingError.toString());
                            }
                        }
                    });
        }
        // A route may contain several warnings, for example, when a certain route option could not be fulfilled.
        // An implementation may decide to reject a route if one or more violations are detected.
        private void logRouteViolations(Route route) {
            for (Section section : route.getSections()) {
                for (SectionNotice notice : section.getSectionNotices()) {
                    Log.e(TAG, "This route contains the following warning: " + notice.code.toString());
                }
            }
        }

        private void logRouteSectionDetails(Route route) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");

            for (int i = 0; i< route.getSections().size(); i++) {
                Section section = route.getSections().get(i);

                Log.d(TAG, "Route Section : " + (i+1));
                Log.d(TAG, "Route Section Departure Time : "
                        + dateFormat.format(section.getDepartureLocationTime().localTime));
                Log.d(TAG, "Route Section Arrival Time : "
                        + dateFormat.format(section.getArrivalLocationTime().localTime));
                Log.d(TAG, "Route Section length : " +  section.getLengthInMeters() + " m");
                Log.d(TAG, "Route Section duration : " + section.getDuration().getSeconds() + " s");
            }
        }

        private void showRouteDetails(Route route) {
            long estimatedTravelTimeInSeconds = route.getDuration().getSeconds();
            int lengthInMeters = route.getLengthInMeters();

            //converts meters to miles
            double metermiles;
            metermiles = lengthInMeters * 0.00062137;

            String routeDetails = "Travel Time: " + formatTime(estimatedTravelTimeInSeconds)
                    + ", KM's: " + formatLength(lengthInMeters) +" Miles "+metermiles
                    + " \nRobben Island, Afrikaans Robbeneiland, island in Table Bay, Western Cape province, South Africa. " +
                    "Robben Island served as South Africa`s maximum-security prison. Most inmates, including Nelson Mandela, " +
                    "were black men incarcerated for political offenses.";

            showDialog("Route Details", routeDetails);
        }

        private String formatTime(long sec) {
            int hours = (int) (sec / 3600);
            int minutes = (int) ((sec % 3600) / 60);

            return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        }

        private String formatLength(int meters) {
            int kilometers = meters / 1000;
            int remainingMeters = meters % 1000;

            return String.format(Locale.getDefault(), "%02d.%02d km", kilometers, remainingMeters);
        }

        private void showRouteOnMap(Route route) {
            // Optionally, clear any previous route.
            clearMap();

            // Show route as polyline.
            GeoPolyline routeGeoPolyline = route.getGeometry();
            float widthInPixels = 20;
            com.here.sdk.core.Color rgb = Color.valueOf(0, 0.56f, 0.54f, 0.63f);
            MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline, widthInPixels, rgb); // RGBA

            mapView.getMapScene().addMapPolyline(routeMapPolyline);
            mapPolylines.add(routeMapPolyline);

            GeoCoordinates startPoint =
                    route.getSections().get(0).getDeparturePlace().mapMatchedCoordinates;
            GeoCoordinates destination =
                    route.getSections().get(route.getSections().size() - 1).getArrivalPlace().mapMatchedCoordinates;

            // Draw a circle to indicate starting point and destination.
            addCircleMapMarker(startPoint, R.drawable.red_dot);
            addCircleMapMarker(destination, R.drawable.green_dot);

            // Log maneuver instructions per route section.
            List<Section> sections = route.getSections();
            for (Section section : sections) {
                logManeuverInstructions(section);
            }
        }

        private void logManeuverInstructions(Section section) {
            Log.d(TAG, "Log maneuver instructions per route section:");
            List<Maneuver> maneuverInstructions = section.getManeuvers();
            for (Maneuver maneuverInstruction : maneuverInstructions) {
                ManeuverAction maneuverAction = maneuverInstruction.getAction();
                GeoCoordinates maneuverLocation = maneuverInstruction.getCoordinates();
                String maneuverInfo = maneuverInstruction.getText()
                        + ", Action: " + maneuverAction.name()
                        + ", Location: " + maneuverLocation.toString();
                Log.d(TAG, maneuverInfo);
            }
        }

        public void addWaypoints() {
            if (startGeoCoordinates == null || destinationGeoCoordinates == null) {
                showDialog("Error", "Please add a route first.");
                return;
            }

            Waypoint waypoint1 = new Waypoint(createRandomGeoCoordinatesAroundMapCenter());
            Waypoint waypoint2 = new Waypoint(createRandomGeoCoordinatesAroundMapCenter());
            List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(new Waypoint(startGeoCoordinates),
                    waypoint1, waypoint2, new Waypoint(destinationGeoCoordinates)));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                Route route = routes.get(0);
                                showRouteDetails(route);
                                showRouteOnMap(route);
                                logRouteSectionDetails(route);
                                logRouteViolations(route);

                                // Draw a circle to indicate the location of the waypoints.
                                addCircleMapMarker(waypoint1.coordinates, R.drawable.red_dot);
                                addCircleMapMarker(waypoint2.coordinates, R.drawable.green_dot);
                            } else {
                                showDialog("Error while calculating a route:", routingError.toString());
                            }
                        }
                    });
        }

        public void clearMap() {
            clearWaypointMapMarker();
            clearRoute();
        }

        private void clearWaypointMapMarker() {
            for (MapMarker mapMarker : mapMarkerList) {
                mapView.getMapScene().removeMapMarker(mapMarker);
            }
            mapMarkerList.clear();
        }

        private void clearRoute() {
            for (MapPolyline mapPolyline : mapPolylines) {
                mapView.getMapScene().removeMapPolyline(mapPolyline);
            }
            mapPolylines.clear();
        }

        private GeoCoordinates createRandomGeoCoordinatesAroundMapCenter() {
            GeoCoordinates centerGeoCoordinates = mapView.viewToGeoCoordinates(
                    new Point2D(mapView.getWidth() / 2, mapView.getHeight() / 2));
            if (centerGeoCoordinates == null) {
                // Should never happen for center coordinates.
                throw new RuntimeException("CenterGeoCoordinates are null");
            }
//        double lat = centerGeoCoordinates.latitude;
//        double lon = centerGeoCoordinates.longitude;
            return new GeoCoordinates(lat,longit);
        }

        private double getRandom(double min, double max) {
            return min + Math.random() * (max - min);
        }

        private void addCircleMapMarker(GeoCoordinates geoCoordinates, int resourceId) {
            MapImage mapImage = MapImageFactory.fromResource(context.getResources(), resourceId);
            MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage);
            mapView.getMapScene().addMapMarker(mapMarker);
            mapMarkerList.add(mapMarker);
        }

        private void showDialog(String title, String message) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }
    }
    public class RoutingExample3 {
        private  final String TAG = com.example.heremapss.MainActivity.class.getName();
        private final Context context;
        private final MapView mapView;
        private final List<MapMarker> mapMarkerList = new ArrayList<>();
        private final List<MapPolyline> mapPolylines = new ArrayList<>();
        private final RoutingEngine routingEngine;
        private GeoCoordinates startGeoCoordinates;
        public GeoCoordinates destinationGeoCoordinates;
        private CurrentLocation gpsTracker;
        private double lat;
        private double longit;

        //call destination co ordinates
        // in the main activity class
        //once the user clicks a button the co ordinates will change

        //Co ordinates for popular, modern, historical landmarks

        public RoutingExample3(Context context, MapView mapView) {
            this.context = context;
            this.mapView = mapView;

            gpsTracker = new CurrentLocation(context);
            if(gpsTracker.canGetLocation()){
                longit = gpsTracker.getLongitude();
                lat = gpsTracker.getLatitude();
                //  tvLatitude.setText(String.valueOf(longit));
                // tvLongitude.setText(String.valueOf(lat));
            }else{
                gpsTracker.showSettingsAlert();
            }
            MapCamera camera = mapView.getCamera();
            double distanceInMeters = 1000 * 10;
            MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);
            camera.lookAt(new GeoCoordinates(lat, longit), mapMeasureZoom);

            try {
                routingEngine = new RoutingEngine();
            } catch (InstantiationErrorException e) {
                throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
            }
        }

        public void addRoute3() {


            startGeoCoordinates = new GeoCoordinates(lat,longit);
            //going to try and use if statements
            destinationGeoCoordinates = new GeoCoordinates(26.8206, 30.8025);
            Waypoint startWaypoint = new Waypoint(startGeoCoordinates);
            Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);

            List<Waypoint> waypoints =
                    new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                Route route = routes.get(0);
                                showRouteDetails(route);
                                showRouteOnMap(route);
                                logRouteSectionDetails(route);
                                logRouteViolations(route);
                            } else {
                                showDialog("Error while calculating a route:", routingError.toString());
                            }
                        }
                    });
        }
        // A route may contain several warnings, for example, when a certain route option could not be fulfilled.
        // An implementation may decide to reject a route if one or more violations are detected.
        private void logRouteViolations(Route route) {
            for (Section section : route.getSections()) {
                for (SectionNotice notice : section.getSectionNotices()) {
                    Log.e(TAG, "This route contains the following warning: " + notice.code.toString());
                }
            }
        }

        private void logRouteSectionDetails(Route route) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");

            for (int i = 0; i< route.getSections().size(); i++) {
                Section section = route.getSections().get(i);

                Log.d(TAG, "Route Section : " + (i+1));
                Log.d(TAG, "Route Section Departure Time : "
                        + dateFormat.format(section.getDepartureLocationTime().localTime));
                Log.d(TAG, "Route Section Arrival Time : "
                        + dateFormat.format(section.getArrivalLocationTime().localTime));
                Log.d(TAG, "Route Section length : " +  section.getLengthInMeters() + " m");
                Log.d(TAG, "Route Section duration : " + section.getDuration().getSeconds() + " s");
            }
        }

        private void showRouteDetails(Route route) {
            long estimatedTravelTimeInSeconds = route.getDuration().getSeconds();
            int lengthInMeters = route.getLengthInMeters();

            //converts meters to miles
            double metermiles;
            metermiles = lengthInMeters * 0.00062137;

            String routeDetails = "Travel Time: " + formatTime(estimatedTravelTimeInSeconds)
                    + ", KM's: " + formatLength(lengthInMeters) +" Miles "+metermiles
                    + " \nCairo, Arabic Al-Qāhirah (“The Victorious”), city, capital of Egypt, and one of the largest cities in Africa. " +
                    "Cairo has stood for more than 1,000 years on the same site on the banks of the Nile, primarily on the eastern shore, " +
                    "some 500 miles (800 km) downstream from the Aswan High Dam. Located in the northeast of the country, " +
                    "Cairo is the gateway to the Nile delta, where the lower Nile separates into the Rosetta and Damietta branches" ;

            showDialog("Route Details", routeDetails);
        }

        private String formatTime(long sec) {
            int hours = (int) (sec / 3600);
            int minutes = (int) ((sec % 3600) / 60);

            return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        }

        private String formatLength(int meters) {
            int kilometers = meters / 1000;
            int remainingMeters = meters % 1000;

            return String.format(Locale.getDefault(), "%02d.%02d km", kilometers, remainingMeters);
        }

        private void showRouteOnMap(Route route) {
            // Optionally, clear any previous route.
            clearMap();

            // Show route as polyline.
            GeoPolyline routeGeoPolyline = route.getGeometry();
            float widthInPixels = 20;
            com.here.sdk.core.Color rgb = Color.valueOf(0, 0.56f, 0.54f, 0.63f);
            MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline, widthInPixels, rgb); // RGBA

            mapView.getMapScene().addMapPolyline(routeMapPolyline);
            mapPolylines.add(routeMapPolyline);

            GeoCoordinates startPoint =
                    route.getSections().get(0).getDeparturePlace().mapMatchedCoordinates;
            GeoCoordinates destination =
                    route.getSections().get(route.getSections().size() - 1).getArrivalPlace().mapMatchedCoordinates;

            // Draw a circle to indicate starting point and destination.
            addCircleMapMarker(startPoint, R.drawable.red_dot);
            addCircleMapMarker(destination, R.drawable.green_dot);

            // Log maneuver instructions per route section.
            List<Section> sections = route.getSections();
            for (Section section : sections) {
                logManeuverInstructions(section);
            }
        }

        private void logManeuverInstructions(Section section) {
            Log.d(TAG, "Log maneuver instructions per route section:");
            List<Maneuver> maneuverInstructions = section.getManeuvers();
            for (Maneuver maneuverInstruction : maneuverInstructions) {
                ManeuverAction maneuverAction = maneuverInstruction.getAction();
                GeoCoordinates maneuverLocation = maneuverInstruction.getCoordinates();
                String maneuverInfo = maneuverInstruction.getText()
                        + ", Action: " + maneuverAction.name()
                        + ", Location: " + maneuverLocation.toString();
                Log.d(TAG, maneuverInfo);
            }
        }

        public void addWaypoints() {
            if (startGeoCoordinates == null || destinationGeoCoordinates == null) {
                showDialog("Error", "Please add a route first.");
                return;
            }

            Waypoint waypoint1 = new Waypoint(createRandomGeoCoordinatesAroundMapCenter());
            Waypoint waypoint2 = new Waypoint(createRandomGeoCoordinatesAroundMapCenter());
            List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(new Waypoint(startGeoCoordinates),
                    waypoint1, waypoint2, new Waypoint(destinationGeoCoordinates)));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                Route route = routes.get(0);
                                showRouteDetails(route);
                                showRouteOnMap(route);
                                logRouteSectionDetails(route);
                                logRouteViolations(route);

                                // Draw a circle to indicate the location of the waypoints.
                                addCircleMapMarker(waypoint1.coordinates, R.drawable.red_dot);
                                addCircleMapMarker(waypoint2.coordinates, R.drawable.green_dot);
                            } else {
                                showDialog("Error while calculating a route:", routingError.toString());
                            }
                        }
                    });
        }

        public void clearMap() {
            clearWaypointMapMarker();
            clearRoute();
        }

        private void clearWaypointMapMarker() {
            for (MapMarker mapMarker : mapMarkerList) {
                mapView.getMapScene().removeMapMarker(mapMarker);
            }
            mapMarkerList.clear();
        }

        private void clearRoute() {
            for (MapPolyline mapPolyline : mapPolylines) {
                mapView.getMapScene().removeMapPolyline(mapPolyline);
            }
            mapPolylines.clear();
        }

        private GeoCoordinates createRandomGeoCoordinatesAroundMapCenter() {
            GeoCoordinates centerGeoCoordinates = mapView.viewToGeoCoordinates(
                    new Point2D(mapView.getWidth() / 2, mapView.getHeight() / 2));
            if (centerGeoCoordinates == null) {
                // Should never happen for center coordinates.
                throw new RuntimeException("CenterGeoCoordinates are null");
            }
//        double lat = centerGeoCoordinates.latitude;
//        double lon = centerGeoCoordinates.longitude;
            return new GeoCoordinates(lat,longit);
        }

        private double getRandom(double min, double max) {
            return min + Math.random() * (max - min);
        }

        private void addCircleMapMarker(GeoCoordinates geoCoordinates, int resourceId) {
            MapImage mapImage = MapImageFactory.fromResource(context.getResources(), resourceId);
            MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage);
            mapView.getMapScene().addMapMarker(mapMarker);
            mapMarkerList.add(mapMarker);
        }

        private void showDialog(String title, String message) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }
    }

    public class RoutingExample4 {
        private  final String TAG = com.example.heremapss.MainActivity.class.getName();
        private final Context context;
        private final MapView mapView;
        private final List<MapMarker> mapMarkerList = new ArrayList<>();
        private final List<MapPolyline> mapPolylines = new ArrayList<>();
        private final RoutingEngine routingEngine;
        private GeoCoordinates startGeoCoordinates;
        public GeoCoordinates destinationGeoCoordinates;
        private CurrentLocation gpsTracker;
        private double lat;
        private double longit;

        //call destination co ordinates
        // in the main activity class
        //once the user clicks a button the co ordinates will change







        //Co ordinates for popular, modern, historical landmarks









        public RoutingExample4(Context context, MapView mapView) {
            this.context = context;
            this.mapView = mapView;

            gpsTracker = new CurrentLocation(context);
            if(gpsTracker.canGetLocation()){
                longit = gpsTracker.getLongitude();
                lat = gpsTracker.getLatitude();
                //  tvLatitude.setText(String.valueOf(longit));
                // tvLongitude.setText(String.valueOf(lat));
            }else{
                gpsTracker.showSettingsAlert();
            }
            MapCamera camera = mapView.getCamera();
            double distanceInMeters = 1000 * 10;
            MapMeasure mapMeasureZoom = new MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters);
            camera.lookAt(new GeoCoordinates(lat, longit), mapMeasureZoom);

            try {
                routingEngine = new RoutingEngine();
            } catch (InstantiationErrorException e) {
                throw new RuntimeException("Initialization of RoutingEngine failed: " + e.error.name());
            }
        }

        public void addRoute4() {


            startGeoCoordinates = new GeoCoordinates(lat,longit);
            //going to try and use if statements
            destinationGeoCoordinates = new GeoCoordinates(25.3548, 51.1839);
            Waypoint startWaypoint = new Waypoint(startGeoCoordinates);
            Waypoint destinationWaypoint = new Waypoint(destinationGeoCoordinates);

            List<Waypoint> waypoints =
                    new ArrayList<>(Arrays.asList(startWaypoint, destinationWaypoint));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                Route route = routes.get(0);
                                showRouteDetails(route);
                                showRouteOnMap(route);
                                logRouteSectionDetails(route);
                                logRouteViolations(route);
                            } else {
                                showDialog("Error while calculating a route:", routingError.toString());
                            }
                        }
                    });
        }
        // A route may contain several warnings, for example, when a certain route option could not be fulfilled.
        // An implementation may decide to reject a route if one or more violations are detected.
        private void logRouteViolations(Route route) {
            for (Section section : route.getSections()) {
                for (SectionNotice notice : section.getSectionNotices()) {
                    Log.e(TAG, "This route contains the following warning: " + notice.code.toString());
                }
            }
        }

        private void logRouteSectionDetails(Route route) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm");

            for (int i = 0; i< route.getSections().size(); i++) {
                Section section = route.getSections().get(i);

                Log.d(TAG, "Route Section : " + (i+1));
                Log.d(TAG, "Route Section Departure Time : "
                        + dateFormat.format(section.getDepartureLocationTime().localTime));
                Log.d(TAG, "Route Section Arrival Time : "
                        + dateFormat.format(section.getArrivalLocationTime().localTime));
                Log.d(TAG, "Route Section length : " +  section.getLengthInMeters() + " m");
                Log.d(TAG, "Route Section duration : " + section.getDuration().getSeconds() + " s");
            }
        }

        private void showRouteDetails(Route route) {
            long estimatedTravelTimeInSeconds = route.getDuration().getSeconds();
            int lengthInMeters = route.getLengthInMeters();

            //converts meters to miles
            double metermiles;
            metermiles = lengthInMeters * 0.00062137;

            String routeDetails = "Travel Time: " + formatTime(estimatedTravelTimeInSeconds)
                    + ", KM's: " + formatLength(lengthInMeters) +" Miles "+metermiles
                    + "\nQatar is a high income economy and is a developed country, with the world`s third largest natural gas reserves and oil reserves. Qatar is classified by the UN as a country of very high human development and is the most advanced Arab state for human development." +
                    "\n" + "For its small size, Qatar has a lot of influence in the world, and has been identified as a middle power. Qatar will host the 2022 FIFA World Cup, becoming the first Arab country to do so.";

            showDialog("Route Details", routeDetails);
        }

        private String formatTime(long sec) {
            int hours = (int) (sec / 3600);
            int minutes = (int) ((sec % 3600) / 60);

            return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
        }

        private String formatLength(int meters) {
            int kilometers = meters / 1000;
            int remainingMeters = meters % 1000;

            return String.format(Locale.getDefault(), "%02d.%02d km", kilometers, remainingMeters);
        }

        private void showRouteOnMap(Route route) {
            // Optionally, clear any previous route.
            clearMap();

            // Show route as polyline.
            GeoPolyline routeGeoPolyline = route.getGeometry();
            float widthInPixels = 20;
            com.here.sdk.core.Color rgb = Color.valueOf(0, 0.56f, 0.54f, 0.63f);
            MapPolyline routeMapPolyline = new MapPolyline(routeGeoPolyline, widthInPixels, rgb); // RGBA

            mapView.getMapScene().addMapPolyline(routeMapPolyline);
            mapPolylines.add(routeMapPolyline);

            GeoCoordinates startPoint =
                    route.getSections().get(0).getDeparturePlace().mapMatchedCoordinates;
            GeoCoordinates destination =
                    route.getSections().get(route.getSections().size() - 1).getArrivalPlace().mapMatchedCoordinates;

            // Draw a circle to indicate starting point and destination.
            addCircleMapMarker(startPoint, R.drawable.red_dot);
            addCircleMapMarker(destination, R.drawable.green_dot);

            // Log maneuver instructions per route section.
            List<Section> sections = route.getSections();
            for (Section section : sections) {
                logManeuverInstructions(section);
            }
        }

        private void logManeuverInstructions(Section section) {
            Log.d(TAG, "Log maneuver instructions per route section:");
            List<Maneuver> maneuverInstructions = section.getManeuvers();
            for (Maneuver maneuverInstruction : maneuverInstructions) {
                ManeuverAction maneuverAction = maneuverInstruction.getAction();
                GeoCoordinates maneuverLocation = maneuverInstruction.getCoordinates();
                String maneuverInfo = maneuverInstruction.getText()
                        + ", Action: " + maneuverAction.name()
                        + ", Location: " + maneuverLocation.toString();
                Log.d(TAG, maneuverInfo);
            }
        }

        public void addWaypoints() {
            if (startGeoCoordinates == null || destinationGeoCoordinates == null) {
                showDialog("Error", "Please add a route first.");
                return;
            }

            Waypoint waypoint1 = new Waypoint(createRandomGeoCoordinatesAroundMapCenter());
            Waypoint waypoint2 = new Waypoint(createRandomGeoCoordinatesAroundMapCenter());
            List<Waypoint> waypoints = new ArrayList<>(Arrays.asList(new Waypoint(startGeoCoordinates),
                    waypoint1, waypoint2, new Waypoint(destinationGeoCoordinates)));

            routingEngine.calculateRoute(
                    waypoints,
                    new CarOptions(),
                    new CalculateRouteCallback() {
                        @Override
                        public void onRouteCalculated(@Nullable RoutingError routingError, @Nullable List<Route> routes) {
                            if (routingError == null) {
                                Route route = routes.get(0);
                                showRouteDetails(route);
                                showRouteOnMap(route);
                                logRouteSectionDetails(route);
                                logRouteViolations(route);

                                // Draw a circle to indicate the location of the waypoints.
                                addCircleMapMarker(waypoint1.coordinates, R.drawable.red_dot);
                                addCircleMapMarker(waypoint2.coordinates, R.drawable.green_dot);
                            } else {
                                showDialog("Error while calculating a route:", routingError.toString());
                            }
                        }
                    });
        }

        public void clearMap() {
            clearWaypointMapMarker();
            clearRoute();
        }

        private void clearWaypointMapMarker() {
            for (MapMarker mapMarker : mapMarkerList) {
                mapView.getMapScene().removeMapMarker(mapMarker);
            }
            mapMarkerList.clear();
        }

        private void clearRoute() {
            for (MapPolyline mapPolyline : mapPolylines) {
                mapView.getMapScene().removeMapPolyline(mapPolyline);
            }
            mapPolylines.clear();
        }

        private GeoCoordinates createRandomGeoCoordinatesAroundMapCenter() {
            GeoCoordinates centerGeoCoordinates = mapView.viewToGeoCoordinates(
                    new Point2D(mapView.getWidth() / 2, mapView.getHeight() / 2));
            if (centerGeoCoordinates == null) {
                // Should never happen for center coordinates.
                throw new RuntimeException("CenterGeoCoordinates are null");
            }
//        double lat = centerGeoCoordinates.latitude;
//        double lon = centerGeoCoordinates.longitude;
            return new GeoCoordinates(lat,longit);
        }

        private double getRandom(double min, double max) {
            return min + Math.random() * (max - min);
        }

        private void addCircleMapMarker(GeoCoordinates geoCoordinates, int resourceId) {
            MapImage mapImage = MapImageFactory.fromResource(context.getResources(), resourceId);
            MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage);
            mapView.getMapScene().addMapMarker(mapMarker);
            mapMarkerList.add(mapMarker);
        }

        private void showDialog(String title, String message) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
        }



    }
















}



