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
