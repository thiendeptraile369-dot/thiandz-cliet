package com.thiandz.client;

/** Màu nhấn dùng chung cho toàn bộ giao diện. */
public final class Theme {
    private Theme() {}

    public static final int[] PRESETS = {
        0x3B9BFF, // xanh dương (mặc định)
        0x3DDC84, // xanh lá
        0xFF5A5A, // đỏ
        0xB066FF, // tím
        0xFFA94D, // cam
        0x22D3EE  // lam
    };

    public static int accent = PRESETS[0];

    public static void nextAccent() {
        int idx = 0;
        for (int i = 0; i < PRESETS.length; i++) {
            if (PRESETS[i] == accent) idx = i;
        }
        accent = PRESETS[(idx + 1) % PRESETS.length];
    }
}
