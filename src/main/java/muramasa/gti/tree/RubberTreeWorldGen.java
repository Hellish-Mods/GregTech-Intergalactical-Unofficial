package muramasa.gti.tree;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Ref;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.trunkplacer.ForkyTrunkPlacer;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;

import muramasa.gti.data.GregTechData;

import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RubberTreeWorldGen extends WorldGenBase<RubberTreeWorldGen> {

    /*@Override
    public Predicate<Biome> getValidBiomes() {
        return getValidBiomesStatic();
    }*/

    public static Predicate<Biome.Category> getValidBiomesStatic() {
        final Set<Biome.Category> blacklist = new ObjectOpenHashSet<>();
        blacklist.add(Biome.Category.DESERT);
        blacklist.add(Biome.Category.TAIGA);
        blacklist.add(Biome.Category.EXTREME_HILLS);
        blacklist.add(Biome.Category.ICY);
        blacklist.add(Biome.Category.THEEND);
        blacklist.add(Biome.Category.OCEAN);
        blacklist.add(Biome.Category.NETHER);
        return b -> !blacklist.contains(b);
    }

    
    final static BaseTreeFeatureConfig RUBBER_TREE_CONFIG_SWAMP =
            (new BaseTreeFeatureConfig.Builder(RubberTree.TRUNK_BLOCKS, new SimpleBlockStateProvider(GregTechData.RUBBER_LEAVES.getDefaultState()),
                    new AcaciaFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0)), new ForkyTrunkPlacer(5, 2, 2), new TwoLayerFeature(1, 0, 2))).setIgnoreVines().setMaxWaterDepth(1).build();

    final static BaseTreeFeatureConfig RUBBER_TREE_CONFIG_JUNGLE =
            (new BaseTreeFeatureConfig.Builder(RubberTree.TRUNK_BLOCKS, new SimpleBlockStateProvider(GregTechData.RUBBER_LEAVES.getDefaultState()),
                     new AcaciaFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0)), new ForkyTrunkPlacer(7, 2, 2), new TwoLayerFeature(1, 0, 2))).setIgnoreVines().build();
                 ///   .trunkHeight(4).trunkHeightRandom(1) // bare trunk height
               //     .trunkTopOffset(2) // depresses trunk top within leaves
               //     .setSapling(RUBBER_SAPLING)
          //          .decorators(ImmutableList.of(new LeaveVineTreeDecorator())).build();

    final static BaseTreeFeatureConfig RUBBER_TREE_CONFIG_NORMAL =
            (new BaseTreeFeatureConfig.Builder(RubberTree.TRUNK_BLOCKS, new SimpleBlockStateProvider(GregTechData.RUBBER_LEAVES.getDefaultState()),
                     new AcaciaFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0)), new ForkyTrunkPlacer(5, 2, 2), new TwoLayerFeature(1, 0, 2))).setIgnoreVines().build();

    public RubberTreeWorldGen(){
        super("rubber_tree", RubberTreeWorldGen.class, World.OVERWORLD);
    }
    public static void onEvent(BiomeLoadingEvent builder){
        for (Biome biome : ForgeRegistries.BIOMES) {
            if (!getValidBiomesStatic().test(biome.getCategory()) || biome.getCategory() == Biome.Category.PLAINS)
                continue;
            float p = 0.05F;
            if (builder.getClimate().temperature > 0.8f) {
                p = 0.2F;
                if (builder.getClimate().precipitation == Biome.RainType.RAIN)
                    p += 0.1F;
            }
            float finalp = p;
            builder.getGeneration().getFeatures(GenerationStage.Decoration.VEGETAL_DECORATION).add(() -> RubberTree.TREE_FEATURE.withConfiguration(getTreeConfig(biome))
            .withPlacement(new RubberTreePlacement().configure(new AtSurfaceWithExtraConfig(0, finalp, 3))));
        }
    }

    static BaseTreeFeatureConfig getTreeConfig(Biome biome){
        BaseTreeFeatureConfig config = RUBBER_TREE_CONFIG_NORMAL;
        if (biome.getCategory() == Biome.Category.SWAMP)
            config = RUBBER_TREE_CONFIG_SWAMP;
        else if (biome.getCategory() == Biome.Category.JUNGLE)
            config = RUBBER_TREE_CONFIG_JUNGLE;
        return config;
    }
    public static class RubberTreePlacement extends Placement<AtSurfaceWithExtraConfig> {
        public RubberTreePlacement() {
            super(AtSurfaceWithExtraConfig.CODEC);
        }
        @Override
        public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, AtSurfaceWithExtraConfig config, BlockPos pos) {
            int i = config.count;
            if (random.nextFloat() < config.extraChance) {
                i = random.nextInt(config.extraCount) + config.extraCount;
            }
            return IntStream.range(0, i).mapToObj((ix) -> {
                int j = random.nextInt(16) + pos.getX();
                int k = random.nextInt(16) + pos.getZ();
                return new BlockPos(j, helper.func_242893_a(Heightmap.Type.MOTION_BLOCKING, j, k), k);
            });
        }


}
}
