//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021 emilyy-dev
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package io.github.emilyydev.fancyechests.config;

import io.github.emilyydev.fancyechests.config.types.DoubleConfigKey;
import io.github.emilyydev.fancyechests.config.types.IntegerConfigKey;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public final class ConfigKeys {

  public static final DoubleConfigKey RPM = new DoubleConfigKey("rpm", 45.0, true);

  public static final IntegerConfigKey SECONDS_HIDDEN = new IntegerConfigKey("secondsHidden", 60, true);

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

  public static final IntegerConfigKey PARTICLE_COUNT = new IntegerConfigKey("particleCount", 100, true);

  public static final DoubleConfigKey PARTICLE_SPEED = new DoubleConfigKey("particleSpeed", 0.1, true);

  private ConfigKeys() {
    throw new UnsupportedOperationException();
  }
}
