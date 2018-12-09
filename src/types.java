import java.util.LinkedList;
import java.util.TreeMap;

interface Node {
    String show();
}

interface StmtNode extends Node {
    void interpret(TreeMap<VarNode, Integer> vars);
}

interface ANode extends Node {
    Integer interpretA(TreeMap<VarNode, Integer> vars);
}

interface BNode extends Node {
    Boolean interpretB(TreeMap<VarNode, Integer> vars);
}

interface InstructionNode extends StmtNode {
    void openBlock();

    void closeBlock();

    void setCondition(Node condition);

    void addStmt(Node stmt);

    boolean done();
}

class Symbol implements Node {
    private String symbol;

    public Symbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Symbol)
            return ((Symbol) obj).symbol.equals(symbol);
        return false;
    }

    @Override
    public String toString() {
        return symbol;
    }

    @Override
    public String show() {
        return null;
    }
}

class MainNode implements StmtNode {
    private StmtNode prog;

    public MainNode(Node prog) {
        // TODO check everywhere if parameters are instances of the right class before casting
        this.prog = (StmtNode) prog;
    }

    @Override
    public String show() {
        return "<MainNode>\n" + Parser.addNewline(prog.show());
    }

    @Override
    public void interpret(TreeMap<VarNode, Integer> vars) {
        prog.interpret(vars);
    }
}

class IntNode implements ANode {
    private int number;

    public IntNode(String number) {
        this.number = Integer.parseInt(number);
    }

    @Override
    public String toString() {
        return Integer.toString(number);
    }

    @Override
    public String show() {
        return "<IntNode> " + number + "\n";
    }

    @Override
    public Integer interpretA(TreeMap<VarNode, Integer> vars) {
        return number;
    }
}

class BoolNode implements BNode {
    private boolean bool;

    public BoolNode(String bool) {
        this.bool = bool.equalsIgnoreCase("true");
    }

    @Override
    public String toString() {
        return (bool ? "true" : "false");
    }

    @Override
    public String show() {
        return "<BoolNode> " + (bool ? "true" : "false") + "\n";
    }

    @Override
    public Boolean interpretB(TreeMap<VarNode, Integer> vars) {
        return bool;
    }
}

class VarNode implements ANode, Comparable {
    private String var;

    public VarNode(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return var;
    }

    @Override
    public String show() {
        return "<VariableNode> " + var + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VarNode)
            return compareTo(o) == 0;
        return false;
    }

    @Override
    public int compareTo(Object o) {
        return this.var.compareTo(((VarNode) o).var);
    }

    @Override
    public Integer interpretA(TreeMap<VarNode, Integer> vars) {
        return vars.get(this);
    }
}

class PlusNode implements ANode {
    private ANode aExpr1;
    private ANode aExpr2;

    public PlusNode(Node aExpr1, Node aExpr2) {
        this.aExpr1 = (ANode) aExpr1;
        this.aExpr2 = (ANode) aExpr2;
    }

    @Override
    public String show() {
        String print = aExpr1.show() + aExpr2.show();

        return "<PlusNode> +\n" + Parser.addNewline(print);
    }

    @Override
    public Integer interpretA(TreeMap<VarNode, Integer> vars) {
        return aExpr1.interpretA(vars) + aExpr2.interpretA(vars);
    }
}

class DivNode implements ANode {
    private ANode aExpr1;
    private ANode aExpr2;

    public DivNode(Node aExpr1, Node aExpr2) {
        this.aExpr1 = (ANode) aExpr1;
        this.aExpr2 = (ANode) aExpr2;
    }

    @Override
    public String show() {
        String print = aExpr1.show() + aExpr2.show();

        return "<DivNode> /\n" + Parser.addNewline(print);
    }

    @Override
    public Integer interpretA(TreeMap<VarNode, Integer> vars) {
        return aExpr1.interpretA(vars) / aExpr2.interpretA(vars);
    }
}

class BracketNode implements ANode, BNode {
    private Node expr;

