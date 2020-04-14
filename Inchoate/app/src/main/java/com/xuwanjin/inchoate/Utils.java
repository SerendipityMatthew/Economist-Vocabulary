package com.xuwanjin.inchoate;

import androidx.navigation.NavController;

public class Utils {
    public static void navigationControllerUtils(NavController navController, int resId){
        if (navController != null){
            navController.navigate(resId);
        }
    }
}
