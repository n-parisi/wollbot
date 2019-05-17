package com.nickparisi.wollbot.utils;

import net.dv8tion.jda.api.entities.User;

public class UserUtils {
  public static void sendPrivateMessage(User user, String message) {
    user.openPrivateChannel().queue(channel -> channel.sendMessage(message).queue());
  }
}