    public BracketNode(Node expr) {
        this.expr = expr;
    }

    @Override
    public String show() {
        return "<BracketNode> ()\n" + Parser.addNewline(expr.show());
    }

    @Override
    public Integer interpretA(TreeMap<VarNode, Integer> vars) {
        if (expr instanceof ANode)
            return ((ANode) expr).interpretA(vars);
        return null;
    }

    @Override
    public Boolean interpretB(TreeMap<VarNode, Integer> vars) {
        if (expr instanceof BNode)
            return ((BNode) expr).interpretB(vars);
        return null;
    }
}

class AndNode implements BNode {
    private BNode bExpr1;
    private BNode bExpr2;

    public AndNode(Node bExpr1, Node bExpr2) {
        this.bExpr1 = (BNode) bExpr1;
        this.bExpr2 = (BNode) bExpr2;
    }

    @Override
    public String show() {
        String print = bExpr1.show() + bExpr2.show();

        return "<AndNode> &&\n" + Parser.addNewline(print);
    }

    @Override
    public Boolean interpretB(TreeMap<VarNode, Integer> vars) {
        return bExpr1.interpretB(vars) && bExpr2.interpretB(vars);
    }
}

class GreaterNode implements BNode {
    private ANode aExpr1;
    private ANode aExpr2;

    public GreaterNode(Node aExpr1, Node aExpr2) {
        this.aExpr1 = (ANode) aExpr1;
        this.aExpr2 = (ANode) aExpr2;
    }

    @Override
    public String show() {
        String print = aExpr1.show() + aExpr2.show();

        return "<GreaterNode> >\n" + Parser.addNewline(print);
    }

    @Override
    public Boolean interpretB(TreeMap<VarNode, Integer> vars) {
        return aExpr1.interpretA(vars) > aExpr2.interpretA(vars);
    }
}

class NotNode implements BNode {
    private BNode bExpr;

    public NotNode(Node bExpr) {
        this.bExpr = (BNode) bExpr;
    }

    @Override
    public String show() {
        return "<NotNode> !\n" + Parser.addNewline(bExpr.show());
    }

    @Override
    public Boolean interpretB(TreeMap<VarNode, Integer> vars) {
        return !bExpr.interpretB(vars);
    }
}

class AssignmentNode implements StmtNode {
    private VarNode var;
    private ANode aExpr;

    public AssignmentNode(Node left, Node right) {
        this.var = (VarNode) left;
        this.aExpr = (ANode) right;
    }

    @Override
    public String show() {
        String print = var.show() + aExpr.show();

        return "<AssignmentNode> =\n" + Parser.addNewline(print);
    }

    @Override
    public void interpret(TreeMap<VarNode, Integer> vars) {
        vars.put(var, aExpr.interpretA(vars));
    }
}

class BlockNode implements StmtNode {
    private LinkedList<StmtNode> stmts = new LinkedList<>();

    void pushStmt(Node stmt) {
        stmts.addFirst((StmtNode) stmt);
    }

    StmtNode getStmt() {
        LinkedList<StmtNode> copy = (LinkedList<StmtNode>) stmts.clone();
        StmtNode stmt = getStmtAux();
        stmts = copy;
        return stmt;
    }

    private StmtNode getStmtAux() {
        if (stmts.isEmpty())
            return null;
        if (stmts.size() == 1)
            return stmts.getFirst();
        return buildSequence();
    }

    private StmtNode buildSequence() {
        StmtNode node = stmts.removeLast();
        if (stmts.isEmpty())
            return node;
        return new SequenceNode(node, buildSequence());
    }

    @Override
    public String show() {
        StmtNode stmt = getStmt();
        String blockNode = "<BlockNode> {}\n";
        return stmt == null ? blockNode : blockNode + Parser.addNewline(stmt.show());
    }

    @Override
    public void interpret(TreeMap<VarNode, Integer> vars) {
        if (!stmts.isEmpty())
            getStmt().interpret(vars);
    }
}

