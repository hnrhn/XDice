package net.xdice;

import java.util.concurrent.ThreadLocalRandom;

public class ArtificialIntelligence {
    public String randomWtfResponse() {
        return Constants.wtfResponses[ThreadLocalRandom.current().nextInt(Constants.wtfResponses.length)];
    }

    public String randomThanksResponse() {
        return Constants.thanksResponses[ThreadLocalRandom.current().nextInt(Constants.thanksResponses.length)];
    }

    public String randomInsultResponse() {
        return Constants.insultResponses[ThreadLocalRandom.current().nextInt(Constants.insultResponses.length)];
    }

    public String randomLoveResponse() {
        return ":heart:";
    }
}
