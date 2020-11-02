package com.mh.jwaer.familygo.data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng position;
    private String  title;
    private String snippet;
    private int iconPicture;
    private CombinedMember combinedMember;

    public ClusterMarker(LatLng position, String title, String snippet, int iconPicture, CombinedMember combinedMember) {
        this.position = position;
        this.title = title;
        this.snippet = snippet;
        this.iconPicture = iconPicture;
        this.combinedMember = combinedMember;
    }
    public ClusterMarker() {

    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getIconPicture() {
        return iconPicture;
    }

    public void setIconPicture(int iconPicture) {
        this.iconPicture = iconPicture;
    }

    public CombinedMember getCombinedUser() {
        return combinedMember;
    }

    public void setCombinedMember(CombinedMember combinedMember) {
        this.combinedMember = combinedMember;
    }
}
