package com.tvm.tvm.util;

import com.squareup.picasso.Picasso;

public class ImageUtils {
    static volatile Picasso singleton = null;

    public static Picasso get() {
        if (singleton == null) {
            synchronized (Picasso.class) {
                if (singleton == null) {
                    if (PicassoProvider.context == null) {
                        throw new IllegalStateException("context == null");
                    }
                    singleton = new Picasso.Builder(PicassoProvider.context).build();
                }
            }
        }
        return singleton;
    }
}
