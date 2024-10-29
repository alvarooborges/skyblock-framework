package net.hyze.skyblock.framework.api.slime;

import java.io.File;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider.Database;
import net.hyze.slime.loaders.FileLoader;
import net.hyze.slime.loaders.SeaweedLoader;
import net.hyze.slime.loaders.SlimeLoader;

@RequiredArgsConstructor
public enum EnumSlimeLoaders {

  FILE(new FileLoader(new File("slime-worlds"))),
  SEAWEED(new SeaweedLoader(Database.SEAWEED_SKYBLOCK));

  @Getter
  private final SlimeLoader loader;

}
