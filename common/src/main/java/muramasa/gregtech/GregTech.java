package muramasa.gregtech;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.AntimatterMod;
import muramasa.antimatter.datagen.providers.*;
import muramasa.antimatter.event.CraftingEvent;
import muramasa.antimatter.event.ProvidersEvent;
import muramasa.antimatter.recipe.loader.IRecipeRegistrate;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.Side;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.gregtech.block.tree.RubberFoliagePlacer;
import muramasa.gregtech.block.tree.RubberTree;
import muramasa.gregtech.data.*;
import muramasa.gregtech.data.Machines;
import muramasa.gregtech.datagen.GregTechBlockTagProvider;
import muramasa.gregtech.datagen.GregtechBlockLootProvider;
import muramasa.gregtech.datagen.ProgressionAdvancements;
import muramasa.gregtech.loader.crafting.*;
import muramasa.gregtech.loader.items.Circuitry;
import muramasa.gregtech.loader.machines.*;
import muramasa.gregtech.loader.machines.generator.CoalBoilerHandler;
import muramasa.gregtech.loader.machines.generator.Fuels;
import muramasa.gregtech.loader.multi.*;

import java.util.function.BiConsumer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GregTech extends AntimatterMod {

    public static GregTech INSTANCE;
    public static Logger LOGGER = LogManager.getLogger(Ref.ID);

    public GregTech() {
        super();
    }

    @Override
    public void onRegistrarInit() {
        super.onRegistrarInit();
        LOGGER.info("Loading GregTech");
        INSTANCE = this;


        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterBlockStateProvider(Ref.ID, Ref.NAME + " BlockStates", g));
        AntimatterDynamics.clientProvider(Ref.ID,
                g -> new AntimatterItemModelProvider(Ref.ID, Ref.NAME + " Item Models", g));
        AntimatterDynamics.clientProvider(Ref.ID, GregTechLocalizations.en_US::new);
    }

    public static void onProviders(ProvidersEvent ev) {
        if (ev.getSide() == Side.CLIENT) {

        } else {
            final AntimatterBlockTagProvider[] p = new AntimatterBlockTagProvider[1];
            ev.addProvider(Ref.ID, g -> {
                p[0] = new GregTechBlockTagProvider(Ref.ID, Ref.NAME.concat(" Block Tags"), false, g);
                return p[0];
            });
            ev.addProvider(Ref.ID, g -> new AntimatterItemTagProvider(Ref.ID, Ref.NAME.concat(" Item Tags"),
                    false, g, p[0]));
            ev.addProvider(Ref.ID, g -> new AntimatterFluidTagProvider(Ref.ID,
                    Ref.NAME.concat(" Fluid Tags"), false, g));
            ev.addProvider(Ref.ID, g -> new AntimatterAdvancementProvider(Ref.ID,
                    Ref.NAME.concat(" Advancements"), g, new ProgressionAdvancements()));
            ev.addProvider(Ref.ID,
                    g -> new GregtechBlockLootProvider(Ref.ID, Ref.NAME.concat(" Loot generator"), g));
        }
    }
    
    public static void registerCraftingLoaders(CraftingEvent event) {
        event.addLoader(Parts::loadRecipes);
        event.addLoader(Smelting::loadRecipes);
        event.addLoader(WireCablesPlates::loadRecipes);
        event.addLoader(VanillaExtensions::loadRecipes);
        event.addLoader(muramasa.gregtech.loader.crafting.Machines::loadRecipes);
        event.addLoader(SteamMachines::loadRecipes);
        event.addLoader(BlockParts::loadRecipes);
    }

    public static void registerRecipeLoaders(IAntimatterRegistrar registrar, IRecipeRegistrate reg) {
        BiConsumer<String, IRecipeRegistrate.IRecipeLoader> loader = (a, b) -> reg.add(Ref.ID, a, b);
        loader.accept("wiremill", WiremillLoader::init);
        loader.accept("washer", WasherLoader::init);
        loader.accept("blasting", Blasting::init);
        loader.accept("coking", Coking::init);
        loader.accept("bending", BendingLoader::init);
        loader.accept("assembling", AssemblyLoader::init);
        loader.accept("arc", ArcFurnace::init);
        loader.accept("cracking", CrackingUnit::init);
        loader.accept("fluid_solidify", FluidSolidifier::init);
        loader.accept("circuitry", Circuitry::init);
        loader.accept("packaging", PackagerLoader::init);
        loader.accept("chem_reacting", ChemicalReactorLoader::init);
        loader.accept("canning", CanningLoader::init);
        loader.accept("nuclear", NuclearLoader::init);
        loader.accept("fuels", Fuels::init);
        loader.accept("coal_boiler", CoalBoilerHandler::init);
        loader.accept("fluid_extracting", FluidExtractor::init);
        loader.accept("alloy_loading", AlloyLoader::init);
        loader.accept("distillation_tower", DistillationTower::init);
        loader.accept("mixing", MixerLoader::init);
        loader.accept("hammering", HammerLoader::init);
        loader.accept("lathing", LatheLoader::init);
        loader.accept("electrolyzing", ElectrolyzerLoader::init);
        loader.accept("fluid_canning", FluidCanningLoader::init);
        loader.accept("centrifuging", CentrifugingLoader::init);
        loader.accept("extracting", ExtractorLoader::init);
        loader.accept("compressing", CompressorLoader::init);
        loader.accept("vac_freezing", VacFreezer::init);
        loader.accept("ore_byproducts", OreByproducts::init);
        loader.accept("pulverizing", PulverizerLoader::init);
        loader.accept("sifting", SiftingLoader::init);
        loader.accept("thermal_centrifuging", ThermalCentrifuge::init);
        loader.accept("cutting", CuttingLoader::init);
        loader.accept("fermenting", Fermenter::init);
        loader.accept("pressing", FormingPress::init);
        loader.accept("chemical_bathing", ChemicalBath::init);
        loader.accept("heat_exchanging", HeatExchangerLoader::init);
    }

    public static <T> T get(Class<? extends T> clazz, String id) {
        return AntimatterAPI.get(clazz, id, Ref.ID);
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event, Side side) {
        switch (event) {
            case DATA_INIT -> {
                Materials.init();
                TierMaps.init();
                GregTechData.init(side);
                Machines.init();
                Guis.init(side);
                Models.init();
                GregTechSounds.init();
                RubberTree.init();
                if (AntimatterPlatformUtils.isFabric()){
                    Registry.register(Registry.FOLIAGE_PLACER_TYPES, new ResourceLocation(Ref.ID, "rubber_foilage_placer"), RubberFoliagePlacer.RUBBER);
                }
            }
            case DATA_READY -> {
                Structures.init();
              //  if (side == Dist.CLIENT) StructureInfo.init();
                TierMaps.providerInit();
            }
            default -> {
            }
        }
    }

    @Override
    public String getId() {
        return Ref.ID;
    }
}
