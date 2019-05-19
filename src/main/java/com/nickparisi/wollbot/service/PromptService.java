package com.nickparisi.wollbot.service;

import com.nickparisi.wollbot.callbacks.IBooleanCallback;
import com.nickparisi.wollbot.listeners.YesNoPromptListener;
import com.nickparisi.wollbot.utils.UserUtils;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public class PromptService {

  private static PromptService service;

  private Map<User, ListenerAdapter> users;

  private PromptService() {
    users = new HashMap<>();
  }

  public static PromptService getInstance() {
    if (service == null) {
      service = new PromptService();
    }
    return service;
  }

  public void startPromptBoolean(User user, String prompt, IBooleanCallback callback) {
    ListenerAdapter listener = new YesNoPromptListener(user, callback);
    user.getJDA().addEventListener(listener);
    users.put(user, listener);
    UserUtils.sendPrivateMessage(user, prompt);
  }

  public void stopPrompt(User user) {
    if (users.containsKey(user)) {
      ListenerAdapter listener = users.remove(user);
      user.getJDA().removeEventListener(listener);
    }
  }

}
