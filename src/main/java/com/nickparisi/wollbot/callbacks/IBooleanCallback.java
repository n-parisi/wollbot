package com.nickparisi.wollbot.callbacks;

import net.dv8tion.jda.api.entities.User;

@FunctionalInterface
public interface IBooleanCallback {
  void fire(User user, boolean value);
}
