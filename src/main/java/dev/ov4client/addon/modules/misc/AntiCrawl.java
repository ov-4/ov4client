package dev.ov4client.addon.modules.misc;

import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ov4Module;

public class AntiCrawl extends ov4Module {
    public AntiCrawl() {
        super(ov4client.Misc, "Anti Crawl", "Doesn't crawl or sneak when in low space (should be used on 1.12.2).");
    }
}
