package GroupDiscordGamerBot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.*;

import music.AudioPlayerSendHandler;
import music.GuildMusicManager;
import music.PlayerManager;
import music.TrackScheduler;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Main extends ListenerAdapter {


    public static void main(String[] args) throws LoginException, IOException {
        // TODO Auto-generated method stub
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        Path path = Paths.get("BotToken.txt");
        String token = Files.readAllLines(path).get(0);
        builder.setToken(token);
        builder.addEventListeners(new Main());
        builder.build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        VoiceChannel myChannel = guild.getVoiceChannelsByName("music", true).get(0);
        AudioManager audioManager = guild.getAudioManager();
        AudioPlayerManager audioPM = new DefaultAudioPlayerManager();
        AudioPlayer player = audioPM.createPlayer();
        GuildMusicManager gmm = new GuildMusicManager(audioPM);
        TrackScheduler trackScheduler = new TrackScheduler(player);
        PlayerManager playerManager = PlayerManager.getInstance();
        player.addListener(trackScheduler);
        System.out.println("We received a message from " +
                event.getAuthor().getName() + ": " +
                event.getMessage().getContentDisplay());
        String cmd;

        if (event.getMessage().getContentRaw().trim().split(" ").length > 1) {
            cmd = event.getMessage().getContentRaw().trim().split(" ")[0].toString();
        } else {
            cmd = event.getMessage().getContentRaw().trim().toString();
        }

        switch (cmd) {
            case "!ping":
                event.getChannel().sendMessage("Pong!").queue();
                break;
            case "!say":
                event.getChannel().sendMessage(event.getMessage().getContentRaw().substring(cmd.length() + 1)).queue();
                break;
            case "!join":
                audioManager.openAudioConnection(myChannel);
                audioManager.setConnectTimeout(TimeUnit.MINUTES.toMillis(3));
                audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
                audioManager.openAudioConnection(myChannel);
                break;
            case "!leave":
                audioManager.closeAudioConnection();
                break;
            case "!play":
                playerManager.loadAndPlay(event.getTextChannel(), event.getMessage().getContentRaw().substring(cmd.length() + 1));
                playerManager.getGuildMusicManager(event.getGuild()).player.setVolume(10);
                break;
            case "!pause":
                gmm.scheduler.onPlayerPause(playerManager.getGuildMusicManager(event.getGuild()).player);
                break;
            case "!playlist":
                String s = "Queue: ";
                for (AudioTrack tr : playerManager.getGuildMusicManager(event.getGuild()).scheduler.getPlaylist()) {
                    s += tr.getInfo().title;
                }
                s.toCharArray()[s.length() - 1] = ' ';
                event.getChannel().sendMessage(s.trim()).queue();
                break;
            case "!resume":
                gmm.scheduler.onPlayerResume(playerManager.getGuildMusicManager(event.getGuild()).player);
                break;
            case "!volume":
                int vol = 0;
                try {
                    vol = Integer.parseInt(event.getMessage().getContentRaw().substring(cmd.length() + 1));
                } catch (NumberFormatException nf) {
                    System.out.println("Not a number");
                    break;
                }
                playerManager.getGuildMusicManager(event.getGuild()).player.setVolume(vol);
                break;
            case "!skip":
                playerManager.getGuildMusicManager(event.getGuild()).scheduler.nextTrack();
                System.out.println("Skipped Song");
                break;
            case "!kick":
                Member author = event.getMessage().getMember();

                if (!author.hasPermission(Permission.KICK_MEMBERS)) {
                    event.getChannel().sendMessage("You don't have permission to kick people!").queue();
                    return;
                }
                List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
                if ((mentionedMembers).isEmpty()) {
                    event.getChannel().sendMessage("You must mention who you want to be kicked").queue();
                    return;
                }
                if (!mentionedMembers.isEmpty()) {
                    String name = mentionedMembers.get(0).getAsMention();

                    guild.kick(mentionedMembers.get(0)).queue();
                    event.getChannel().sendMessage("Kicked " + name).queue();
                }
                break;
            case "!ban":
                Member person = event.getMessage().getMember();

                if (!person.hasPermission(Permission.BAN_MEMBERS)) {
                    event.getChannel().sendMessage("You don't have permission to ban people!").queue();
                    return;
                }
                List<Member> Member = event.getMessage().getMentionedMembers();

                if ((Member).isEmpty()) {
                    event.getChannel().sendMessage("You must mention who you want to be banned").queue();
                    return;
                }
                if (!Member.isEmpty()) {
                    String names = Member.get(0).getAsMention();

                    guild.ban(Member.get(0), 7).queue();
                    event.getChannel().sendMessage("Banned " + names).queue();

                }

                break;
            case "!mute":
                Member yeeter = event.getMessage().getMember();
                if (!yeeter.hasPermission(Permission.ADMINISTRATOR)) {
                    event.getChannel().sendMessage("You don't have permission to mute people!").queue();
                    return;
                }
                List<Member> Membes = event.getMessage().getMentionedMembers();
                if ((Membes).isEmpty()) {
                    event.getChannel().sendMessage("You must mention who you want to be muted").queue();
                    return;
                }
                net.dv8tion.jda.api.entities.Role role = cmd.matches("(!mute)") ? guild.getRolesByName("muted", true).get(0) : null;
                if (!Membes.isEmpty()) {
                    String nam = Membes.get(0).getAsMention();

                    guild.addRoleToMember(Membes.get(0), role).queue();
                    event.getChannel().sendMessage("Muted " + nam).queue();

                }
                break;
            case "!unmute":
                Member admin = event.getMessage().getMember();
                if (!admin.hasPermission(Permission.ADMINISTRATOR)) {
                    event.getChannel().sendMessage("You don't have permission to mute people!").queue();
                    return;
                }
                List<Member> Membe = event.getMessage().getMentionedMembers();
                if ((Membe).isEmpty()) {
                    event.getChannel().sendMessage("You must mention who you want to be unmuted").queue();
                    return;
                }
                net.dv8tion.jda.api.entities.Role rol = cmd.matches("(!unmute)") ? guild.getRolesByName("muted", true).get(0) : null;
                if (!Membe.isEmpty()) {
                    String nm = Membe.get(0).getAsMention();
                    guild.removeRoleFromMember(Membe.get(0), rol).queue();
                    event.getChannel().sendMessage("Unmuted " + nm).queue();

                }

            case "!addrole":
                Member admi = event.getMessage().getMember();
                if (!admi.hasPermission(Permission.ADMINISTRATOR)) {
                    event.getChannel().sendMessage("You don't have permission to mute people!").queue();
                    return;
                }
                List<Member> Memb = event.getMessage().getMentionedMembers();
                String n = Memb.get(0).getAsMention();
                if ((Memb).isEmpty()) {
                    event.getChannel().sendMessage("You must mention who you want to be unmuted").queue();
                    return;
                }
                net.dv8tion.jda.api.entities.Role ro = cmd.matches("(!addrole)") ? guild.getRolesByName("admin", true).get(0) : null;
                if (!Memb.isEmpty()) {
                    guild.addRoleToMember(Memb.get(0), ro).queue();
                    event.getChannel().sendMessage("Add role " + ro.getName() + " to " + n).queue();

                }
                break;
            case "!botstatus":
                break;
            case "!clear":
                if (!event.getMember().hasPermission((GuildChannel) event.getChannel(), Permission.MESSAGE_MANAGE)) {
                    event.getChannel().sendMessage("You don't have permission to delete messages!").queue();
                } else {
                    try {
                        int howMany = Integer.parseInt(event.getMessage().getContentRaw().substring(cmd.length() + 1).trim());
                        MessageHistory history = new MessageHistory(event.getTextChannel());
                        List<Message> msgs;
                        msgs = history.retrievePast(howMany+1).complete();
                        event.getChannel().purgeMessages(msgs);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case "!delrole":
                Member adm = event.getMessage().getMember();
                if (!adm.hasPermission(Permission.ADMINISTRATOR)) {
                    event.getChannel().sendMessage("You don't have permission to mute people!").queue();
                    return;
                }
                List<Member> Mem = event.getMessage().getMentionedMembers();
                if ((Mem).isEmpty()) {
                    event.getChannel().sendMessage("You must mention who you want to be unmuted").queue();
                    return;
                }
                net.dv8tion.jda.api.entities.Role r = cmd.matches("(!delrole)") ? guild.getRolesByName("admin", true).get(0) : null;
                if (!Mem.isEmpty()) {
                    String na = Mem.get(0).getAsMention();
                    guild.removeRoleFromMember(Mem.get(0), r).queue();
                    event.getChannel().sendMessage("Removed role " + r.getName() + " from " + na).queue();

                }
                break;
        }
    }
}
