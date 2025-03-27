package io;

/**
 * Constants used in the DotExport class.
 * @author uuifx
 */
public final class DotExportConstants {
    /**
     * Index for CONTAINS relationship in ordering.
     */
    public static final int CONTAINS_ORDER = 0;

    /**
     * Index for CONTAINED_IN relationship in ordering.
     */
    public static final int CONTAINED_IN_ORDER = 1;

    /**
     * Index for PART_OF relationship in ordering.
     */
    public static final int PART_OF_ORDER = 2;

    /**
     * Index for HAS_PART relationship in ordering.
     */
    public static final int HAS_PART_ORDER = 3;

    /**
     * Index for SUCCESSOR_OF relationship in ordering.
     */
    public static final int SUCCESSOR_OF_ORDER = 4;

    /**
     * Index for PREDECESSOR_OF relationship in ordering.
     */
    public static final int PREDECESSOR_OF_ORDER = 5;

    /**
     * Index for DEFAULT relationship in ordering.
     */
    public static final int DEFAULT_ORDER = 6;

    /**
     * Private constructor to prevent instantiation.
     */
    private DotExportConstants() {
        // Utility class should not be instantiated
    }
}