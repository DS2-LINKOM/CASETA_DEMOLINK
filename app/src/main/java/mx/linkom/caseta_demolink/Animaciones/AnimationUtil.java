package mx.linkom.caseta_demolink.Animaciones;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

import mx.linkom.caseta_demolink.R;

public class AnimationUtil {
    public static void startAnimation(Context context, ConstraintLayout layout) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.anim);
        layout.startAnimation(animation);
    }
}