package com.github.elturtco.lepton.json;

import com.github.elturtco.lepton.block.LeptonStairsBlock;
import com.google.common.collect.ImmutableList;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.loot.JLootTable;
import net.devtech.arrp.json.models.JModel;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static net.devtech.arrp.json.blockstate.JState.variant;
import static net.devtech.arrp.json.models.JModel.*;

public class BlockGenerator extends Generator<Block, BlockGenerator> {

    private Function<Identifier, JState> blockStateGetter;
    private Function<Identifier, JModel> modelGetter;
    private Function<Identifier, JLootTable> lootTableGetter;
    private Function<Identifier, Block> blockGetter;
    private List<Identifier> ids;

    public BlockGenerator(String modid, RuntimeResourcePack resourcePack) {
        super(modid, resourcePack);
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator setIds(List<Identifier> ids) {
        this.ids = ImmutableList.copyOf(ids);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator setIds(Identifier... ids) {
        this.ids = ImmutableList.copyOf(ids);
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator setIds(String... paths) {
        //noinspection UnstableApiUsage
        this.ids = Arrays.stream(paths)
                .map(s -> new Identifier(namespace, s))
                .collect(ImmutableList.toImmutableList());
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator blockState(Function<Identifier, JState> getter) {
        this.blockStateGetter = getter;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator model(Function<Identifier, JModel> getter) {
        this.modelGetter = getter;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator lootTable(Function<Identifier, JLootTable> getter) {
        this.lootTableGetter = getter;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator block(Function<Identifier, Block> getter) {
        this.blockGetter = getter;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator block(Block block) {
        this.blockGetter = ignored -> block;
        return this;
    }

    @Contract(value = "_ -> this", mutates = "this")
    public BlockGenerator block(AbstractBlock.Settings settings) {
        return this.block(new Block(settings));
    }

    public BlockGenerator applyPreset(Function preset) {
        this.modelGetter = preset;
        return this;
    }

    @Override
    public RegistrableResults<Block> build() {
        return null;
    }


    /**
     * Registers a block that is mostly uninteresting, like a true decoration block
     * hanging there in the backyard.
     */
    public <T extends Block> T registerBlandBlock(T block, String path) {
        Identifier id = new Identifier(namespace, path);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        resourcePack.addBlockState(
                JState.state()
                        .add(variant()
                                .put("", JState.model(prefixPathAsString(id, "block")))
                        ),
                id
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/cube_all")
                        .textures(textures()
                                .var("all", prefixPathAsString(id, "block"))
                        ),
                prefixPath(id, "block")
        );

        resourcePack.addModel(
                JModel.model(prefixPathAsString(id, "block")),
                prefixPath(id, "item")
        );

        return block;
    }

    public <T extends SlabBlock> T registerSlabBlock(T block, String path, String doublepath) {
        Identifier id = new Identifier(namespace, path);
        Identifier idtop = new Identifier(namespace, path + "_top");
        Identifier iddouble = new Identifier(namespace, doublepath);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        resourcePack.addBlockState(
                JState.state()
                        .add(variant()
                                .put("type=bottom", JState.model(prefixPathAsString(id, "block")))
                                .put("type=double", JState.model(prefixPathAsString(iddouble, "block")))
                                .put("type=top", JState.model(prefixPathAsStringTop(id, "block")))
                        ),
                id
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/slab")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(id, "block")
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/slab_top")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(idtop, "block")
        );

        resourcePack.addModel(
                JModel.model(prefixPathAsString(id, "block")),
                prefixPath(id, "item")
        );

        return block;
    }

    public <T extends SlabBlock> T registerConcreteSlabBlock(T block, String path, String doublepath) {
        Identifier id = new Identifier(namespace, path);
        Identifier idtop = new Identifier(namespace, path + "_top");
        Identifier iddouble = new Identifier("minecraft", doublepath);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        resourcePack.addBlockState(
                JState.state()
                        .add(variant()
                                .put("type=bottom", JState.model(prefixPathAsString(id, "block")))
                                .put("type=double", JState.model(prefixPathAsString(iddouble, "block")))
                                .put("type=top", JState.model(prefixPathAsStringTop(id, "block")))
                        ),
                id
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/slab")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(id, "block")
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/slab_top")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(idtop, "block")
        );

        resourcePack.addModel(
                JModel.model(prefixPathAsString(id, "block")),
                prefixPath(id, "item")
        );

        return block;
    }

    public <T extends LeptonStairsBlock> T registerConcreteStairBlock(T block, String path, String doublepath) {
        Identifier id = new Identifier(namespace, path);
        Identifier idinner = new Identifier(namespace, path + "_inner");
        Identifier idoutter = new Identifier(namespace, path + "_inner");
        Identifier iddouble = new Identifier("minecraft", doublepath);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));

        resourcePack.addBlockState(
                JState.state()
                        .add(variant()
                                .put("facing=east,half=bottom,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).y(270).uvlock())
                                .put("facing=east,half=bottom,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")))
                                .put("facing=east,half=bottom,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).y(270).uvlock())
                                .put("facing=east,half=bottom,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")))
                                .put("facing=east,half=bottom,shape=straight", JState.model(prefixPathAsString(id, "block")))
                                .put("facing=east,half=top,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).x(180).uvlock())
                                .put("facing=east,half=top,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).x(180).y(90).uvlock())
                                .put("facing=east,half=top,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).x(180).uvlock())
                                .put("facing=east,half=top,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).x(180).y(90).uvlock())
                                .put("facing=east,half=top,shape=straight", JState.model(prefixPathAsString(id, "block")).x(180).uvlock())
                                .put("facing=north,half=bottom,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).y(180).uvlock())
                                .put("facing=north,half=bottom,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).y(270).uvlock())
                                .put("facing=north,half=bottom,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).y(180).uvlock())
                                .put("facing=north,half=bottom,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).y(270).uvlock())
                                .put("facing=north,half=bottom,shape=straight", JState.model(prefixPathAsString(id, "block")).y(270).uvlock())
                                .put("facing=north,half=top,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).x(180).y(270).uvlock())
                                .put("facing=north,half=top,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).x(180).uvlock())
                                .put("facing=north,half=top,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).x(180).y(270).uvlock())
                                .put("facing=north,half=top,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).x(180).uvlock())
                                .put("facing=north,half=top,shape=straight", JState.model(prefixPathAsString(id, "block")).x(180).y(270).uvlock())
                                .put("facing=south,half=bottom,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")))
                                .put("facing=south,half=bottom,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).y(90).uvlock())
                                .put("facing=south,half=bottom,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")))
                                .put("facing=south,half=bottom,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).y(90).uvlock())
                                .put("facing=south,half=bottom,shape=straight", JState.model(prefixPathAsString(id, "block")).y(90).uvlock())
                                .put("facing=south,half=top,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).x(180).y(90).uvlock())
                                .put("facing=south,half=top,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).x(180).y(180).uvlock())
                                .put("facing=south,half=top,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).x(180).y(90).uvlock())
                                .put("facing=south,half=top,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).x(180).y(180).uvlock())
                                .put("facing=south,half=top,shape=straight", JState.model(prefixPathAsString(id, "block")).x(180).y(90).uvlock())
                                .put("facing=west,half=bottom,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).y(90).uvlock())
                                .put("facing=west,half=bottom,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).y(180).uvlock())
                                .put("facing=west,half=bottom,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).y(90).uvlock())
                                .put("facing=west,half=bottom,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).y(180).uvlock())
                                .put("facing=west,half=bottom,shape=straight", JState.model(prefixPathAsString(id, "block")).y(180).uvlock())
                                .put("facing=west,half=top,shape=inner_left", JState.model(prefixPathAsStringInner(id, "block")).x(180).y(180).uvlock())
                                .put("facing=west,half=top,shape=inner_right", JState.model(prefixPathAsStringInner(id, "block")).x(180).y(270).uvlock())
                                .put("facing=west,half=top,shape=outer_left", JState.model(prefixPathAsStringOuter(id, "block")).x(180).y(180).uvlock())
                                .put("facing=west,half=top,shape=outer_right", JState.model(prefixPathAsStringOuter(id, "block")).x(180).y(270).uvlock())
                                .put("facing=west,half=top,shape=straight", JState.model(prefixPathAsString(id, "block")).x(180).y(180).uvlock())

                        ),
                id
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/stairs")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(id, "block")
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/inner_stairs")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(idinner, "block")
        );

        resourcePack.addModel(
                JModel.model("minecraft:block/outer_stairs")
                        .textures(textures()
                                .var("bottom", prefixPathAsString(iddouble, "block"))
                                .var("top", prefixPathAsString(iddouble, "block"))
                                .var("side", prefixPathAsString(iddouble, "block"))
                        ),
                prefixPath(idoutter, "block")
        );

        resourcePack.addModel(
                JModel.model(prefixPathAsString(id, "block")),
                prefixPath(id, "item")
        );

        return block;
    }

}