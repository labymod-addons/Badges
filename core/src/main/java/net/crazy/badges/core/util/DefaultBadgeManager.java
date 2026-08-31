package net.crazy.badges.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.crazy.badges.api.Badge;
import net.crazy.badges.api.BadgeManager;
import net.crazy.badges.core.model.BadgeEntry;
import net.labymod.api.models.Implements;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;
import net.labymod.api.util.logging.Logging;
import org.jetbrains.annotations.NotNull;
import javax.inject.Singleton;

@Singleton
@Implements(BadgeManager.class)
public class DefaultBadgeManager implements BadgeManager {

  private static final String BADGES_ENDPOINT = "https://laby.net/api/v3/badges";

  private static final Logging LOGGER = Logging.getLogger();

  private final List<Badge> badges = new ArrayList<>();
  private final Map<UUID, List<Badge>> playerBadges = new HashMap<>();

  @Override
  @NotNull
  public List<Badge> getPlayerBadges(UUID uuid) {
    if (this.playerBadges.containsKey(uuid)) {
      return Collections.unmodifiableList(this.playerBadges.get(uuid));
    }
    List<Badge> playerBadges = new ArrayList<>();

    for(Badge badge : this.badges) {
      if (badge.getId() == Badge.STAFF_BADGE || badge.getId() == Badge.TRANSLATOR_BADGE) {
        continue;
      }
      if (badge.players().contains(uuid))
        playerBadges.add(badge);
    }

    this.playerBadges.put(uuid, playerBadges);
    return this.getPlayerBadges(uuid);
  }

  @Override
  public void removeFromPlayerCache(UUID uuid) {
    this.playerBadges.remove(uuid);
  }

  @Override
  public void clearPlayerCache() {
    this.playerBadges.clear();
  }

  @Override
  @NotNull
  public List<Badge> getBadges() {
    return Collections.unmodifiableList(this.badges);
  }

  @Override
  public void cacheBadges() {
    Request.ofGsonList(BadgeEntry.class)
        .url(BADGES_ENDPOINT)
        .handleErrorStream()
        .async(true)
        .execute(this::handleResponse);
  }

  private void handleResponse(Response<List<BadgeEntry>> response) {
    try {
      if (response.hasException()) {
        throw new IllegalStateException(response.exception());
      }

      if (response.isEmpty()) {
        throw new IllegalStateException("Response is empty");
      }

      this.badges.clear();

      for (BadgeEntry entry : response.get()) {
        String description = entry.description() == null ? "" : entry.description();

        Badge badge = new Badge(entry.id(), entry.uuid(), entry.name(), description);
        this.badges.add(badge);
      }

      this.badges.sort(Comparator.comparingInt(Badge::getId));
      LOGGER.info("Loaded {} badges", this.badges.size());
    } catch (Exception exception) {
      LOGGER.warn("Failed to load badges", exception);
    }
  }
}
