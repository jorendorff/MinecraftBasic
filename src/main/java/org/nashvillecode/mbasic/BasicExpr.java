package org.nashvillecode.mbasic;

import java.io.File;

public abstract class BasicExpr {
	public final File file;
	public final int line;

	public BasicExpr(File file, int line) { this.file = file; this.line = line; }

	public abstract int evaluate(BasicExecutionContext context);

	public static class Number extends BasicExpr {
		public int value;
		public Number(File file, int line, int value) { super(file, line); this.value = value; }
		public int evaluate(BasicExecutionContext context) {
			return value;
		}
	}
	
	public static class Variable extends BasicExpr {
		public String name;
		public Variable(File file, int line, String name) { super(file, line); this.name = name; }
		public int evaluate(BasicExecutionContext context) {
			return context.getVariable(name);
		}
	}

	public static enum UnaryOp {
		NEG, NOT
	}

	public static class Unary extends BasicExpr {
		public UnaryOp op;
		public BasicExpr operand;
		public Unary(File file, int line, UnaryOp op, BasicExpr operand) {
			super(file, line); this.op = op; this.operand = operand;
		}
		public int evaluate(BasicExecutionContext context) {
			int v = operand.evaluate(context);
			switch (op) {
			case NEG: return -v;
			case NOT: return v == 0 ? 1 : 0;
			}
			throw new RuntimeException("internal error: bad opcode");
		}
	}

	public static enum Op {
		ADD, SUB, MUL, DIV, MOD, EQ, NE, LT, LE, GT, GE, AND, OR
	}

	public static class Binary extends BasicExpr {
		public BasicExpr left;
		public BasicExpr right;
		public Op op;
		public Binary(File file, int line, BasicExpr left, BasicExpr right, Op op) {
			super(file, line); this.left = left; this.right = right; this.op = op;
		}
		public int evaluate(BasicExecutionContext context) {
			int l = left.evaluate(context);
			int r = right.evaluate(context);
			switch (op) {
			case ADD: return l + r;
			case SUB: return l - r;
			case MUL: return l * r;
			case DIV:
				if (r == 0)
					throw new BasicException("division by zero", file, line);
				return l / r;
			case MOD:
				if (r == 0)
					throw new BasicException("division by zero", file, line);
				return l % r;
			case EQ: return l == r ? 1 : 0;
			case NE: return l != r ? 1 : 0;
			case LT: return l < r ? 1 : 0;
			case LE: return l <= r ? 1 : 0;
			case GT: return l > r ? 1 : 0;
			case GE: return l >= r ? 1 : 0;
			case AND: return (l != 0 && r != 0) ? 1 : 0;
			case OR: return (l != 0 || r != 0) ? 1 : 0;
			default: throw new RuntimeException("internal error: bad opcode");
			}
		}
	}
}
