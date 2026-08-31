package net.crazy.badges.core.model;

import java.util.UUID;

public record BadgeEntry(int id, UUID uuid, String name, String description) {

}
