package com.mh.jwaer.familygo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.data.models.ClusterMarker;
import com.mh.jwaer.familygo.data.network.RetrofitClient;

import static com.mh.jwaer.familygo.util.CONSTANTS.NULL_IMAGE;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {
    private final IconGenerator iconGenerator;
    private final ImageView imageView;
    private Context _context;
    private Bitmap icon;


    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        _context = context;
        iconGenerator =new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        int markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        int markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        int padding = (int)context.getResources().getDimension(R.dimen.custom_marker_padding);
        imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(final ClusterMarker item, final MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIconPicture());
        icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected void onClusterItemRendered(ClusterMarker clusterItem, final Marker marker) {
        try {
            if (clusterItem.getCombinedUser().getPhotoUrl().equals(NULL_IMAGE)) return;
            String url = clusterItem.getCombinedUser().getPhotoUrl().replace("\\", "/");
            String photoUrl = RetrofitClient.BASE_URL + url;
            Glide.with(_context.getApplicationContext())
                    .load(photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .apply(new RequestOptions().signature(new ObjectKey("signature string")))
                    .thumbnail(0.1f)
                    .centerInside()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            imageView.setImageDrawable(resource);
                            icon = iconGenerator.makeIcon();
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return false;
    }


    public void setUpdateMarker(ClusterMarker clusterMarker) {
        Marker marker = getMarker(clusterMarker);
        if (marker != null) {
            marker.setPosition(clusterMarker.getPosition());
            marker.setSnippet(clusterMarker.getSnippet());
        }
    }
}