package com.seeapenny.client.sound;

public enum Sound {

    CANNON_LITTLE("/sounds/cannon_little.ogg"),
    CANNON_MIDDLE("/sounds/cannon_middle.ogg"),
    CANNON_BIG("/sounds/cannon_big.ogg"),

    SHOT_HIT_1("/sounds/shot_hit_1.ogg"),
    SHOT_HIT_2("/sounds/shot_hit_2.ogg"),

    MISS_SPLASH_1("/sounds/miss_splash_1.ogg"),
    MISS_SPLASH_2("/sounds/miss_splash_2.ogg"),

    SEA_SHIP_AMBIENCE("/sounds/sea_ship_ambience.ogg"),

    SHOT("/sounds/cannon_middle.ogg"),
    HIT("/sounds/hit.ogg"),
    SLIP("/sounds/slip.ogg"),
    SEA("/sounds/sea.ogg"),

    EXTRA_SPEED("/sounds/magic_speed_up_2.ogg"),
    EXTRA_DAMAGE("/sounds/install_cannonball.ogg"),

    WIN_YEAH_8("/sounds/win_yeah_8.ogg"),

    WIN("/sounds/win.ogg"),
    LOSE("/sounds/loss_2.ogg"),

    INSTALL_ARMOR("/sounds/install_armor.ogg"),
    INSTALL_CANNON("/sounds/install_cannon.ogg"),
    INSTALL_CANNONBALL("/sounds/install_cannonball.ogg"),
    INSTALL_GOLD("/sounds/install_gold.ogg"),
    INSTALL_MAGIC("/sounds/install_magic.ogg"),
    INSTALL_SAIL("/sounds/install_sail.ogg"),
    INSTALL_SHIP("/sounds/install_ship.ogg"),
    INSTALL_PIRATE1("/sounds/install_pirate_1.ogg"),
    INSTALL_PIRATE2("/sounds/install_pirate_2.ogg"),

    //    SEA_GRASS1("/sounds/magic_algae_1.ogg"),
//    SEA_GRASS2("/sounds/magic_algae_2.ogg"),
    BOILING("/sounds/magic_boiling.ogg"),
    ICE1("/sounds/magic_iceberg_1.ogg"),
    ICE2("/sounds/magic_iceberg_2.ogg"),
    ICE3("/sounds/magic_iceberg_3.ogg"),
    FIRE_MESH("/sounds/magic_lighter.ogg"),
    FIRE_MESH1("/sounds/magic_lighter_1.ogg"),
    FIRE_MESH2("/sounds/magic_lighter_2.ogg"),
    AREA_FLAME1("/sounds/magic_napalm_1.ogg"),
    AREA_FLAME2("/sounds/magic_napalm_2.ogg"),
    WIND_SPEED1("/sounds/magic_speed_up_1.ogg"),
    WIND_SPEED2("/sounds/magic_speed_up_1.ogg"),
    REPAIR("/sounds/magic_repair.ogg"),
    TORNADO("/sounds/magic_typhoon.ogg"),

    GONG("/sounds/shanneton_gong1.ogg");

    private String soundFile;

    private Sound(String soundFile) {
        this.soundFile = soundFile;
    }

    public String getSoundFile() {
        return soundFile;
    }

    public static Sound findResource(String valueSearch) {
        for (Sound value : Sound.values()) {
            if (value.getSoundFile().equals(valueSearch)) {
                return value;
            }
        }
        return null;
    }
}
