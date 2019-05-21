package com.nickparisi.wollbot.service;

import com.nickparisi.wollbot.listeners.YesNoPromptListener;
import com.nickparisi.wollbot.utils.BotConstants;
import com.nickparisi.wollbot.utils.UserUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulerService {
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mma");

  //maps
  private Set<User> participants = new HashSet<>();
  private Map<User, Boolean> participantStatus = new HashMap<>();
  private Map<User, ListenerAdapter> listeners = new HashMap<>();
  //event
  private String event = "";
  private LocalDateTime eventDateTime;
  //prompt
  private MessageChannel channelToReportPrompts;
  private Timer promptReminderTimer;

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
    participants.addAll(message.getMentionedUsers().stream()
        .filter(user -> !user.isBot()).collect(Collectors.toList()));
  }

  public void promptParticipants(Message message) {
    if (isAdmin(message.getAuthor())) {
      if (promptReminderTimer != null) {
        promptReminderTimer.cancel();
      }
      channelToReportPrompts = message.getChannel();

      for (User participant : participants) {
        participantStatus.put(participant, false);

        ListenerAdapter listener = new YesNoPromptListener(participant, this::promptCallback);
        message.getJDA().addEventListener(listener);
        listeners.put(participant, listener);
        UserUtils.sendPrivateMessage(participant, "Please answer this message yes or no to confirm your status.");
      }

      TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
          for (User participant : participants) {
            UserUtils.sendPrivateMessage(participant, "Reminder: Please answer this message yes or no to confirm your status.");
          }
        }
      };
      promptReminderTimer = new Timer();
      promptReminderTimer.scheduleAtFixedRate(timerTask, BotConstants.PROMPT_REMINDER_INTERVAL_MS, BotConstants.PROMPT_REMINDER_INTERVAL_MS);
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

  public void reset(Message message) {
    participants = new HashSet<>();
    participantStatus = new HashMap<>();
    for (Map.Entry<User, ListenerAdapter> entry : listeners.entrySet()) {
      entry.getKey().getJDA().removeEventListener(entry.getValue()); //lol
    }
    listeners = new HashMap<>();

    event = "";
    eventDateTime = null;
    channelToReportPrompts = null;
    if (promptReminderTimer != null) {
      promptReminderTimer.cancel();
    }
    promptReminderTimer = null;
  }


  /*
  Callbacks
   */

  private void promptCallback(User user, Boolean value) {
    participantStatus.put(user, value);
    UserUtils.sendPrivateMessage(user, "Thank you for your response!");

    if (listeners.containsKey(user)) {
      ListenerAdapter listener = listeners.remove(user);
      user.getJDA().removeEventListener(listener);
    }

    if (value == false) {
      channelToReportPrompts.sendMessage(user.getAsMention() + " did not accept date!").queue();
    }

    if (listeners.isEmpty()) {
      promptReminderTimer.cancel();
    }
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
