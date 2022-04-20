import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

public class Main {

    public static Logger log = LoggerFactory.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) throws LoginException {

        if (args == null) {
            System.out.println("Please start bot with Discord Bot Token.");
            return;
        }

        CommandClientBuilder commandClient = new CommandClientBuilder();
        commandClient.setOwnerId("419761037861060619");
        commandClient.forceGuildOnly("259938466769534976");

        try {
            registerCommands(commandClient);
        } catch (ReflectiveOperationException e) {
            log.error("Error on loading commands");
            e.printStackTrace();
            System.exit(1);
        }

        JDABuilder builder = JDABuilder.createDefault(args[0]);
        configureMemoryUsage(builder);
        builder.addEventListeners(
                commandClient.build());
        builder.build();


        log.info("cultureland bot is Starting!");

    }

    public static void configureMemoryUsage(JDABuilder builder) {
        builder.disableCache(CacheFlag.VOICE_STATE);
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.disableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING);
    }

    private static void registerCommands(CommandClientBuilder commandClient) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        log.info("Loading Commandsz");

        Reflections classes = new Reflections("com.cultureland.discord.command");
        Set<Class<?>> commands = classes.get(SubTypes.of(SlashCommand.class).asClass());

        if (commands.size() == 0) log.warn("There is no command in the registered package. No commands were loaded.");
        for (Class<?> command : commands) {
            if (command.isMemberClass()) continue;

            commandClient.addSlashCommand((SlashCommand) command.getConstructor().newInstance());
            log.info("Loaded " + command.getSimpleName() + " successfully");
        }
    }

}
