package net.hyze.skyblock.framework.plugin.cache.local;


import com.github.benmanes.caffeine.cache.CacheLoader;
import lombok.NonNull;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.cache.local.CredentialLocalCache;
import net.hyze.core.shared.user.User;
import net.hyze.skyblock.framework.plugin.user.SkyBlockUser;
import org.bukkit.entity.Player;

public class SkyBlockUserLocalCache extends CredentialLocalCache<SkyBlockUser> {

  @Override
  public CacheLoader<String, SkyBlockUser> getLoaderByNick() {
    return (String nick) -> {
      User user = CoreProvider.Cache.Local.USERS.provide().get(nick);

      return build0(user);
    };
  }

  @Override
  public CacheLoader<Integer, SkyBlockUser> getLoaderById() {
    return (Integer id) -> {
      User user = CoreProvider.Cache.Local.USERS.provide().get(id);

      return build0(user);
    };
  }

  private SkyBlockUser build0(User user) {
    if (user == null) {
      return null;
    }

    SkyBlockUser skyBlockUser = new SkyBlockUser(user);

    return skyBlockUser;
  }

  public SkyBlockUser get(@NonNull User user) {
    return this.get(user.getNick());
  }

  public SkyBlockUser get(@NonNull Player player) {
    return this.get(player.getName());
  }

  public void remove(User user) {
    if (user != null) {
      this.remove(user.getNick());
    }
  }

}