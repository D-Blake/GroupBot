package GroupDiscordGamerBot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Main extends ListenerAdapter{

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
			case "!echo":
				event.getChannel().sendMessage(event.getMessage().getContentRaw().substring(cmd.length()+1)).queue();
				break;
		}
	}
}
