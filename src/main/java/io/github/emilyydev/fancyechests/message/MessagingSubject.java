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

package io.github.emilyydev.fancyechests.message;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class MessagingSubject implements ForwardingAudience.Single {

  protected final Audience audience;
  protected final String name;
  protected final WeakReference<Permissible> permissible;

  MessagingSubject(final @NotNull Audience audience,
                   final @NotNull String name,
                   final @NotNull Permissible permissible) {
    this.audience = audience;
    this.name = name;
    this.permissible = new WeakReference<>(permissible);
  }

  @Override
  public @NotNull Audience audience() {
    return this.audience;
  }

  public @NotNull String getName() {
    return this.name;
  }

  public boolean isPlayer() {
    return false;
  }

  public @Nullable PlayerMessagingSubject asPlayerSubject() {
    if (isPlayer()) {
      return (PlayerMessagingSubject) this;
    }
    return null;
  }

  public boolean hasPermission(final @NotNull String name) {
    final Permissible handle = this.permissible.get();
    if (handle != null) {
      return handle.hasPermission(name);
    }
    return false;
  }
}
