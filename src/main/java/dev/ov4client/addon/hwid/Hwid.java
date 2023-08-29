package dev.ov4client.addon.hwid;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

import static dev.ov4client.addon.ov4client.log;

public class Hwid {
    public static void get() {
        if (Hwid.getBoolean()) {
            log(new String(Base64.getDecoder().decode("SFdJRCBjaGVja2luZyBpcyBkaXNhYmxlZCBub3chIFdlbGNvbWUgdG8gdXNlIG92NENsaWVudCEgTWFkZSBieSBfb3Y0IGFuZCBOMW5lTDF2ZXNf")));
/*            try {
                Hwid.sendWebhook();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        } else {
//            log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQh")));
//            log(new String(Base64.getDecoder().decode("SFdJRCBjaGVjayBmYWlsZWQhIEhXSUQ6IA==")) + Hwid.getValue());
            System.exit(-19890604);
        }
    }

    public static boolean getBoolean() {
        String hwid = getValue();

        try {
            URL url = new URL(new String(Base64.getDecoder().decode("aHR0cHM6Ly9lcmE0ZnVubWMuZ2l0aHViLmlvL0xDSFdJRExpc3QvbWlzYy9saHdpZHMudHh0")));
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(hwid)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
//        return false;
    }

    // Send a discord webhook when someone is logging in
/*    public static void sendWebhook() throws IOException {
        try {
            // ur webhook url, if u even want to use webhook.
            Webhook webhook = new Webhook("");
            Webhook.EmbedObject embed = new Webhook.EmbedObject();
            // Embed content
            embed.setTitle("hwid");
            // Get current skin of the player and set it as the thumbnail
            embed.setThumbnail("https://crafatar.com/avatars/" + mc.getSession().getUuid() + "?size=128&overlay");
            embed.setDescription("New login - " + mc.getSession().getUsername());
            embed.setColor(Color.GRAY);
            embed.setFooter(getTime(), null);
            webhook.addEmbed(embed);

            if (validateHwid()) webhook.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static String getValue() {
        return DigestUtils.sha256Hex(System.getenv("os") + System.getProperty("os.name") + System.getProperty("os.arch") + System.getProperty("user.name") + System.getenv("PROCESSOR_LEVEL") + System.getenv("PROCESSOR_REVISION") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_ARCHITEW6432"));
    }

    /*public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return (formatter.format(date));
    }*/
}
