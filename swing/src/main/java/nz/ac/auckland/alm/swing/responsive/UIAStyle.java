package nz.ac.auckland.alm.swing.responsive;

import nz.ac.auckland.alm.algebra.string.Lexer;
import nz.ac.auckland.alm.algebra.string.Parser;
import nz.ac.auckland.alm.swing.responsive.graph.Token;

import javax.swing.text.*;
import java.awt.*;

/**
 * Created by Marc Janßen on 10.07.2015.
 */
public class UIAStyle extends DefaultStyledDocument implements Parser.IListener {

    private static final int FONT_SIZE = 16;
//    final StyleContext cont = StyleContext.getDefaultStyleContext();
    SimpleAttributeSet BRACKET = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(80, 160, 255));//, StyleConstants.FontSize, 20);
    SimpleAttributeSet MISTAKE = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255,0,0));
    SimpleAttributeSet IDENTIFIER = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(80, 0, 255));
    SimpleAttributeSet STAR = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(0,180,0));
    SimpleAttributeSet TILING = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(96,46,76));
    SimpleAttributeSet STACKING = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(90,134,50));
    SimpleAttributeSet BRACKETID = new SimpleAttributeSet();// cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(190,120,255));
    private String m_text;

    public UIAStyle() {
        StyleConstants.setFontSize(BRACKET, FONT_SIZE);
        StyleConstants.setForeground(BRACKET, new Color(80, 160, 255));

        StyleConstants.setFontSize(MISTAKE, FONT_SIZE);
        StyleConstants.setForeground(MISTAKE, new Color(255,0,0));

        StyleConstants.setFontSize(IDENTIFIER, FONT_SIZE);
        StyleConstants.setForeground(IDENTIFIER,new Color(80, 0, 255));

        StyleConstants.setFontSize(STAR, FONT_SIZE);
        StyleConstants.setBold(STAR, true);
        StyleConstants.setForeground(STAR, new Color(0,180,0));

        StyleConstants.setFontSize(TILING, FONT_SIZE);
        StyleConstants.setBold(TILING, true);
        StyleConstants.setForeground(TILING, new Color(195, 104, 60));

        StyleConstants.setFontSize(STACKING, FONT_SIZE);
        StyleConstants.setBold(STACKING, true);
        StyleConstants.setForeground(STACKING, new Color(123,60,195));

        StyleConstants.setFontSize(BRACKETID, FONT_SIZE);
        StyleConstants.setItalic(BRACKETID, true);
        StyleConstants.setForeground(BRACKETID, new Color(190,120,255));
    }

    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
        super.insertString(offset, str, a);
        highlighting();
    }

    private void highlighting() throws BadLocationException {
        m_text = getText(0, getLength());



        if (!m_text.isEmpty())
            paintEverything();
    }

    public void remove(int offset, int len) throws BadLocationException {
        super.remove(offset, len);

        m_text = getText(0, getLength());

        if (!m_text.isEmpty())
            paintEverything();
    }

    private void paintCharacters(int index) {
        Character c = m_text.charAt(index);

        switch (c) {
            case Token.LEXTOKEN_BR_CLOSE:
            case Token.LEXTOKEN_BR_OPEN:
                setCharacterAttributes(index, 1, BRACKET, false);
                break;
            case Token.LEXTOKEN_TILING_INDEX_OPEN:
                if (m_text.charAt(index-1) == Token.LEXTOKEN_OP_HORI_TILING || m_text.charAt(index-1) == Token.LEXTOKEN_OP_VERT_TILING) {
                    int getClosingBR = m_text.indexOf(Token.LEXTOKEN_TILING_INDEX_CLOSE, index);
                    if (getClosingBR == -1) {
                        setCharacterAttributes(index, m_text.length() - index, MISTAKE, false);
                        index = m_text.length();
                    } else {
                        setCharacterAttributes(index, (getClosingBR + 1 - index), BRACKETID, false);
                        index = getClosingBR;
                    }
                    break;
                }
            case Token.LEXTOKEN_TILING_INDEX_CLOSE:
                setCharacterAttributes(index, m_text.length()-index, MISTAKE, false);
                index = m_text.length();
                break;
            case Token.LEXTOKEN_OP_CONCAT:
                setCharacterAttributes(index, 1, STAR, false);
                break;
            case Token.LEXTOKEN_OP_HORI_TILING:
                setCharacterAttributes(index, 1, TILING, false);
                break;
            case Token.LEXTOKEN_OP_VERT_TILING:
                setCharacterAttributes(index, 1, STACKING, false);
                break;
            default:
                setCharacterAttributes(index, 1, IDENTIFIER, false);
                break;
        }
        if (m_text.length()>++index) {
            paintCharacters(index);
        }
    }

    private void paintEverything() {
        paintCharacters(0);

        Lexer lexer = new Lexer(m_text);
        Parser parser = new Parser(lexer.run(), Parser.getDefaultAreaFactory(), this);/*new Parser.IListener() {
            @Override
            public void onError(String error, Lexer.Token token) {
                System.out.println(input);
                String errorIndicator = "";
                for (int i = 0; i < token.position; i++)
                    errorIndicator += " ";
                errorIndicator += "^";
                System.out.println(errorIndicator);
                System.out.println("Parser error at " + token.position + ": " + error);
            }
        });*/
        parser.run();
    }

    @Override
    public void onError(String error, Lexer.Token token) {
        setCharacterAttributes(token.position, m_text.length(), MISTAKE, false);
        return;
    }

    /*
    private void paintEverything() {
        paintCharacters(0);

        String[] toPaint = m_text.split("\\"+ Token.LEXTOKEN_OP_CONCAT);

        int indexPoint = 0;
        for (int i=0;i<toPaint.length;i++) {
            if (toPaint[i].startsWith("" + Token.LEXTOKEN_OP_HORI_TILING) || toPaint[i].startsWith("" + Token.LEXTOKEN_OP_HORI_TILING) ||
                    toPaint[i].startsWith("" + Token.LEXTOKEN_TILING_INDEX_OPEN) || toPaint[i].startsWith(""+Token.LEXTOKEN_TILING_INDEX_CLOSE)
                    || toPaint[i].length()==0) {
                setCharacterAttributes(indexPoint, m_text.length(), MISTAKE, false);
            } else {
                findMistakes(toPaint[i], indexPoint);
                indexPoint = indexPoint + toPaint[i].length() + 1;
            }
        }
    }


    private void findMistakes(String part, int indexPoint) {
        bracketTilingCheck(indexPoint, part);
    }


    private void bracketTilingCheck(int offset, String toCheck) {
        String erg = toCheck;

        Stack<BracketPointer> bracketStack = new Stack<BracketPointer>();
        Character currentTiling = null;

        for (int i = 0; i<erg.length(); i++) {
            Character pointer = erg.charAt(i);
            switch (pointer) {
                case Token.LEXTOKEN_BR_OPEN:
                    bracketStack.push(new BracketPointer(i));
                    if (currentTiling != null)
                        currentTiling = switchTiling(currentTiling);
                    break;
                case Token.LEXTOKEN_BR_CLOSE:
                    if (bracketStack.empty()) {
//                        throw new MalFormedUIA_Exception("There is a closing bracket without an matching open Bracket!");
                        setCharacterAttributes(offset+i, m_text.length(), MISTAKE, false);
                        return;
                    }
                    BracketPointer temp = bracketStack.pop();
                    //if (temp.isNotNecassary) {//UnnecessaryBrackets arent import at the Highlighting-Step. I remove them later anyway
                        //erg = erg.substring(0,temp.m_position)+erg.substring(temp.m_position+1,i)+erg.substring(i+1,erg.length());
                        //i--;
                        //throw new MalFormedUIA_Exception("The User-Interface-Algebra String consists some unnecessary Brackets");
                    //}
                    //A Closing bracket should not be in front of all tiling tokens of the String. There has to be a Tiling before this bracket symbol
                    currentTiling = switchTiling(currentTiling);
                    break;
                case Token.LEXTOKEN_OP_HORI_TILING:
                case Token.LEXTOKEN_OP_VERT_TILING:
                    if (currentTiling==null) {
                        currentTiling = pointer;
                    } else {
                        if (!pointer.equals(currentTiling)) {
//                            throw new MalFormedUIA_Exception("The operator " + Token.LEXTOKEN_OP_HORI_TILING + " and " + Token.LEXTOKEN_OP_VERT_TILING + " have to be the same on the same bracket-level\n" +
//                                    "and they have to be switching between the levels!");
                            setCharacterAttributes(offset+i, m_text.length(), MISTAKE, false);
                            return;
                        }
                    }
                    if (!bracketStack.empty())
                        bracketStack.peek().isNotNecassary = false;
                    break;
            }
        }
        if (!bracketStack.empty()) {
//            throw new MalFormedUIA_Exception("There is an opened Bracket left!\n(The Star-Operator must not be in some Brackets)");
            int mistakePosition = bracketStack.peek().m_position;
            setCharacterAttributes(offset+mistakePosition, m_text.length(), MISTAKE, false);
            return;
        }
    }

    private char switchTiling(char p_cTiling) {
        if (p_cTiling==Token.LEXTOKEN_OP_HORI_TILING)
            return Token.LEXTOKEN_OP_VERT_TILING;
        else
            return Token.LEXTOKEN_OP_HORI_TILING;
    }

    public class BracketPointer {
        public boolean isNotNecassary;
        public int m_position;

        public BracketPointer(int p_position) {
            m_position = p_position;
        }
    }*/
}
