package com.nickparisi.wollbot.listeners;

import com.nickparisi.wollbot.commands.MessageCommand;
import com.nickparisi.wollbot.service.SchedulerService;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class SchedulerListener extends ListenerAdapter {

  private static final String PREFIX = "scheduler";

  private Map<String, MessageCommand> commands;
  private SchedulerService service;

  public SchedulerListener() {
    service = new SchedulerService();
    commands = new HashMap<>();

    commands.put("add", service::add);
    commands.put("status", service::status);
    commands.put("ping", service::ping);
  }


  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.isFromType(ChannelType.TEXT)) {
      Message message = event.getMessage();
      String[] messageContents = message.getContentDisplay().split(" ");

      if (messageContents[0].equals("!" + PREFIX)) {
        String command = messageContents[1];

        if (commands.containsKey(command)) {
          commands.get(command).process(message);
        }
      }
    }

  }
}
