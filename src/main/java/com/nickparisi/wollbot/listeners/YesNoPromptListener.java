package com.nickparisi.wollbot.listeners;

import com.nickparisi.wollbot.callbacks.BooleanCallback;
import com.nickparisi.wollbot.utils.UserUtils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Wait for a user to respond with either yes/no and then call a callback function
 */
public class YesNoPromptListener extends ListenerAdapter {
  private User user;
  private BooleanCallback callback;

  public YesNoPromptListener(User user, BooleanCallback callback) {
    this.user = user;
    this.callback = callback;

    user.getJDA().addEventListener(this);
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    if (event.isFromType(ChannelType.PRIVATE) && event.getAuthor().equals(user)) {
      String messageContents = event.getMessage().getContentDisplay().trim().toLowerCase();

      if (messageContents.equals("yes")) {
        callback.fire(user, true);
        user.getJDA().removeEventListener(this);

      } else if (messageContents.equals("no")) {
        callback.fire(user, false);
        user.getJDA().removeEventListener(this);
      }
    }
  }
}
