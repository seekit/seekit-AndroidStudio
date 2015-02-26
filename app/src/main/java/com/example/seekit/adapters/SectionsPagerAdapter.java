package com.example.seekit.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.example.seekit.FriendsFragment;
import com.example.seekit.R;
import com.example.seekit.ShareFragment;

import java.util.Locale;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    String idTri=null;
    String identificador=null;
    String nombreTri=null;
    String img=null;
    String json = null;



    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a DummySectionFragment (defined as a static inner class
        // below) with the page number as its lone argument.

        switch (position) {
            case 0:
                ShareFragment sad=new ShareFragment();

                return sad;

            case 1:

                      FriendsFragment friend =  new FriendsFragment();

                friend.setIdTri(idTri);
                friend.setImg(img);
                friend.setIdentificador(identificador);
                friend.setNombreTri(nombreTri);
                friend.setJson(json);

                return friend;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "Compartir Tri".toUpperCase(l);
            case 1:
                return "TRIs compartidos".toUpperCase(l);
        }
        return null;
    }

    public int getIcon(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_social_add_person;
            case 1:

                return R.drawable.ic_social_group;
        }

        return R.drawable.ic_action_share;
    }

    public String getIdTri() {
        return idTri;
    }

    public void setIdTri(String idTri) {
        this.idTri = idTri;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getNombreTri() {
        return nombreTri;
    }

    public void setNombreTri(String nombreTri) {
        this.nombreTri = nombreTri;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}





