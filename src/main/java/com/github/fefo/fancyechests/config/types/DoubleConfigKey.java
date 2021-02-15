//
// This file is part of FancyE-Chests, licensed under the MIT License.
//
// Copyright (c) 2021  Fefo6644 <federico.lopez.1999@outlook.com>
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

package com.github.fefo.fancyechests.config.types;

import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.github.fefo.fancyechests.config.ConfigKey;
import org.jetbrains.annotations.NotNull;

public class DoubleConfigKey extends ConfigKey<Double> {

  public DoubleConfigKey(final @NotNull String key,
                         final double fallback,
                         final boolean reloadable) {
    super(key, fallback, reloadable);
  }

  @Override
  public @NotNull Double get(final @NotNull ConfigAdapter configAdapter) {
    final Double value = configAdapter.getDouble(this.key);
    return value != null ? value : this.fallback;
  }
}
