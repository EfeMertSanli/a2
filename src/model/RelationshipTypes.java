package model;

import util.Constants.Relationship;

/**
 * Defines the relationship types between nodes in the product recommendation system.
 * Each relationship type has a corresponding inverse relationship.
 *
 * @author uuifx
 */
public enum RelationshipTypes {
    CONTAINS(Relationship.CONTAINS, Relationship.CONTAINED_IN),
    CONTAINED_IN(Relationship.CONTAINED_IN, Relationship.CONTAINS),
    PART_OF(Relationship.PART_OF, Relationship.HAS_PART),
    HAS_PART(Relationship.HAS_PART, Relationship.PART_OF),
    SUCCESSOR_OF(Relationship.SUCCESSOR_OF, Relationship.PREDECESSOR_OF),
    PREDECESSOR_OF(Relationship.PREDECESSOR_OF, Relationship.SUCCESSOR_OF);

    private final String name;
    private final String inverseName;

    /**
     * Creates a new relationship type.
     *
     * @param name The name of the relationship type
     * @param inverseName The name of the inverse relationship type
     */
    private RelationshipTypes(String name, String inverseName) {
        this.name = name;
        this.inverseName = inverseName;
    }

    /**
     * Gets the name of the relationship type.
     *
     * @return The name of the relationship type
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the inverse relationship type.
     *
     * @return The inverse relationship type
     */
    public RelationshipTypes getInverse() {
        switch (this) {
            case CONTAINS:
                return CONTAINED_IN;
            case CONTAINED_IN:
                return CONTAINS;
            case PART_OF:
                return HAS_PART;
            case HAS_PART:
                return PART_OF;
            case SUCCESSOR_OF:
                return PREDECESSOR_OF;
            case PREDECESSOR_OF:
                return SUCCESSOR_OF;
            default:
                throw new IllegalStateException("Unknown relationship type");
        }
    }

    /**
     * Gets a relationship type by its name.
     *
     * @param name The name of the relationship type
     * @return The relationship type with the given name or null if not found
     */
    public static RelationshipTypes fromString(String name) {
        for (RelationshipTypes type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}