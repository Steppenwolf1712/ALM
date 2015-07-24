package nz.ac.auckland.alm.swing.responsive.graph;

/**
 * Created by Marc Janßen on 20.05.2015.
 */
public interface Token {
    public static final char LEXTOKEN_OP_VERT_TILING = '|';
    public static final char LEXTOKEN_OP_HORI_TILING = '/';
    public static final char LEXTOKEN_OP_CONCAT = '*';

    public static final char LEXTOKEN_BR_OPEN = '(';
    public static final char LEXTOKEN_BR_CLOSE = ')';

    public static final char LEXTOKEN_TILING_INDEX_OPEN = '{';
    public static final char LEXTOKEN_TILING_INDEX_CLOSE = '}';
}
