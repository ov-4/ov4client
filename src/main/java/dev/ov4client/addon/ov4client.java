package dev.ov4client.addon;

import dev.ov4client.addon.commands.CoordsCommand;
import dev.ov4client.addon.commands.UUIDCommand;
import dev.ov4client.addon.modules.combat.XPThrower;
import dev.ov4client.addon.modules.settings.*;
import dev.ov4client.addon.gui.themes.rounded.ov4clientGuiTheme;
import dev.ov4client.addon.hud.*;
import dev.ov4client.addon.modules.combat.*;
import dev.ov4client.addon.modules.info.*;
import dev.ov4client.addon.modules.misc.*;
import dev.ov4client.addon.utils.Version;
import dev.ov4client.addon.utils.Wrapper;
import dev.ov4client.addon.utils.player.DeathUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.item.Items;

public class ov4client extends MeteorAddon {
    public static final Category Combat = new Category("Combat+", Items.TOTEM_OF_UNDYING.getDefaultStack());
    public static final Category Misc = new Category("Misc+", Items.GRASS_BLOCK.getDefaultStack());
    public static final Category Info = new Category("Info", Items.PLAYER_HEAD.getDefaultStack());
    public static final Category Settings = new Category("Settings", Items.OBSIDIAN.getDefaultStack());
    public static final HudGroup HUD_GROUP = new HudGroup("ov4client");
    public static final String ADDON;
    public static final String MOD_ID = "ov4-client";
    public static final Version VERSION;
    public static final ModMetadata MOD_META;
    public static final String COLOR = "Color is the visual perception of different wavelengths of light as hue, saturation, and brightness";

    static {
        MOD_META = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();

        ADDON = MOD_META.getName();

        String versionString = MOD_META.getVersion().getFriendlyString();
        if (versionString.contains("-")) versionString = versionString.split("-")[0];

        if (versionString.equals("${version}")) versionString = "0.0.0";

        VERSION = new Version(versionString);
    }

    @Override
    public void onInitialize() {
        log("Initializing ov4client");

        //--------------------Modules--------------------//
        initializeModules(Modules.get());
        initializeCommands();
        initializeHud(Hud.get());

        //--------------------Themes--------------------//
        GuiThemes.add(new ov4clientGuiTheme());

        //--------------------Utils--------------------//
        DeathUtils.init();
        Wrapper.init();

        log("Finish initializing ov4client");
    }

    private void initializeModules(Modules modules) {
        //--------------------Combat--------------------//
        modules.add(new AimAssist());
        modules.add(new AnchorAuraPlus());
        modules.add(new AntiAim());
//        modules.add(new AntiPistonCrystal());
        modules.add(new AntiRegear());
        modules.add(new AutoCrystal());
        modules.add(new AutoCrystalPlus());
        modules.add(new AutoHoleFill());
        modules.add(new AutoHoleFillPlus());
        modules.add(new AutoMine());
        modules.add(new AutoTrapPlus());
        modules.add(new BedBombV2());
        modules.add(new BedBombV4());
        modules.add(new Burrow());
        modules.add(new CevBreaker());
        modules.add(new CityBreaker());
        modules.add(new CityMiner());
        modules.add(new HolePush());
        modules.add(new HoleSnap());
        modules.add(new KillAura());
        modules.add(new NewSurround());
        modules.add(new OffHandPlus());
        modules.add(new PistonCrystal());
        modules.add(new SelfProtect());
        modules.add(new SelfTrapPlus());
        modules.add(new SpeedMine());
        modules.add(new Strafe());
        modules.add(new StrafePlus());
        modules.add(new SurroundPlus());
        modules.add(new TickShift());
        modules.add(new TNTAura());
        //--------------------Info--------------------//
        modules.add(new AnteroTaateli());
        modules.add(new AutoEz());
        modules.add(new AutoLoadKit());
        modules.add(new GroupChat());
        modules.add(new KillEffects());
        modules.add(new Notifications());
        //--------------------Misc--------------------//
        modules.add(new AntiCrawl());
        modules.add(new AutoCraft());
        modules.add(new Automation());
        modules.add(new AutoMoan());
        modules.add(new AutoPearl());
        modules.add(new AutoWither());
        modules.add(new BedCrafter());
        modules.add(new BreakESP());
        modules.add(new CustomFOV());
        modules.add(new EFlyBypass());
        modules.add(new ElytraFlyPlus());
        modules.add(new FlightPlus());
        modules.add(new FogRenderer());
        modules.add(new ForceSneak());
        modules.add(new InvMove());
        modules.add(new LightsOut());
        modules.add(new LogSpots());
        modules.add(new MidClickExtra());
        modules.add(new MultiTask());
        modules.add(new NoHurtCam());
        modules.add(new PacketFly());
//        modules.add(new PenisESP());
        modules.add(new PingSpoof());
        modules.add(new ScaffoldPlus());
        modules.add(new ShieldBypass());
        modules.add(new ShulkerDupe());
        modules.add(new SkinBlinker());
        modules.add(new SoundModifier());
        modules.add(new SprintPlus());
        modules.add(new StepPlus());
        modules.add(new StrictNoSlow());
        modules.add(new Suicide());
        modules.add(new SwingAnimation());
        modules.add(new Twerk());
        modules.add(new WeakNotifier());
        modules.add(new XPThrower());
        //--------------------Settings--------------------//
        modules.add(new FacingSettings());
        modules.add(new RangeSettings());
        modules.add(new RaytraceSettings());
        modules.add(new RotationSettings());
        modules.add(new ServerSettings());
        modules.add(new SwingSettings());
    }
    private void initializeCommands() {
        Commands.add(new CoordsCommand());
        Commands.add(new UUIDCommand());
    }

    private void initializeHud(Hud hud) {
        hud.register(ArmorHud.INFO);
        hud.register(CatGirl.INFO);
//        hud.register(ModuleArrayList.INFO);
        hud.register(NotificationsHud.INFO);
        hud.register(GearHud.INFO);
        hud.register(Keys.INFO);
        hud.register(Logo.INFO);
        hud.register(PacketHud.INFO);
        hud.register(Radar.INFO);
        hud.register(TargetHud.INFO);
        hud.register(ToastNotifications.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(Combat);
        Modules.registerCategory(Misc);
        Modules.registerCategory(Info);
        Modules.registerCategory(Settings);
    }

    public static void log(String message) {
        System.out.println("[" + ov4client.ADDON + "] " + message);
    }

    @Override
    public String getWebsite() {
        return "http://ov4client.cn/";
    }

    @Override
    public String getPackage() {
        return "dev.ov4client.addon";
    }
}
