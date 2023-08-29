package dev.ov4client.addon.modules.info;

/*import dev.ov4client.addon.ov4client;
import dev.ov4client.addon.ModuleSetting;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatConfig extends ModuleSetting {
    public final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>().name("prefix").description("The way to render BedTrap prefix.").defaultValue(Mode.ov4).build());
    public final Setting<String> text = sgGeneral.add(new StringSetting.Builder().name("text").description("Text of the prefix").defaultValue("ov4client").build());
    public final Setting<Boolean> chatFormatting = sgGeneral.add(new BoolSetting.Builder().name("chat-formatting").description("Changes style of messages.").defaultValue(false).build());
    private final Setting<ChatFormatting> formattingMode = sgGeneral.add(new EnumSetting.Builder<ChatFormatting>().name("mode").description("The style of messages.").defaultValue(ChatFormatting.Bold).visible(chatFormatting::get).build());

    public ChatConfig() {
        super(ov4client.InfoCommand, "Chat Config", "The way to render chat messages.");
    }

    @Override
    public void onActivate() {
        if (mode.get() == Mode.ov4) ChatUtils.registerCustomPrefix("dev.ov4client.addon", this::getPrefix);
    }

    @EventHandler
    public void chatFormatting(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket) || !chatFormatting.get()) return;
        Text message = ((GameMessageS2CPacket) event.packet).content();

        mc.inGameHud.getChatHud().addMessage(Text.literal("").setStyle(Style.EMPTY.withFormatting(getFormatting(formattingMode.get()))).append(message));
        event.cancel();
    }

    public Text getPrefix() {
        MutableText logo = Text.literal(text.get());
        MutableText prefix = Text.literal("");
        logo.setStyle(logo.getStyle().withFormatting(Formatting.BLUE));
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.BLUE));
        prefix.append("[");
        prefix.append(logo);
        prefix.append("] ");
        return prefix;
    }

    private Formatting getFormatting(ChatFormatting chatFormatting) {
        return switch (chatFormatting) {
            case Obfuscated -> Formatting.OBFUSCATED;
            case Bold -> Formatting.BOLD;
            case Strikethrough -> Formatting.STRIKETHROUGH;
            case Underline -> Formatting.UNDERLINE;
            case Italic -> Formatting.ITALIC;
        };
    }

    public enum Mode {
        Always, ov4, None
    }

    public enum ChatFormatting {
        Obfuscated, Bold, Strikethrough, Underline, Italic
    }
}
*/
