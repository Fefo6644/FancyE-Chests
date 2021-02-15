//
// FancyE-Chests - Provide your players with isolated, fancy spinning ender chests.
// Copyright (C) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
//

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
