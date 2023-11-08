package mx.linkom.caseta_demolink.Controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerControlador extends FragmentPagerAdapter {
    int numofTabs;

    public PagerControlador(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.numofTabs=behavior;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new mx.linkom.caseta_demolink.Controller.UbicacionGeo();
            case 1:
                return new mx.linkom.caseta_demolink.Controller.Qr();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numofTabs;
    }
}
