package com.github.fefo.fancyechests.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class CommandMapHelper {

  private static final Method GET_COMMAND_MAP_METHOD;

  static {
    try {
      GET_COMMAND_MAP_METHOD = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap");
    } catch (final ReflectiveOperationException exception) {
      throw new RuntimeException(exception);
    }
  }

  public static CommandMap getCommandMap() {
    try {
      return (CommandMap) GET_COMMAND_MAP_METHOD.invoke(Bukkit.getServer());
    } catch (final InvocationTargetException | IllegalAccessException exception) {
      throw new RuntimeException(exception);
    }
  }
}
