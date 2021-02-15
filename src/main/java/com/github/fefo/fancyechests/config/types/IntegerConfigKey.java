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
