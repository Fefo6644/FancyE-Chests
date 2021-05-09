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

package io.github.emilyydev.fancyechests.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class CommandMapHelper {

  private static final MethodHandle GET_COMMAND_MAP_METHOD;

  static {
    try {
      final Class<?> craftServerClass = Bukkit.getServer().getClass();
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      final MethodType getCommandMapMethodType = MethodType.methodType(CommandMap.class);
      GET_COMMAND_MAP_METHOD = lookup.findVirtual(craftServerClass, "getCommandMap", getCommandMapMethodType);
    } catch (final ReflectiveOperationException exception) {
      // throw unchecked, can't register commands without it
      throw new RuntimeException(exception);
    }
  }

  public static CommandMap getCommandMap() {
    try {
      return (CommandMap) GET_COMMAND_MAP_METHOD.invoke(Bukkit.getServer());
    } catch (final Throwable exception) {
      throw new RuntimeException(exception);
    }
  }

  private CommandMapHelper() {
    throw new UnsupportedOperationException("Cannot instantiate utility class");
  }
}
