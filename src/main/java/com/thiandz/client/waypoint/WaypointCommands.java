package com.thiandz.client.waypoint;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.thiandz.client.Lang;
import com.thiandz.client.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/** Lệnh: /tc add <tên> | /tc remove <tên> | /tc list   (alias: /thiandz) */
public class WaypointCommands {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(root("tc"));
            dispatcher.register(root("thiandz"));
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> root(String name) {
        return ClientCommandManager.literal(name)
            .then(ClientCommandManager.literal("add")
                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                    .executes(WaypointCommands::add)))
            .then(ClientCommandManager.literal("remove")
                .then(ClientCommandManager.argument("name", StringArgumentType.word())
                    .executes(WaypointCommands::remove)))
            .then(ClientCommandManager.literal("list").executes(WaypointCommands::list));
    }

    private static int add(CommandContext<FabricClientCommandSource> ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return 0;
        String name = StringArgumentType.getString(ctx, "name");
        BlockPos p = mc.player.getBlockPos();
        String dim = mc.world.getRegistryKey().getValue().toString();
        WaypointManager.add(new Waypoint(name, p.getX(), p.getY(), p.getZ(), dim));
        ConfigManager.save();
        ctx.getSource().sendFeedback(Lang.text("wp.added", name, p.getX(), p.getY(), p.getZ()));
        return 1;
    }

    private static int remove(CommandContext<FabricClientCommandSource> ctx) {
        String name = StringArgumentType.getString(ctx, "name");
        boolean ok = WaypointManager.remove(name);
        if (ok) ConfigManager.save();
        ctx.getSource().sendFeedback(Lang.text(ok ? "wp.removed" : "wp.notfound", name));
        return ok ? 1 : 0;
    }

    private static int list(CommandContext<FabricClientCommandSource> ctx) {
        if (WaypointManager.all().isEmpty()) {
            ctx.getSource().sendFeedback(Lang.text("wp.empty"));
            return 1;
        }
        for (Waypoint w : WaypointManager.all()) {
            ctx.getSource().sendFeedback(Text.literal(
                "§b" + w.name + " §7- " + w.x + " " + w.y + " " + w.z + " (" + w.dimension + ")"));
        }
        return 1;
    }
}
