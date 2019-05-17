package com.nickparisi.wollbot.callbacks;

import net.dv8tion.jda.api.entities.User;

@FunctionalInterface
public interface BooleanCallback {
  void fire(User user, boolean value);
}
