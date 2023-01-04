package net.azisaba.playerdataitemfinder;

import net.azisaba.playerdataitemfinder.filter.FilterPredicate;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class PlayerDataItemFinder {
    private final List<File> files;
    private final boolean ignoreInvalidNBT;
    private final FilterPredicate filter;

    private PlayerDataItemFinder(@NotNull List<File> files, boolean ignoreInvalidNBT, @NotNull FilterPredicate filter) {
        this.files = files;
        this.ignoreInvalidNBT = ignoreInvalidNBT;
        this.filter = filter;
    }

    @Contract("_, _, _ -> new")
    public static @NotNull PlayerDataItemFinder create(@NotNull List<File> inputList, boolean ignoreInvalidNBT, @NotNull FilterPredicate predicate) {
        List<File> files = new ArrayList<>();
        for (File fileOrDirectory : inputList) {
            if (!fileOrDirectory.exists()) {
                Log.warn("File or directory does not exist: " + fileOrDirectory);
                continue;
            }
            if (fileOrDirectory.isDirectory()) {
                files.addAll(getFiles(fileOrDirectory));
            } else {
                files.add(fileOrDirectory);
            }
        }
        return new PlayerDataItemFinder(files, ignoreInvalidNBT, predicate);
    }

    private static @NotNull Collection<? extends File> getFiles(File file) {
        List<File> files = new ArrayList<>();
        for (File f : Objects.requireNonNull(file.listFiles(), "file.listFiles()")) {
            if (f.isDirectory()) {
                files.addAll(getFiles(f));
            } else {
                files.add(f);
            }
        }
        return files;
    }

    @Contract(pure = true)
    public @NotNull List<File> getFiles() {
        return files;
    }

    @Contract(pure = true)
    public void processAll() throws IOException {
        for (File file : files) {
            try {
                process(file);
            } catch (Exception e) {
                Log.error("Failed to process file: " + file, e);
                if (!ignoreInvalidNBT) {
                    throw e;
                }
            }
        }
    }

    @Contract(pure = true)
    public void process(@NotNull File file) throws IOException {
        NamedTag tag = NBTUtil.read(file);
        CompoundTag root = (CompoundTag) tag.getTag();
        ListTag<CompoundTag> inventory = root.getListTag("Inventory").asCompoundTagList();
        for (CompoundTag item : inventory) {
            String id = item.getString("id");
            int count = item.getByte("Count");
            int damage = item.getShort("Damage");
            String name = null;
            String lore = null;
            CompoundTag itemTag = item.getCompoundTag("tag");
            if (itemTag != null) {
                CompoundTag display = itemTag.getCompoundTag("display");
                if (display != null) {
                    name = display.getString("Name");
                    List<String> loreList = new ArrayList<>();
                    ListTag<?> loreTag = display.getListTag("Lore");
                    if (loreTag != null) {
                        loreTag.asStringTagList().asStringTagList().forEach(s -> loreList.add(s.getValue()));
                        lore = String.join("\n", loreList);
                    }
                }
            }
            ItemData itemData = new ItemData(id, count, damage, name, lore);
            if (filter.test(itemData)) {
                Log.info("Found item in {}: " + itemData, file.getName());
            }
        }
    }
}
