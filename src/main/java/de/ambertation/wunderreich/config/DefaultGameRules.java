package de.ambertation.wunderreich.config;

import de.ambertation.wunderlib.configs.ConfigFile;
import de.ambertation.wunderreich.Wunderreich;

public class DefaultGameRules extends ConfigFile {
    public final static String WUNDERKISTE_CATEGORY = "wunderkiste";
    public final static String WHIPSER_CATEGORY = "whispers";
    public final static String FEATURE_CATEGORY = "wunderreich";

    public final IntValue whisperDurability = new IntValue(WHIPSER_CATEGORY, "durability", 20);
    public final IntValue whisperTrainedDurability = new IntValue(WHIPSER_CATEGORY, "trainedDurability", 40);
    public final IntValue whisperMinXPMultiplier = new IntValue(WHIPSER_CATEGORY, "minXPMultiplier", 75);
    public final IntValue whisperMaxXPMultiplier = new IntValue(WHIPSER_CATEGORY, "maxXPMultiplier", 100);

    public final BooleanValue wunderkisteRedstonePowerWhenOpened = new BooleanValue(
            WUNDERKISTE_CATEGORY,
            "redstonePowerWhenOpened",
            true
    );

    public final BooleanValue wunderkisteAnalogRedstoneOutput = new BooleanValue(
            WUNDERKISTE_CATEGORY,
            "analogRedstoneOutput",
            true
    );

    public final BooleanValue wunderkisteCanColor = new BooleanValue(
            WUNDERKISTE_CATEGORY,
            "canColor",
            true
    );

    public final BooleanValue wunderkisteShowColored = new BooleanValue(
            WUNDERKISTE_CATEGORY,
            "showColored",
            true
    );

    public final BooleanValue wunderkisteAllowDomains = new BooleanValue(
            WUNDERKISTE_CATEGORY,
            "allowNetworks",
            true
    );

    public final BooleanValue wunderkisteAllowNamedDomains = new BooleanValue(
            WUNDERKISTE_CATEGORY,
            "allowNamedNetworks",
            true
    );

    public final IntValue wunderkisteChangeDomainCost = new IntValue(
            WUNDERKISTE_CATEGORY,
            "changeNetworkCost",
            1
    );

    public final BooleanValue allowTradesCycling = new BooleanValue(FEATURE_CATEGORY, "allowTradesCycling", true);

    public final BooleanValue allowLibrarianSelection = new BooleanValue(
            FEATURE_CATEGORY,
            "allowLibrarianSelection",
            true
    ).and(allowTradesCycling).and(Configs.MAIN.addImprintedWhispers);

    public final BooleanValue cyclingNeedsWhisperer = new BooleanValue(
            FEATURE_CATEGORY,
            "cyclingNeedsWhisperer",
            true
    )
            .and(allowTradesCycling)
            .and(() -> Configs.MAIN.addBlankWhispere.get() || Configs.MAIN.addImprintedWhispers.get());

    public final BooleanValue doNotDespawnWithNameTag = new BooleanValue(
            FEATURE_CATEGORY,
            "doNotDespawnWithNameTag",
            true
    );

    public final IntValue wunderkisteChunkLoaderRadius = new IntValue(
            WUNDERKISTE_CATEGORY,
            "chunkLoaderRadius",
            1
    );


    public DefaultGameRules() {
        super(Wunderreich.VERSION_PROVIDER, "default_game_rules");
    }
}
