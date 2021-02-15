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
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public final class LocationAdapter extends TypeAdapter<Location> {

  public static final LocationAdapter ADAPTER = new LocationAdapter();

  private LocationAdapter() { }

  @Override
  public void write(final JsonWriter out, final Location location) throws IOException {
    out.beginObject();

    out.name("world");
    if (location.getWorld() != null) {
      WorldAdapter.ADAPTER.write(out, location.getWorld());
    } else {
      out.nullValue();
    }

    out.name("x").value(location.getX());
    out.name("y").value(location.getY());
    out.name("z").value(location.getZ());

    out.endObject();
  }

  @Override
  public Location read(final JsonReader in) throws IOException {
    in.beginObject();

    World world = null;
    Double x = null, y = null, z = null;

    while (in.hasNext()) {
      final String field = in.nextName();

      switch (field) {
        case "world":
          if (in.peek() != JsonToken.NULL) {
            world = WorldAdapter.ADAPTER.read(in);
          }
          break;

        case "x":
          x = in.nextDouble();
          break;

        case "y":
          y = in.nextDouble();
          break;

        case "z":
          z = in.nextDouble();
          break;
      }
    }

    Validate.notNull(x, "x");
    Validate.notNull(y, "y");
    Validate.notNull(z, "z");

    in.endObject();
    return new Location(world, x, y, z);
  }
}
