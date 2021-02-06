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
