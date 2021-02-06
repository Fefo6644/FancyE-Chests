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
