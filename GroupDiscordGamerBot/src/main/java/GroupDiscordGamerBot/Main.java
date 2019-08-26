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
		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		AudioPlayer player = playerManager.createPlayer();
		TrackScheduler trackScheduler = new TrackScheduler(player);
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
				AudioTrack track;
//				trackScheduler.getPlayer().playTrack(track);
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
