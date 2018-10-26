package com.liteteam.mahabaliuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final int MY_LOCATION_REQUEST_CODE = 200;
    private static final int REQUEST_CHECK_SETTINGS = 300;
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;

    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    // Choose an arbitrary request code value
    private static final int RC_SIGN_IN = 123;

    FirebaseUser firebaseUser;
    CollectionReference rescueTeamLocationCollectionRef;
    CollectionReference userLocationCollectionsRef;

    HashMap<String, Marker> teamLocationMarkerHashMap;

    ListenerRegistration rescueTeamsListenerRegistration;
    ListenerRegistration engagedListenerRegistration;

    SharedPreferences sharedPreferences;

    Location userCurrentLocation;

    Button makeRequestButton;
    Button cancelRequestButton;
    ProgressBar requestProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FirebaseApp.initializeApp(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //find the reference to the collections
        rescueTeamLocationCollectionRef = FirebaseFirestore.getInstance()
                .collection(FirestoreCollections.RESCUE_TEAM_LOCATIONS.getName());
        userLocationCollectionsRef = FirebaseFirestore.getInstance()
                .collection(FirestoreCollections.USER_LOCATIONS.getName());

        teamLocationMarkerHashMap = new HashMap<>();

        //instance of shared preferences for managing status of user or team
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        //check for authentication state of the user
        initiateSignIn();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //finding necessary views
        makeRequestButton = findViewById(R.id.make_request_button);
        cancelRequestButton = findViewById(R.id.cancel_request_button);
        requestProgress = findViewById(R.id.progress_bar);

        //Defining the location callback class
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    UserLocation userLocation = new UserLocation(location.getLatitude(),
                            location.getLongitude());
                    String rescueTeamId = sharedPreferences.getString(getString(R.string.user_id),
                            null);
                    if(rescueTeamId != null) {
                        userLocation.setRescueTeamId(rescueTeamId);
                    }
                    userLocationCollectionsRef.document(firebaseUser.getUid()).set(userLocation);
                }
            }
        };
    }

    /**
     * Method to check authentication state of a user and perform
     * sign in if the user was not previously signed in already
     */
    private void initiateSignIn() {
        setUserAsFree();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            firebaseUser = auth.getCurrentUser();
            performSignedInActions();
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Collections.singletonList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        }
    }

    /**
     * Method creates a location request and searches for all teams
     */
    private void performSignedInActions() {
        showAllTeams();
    }

    /**
     * Sets user status as engaged
     */
    private void setUserAsEngaged() {
        sharedPreferences.edit()
                .putString(getString(R.string.status), UserStatus.ENGAGED.name())
                .apply();
    }

    /**
     * Sets user status as engaged
     */
    private void setUserAsRequested() {
        sharedPreferences.edit()
                .putString(getString(R.string.status), UserStatus.REQUESTED.name())
                .apply();
    }

    /**
     * Sets user status as free
     */
    private void setUserAsFree() {
        sharedPreferences.edit()
                .putString(getString(R.string.status), UserStatus.FREE.name())
                .apply();
    }

    /**
     * Adds ID of the engaged user
     * @param userId The ID of the user who has been engaged
     */
    private void setEngagedTeamId(String userId) {
        sharedPreferences.edit()
                .putString(getString(R.string.user_id), userId)
                .apply();
    }

    /**
     * Removed ID of any engaged user
     */
    private void removeEngagedTeamId() {
        sharedPreferences.edit()
                .putString(getString(R.string.user_id), null)
                .apply();
    }

    /**
     * Removes location trace of user from database and signs him out
     */
    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        //unregistering snapshot listener to prevent listening for changes
                        if(rescueTeamsListenerRegistration != null) {
                            rescueTeamsListenerRegistration.remove();
                        }
                        //deleting user location from database
                        userLocationCollectionsRef.document(firebaseUser.getUid()).delete();
                        //clearing any saved data about last user
                        firebaseUser = null;
                        //initiating sign in flow again
                        initiateSignIn();
                    }
                });
    }

    /**
     * Delete anu trace of user location from database is user leaves the app
     */
    @Override
    protected void onUserLeaveHint() {
        if(rescueTeamsListenerRegistration != null) {
            rescueTeamsListenerRegistration.remove();
        }
        if(firebaseUser != null) {
            userLocationCollectionsRef.document(firebaseUser.getUid()).delete();
        }
        setUserAsFree();
        removeEngagedTeamId();
        super.onUserLeaveHint();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method checks if location permission has been granted. If yes, add location
     * on the map. Else, request location permission
     */
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            addUserLocation();
        } else {
            // Show rationale and request permission.
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            int[] grantResults = {PackageManager.PERMISSION_DENIED};
            onRequestPermissionsResult(MY_LOCATION_REQUEST_CODE, permissions, grantResults);
        }
    }

    /**
     * Method to create a location request
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //update every 10 seconds
        mLocationRequest.setFastestInterval(5000); //fastest update every 5 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    /**
     * Method to start receiving Location updates
     */
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        setUserAsRequested();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    /**
     * Method to add user location to map
     */
    @SuppressLint("MissingPermission")
    private void addUserLocation() {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);

        //ensuring that map pans to the user's current location during start
        onMyLocationButtonClick();
    }

    /**
     * Method to register a listener that would add all the active teams on the map
     * and update their position frequently
     */
    private void showAllTeams() {
        rescueTeamsListenerRegistration = rescueTeamLocationCollectionRef.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for(DocumentChange documentChange : value.getDocumentChanges()) {

                            QueryDocumentSnapshot documentSnapshot = documentChange.getDocument();
                            String userId = documentSnapshot.getId();
                            TeamLocations teamLocations = documentSnapshot.toObject(TeamLocations.class);

                            if(!firebaseUser.getUid().equals(userId) && mMap != null) {
                                switch (documentChange.getType()) {
                                    case ADDED:
                                        Log.i(TAG, "Added TeamLocations = " + teamLocations);
                                        Log.i(TAG, "Added userId = " + userId);
                                        addTeamMarker(teamLocations, userId);
                                        break;
                                    case REMOVED:
                                        Log.i(TAG, "Removed TeamLocations = " + teamLocations);
                                        Log.i(TAG, "Removed userId = " + userId);
                                        teamLocationMarkerHashMap.remove(userId).remove();
                                        break;
                                    case MODIFIED:
                                        Log.i(TAG, "Modified TeamLocations = " + teamLocations);
                                        Log.i(TAG, "Modified userId = " + userId);
                                        teamLocationMarkerHashMap.remove(userId).remove();
                                        addTeamMarker(teamLocations, userId);
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Method shows the location of the engaged team
     * @param rescueTeamId ID of the engaged team
     */
    private void showEngagedTeam(final String rescueTeamId) {
        final HashMap<String, Marker> rescueTeamMarker = new HashMap<>();
        engagedListenerRegistration = rescueTeamLocationCollectionRef
                .document(rescueTeamId).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                TeamLocations teamLocation = documentSnapshot.toObject(TeamLocations.class);
                if(teamLocation != null) {
                    if(rescueTeamMarker.containsKey(rescueTeamId)) {
                        rescueTeamMarker.remove(rescueTeamId).remove();
                    }
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(teamLocation.getLatitude(),
                                    teamLocation.getLongitude())));
                    rescueTeamMarker.put(rescueTeamId, marker);
                }
            }
        });
    }

    /**
     * Method to add a team marker for each rescue team that is currently active
     * @param teamLocations The location of each team
     * @param userId The id of each team
     */
    private void addTeamMarker(TeamLocations teamLocations, String userId) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(teamLocations.getLatitude(),
                        teamLocations.longitude)));
        teamLocationMarkerHashMap.put(userId, marker);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we show the user's location on the map
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //adding user location on map: If user is on Lollipop or above,
        //request location permission in case it has not been granted
        //else simply add the location
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions
            checkLocationPermission();
        } else{
            // do something for phones running an SDK before lollipop
            addUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    addUserLocation();
                } else {
                    // permission denied, boo! Request for permission again
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_LOCATION_REQUEST_CODE);
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
            // Keeping this space just in case we may need it later

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: //checking settings for location request
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made ...
                        // ***REQUEST LAST LOCATION HERE***
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to ...
                        Toast.makeText(this,
                                R.string.settings_change_cancelled,
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        super.onActivityResult(requestCode, resultCode, data);
                        break;
                }
                break;
            case RC_SIGN_IN: //checking sign in response
                IdpResponse response = IdpResponse.fromResultIntent(data);

                // Successfully signed in
                if (resultCode == RESULT_OK) {
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    performSignedInActions();
                } else {
                    // Sign in failed
                    if (response == null) {
                        // User pressed back button
                        MapsActivity.this.finish(); //kill the app as user pressed back on sign in page
                        return;
                    } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, R.string.no_internet_message,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Sign-in error: ", response.getError());
                    initiateSignIn();
                }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Make any other necessary changes when the user clicks on location button
     */
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "TeamLocations button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int googleApiStatus = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);
        if(googleApiStatus == ConnectionResult.SUCCESS) {
            //make connection
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, googleApiStatus, 1,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(MapsActivity.this,
                                    R.string.google_play_services_error_dialog_cancelled,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    /**
     * Method to post a request to the database
     * @param view The button which was pressed to make request
     */
    public void makeRequest(View view) {
        createLocationRequest();
        toggleViews();
        userLocationCollectionsRef.document(firebaseUser.getUid()).addSnapshotListener(
                new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(documentSnapshot != null) {
                    if(documentSnapshot.exists()) {
                        //The request is active
                        UserLocation userLocation = documentSnapshot.toObject(UserLocation.class);

                        if (userLocation != null) {
                            if(userLocation.getRescueTeamId() != null) {
                                setUserAsEngaged();
                                setEngagedTeamId(userLocation.getRescueTeamId());
                                rescueTeamsListenerRegistration.remove();
                                teamLocationMarkerHashMap.clear();
                                mMap.clear();
                                showEngagedTeam(userLocation.getRescueTeamId());
                            } else {
                                setUserAsRequested();
                            }
                        }
                    } else if(!documentSnapshot.exists()) {
                        //The request has either been cancelled or completed
                        mMap.clear();
                        setUserAsFree();
                        removeEngagedTeamId();
                        showAllTeams();
                        if(engagedListenerRegistration != null) {
                            engagedListenerRegistration.remove();
                        }
                    }
                }
            }
        });
    }

    /**
     * Method to cancel the request
     * @param view The button which was pressed to make request
     */
    public void cancelRequest(View view) {
        removeLocationRequest();
        setUserAsFree();
        removeEngagedTeamId();
        toggleViews();
    }

    /**
     * Method to remove updation of location requests
     */
    private void removeLocationRequest() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Method displays correct UI based on scenario
     */
    private void toggleViews() {
        String status = sharedPreferences.getString(getString(R.string.status),
                UserStatus.FREE.name());
        if(status.equals(UserStatus.FREE.name())) {
            //user is free
            makeRequestButton.setVisibility(View.VISIBLE);
            cancelRequestButton.setVisibility(View.GONE);
            requestProgress.setVisibility(View.GONE);
        } else if(status.equals(UserStatus.REQUESTED.name())){
            //requested
            makeRequestButton.setVisibility(View.GONE);
            cancelRequestButton.setVisibility(View.VISIBLE);
            requestProgress.setVisibility(View.VISIBLE);
        } else {
            //engaged
            makeRequestButton.setVisibility(View.GONE);
            cancelRequestButton.setVisibility(View.VISIBLE);
            requestProgress.setVisibility(View.GONE);
        }
    }
}
