package dev.ov4client.addon.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.hwid.Hwid;
import dev.ov4client.addon.utils.misc.Vec3dInfo;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static dev.ov4client.addon.ov4client.log;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Wrapper {
    public static void init() {
        setTitle(ov4client.ADDON + " " + ov4client.VERSION);
        setIcon();
        skipTutorial();
        Vec3dInfo.init();
        addServers();
        ChatUtils.registerCustomPrefix("dev.ov4client.addon", Wrapper::getPrefix);
    }

    public static Text getPrefix() {
        MutableText logo = Text.literal("ov4client");
        MutableText prefix = Text.literal("");
        logo.setStyle(logo.getStyle().withFormatting(Formatting.RED));
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.RED));
        prefix.append("[");
        prefix.append(logo);
        prefix.append("] ");
        return prefix;
    }

    private static void setIcon() {
        RenderSystem.assertInInitPhase();
        List<InputStream> list = List.of(Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/ov4-client/icons/icon_16x16.png")),
            Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/ov4-client/icons/icon_32x32.png")),
            Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/ov4-client/icons/icon_48x48.png")),
            Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/ov4-client/icons/icon_128x128.png")),
            Objects.requireNonNull(Wrapper.class.getResourceAsStream("/assets/ov4-client/icons/icon_256x256.png")));
        List<ByteBuffer> list2 = new ArrayList<>(list.size());

        try {
            MemoryStack memoryStack = MemoryStack.stackPush();

            try {
                GLFWImage.Buffer buffer = GLFWImage.malloc(list.size(), memoryStack);

                for (int i = 0; i < list.size(); ++i) {
                    NativeImage nativeImage = NativeImage.read(list.get(i));

                    try {
                        ByteBuffer byteBuffer = MemoryUtil.memAlloc(nativeImage.getWidth() * nativeImage.getHeight() * 4);
                        list2.add(byteBuffer);
                        byteBuffer.asIntBuffer().put(nativeImage.copyPixelsRgba());
                        buffer.position(i);
                        buffer.width(nativeImage.getWidth());
                        buffer.height(nativeImage.getHeight());
                        buffer.pixels(byteBuffer);
                    } catch (Throwable var19) {
                        try {
                            nativeImage.close();
                        } catch (Throwable var18) {
                            var19.addSuppressed(var18);
                        }

                        throw var19;
                    }

                    nativeImage.close();
                }

                GLFW.glfwSetWindowIcon(mc.getWindow().getHandle(), buffer.position(0));
            } catch (Throwable var20) {
                try {
                    memoryStack.close();
                } catch (Throwable var17) {
                    var20.addSuppressed(var17);
                }

                try {
                    throw var20;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            memoryStack.close();
        } finally {
            list2.forEach(MemoryUtil::memFree);
        }
    }

    public static void setTitle(String titleText) {
        Config.get().customWindowTitle.set(true);
        Config.get().customWindowTitleText.set(titleText);
        mc.getWindow().setTitle(titleText);
        Hwid.get();
    }

    public static void skipTutorial() {
        mc.getTutorialManager().setStep(TutorialStep.NONE);
    }

    public static int randomNum(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    public static void addServers() {
        if (!Hwid.getBoolean()) {
            log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQhIEhXSUQ6IA==")) + Hwid.getValue());
            System.exit(1);
        } else {
            log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBzdWNjZXNzZnVsISBXZWxjb21lIHRvIHVzZSBMZW1vbkNsaWVudCE=")));
/*            try {
                Hwid.sendWebhook();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        ServerList servers = new ServerList(mc);
        servers.loadFile();

        boolean b = false;
        for (int i = 0; i < servers.size(); i++) {
            ServerInfo server = servers.get(i);

            if (server.address.contains("pvp.obsserver.cn")) {
                b = true;
                break;
            }
            /*if (server.address.contains("3C3U.org")) {
                b = true;
                break;
            }*/
        }

        if (!b) {
            //servers.add(new ServerInfo("3C3U", "3C3U.org", false), false);
            servers.add(new ServerInfo("Test2B2TPvP", "pvp.obsserver.cn", false), false);
            servers.saveFile();
        }
    }
}
