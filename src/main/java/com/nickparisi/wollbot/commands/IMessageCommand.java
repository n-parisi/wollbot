package com.nickparisi.wollbot.commands;

import net.dv8tion.jda.api.entities.Message;

@FunctionalInterface
public interface IMessageCommand {
  void process(Message msg);
}
