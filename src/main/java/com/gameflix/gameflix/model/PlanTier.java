package com.gameflix.gameflix.model;

import java.util.List;
import java.util.Locale;

public final class PlanTier {
    public static final String FREE = "Free";
    public static final String RETRO = "Retro";
    public static final String INDIE = "Indie";
    public static final String AAA = "AAA";

    private static final List<String> ORDER = List.of(FREE, RETRO, INDIE, AAA);

    private PlanTier() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return null;
        }
        String tier = raw.trim().toLowerCase(Locale.ROOT);
        switch (tier) {
            case "free":
            case "starter":
                return FREE;
            case "retro":
            case "retro pack":
                return RETRO;
            case "indie":
            case "indie pack":
            case "pro":
                return INDIE;
            case "aaa":
            case "aaa pack":
            case "ultimate":
                return AAA;
            default:
                return null;
        }
    }

    public static int weight(String tier) {
        String normalized = normalize(tier);
        if (normalized == null) {
            return 0;
        }
        return ORDER.indexOf(normalized) + 1;
    }

    public static List<PlanDescriptor> descriptors() {
        return List.of(
                new PlanDescriptor(
                        FREE,
                        "Free",
                        "$0",
                        "Access the free-to-play catalog with cloud saves and two linked devices.",
                        List.of("1080p streaming", "Free-to-play catalog", "Cloud saves", "2 devices"),
                        List.of("Free catalog")
                ),
                new PlanDescriptor(
                        RETRO,
                        "Retro Pack",
                        "$5.99",
                        "Unlock the full retro vault. Includes everything in Free.",
                        List.of("Upscaled classics", "Retro achievements", "CRT-style shaders", "Early event access"),
                        List.of("Retro catalog", "Free catalog")
                ),
                new PlanDescriptor(
                        INDIE,
                        "Indie Pack",
                        "$12.99",
                        "Indie darlings plus every retro and free game. Perfect middle lane.",
                        List.of("4K streaming on indies", "Cross-save sync", "5 devices", "Queue priority"),
                        List.of("Indie catalog", "Retro Pack", "Free catalog")
                ),
                new PlanDescriptor(
                        AAA,
                        "AAA Pack",
                        "$17.99",
                        "Day-one AAA drops with the entire library beneath it.",
                        List.of("120fps where available", "Unlimited devices", "Latency priority", "Premium support"),
                        List.of("AAA catalog", "Indie Pack", "Retro Pack", "Free catalog")
                )
        );
    }

    public record PlanDescriptor(
            String key,
            String label,
            String price,
            String description,
            List<String> perks,
            List<String> includes
    ) {
    }
}
