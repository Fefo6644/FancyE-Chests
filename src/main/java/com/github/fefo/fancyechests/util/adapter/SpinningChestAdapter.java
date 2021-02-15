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

package com.github.fefo.fancyechests.util.adapter;


import com.github.fefo.fancyechests.model.chest.SpinningChest;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import java.io.IOException;
import java.util.UUID;

public final class SpinningChestAdapter extends TypeAdapter<SpinningChest> {

  public static final SpinningChestAdapter ADAPTER = new SpinningChestAdapter();

  private SpinningChestAdapter() { }

  @Override
  public void write(final JsonWriter out, final SpinningChest chest) throws IOException {
    out.beginObject();

    out.name("uuid").value(String.valueOf(chest.getUuid()));
    out.name("location");
    LocationAdapter.ADAPTER.write(out, chest.getLocation());
    out.name("hiddenUntil").value(chest.getHiddenUntil());
    out.name("shouldDisappear").value(chest.shouldDisappear());

    out.endObject();
  }

  @Override
  public SpinningChest read(final JsonReader in) throws IOException {
    in.beginObject();

    UUID uuid = null;
    Location location = null;
    Long hiddenUntil = null;
    Boolean shouldDisappear = null;

    while (in.hasNext()) {
      final String field = in.nextName();

      switch (field) {
        case "uuid":
          uuid = UUID.fromString(in.nextString());
          break;

        case "location":
          location = LocationAdapter.ADAPTER.read(in);
          break;

        case "hiddenUntil":
          hiddenUntil = in.nextLong();
          break;

        case "shouldDisappear":
          shouldDisappear = in.nextBoolean();
          break;
      }
    }

    Validate.notNull(uuid, "uuid");
    Validate.notNull(location, "location");
    Validate.notNull(location.getWorld(), "location.getWorld()");
    Validate.notNull(hiddenUntil, "hiddenUntil");
    Validate.notNull(shouldDisappear, "shouldDisappear");

    in.endObject();
    return new SpinningChest(uuid, location, hiddenUntil, shouldDisappear);
  }
}
