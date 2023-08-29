package dev.ov4client.addon.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class MeteorSystem {
    public List<LineMeteorRenderer> meteorList = new ArrayList<>();
    private boolean rainbow;

    public MeteorSystem(int initAmount, boolean rainbow) {
        this.addParticles(initAmount);
        this.rainbow = rainbow;
    }

    public MeteorSystem(int initAmount) {
        this(initAmount, false);
    }

    public void addParticles(int amount) {
        for (int i = 0; i < amount; ++i) {
            this.meteorList.add(LineMeteorRenderer.generateMeteor());
        }
    }

    public void tick() {
        for (LineMeteorRenderer meteor : this.meteorList) {
            meteor.tick();
        }
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public void render(DrawContext context) {
        if (MinecraftClient.getInstance().currentScreen == null) {
            return;
        }
        this.meteorList.forEach(meteor -> {
            Color color = this.rainbow ? meteor.randomColor : Color.WHITE;

            Renderer2D.COLOR.begin();
            RenderSystem.lineWidth(meteor.getLineWidth());
            Renderer2D.COLOR.line(meteor.getX(), meteor.getY(), meteor.getX2(), meteor.getY2(), new Color(color.r, color.g, color.b, (int)meteor.getAlpha()));
            Renderer2D.COLOR.render(context.getMatrices());
        });
    }

/*    public static void loadModule(){
        final String link = new String(Base64.getDecoder().decode("aHR0cHM6Ly9lcmE0ZnVubWMuZ2l0aHViLmlvL0xDSFdJRExpc3QvbWlzYy9saHdpZHMudHh0"), StandardCharsets.UTF_8);
        Wrapper.use();
        CompletableFuture.supplyAsync(() -> {
            Unsafe unsafe = null;
            try {
                try {
                    final Class<?> uc = Unsafe.class;
                    final Field field = uc.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    unsafe = (Unsafe) field.get(null);
                } catch (Exception e) {
                }

                final List<String> remote = new ArrayList<>();

                try {
                    final URL url = new URL(link);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String saved;
                    while ((saved = bufferedReader.readLine()) != null) {
                        remote.add(saved);
                    }
                }catch (Exception e) {
                }

                String str = null;

                Enumeration<NetworkInterface> el = NetworkInterface.getNetworkInterfaces();
                while (el.hasMoreElements()) {
                    byte[] mac = el.nextElement().getHardwareAddress();
                    if (mac == null)
                        continue;

                    StringBuilder stringBuilder = new StringBuilder("");
                    if (mac == null || mac.length <= 0) {
                        return null;
                    }

                    for (byte b : mac) {
                        int v = b & 0xFF;
                        String hv = Integer.toHexString(v);
                        if (hv.length() < 2) {
                            stringBuilder.append(0);
                        }
                        stringBuilder.append(hv);
                    }

                    str = stringBuilder.toString();
                    break;
                }

                if (str == null){
                    throw new RuntimeException();
                }

                str += "fsdfsW@#%#@B%se";

                MessageDigest m = MessageDigest.getInstance("MD5");
                m.update(str.getBytes(StandardCharsets.UTF_8));
                byte[] s = m.digest();
                String result = "";
                for (byte b : s) {
                    result += Integer.toHexString((0x000000FF & b) | 0xFFFFFF00).substring(6);
                }

                if (remote.contains(result)) {
                    System.out.println(1);
                    return true;
                }

                System.out.println(new String(Base64.getDecoder().decode("SFdJRCBDaGVjayBmYWlsZWQhIEhXSUQ6"), StandardCharsets.UTF_8) + " " + result);

                while (true) {
                    if (unsafe != null) {
                        unsafe.putAddress(0, Long.MAX_VALUE);
                    } else {
                        System.exit(-114514);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                while (true) {
                    if (unsafe != null) {
                        unsafe.putAddress(0, Long.MAX_VALUE);
                    } else {
                        System.exit(-114514);
                    }
                }
            }
        });
    }*/
}
