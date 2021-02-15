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

package com.github.fefo.fancyechests.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.util.UUID;

public final class WorldAdapter extends TypeAdapter<World> {

  public static final WorldAdapter ADAPTER = new WorldAdapter();

  private WorldAdapter() { }

  @Override
  public void write(final JsonWriter out, final World world) throws IOException {
    out.beginObject()
       .name("uid").value(String.valueOf(world.getUID()))
       .name("name").value(world.getName())
       .endObject();
  }

  @Override
  public World read(final JsonReader in) throws IOException {
    in.beginObject();
    UUID uuid = null;
    String name = null;

    while (in.hasNext()) {
      final String field = in.nextName();

      switch (field) {
        case "uid":
          uuid = UUID.fromString(in.nextString());
          break;

        case "name":
          name = in.nextString();
          break;

        default:
          in.skipValue();
          break;
      }
    }

    World world = Bukkit.getWorld(uuid);
    if (world == null) {
      world = Bukkit.getWorld(name);
    }
    Validate.notNull(world, "world");

    in.endObject();
    return world;
  }
}
