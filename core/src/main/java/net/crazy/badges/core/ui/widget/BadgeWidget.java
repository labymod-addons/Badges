package net.crazy.badges.core.ui.widget;

import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.IconWidget;

@AutoWidget
public class BadgeWidget extends SimpleWidget {

  private final Icon icon;
  private final String title;
  private final String description;

  public BadgeWidget(Icon icon, String title, String description) {
    this.icon = icon;
    this.title = title;
    this.description = description;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    IconWidget iconWidget = new IconWidget(this.icon);
    iconWidget.addId("badge-icon");
    this.addChild(iconWidget);

    ComponentWidget title = ComponentWidget.text(this.readableTitle(this.title));
    title.addId("badge-title");

    this.setHoverComponent(this.description == null || this.description.isEmpty()
        ? Component.translatable("badges.badge.noDescription")
        : Component.text(this.description));
    this.addChild(title);
  }

  private int getSpaceAmount(String string) {
    int amount = 0;
    for (int i = 0; i < string.length(); i++) {
      if (string.charAt(i) != ' ')
        continue;

      amount++;
    }

    return amount;
  }

  private String readableTitle(String title) {
    if (title.length() <= 10)
      return title;

    int spaceAmount = this.getSpaceAmount(title);

    if (spaceAmount >= 2)
      return this.addLine(title, this.getSpacePosition(title, 2));

    return this.addLine(title, this.getSpacePosition(title, 1));
  }

  private int getSpacePosition(String title, int pos) {
    int position = 0;

    int amount = 0;
    for (int i = 0; i < title.length(); i++) {
      if (title.charAt(i) != ' ')
        continue;

      position = i;
      amount++;

      if (amount == pos)
        break;
    }

    return position;
  }

  private String addLine(String str, int position) {
    return str.substring(0, position) + "\n" + str.substring(position);
  }
}
