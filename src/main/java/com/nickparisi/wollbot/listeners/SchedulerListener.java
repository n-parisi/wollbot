package com.nickparisi.wollbot.listeners;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SchedulerListener extends ListenerAdapter {

  List<User> participants = new ArrayList<>();

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.isFromType(ChannelType.TEXT)) {
      Message message = event.getMessage();
      String messageContents = message.getContentDisplay();

      if (messageContents.startsWith("!wolladd")) {
        message.getMentionedUsers().forEach(user -> participants.add(user));
      }

      if (messageContents.startsWith("!wollstatus")) {
        StringBuffer sb = new StringBuffer();
        sb.append("Currently aware of these users: " + System.lineSeparator());
        participants.forEach(user -> sb.append(user.getAsMention() + System.lineSeparator()));
        message.getChannel().sendMessage(sb.toString()).queue();
      }

      if (messageContents.startsWith("!wollannoy")) {
        participants.forEach(user -> {
          user.openPrivateChannel().queue(channel -> channel.sendMessage("Hello from a bot!").queue());
        });
      }
    }

  }
}
