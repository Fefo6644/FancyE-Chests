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
