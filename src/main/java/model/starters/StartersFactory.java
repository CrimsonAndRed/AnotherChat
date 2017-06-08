package model.starters;

import model.Origin;

public class StartersFactory {

    public static Starter getStarter(Origin origin) {
        switch  (origin) {
            case TWITCHTV:
                return new TwitchStarter();
            case PEKA2TV:
                //TODO
                return null;
            default:
                return null;
        }
    }
}

