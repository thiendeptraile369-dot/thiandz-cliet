package com.thiandz.client;

import com.thiandz.client.config.ConfigManager;
import com.thiandz.client.gui.ThiandzScreen;
import com.thiandz.client.module.ModuleManager;
import com.thiandz.client.waypoint.WaypointCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThiandzClient implements ClientModInitializer {
    public static final String MOD_ID = "thiandzclient";
    public static final Logger LOGGER = LoggerFactory.getLogger("Thiandz Client");
    private static boolean welcomed = false;

    public static KeyBinding OPEN_MENU;
    public static KeyBinding ZOOM_KEY;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[Thiandz Client] Dang khoi dong...");

        // 1. Phím tắt (1.21.9+ yêu cầu KeyBinding.Category)
        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
        OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.thiandzclient.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));
        ZOOM_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.thiandzclient.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, category));

        // 2. Đăng ký module rồi đọc cấu hình đã lưu
        ModuleManager.init();
        ConfigManager.load();

        // 3. Lệnh /tc (waypoint)
        WaypointCommands.register();

        // 4. Vẽ HUD ngay trước khung chat
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CHAT,
            Identifier.of(MOD_ID, "hud"),
            (context, tickCounter) -> ModuleManager.renderHud(context, tickCounter)
        );

        // 5. Tick: mở menu + cập nhật module + báo "đã tải" khi vào thế giới
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ThiandzScreen());
                }
            }
            ModuleManager.tick(client);

            if (client.player == null) {
                welcomed = false;
            } else if (!welcomed) {
                welcomed = true;
                String key = OPEN_MENU.getBoundKeyLocalizedText().getString();
                client.inGameHud.getChatHud().addMessage(Text.literal(
                    "§b[Thiandz Client] §fĐã tải! Nhấn §e" + key + " §fđể mở menu. / Loaded! Press §e" + key + " §fto open the menu."));
            }
        });

        // 6. Chữ nhận diện ở màn hình chính để biết mod đã chạy
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof TitleScreen) {
                ScreenEvents.afterRender(screen).register((scr, ctx, mouseX, mouseY, delta) ->
                    ctx.drawText(client.textRenderer, "§bThiandz Client §7v1.0.0 §8| Right Shift = menu",
                        4, scr.height - 12, 0xFFFFFFFF, true));
            }
        });

        LOGGER.info("[Thiandz Client] Da tai xong, {} module.", ModuleManager.all().size());
    }
}
