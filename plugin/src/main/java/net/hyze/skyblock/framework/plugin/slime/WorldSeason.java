package net.hyze.skyblock.framework.plugin.slime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WorldSeason {

    SPRING("Primavera"),
    SUMMER("Verão"),
    AUTUMN("Outono"),
    WINTER("Inverno");

    @Getter
    private final String displayName;

}
