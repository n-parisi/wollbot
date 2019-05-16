package com.nickparisi.wollbot.commands;

import net.dv8tion.jda.api.entities.Message;

@FunctionalInterface
public interface MessageCommand {
  void process(Message msg);
}
