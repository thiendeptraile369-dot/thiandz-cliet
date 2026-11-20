package com.thiandz.client;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

/** Tiện ích dịch chuỗi: tự thêm tiền tố "thiandzclient." vào key. */
public final class Lang {
    private Lang() {}

    public static String t(String key, Object... args) {
        return I18n.translate("thiandzclient." + key, args);
    }

    public static Text text(String key, Object... args) {
        return Text.translatable("thiandzclient." + key, args);
    }
}
