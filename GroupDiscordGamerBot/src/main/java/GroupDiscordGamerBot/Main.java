package GroupDiscordGamerBot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;

import music.AudioPlayerSendHandler;
import music.GuildMusicManager;
import music.PlayerManager;
import music.TrackScheduler;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Main extends ListenerAdapter{
	Guild g;

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
		g = event.getGuild();
		VoiceChannel myChannel = g.getVoiceChannelsByName("music", true).get(0);
		AudioManager audioManager = g.getAudioManager();
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
		if(event.getMessage().getContentRaw().trim().split(" ").length>1) {
			cmd = event.getMessage().getContentRaw().trim().split(" ")[0].toString();
		}else {
			cmd = event.getMessage().getContentRaw().trim().toString();
		}
		
		switch(cmd) {
			case "!ping":
				event.getChannel().sendMessage("Pong!").queue();
				break;
			case "!say":
				event.getChannel().sendMessage(event.getMessage().getContentRaw().substring(cmd.length()+1)).queue();
				break;
			case "!join":
				audioManager.openAudioConnection(myChannel);
				audioManager.setConnectTimeout(TimeUnit.MINUTES.toMillis(3));
				audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
				audioManager.openAudioConnection(myChannel);
				break;
			case"!leave":
				audioManager.closeAudioConnection();
				break;
			case "!play":
				playerManager.loadAndPlay(event.getTextChannel(), event.getMessage().getContentRaw().substring(cmd.length()+1));
				playerManager.getGuildMusicManager(event.getGuild()).player.setVolume(10);
				break;
			case "!pause":
					gmm.scheduler.onPlayerPause(playerManager.getGuildMusicManager(event.getGuild()).player);
				break;
			case "!playlist":
					String s = "Queue: ";
					for(AudioTrack tr : playerManager.getGuildMusicManager(event.getGuild()).scheduler.getPlaylist()) {
						s+=tr.getInfo().title;
					}
					s.toCharArray()[s.length()-1] = ' ';
					event.getChannel().sendMessage(s.strip()).queue();
				break;
			case "!resume": 
					gmm.scheduler.onPlayerResume(playerManager.getGuildMusicManager(event.getGuild()).player);
				break;
			case "!volume":
				int vol = 0;
				try {
					vol = Integer.parseInt(event.getMessage().getContentRaw().substring(cmd.length()+1));					
				}catch(NumberFormatException nf) {
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
				String userId = event.getMessage().getContentRaw().substring(cmd.length()+1).trim();
				List<Member> mentioned = event.getMessage().getMentionedMembers();
				try {
					for (Member m : mentioned
						 ) {
						g.kick(m).queue();
						event.getChannel().sendMessage("Kicked " + m.getNickname()).queue();
					}
				}catch (NullPointerException e){
					System.out.println("User was null");
				}
				break;
		}
	}
}
