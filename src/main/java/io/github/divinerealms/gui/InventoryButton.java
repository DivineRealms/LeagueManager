package io.github.divinerealms.gui;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

@Getter
public class InventoryButton {
  private Function<Player, ItemStack> iconCreator;
  private Consumer<InventoryClickEvent> eventConsumer;

  public InventoryButton creator(Function<Player, ItemStack> iconCreator) {
    this.iconCreator = iconCreator;
    return this;
  }

  public InventoryButton consumer(Consumer<InventoryClickEvent> eventConsumer) {
    this.eventConsumer = eventConsumer;
    return this;
  }
}