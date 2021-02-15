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

package com.github.fefo.fancyechests.config.adapters;

import com.github.fefo.fancyechests.FancyEChestsPlugin;
import com.github.fefo.fancyechests.config.ConfigAdapter;
import com.google.common.collect.ImmutableMap;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class YamlConfigAdapter extends ConfigAdapter {

  private static final Yaml YAML;

  static {
    final LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setAllowDuplicateKeys(false);
    YAML = new Yaml(loaderOptions);
  }

  public YamlConfigAdapter(final FancyEChestsPlugin plugin, final Path dataFolder) {
    super(plugin, dataFolder, "config.yml");
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void reload(final boolean force) throws IOException {
    try (final Reader reader = Files.newBufferedReader(this.configPath)) {
      final Map<String, ?> map = YAML.loadAs(reader, Map.class);
      this.rootRaw.putAll(map != null ? map : ImmutableMap.of());
    }
  }
}