class IfNode implements InstructionNode {
    private BNode condition;
    private BlockNode ifBlock;
    private BlockNode elseBlock;
    private boolean ifBlockOpen = false;
    private boolean elseBlockOpen = false;
    private boolean ifDone = false;
    private boolean elseDone = false;

    public IfNode() {
        condition = null;
        ifBlock = new BlockNode();
        elseBlock = new BlockNode();
    }

    public IfNode(Node condition) {
        this.condition = (BNode) condition;
        ifBlock = new BlockNode();
        elseBlock = new BlockNode();
    }

    public IfNode(Node condition, Node ifBlock, Node elseBlock) {
        this.condition = (BNode) condition;
        this.ifBlock = (BlockNode) ifBlock;
        this.elseBlock = (BlockNode) elseBlock;
    }

    @Override
    public void openBlock() {
        if (done()) return;

        if (ifDone)
            elseBlockOpen = true;
        else
            ifBlockOpen = true;
    }

    @Override
    public void closeBlock() {
        if (done()) return;

        if (ifBlockOpen) {
            ifBlockOpen = false;
            ifDone = true;
        } else if (elseBlockOpen) {
            elseBlockOpen = false;
            elseDone = true;
        }
    }

    @Override
    public void setCondition(Node condition) {
        this.condition = (BNode) condition;
    }

    @Override
    public void addStmt(Node stmt) {
        if (done()) return;

        if (ifBlockOpen)
            ifBlock.pushStmt(stmt);
        else if (elseBlockOpen)
            elseBlock.pushStmt(stmt);
    }

    @Override
    public boolean done() {
        return ifDone && elseDone;
    }

    @Override
    public String show() {
        String print = condition.show() + ifBlock.show() + elseBlock.show();

        return "<IfNode> if\n" + Parser.addNewline(print);
    }

    @Override
    public void interpret(TreeMap<VarNode, Integer> vars) {
        if (condition.interpretB(vars))
            ifBlock.interpret(vars);
        else
            elseBlock.interpret(vars);
    }
}

class WhileNode implements InstructionNode {
    private BNode condition;
    private BlockNode block;
    private boolean blockOpen = false;
    private boolean done = false;

    public WhileNode() {
        condition = null;
        block = new BlockNode();
    }

    public WhileNode(Node condition) {
        this.condition = (BNode) condition;
        block = new BlockNode();
    }

    public WhileNode(Node condition, Node block) {
        this.condition = (BNode) condition;
        this.block = (BlockNode) block;
    }

    @Override
    public void openBlock() {
        if (done()) return;

        blockOpen = true;
    }

    @Override
    public void closeBlock() {
        if (done()) return;

        blockOpen = false;
        done = true;
    }

    @Override
    public void setCondition(Node condition) {
        this.condition = (BNode) condition;
    }

    @Override
    public void addStmt(Node stmt) {
        if (done()) return;

        if (blockOpen)
            block.pushStmt(stmt);
    }

    @Override
    public boolean done() {
        return done;
    }

    @Override
    public String show() {
        String print = condition.show() + block.show();

        return "<WhileNode> while\n" + Parser.addNewline(print);
    }

    @Override
    public void interpret(TreeMap<VarNode, Integer> vars) {
        while (condition.interpretB(vars))
            block.interpret(vars);
    }
}

class SequenceNode implements StmtNode {
    private StmtNode stmt1;
    private StmtNode stmt2;

    SequenceNode(Node stmt1, Node stmt2) {
        this.stmt1 = (StmtNode) stmt1;
        this.stmt2 = (StmtNode) stmt2;
    }

    @Override
    public String show() {
        if (stmt1 == null || stmt2 == null)
            return "<SequenceNode>\n";

        String print = stmt1.show() + stmt2.show();
        return "<SequenceNode>\n" + Parser.addNewline(print);
    }

    @Override
    public void interpret(TreeMap<VarNode, Integer> vars) {
        stmt1.interpret(vars);
        stmt2.interpret(vars);
    }
}
