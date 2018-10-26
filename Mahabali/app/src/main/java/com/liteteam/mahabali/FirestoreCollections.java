package com.liteteam.mahabali;

public enum FirestoreCollections {
    RESCUE_TEAM_LOCATIONS,
    USER_LOCATIONS;

    /**
     * Method returns a formatted String for each collection
     * @return the formatted String for the collection
     */
    public String getName() {
        StringBuilder collection = new StringBuilder();
        String[] terms = name().split("_");
        for (String term : terms) {
            collection.append(term.charAt(0));
            collection.append(term.toLowerCase().substring(1));
        }
        return collection.toString();
    }
}
