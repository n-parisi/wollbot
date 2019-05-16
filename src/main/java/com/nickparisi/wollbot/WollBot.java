package com.nickparisi.wollbot;

import com.nickparisi.wollbot.listeners.SchedulerListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class WollBot {
  public static void main(String[] args) throws LoginException {
    JDA jda = new JDABuilder(args[0]).build();

    jda.addEventListener(new SchedulerListener());
  }
}
