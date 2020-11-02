package com.mh.jwaer.familygo.ui.main.hosts.maps;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.ClusterManager;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.adapters.MapsMembersAdapter;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.ClusterMarker;
import com.mh.jwaer.familygo.data.models.CombinedMember;
import com.mh.jwaer.familygo.data.models.UserLocationModel;
import com.mh.jwaer.familygo.data.network.NetworkUtil;
import com.mh.jwaer.familygo.databinding.FragmentMapsBinding;
import com.mh.jwaer.familygo.ui.home.LoadingActivity;
import com.mh.jwaer.familygo.util.Converters;
import com.mh.jwaer.familygo.util.MyClusterManagerRenderer;
import com.mh.jwaer.familygo.util.UserClient;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements MapsMembersAdapter.OnItemClickListener {

    private GoogleMap mMap;
    private MapsViewModel viewModel;
    private FragmentMapsBinding binding;
    private RecyclerView recyclerView;
    private List<CombinedMember> circleMembers = null;
    private List<UserLocationModel> circleMembersLocations = null;


    //clusters
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private boolean connectivitySnackFlag = false;

//--------------------------------Lifecycle methods overrides---------------------------------------


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false);

        recyclerView = binding.recyclerView;


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MapsViewModel.class);
        getLifecycle().addObserver(viewModel);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        if (!NetworkUtil.isNetworkAvailable()) showConnectivitySnackbar();
        if (!UserClient.getUser().isSharing() && !connectivitySnackFlag) showLocationSharingSnackbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.getCircleMembersLocationsTask().observe(this, new Observer<List<UserLocationModel>>() {
            @Override
            public void onChanged(List<UserLocationModel> userLocations) {
                updateMarkers(userLocations);
                circleMembersLocations = userLocations;
            }
        });
    }


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            try {
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                requireActivity(), R.raw.map_style));

            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }

            viewModel.getCircleMembersTask().observe(requireActivity(), new Observer<List<CircleMember>>() {
                @Override
                public void onChanged(List<CircleMember> circleMembers) {
                    setUpMembersRecycler(circleMembers);

                }
            });

            //observe circle members
            viewModel.getCircleCombinedMembers().observe(requireActivity(), new Observer<List<CombinedMember>>() {
                @Override
                public void onChanged(List<CombinedMember> combinedMembers) {
                    circleMembers = combinedMembers;
                    addMarkers(combinedMembers);
                }
            });
        }
    };

    @Override
    public void onItemClicked(int position) {
        if (circleMembers == null) return;
        if (!circleMembers.get(position).isSharing()) return;
        if (circleMembersLocations != null) {
            updateCameraPosition(
                    new LatLng(Double.parseDouble(circleMembersLocations.get(position).getLatitude()),
                            Double.parseDouble(circleMembersLocations.get(position).getLongitude()))
            );
            return;
        }
        updateCameraPosition(
                new LatLng(Double.parseDouble(circleMembers.get(position).getLatitude()),
                        Double.parseDouble(circleMembers.get(position).getLongitude()))
        );
    }

//------------------------------------------Helper Methods------------------------------------------
    private void addMarkers(List<CombinedMember> combinedMembers) {
    if (mMap == null) return;
    if (mClusterManager == null) { // initialize cluster manager
        mClusterManager = new ClusterManager<>(requireActivity().getApplicationContext(), mMap);
    }
    if (mClusterManagerRenderer == null) { // initialize cluster manager Renderer
        mClusterManagerRenderer = new MyClusterManagerRenderer(requireActivity().getApplicationContext(),
                mMap, mClusterManager);
        mClusterManager.setRenderer(mClusterManagerRenderer);
    }
    for (CombinedMember member : combinedMembers) {
        if (!member.isSharing()) continue;
        try {
            String snippet = Converters.unixToDateConverter(member.getTimestamp()) + " KSA";
            int avatar = R.drawable.add_profile_image;
            double lat = Double.parseDouble(member.getLatitude());
            double log = Double.parseDouble(member.getLongitude());
            ClusterMarker newClusterMarker = new ClusterMarker(
                    new LatLng(lat, log),
                    member.getName(),
                    snippet,
                    avatar,
                    member
            );
            mClusterManager.addItem(newClusterMarker);
            mClusterMarkers.add(newClusterMarker);
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    mClusterManager.cluster();
}

    private void setUpMembersRecycler(List<CircleMember> circleMembers) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        MapsMembersAdapter mapsMembersAdapter = new MapsMembersAdapter(this, getActivity());
        mapsMembersAdapter.setUsersList(circleMembers);
        recyclerView.setAdapter(mapsMembersAdapter);
    }

    private void updateMarkers(List<UserLocationModel> membersLocations) {
        try {
            for (ClusterMarker member : mClusterMarkers) {
                for (UserLocationModel memberLocation : membersLocations) {
                    if (member.getCombinedUser().getUid().equals(memberLocation.getUid())) {
                        double lat = Double.parseDouble(memberLocation.getLatitude());
                        double log = Double.parseDouble(memberLocation.getLongitude());
                        member.setSnippet(Converters.unixToDateConverter(memberLocation.getTimestamp()) + " KSA");
                        member.setPosition(new LatLng(lat, log));
                        mClusterManagerRenderer.setUpdateMarker(member);
                    }
                }

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void showConnectivitySnackbar() {
        Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, "connect to network to get updates and reload the map",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("reload map", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), LoadingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        snackbar.show();
        connectivitySnackFlag = true;
    }

    private void showLocationSharingSnackbar(){
        Snackbar snackbar = Snackbar.make(binding.coordinatorLayout, "Start Share your location with circle" +
                        ", Go to Settings -> Location Sharing",
                Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private void updateCameraPosition(LatLng location) {
        if (mMap == null) return;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .tilt(20)
                .zoom(15)
                .bearing(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}