package com.github.fefo.fancyechests.config;

import com.github.fefo.fancyechests.config.types.DoubleConfigKey;
import com.github.fefo.fancyechests.config.types.IntegerConfigKey;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public final class ConfigKeys {

  public static final DoubleConfigKey RPM = new DoubleConfigKey("rpm", 45.0, true);

  public static final IntegerConfigKey SECONDS_HIDDEN =
      new IntegerConfigKey("secondsUntilReappearance", 60, true);

  public static final ConfigKey<Particle> PARTICLE_TYPE =
      new ConfigKey<Particle>("particleType", Particle.END_ROD, true) {

        @Override
        public @NotNull Particle get(final @NotNull ConfigAdapter configAdapter) {
          final String raw = configAdapter.getString(this.key);
          try {
            return Particle.valueOf(raw);
          } catch (final Exception exception) {
            return this.fallback;
          }
        }
      };

  public static final IntegerConfigKey PARTICLE_COUNT =
      new IntegerConfigKey("particleCount", 100, true);

  public static final DoubleConfigKey PARTICLE_SPEED =
      new DoubleConfigKey("particleSpeed", 0.1, true);

  private ConfigKeys() {
    throw new UnsupportedOperationException();
  }
}
