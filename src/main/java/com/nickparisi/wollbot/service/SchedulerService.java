package com.nickparisi.wollbot.service;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;

public class SchedulerService {
  private Set<User> participants = new HashSet<>();

  public void add(Message message) {
    participants.addAll(message.getMentionedUsers());
  }

  public void status(Message message) {
    StringBuffer sb = new StringBuffer();
    sb.append("Currently aware of these users: " + System.lineSeparator());
    participants.forEach(user -> sb.append(user.getAsMention() + System.lineSeparator()));
    message.getChannel().sendMessage(sb.toString()).queue();
  }

  public void ping(Message message) {
    message.getChannel().sendMessage("Pinging all participants");

    for (User user : participants) {
      user.openPrivateChannel().queue(channel -> channel.sendMessage("Hello from a bot!").queue());
    }
  }

}
