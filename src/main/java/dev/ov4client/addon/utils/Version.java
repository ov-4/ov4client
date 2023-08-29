package dev.ov4client.addon.utils;

import dev.ov4client.addon.utils.misc.JSONUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;

public class Version {
    public static String API_URL = new String(Base64.getDecoder().decode("aHR0cDovLzd6aGFuLnRvcDo2MDAv"), StandardCharsets.UTF_8);

    private final String string;
    private final int[] numbers;

    public Version(String string) {
        this.string = string;
        this.numbers = new int[3];

        String[] split = string.split("\\.");
        if (split.length != 3) throw new IllegalArgumentException("Version string needs to have 3 numbers.");

        for (int i = 0; i < 3; i++) {
            try {
                numbers[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Failed to parse version string.");
            }
        }
    }

    public boolean isZero() {
        return numbers[0] == 0 && numbers[1] == 0 && numbers[2] == 0;
    }

    public boolean isHigherThan(Version version) {
        for (int i = 0; i < 3; i++) {
            if (numbers[i] > version.numbers[i]) return true;
            if (numbers[i] < version.numbers[i]) return false;
        }

        return false;
    }

    public static Version getInstallerVersion() {
        Scanner scanner = new Scanner(Objects.requireNonNull(Version.class.getResourceAsStream("/metadata.json"))).useDelimiter("\\A");
        return new Version(new JSONObject(scanner.hasNext() ? scanner.next() : "").getString("version"));
    }

    public static Version getLatest() throws IOException {
        return new Version(JSONUtils.readJsonFromUrl(API_URL + "Version/Installer/metadata.json").getString("latest_version"));
    }

    @Override
    public String toString() {
        return string;
    }
}
