package com.github.fefo.fancyechests.config;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class ConfigKey<T> {

  public static final Predicate<? super String> VALID_KEY = Pattern.compile("(?i)^[a-z-]{1,32}$")
                                                                   .asPredicate();

  protected final String key;
  protected final T fallback;
  protected final boolean reloadable;
  protected final int bakedHashCode;

  protected ConfigKey(final @NotNull String key,
                      final @NotNull T fallback,
                      final boolean reloadable) {
    Validate.notNull(key);
    Validate.isTrue(VALID_KEY.test(key));
    Validate.notNull(fallback);

    this.key = key;
    this.fallback = fallback;
    this.reloadable = reloadable;
    this.bakedHashCode = hashCodeBakery();
  }

  public abstract @NotNull T get(final @NotNull ConfigAdapter configAdapter);

  public boolean isReloadable() {
    return this.reloadable;
  }

  public @NotNull String getKey() {
    return this.key;
  }

  public @NotNull T getFallback() {
    return this.fallback;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof ConfigKey)) {
      return false;
    }

    final ConfigKey<?> that = (ConfigKey<?>) other;
    if (this.reloadable != that.reloadable) {
      return false;
    }
    return this.key.equals(that.key);
  }

  @Override
  public int hashCode() {
    return this.bakedHashCode;
  }

  private int hashCodeBakery() {
    int result = this.key.hashCode();
    result = 31 * result + (this.reloadable ? 1 : 0);
    return result;
  }
}
