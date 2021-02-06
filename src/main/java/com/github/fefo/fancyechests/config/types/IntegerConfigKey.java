package com.github.fefo.fancyechests.config.types;

import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.config.ConfigKey;
import org.jetbrains.annotations.NotNull;

public class IntegerConfigKey extends ConfigKey<Integer> {

  public IntegerConfigKey(final @NotNull String key,
                          final int fallback,
                          final boolean reloadable) {
    super(key, fallback, reloadable);
  }

  @Override
  public @NotNull Integer get(final @NotNull ConfigAdapter configAdapter) {
    final Integer value = configAdapter.getInt(this.key);
    return value != null ? value : this.fallback;
  }
}
