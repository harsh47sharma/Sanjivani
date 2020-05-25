package com.collection.sanjivani;

import android.net.Uri;

public class Uploads {

    private String mImageUrl;

    public Uploads(){

    }
    public Uploads(String imageUrl){
        this.mImageUrl = imageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

}
