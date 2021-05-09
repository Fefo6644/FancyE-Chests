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

package io.github.emilyydev.fancyechests.util.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.github.emilyydev.fancyechests.model.chest.ChestMap;
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
      ChestMap.GSON.toJson(location.getWorld(), World.class, out);
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
            world = ChestMap.GSON.fromJson(in, World.class);
          } else {
            in.nextNull();
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
