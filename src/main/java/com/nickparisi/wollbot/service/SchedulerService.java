package com.nickparisi.wollbot.service;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class SchedulerService {
  private static final String ADMIN ="woll smoth#4824";
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mma");

  private Set<User> participants = new HashSet<>();
  private String event;
  private LocalDateTime eventDateTime;

  /*
  Service Commands
   */

  public void newEvent(Message message) {
    if (isAdmin(message.getAuthor())) {
      String[] arguments = getArguments(message.getContentDisplay()).split(",");

      event = arguments[0];
      participants = new HashSet<>();
      eventDateTime = LocalDateTime.parse(arguments[1], formatter);

      StringBuffer sb = new StringBuffer();
      sb.append("Created New Event!" + System.lineSeparator());
      sb.append(event + System.lineSeparator());
      sb.append(eventDateTime.toString());

      message.getChannel().sendMessage(sb.toString()).queue();
    }
  }

  public void addParticipants(Message message) {
    participants.addAll(message.getMentionedUsers());
  }

  public void printStatus(Message message) {
    StringBuffer sb = new StringBuffer();

    sb.append("Current Event: " + System.lineSeparator());
    sb.append(event + System.lineSeparator());
    sb.append(eventDateTime.toString() + System.lineSeparator() + System.lineSeparator());
    sb.append("Participants: " + System.lineSeparator());
    for (User participant : participants) {
      sb.append(participant.getAsMention() + System.lineSeparator());
    }
    message.getChannel().sendMessage(sb.toString()).queue();
  }

  public void pingParticipants(Message message) {
    if (isAdmin(message.getAuthor())) {
      message.getChannel().sendMessage("Pinging all participants");

      for (User user : participants) {
        user.openPrivateChannel().queue(channel -> channel.sendMessage("Hello from a bot!").queue());
      }
    }
  }

  /*
  Helpers
   */

  private boolean isAdmin(User user) {
    return user.getAsTag().equals(ADMIN);
  }

  //Helper for stripping preceding prefix and command string from a message
  private String getArguments(String message) {
    int startIndex = message.indexOf(" ", message.indexOf(" ") + 1);
    return message.substring(startIndex + 1);
  }

}
