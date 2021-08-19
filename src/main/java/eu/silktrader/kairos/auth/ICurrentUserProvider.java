package eu.silktrader.kairos.auth;

import eu.silktrader.kairos.user.User;

public interface ICurrentUserProvider {
  User getUser(String name);
  User getCurrentUser();
  String getCurrentUserName();
}
