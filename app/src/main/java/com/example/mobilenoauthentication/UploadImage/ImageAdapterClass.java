package com.example.mobilenoauthentication.UploadImage;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mobilenoauthentication.R;

import java.util.ArrayList;
import java.util.Objects;

public class ImageAdapterClass extends PagerAdapter {

    Context context;
    ArrayList<Uri> imageUri;
    LayoutInflater layoutInflater;
    public ImageAdapterClass(Context context, ArrayList<Uri> imageUri) {
        this.context = context;
        this.imageUri = imageUri;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return imageUri.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
       View view=layoutInflater.inflate(R.layout.single_image,container,false);
        ImageView imageView=view.findViewById(R.id.imageview_single_card_id);
        imageView.setImageURI(imageUri.get(position));

        Objects.requireNonNull(container).addView(view);

       return view;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
