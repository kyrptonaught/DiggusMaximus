package net.kyrptonaught.diggusmaximus.config.modmenu;

import net.kyrptonaught.kyrptconfig.config.screen.NotSuckyButton;
import net.kyrptonaught.kyrptconfig.config.screen.items.ConfigItem;
import net.kyrptonaught.kyrptconfig.config.screen.items.lists.BlockIconList;
import net.kyrptonaught.kyrptconfig.config.screen.items.lists.StringList;
import net.kyrptonaught.kyrptconfig.config.screen.items.lists.entries.ListStringEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GroupingList extends StringList {

    public GroupingList(Text name, List<String> value, List<String> defaultValue) {
        super(name, value, defaultValue);
        this.configs = new CopyOnWriteArrayList<>();
        setValue(value);
        this.addButton = new NotSuckyButton(0, 0, 35, 20, new TranslatableText("key.kyrptconfig.config.add"), widget -> {
            addConfigItem(createListEntry(""));
        });
    }

    @Override
    public void setValue(List<String> value) {
        configs.clear();
        super.setValue(value);
    }

    public void tick() {
        super.tick();
        for (int i = configs.size() - 1; i >= 0; i--) {
            if (configs.get(i) instanceof BlockIconListWDel && ((BlockIconListWDel) configs.get(i)).scheduleToRemove)
                configs.remove(i);
        }
    }

    public void populateFromList() {
        for (String s : value) {
            addConfigItem(createListEntry(s));
        }
    }

    public StringList createListEntry(String string) {
        String[] blocks = string.split(",");
        for (int i = 0; i < blocks.length; i++)
            blocks[i] = blocks[i].trim();

        return new BlockIconListWDel(new TranslatableText("key.diggusmaximus.config.customgroupinggroup"), List.of(blocks), new ArrayList<>());
    }

    public List<String> getNewValues() {
        List<String> newValues = new ArrayList<>();
        configs.forEach(configItem -> {
            if (configItem instanceof StringList stringListEntry) {
                List<String> result = stringListEntry.getNewValues();
                if (result != null && !result.isEmpty()) newValues.add(String.join(",", result));
            }
        });
        return newValues;
    }

    public static class BlockIconListWDel extends BlockIconList {
        protected NotSuckyButton delButton;
        protected boolean scheduleToRemove = false;

        public BlockIconListWDel(Text name, List<String> value, List<String> defaultValue) {
            super(name, value, defaultValue);
            this.delButton = new NotSuckyButton(0, 0, 35, 20, new TranslatableText("key.kyrptconfig.config.delete"), widget -> {
                this.scheduleToRemove = true;
            });
        }

        @Override
        public void mouseClicked(double mouseX, double mouseY, int button) {
            super.mouseClicked(mouseX, mouseY, button);
            delButton.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
            super.render(matrices, x, y, mouseX, mouseY, delta);
            if (expanded) {
                this.delButton.y = y;
                this.delButton.x = addButton.x - (delButton.getWidth() / 2) - 20;
                this.delButton.render(matrices, mouseX, mouseY, delta);
            }
        }
    }
}
