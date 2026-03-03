package com.lildan42.cft.fighterdata.fighters;

public enum FighterHeartClass {
    BLUE_HEART("BLH", 1.0, 50.0),
    PURPLE_HEART("PH", 51.0, 100.0),
    GREEN_HEART("GH", 101.0, 200.0),
    RED_HEART("RH", 201.0, 500.0),
    BROWN_HEART("BRH", 501.0, 1000.0);

    private final String shortName;
    private final double minHp;
    private final double maxHp;

    FighterHeartClass(String shortName, double minHp, double maxHp) {
        this.shortName = shortName;
        this.minHp = minHp;
        this.maxHp = maxHp;
    }

    public String getHeartClassName() {
        char firstChar = Character.toUpperCase(this.name().charAt(0));
        String nameSuffix = this.name().substring(1).toLowerCase().replace("_", "");

        return firstChar + nameSuffix;
    }

    public String getShortName() {
        return this.shortName;
    }

    public double getMinHp() {
        return this.minHp;
    }

    public double getMaxHp() {
        return this.maxHp;
    }

    public static FighterHeartClass getHealthClassByHealthValue(double healthValue) {
        for (FighterHeartClass healthClass : values()) {
            if(healthValue <= healthClass.getMaxHp()) {
                return healthClass;
            }
        }

        return BLUE_HEART;
    }

    public static FighterHeartClass findHealthClassByName(String name) {
        for (FighterHeartClass healthClass : values()) {
            if(healthClass.getShortName().equals(name) || healthClass.getHeartClassName().equals(name) || healthClass.name().equals(name)) {
                return healthClass;
            }
        }

        return null;
    }
}