package org.nashvillecode.mbasic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BasicParser {
    private BasicLineReader reader;
    private String[] tokens;
    private int i;

    private BasicParser(BasicLineReader reader) {
        this.reader = reader;
        this.tokens = null;
        this.i = 0;
    }

    private File file() { return reader.getFile(); }
    private int line() { return reader.getLineNumber(); }

    private boolean loadNextLine() throws IOException {
        tokens = reader.readLine();
        i = 0;
        return tokens != null;
    }

    private boolean takeIf(String kw) {
        if (i < tokens.length && tokens[i].equalsIgnoreCase(kw)) {
            i++;
            return true;
        } else {
            return false;
        }
    }

    private void require(String kw) {
        if (!takeIf(kw))
            throw reader.syntaxError("'" + kw + "' expected");
    }

    private boolean nextTokenIsInt() {
        if (atEndOfLine())
            return false;
        try {
            Integer.parseInt(tokens[i]);
        } catch (NumberFormatException exc) {
            return false;
        }
        return true;
    }

    private int getInt() {
        if (!nextTokenIsInt())
            throw reader.syntaxError("number expected" + (atEndOfLine() ? "" : ", got: " + tokens[i]));
        return Integer.parseInt(tokens[i++]);
    }

    private boolean nextTokenIsName() {
        return i < tokens.length && Character.isJavaIdentifierStart(tokens[i].charAt(0));
    }

    private String getName() {
        if (!nextTokenIsName())
            throw reader.syntaxError("name expected");
        return tokens[i++];
    }

    private boolean atEndOfLine() {
        return i == tokens.length;
    }

    private void endOfLine() {
        if (!atEndOfLine())
            throw reader.syntaxError("unexpected '" + tokens[i] + "' (didn't expect anything else on this line)");
    }

    private BasicExpr parsePrimitive() {
        if (takeIf("(")) {
            BasicExpr expr = parseExpression();
            require(")");
            return expr;
        } else if (nextTokenIsInt()){
            return new BasicExpr.Number(file(), line(), getInt());
        } else if (nextTokenIsName()) {
            return new BasicExpr.Variable(file(), line(), getName());
        } else {
            throw reader.syntaxError("number or variable expected");
        }
    }

    private BasicExpr parseUnary() {
        if (takeIf("-"))
            return new BasicExpr.Unary(file(), line(), BasicExpr.UnaryOp.NEG, parseUnary());
        else
            return parsePrimitive();
    }

    private BasicExpr parseTerm() {
        BasicExpr expr = parseUnary();
        while (true) {
            if (takeIf("*"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseUnary(), BasicExpr.Op.MUL);
            else if (takeIf("/"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseUnary(), BasicExpr.Op.DIV);
            else if (takeIf("MOD"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseUnary(), BasicExpr.Op.MOD);
            else
                return expr;
        }
    }

    private BasicExpr parseSum() {
        BasicExpr expr = parseTerm();
        while (true) {
            if (takeIf("+"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseTerm(), BasicExpr.Op.ADD);
            else if (takeIf("-"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseTerm(), BasicExpr.Op.SUB);
            else
                return expr;
        }
    }

    private BasicExpr parseRelation() {
        BasicExpr expr = parseSum();
        while (true) {
            if (takeIf("=")) {
                expr = new BasicExpr.Binary(file(), line(), expr, parseSum(), BasicExpr.Op.EQ);
            } else if (takeIf("<")) {
                if (takeIf(">"))
                    expr = new BasicExpr.Binary(file(), line(), expr, parseSum(), BasicExpr.Op.NE);
                else if (takeIf("="))
                    expr = new BasicExpr.Binary(file(), line(), expr, parseSum(), BasicExpr.Op.LE);
                else
                    expr = new BasicExpr.Binary(file(), line(), expr, parseSum(), BasicExpr.Op.LT);
            } else if (takeIf(">")) {
                if (takeIf("="))
                    expr = new BasicExpr.Binary(file(), line(), expr, parseSum(), BasicExpr.Op.GE);
                else
                    expr = new BasicExpr.Binary(file(), line(), expr, parseSum(), BasicExpr.Op.GT);
            } else
                return expr;
        }
    }

    private BasicExpr parseCompound() {
        BasicExpr expr = parseRelation();
        while (true) {
            if (takeIf("AND"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseRelation(), BasicExpr.Op.AND);
            else if (takeIf("OR"))
                expr = new BasicExpr.Binary(file(), line(), expr, parseRelation(), BasicExpr.Op.OR);
            else
                return expr;
        }
    }

    private BasicExpr parseExpression() {
        if (takeIf("NOT"))
            return new BasicExpr.Unary(file(), line(), BasicExpr.UnaryOp.NOT, parseExpression());
        return parseCompound();
    }

/*
    private HashMap<String, Item> allItems;

    private Item getItemForName(String name) {
        if (allItems == null) {
            HashMap<String, Item> itemMap = new HashMap<String, Item>();

            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            Item[] items = Item.itemsList;
            for (int i = 0, len = items.length; i < len; i++) {
                Item item = items[i];
                if (item != null) {
                    ItemStack stack = new ItemStack(item, 1, 0);
                    String s = stack.getDisplayName();
                    System.out.println("Adding item: " + s + " (" + (item instanceof ItemBlock) + ")");
                    itemMap.put(s.toLowerCase(), item);
                }
                // The creative inventory does this:
                //	item.getSubItems(item.itemID, (CreativeTabs)null, stacks);
                // which presumably picks up "red wool" and so forth,
                // but it's a client-only method.
            }
            allItems = itemMap;
        }
        return allItems.get(name.toLowerCase());
    }
*/

    private Block getBlockForName(String name) {
        name = name.toLowerCase().replace(' ', '_');

/*
        // old Minecraft 1.6 code that doesn't work anymore
        Item item = getItemForName(name);
        if (item == null)
            throw reader.syntaxError("unrecognized kind of block or item: '" + name + "'");

        if (item instanceof ItemBlock) {
            ItemBlock ib = (ItemBlock) item;
            return ib.getBlockID();
        } else {
            throw reader.syntaxError(name + " is not a block");
        }
*/

        Block block = (Block) Block.blockRegistry.getObject(name);
        if (block == null)
            throw reader.syntaxError(name + " isn't any kind of block I've ever heard of");
        return block;
    }

    private BasicStatement parseStatement() throws BasicException, IOException {
        if (takeIf("USE")) {
            if (!nextTokenIsName())
                throw reader.syntaxError("block kind expected");
            String name = getName();
            while (nextTokenIsName())
                name += " " + getName();
            BasicExpr m = null;
            if (takeIf(","))
                m = parseExpression();
            endOfLine();
            Block block = getBlockForName(name);
            return new BasicStatement.Use(file(), line(), block, m);
        } else if (takeIf("PLOT") || takeIf("PUT")) {
            BasicExpr x = parseExpression();
            require(",");
            BasicExpr y = parseExpression();
            endOfLine();
            return new BasicStatement.Plot(file(), line(), x, y);
        } else if (takeIf("HLIN") || takeIf("HLINE")) {
            BasicExpr x0 = parseExpression();
            require(",");
            BasicExpr x1 = parseExpression();
            require("AT");
            BasicExpr y = parseExpression();
            endOfLine();
            return new BasicStatement.Hlin(file(), line(), x0, x1, y);
        } else if (takeIf("VLIN") || takeIf("VLINE")) {
            BasicExpr y0 = parseExpression();
            require(",");
            BasicExpr y1 = parseExpression();
            require("AT");
            BasicExpr x = parseExpression();
            endOfLine();
            return new BasicStatement.Vlin(file(), line(), y0, y1, x);
        } else if (takeIf("UP")) {
            return new BasicStatement.Up(file(), line());
        } else if (takeIf("DEF")) {
            String name = getName();
            endOfLine();
            BasicStatement command = parseStatements(BlockDelimiter.END);
            return new BasicStatement.Def(file(), line(), name, command);
        } else if (takeIf("RUN")) {
            String name = getName();
            endOfLine();
            return new BasicStatement.Run(file(), line(), name);
        } else if (takeIf("LET")) {
            String name = getName();
            require("=");
            BasicExpr expr = parseExpression();
            return new BasicStatement.Let(file(), line(), name, expr);
        } else if (takeIf("FOR")) {
            String name = getName();
            require("=");
            BasicExpr start = parseExpression();
            require("TO");
            BasicExpr stop = parseExpression();
            BasicExpr step = null;
            if (!atEndOfLine()) {
                require("STEP");
                step = parseExpression();
            }
            endOfLine();
            BasicStatement body = parseStatements(BlockDelimiter.NEXT);
            return new BasicStatement.ForLoop(file(), line(), name, start, stop, step, body);
        } else if (nextTokenIsName()) {
            // LET statement with the optional LET keyword omitted.
            String name = getName();
            require("=");
            BasicExpr expr = parseExpression();
            return new BasicStatement.Let(file(), line(), name, expr);
        } else {
            throw reader.syntaxError("unexpected symbol '" + tokens[0] + "' at start of line");
        }
    }

    private static enum BlockDelimiter {
        NONE, END, NEXT
    }

    private BasicStatement parseStatements(BlockDelimiter delim) throws IOException, BasicException {
        ArrayList<BasicStatement> statements = new ArrayList<BasicStatement>();
        boolean foundDelim = false;

        while (loadNextLine()) {
            if (atEndOfLine())
                continue;
            if (takeIf("END")) {
                if (delim != BlockDelimiter.END)
                    throw reader.syntaxError("unexpected END");
                endOfLine();
                foundDelim = true;
                break;
            } else if (takeIf("NEXT")) {
                if (delim != BlockDelimiter.NEXT)
                    throw reader.syntaxError("unexpected NEXT");
                endOfLine();
                foundDelim = true;
                break;
            }
            statements.add(parseStatement());
        }

        if (!foundDelim) {
            if (delim == BlockDelimiter.END)
                throw reader.syntaxError("expected END statement");
            else if (delim == BlockDelimiter.NEXT)
                throw reader.syntaxError("expected NEXT statement");
        }

        if (statements.size() == 1)
            return statements.get(0);
        return new BasicStatement.Sequence(file(), line(), statements.toArray(new BasicStatement[0]));
    }

    public static BasicStatement parseProgram(BasicLineReader reader) throws IOException, BasicException {
        BasicParser parser = new BasicParser(reader);
        return parser.parseStatements(BlockDelimiter.NONE);
    }
}
