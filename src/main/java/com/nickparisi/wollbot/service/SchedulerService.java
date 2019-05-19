package com.nickparisi.wollbot.service;

import com.nickparisi.wollbot.listeners.YesNoPromptListener;
import com.nickparisi.wollbot.utils.BotConstants;
import com.nickparisi.wollbot.utils.UserUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SchedulerService {
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mma");

  private Set<User> participants = new HashSet<>();
  private Map<User, Boolean> participantStatus = new HashMap<>();
  private String event = "";
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

  public void promptParticipants(Message message) {
    if (isAdmin(message.getAuthor())) {
      for (User participant : participants) {
        participantStatus.put(participant, false);

        PromptService.getInstance().startPromptBoolean(participant,
            "Please answer this message yes or no to confirm your status.",
            this::promptCallback);
      }
    }
  }

  public void printStatus(Message message) {
    StringBuffer sb = new StringBuffer();

    sb.append("Current Event: " + System.lineSeparator());
    if (!event.equals("")) {
      sb.append(event + System.lineSeparator());
      sb.append(eventDateTime.toString() + System.lineSeparator() + System.lineSeparator());
      sb.append("Participants: " + System.lineSeparator());
    }
    for (User participant : participants) {
      sb.append(participant.getAsMention());
      if (participantStatus.containsKey(participant)) {
        String status = participantStatus.get(participant) ? "Confirmed" : "Not Confirmed";
        sb.append("      (" + status + ")");
      }
      sb.append(System.lineSeparator());
    }
    message.getChannel().sendMessage(sb.toString()).queue();
  }

  public void pingParticipants(Message message) {
    if (isAdmin(message.getAuthor())) {
      message.getChannel().sendMessage("Pinging all participants");

      for (User user : participants) {
        UserUtils.sendPrivateMessage(user, "Hello from a bot!");
      }
    }
  }

  /*
  Callbacks
   */

  private void promptCallback(User user, Boolean value) {
    participantStatus.put(user, value);
    UserUtils.sendPrivateMessage(user, "Thank you for your response!");
    PromptService.getInstance().stopPrompt(user);
  }

  /*
  Helpers
   */

  private boolean isAdmin(User user) {
    return user.getAsTag().equals(BotConstants.ADMIN);
  }

  //Helper for stripping preceding prefix and command string from a message
  private String getArguments(String message) {
    int startIndex = message.indexOf(" ", message.indexOf(" ") + 1);
    return message.substring(startIndex + 1);
  }

}
