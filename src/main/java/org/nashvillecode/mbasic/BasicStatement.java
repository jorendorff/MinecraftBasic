package org.nashvillecode.mbasic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A statement in a BASIC program. BasicStatements are the main nodes
 * in the abstract syntax tree (AST), the representation of the BASIC
 * program in memory while it's running.
 *
 * Every statement has a location (file and line). Apart from that,
 * pretty much the only thing you can do with a statement is execute it.
 */
public abstract class BasicStatement {
	public final File file;
	public final int line;
	BasicStatement(File file, int line) { this.file = file; this.line = line; }

	public abstract void execute(BasicExecutionContext context);

	public static class Use extends BasicStatement {
		public int blockID;
		public BasicExpr metadata;	
		public Use(File file, int line, int blockID, BasicExpr metadata) {
			super(file, line); this.blockID = blockID; this.metadata = metadata;
		}
		public void execute(BasicExecutionContext context) {
			int m = metadata == null ? 0 : metadata.evaluate(context);
			context.setBlockIDAndMetadata(blockID, m);
		}
	}

	/** PLOT statement from Applesoft BASIC. */
	public static class Plot extends BasicStatement {
		public BasicExpr x, y;
		public Plot(File file, int line, BasicExpr x, BasicExpr y) {
			super(file, line); this.x = x; this.y = y;
		}
		public void execute(BasicExecutionContext context) {
			int xv = x.evaluate(context);
			int yv = y.evaluate(context);
			context.placeBlock(xv, yv);
		}
	}

	/** HLIN statement from Applesoft BASIC. */
	public static class Hlin extends BasicStatement {
		public BasicExpr x0, x1, y;
		public Hlin(File file, int line, BasicExpr x0, BasicExpr x1, BasicExpr y) {
			super(file, line); this.x0 = x0; this.x1 = x1; this.y = y;
		}
		public void execute(BasicExecutionContext context) {
			int x0v = x0.evaluate(context);
			int x1v = x1.evaluate(context);
			int yv = y.evaluate(context);
			for (int x = x0v; x <= x1v; x++)
				context.placeBlock(x, yv);
		}
	}

	/** VLIN statement from Applesoft BASIC. */
	public static class Vlin extends BasicStatement {
		public BasicExpr y0, y1, x;
		public Vlin(File file, int line, BasicExpr y0, BasicExpr y1, BasicExpr x) {
			super(file, line); this.y0 = y0; this.y1 = y1; this.x = x;
		}
		public void execute(BasicExecutionContext context) {
			int y0v = y0.evaluate(context);
			int y1v = y1.evaluate(context);
			int xv = x.evaluate(context);
			for (int y = y0v; y <= y1v; y++)
				context.placeBlock(xv, y);
		}
	}

	public static class Up extends BasicStatement {
		public Up(File file, int line) {
			super(file, line);
		}
		public void execute(BasicExecutionContext context) {
			context.moveUp();
		}
	}

	public static class Sequence extends BasicStatement {
		public BasicStatement[] commands;
		public Sequence(File file, int line, BasicStatement[] commands) {
			super(file, line); this.commands = commands;
		}
		public void execute(BasicExecutionContext context) {
			for (int i = 0; i < commands.length; i++)
				commands[i].execute(context);
		}
	}

	/** LET statement common to all BASICs. */
	public static class Let extends BasicStatement {
		public String name;
		public BasicExpr expr;
		public Let(File file, int line, String name, BasicExpr expr) {
			super(file, line); this.name = name; this.expr = expr;
		}
		public void execute(BasicExecutionContext context) {
			int value = expr.evaluate(context);
			context.setVariable(name, value);
		}
	}

	public static class Def extends BasicStatement {
		public String name;
		public BasicStatement command;
		public Def(File file, int line, String name, BasicStatement command) {
			super(file, line); this.name = name; this.command = command;
		}
		public void execute(BasicExecutionContext context) {
			context.define(name, command);
		}
	}

	public static class Run extends BasicStatement {
		public String name;
		public Run(File file, int line, String name) {
			super(file, line); this.name = name;
		}
		public void execute(BasicExecutionContext context) {
			context.run(name);
		}
	}

	/** FOR/NEXT loop common to all BASICs. */
	public static class ForLoop extends BasicStatement {
		public String variable;
		public BasicExpr start;
		public BasicExpr stop;
		public BasicExpr step;
		public BasicStatement body;
		public ForLoop(File file, int line, String variable, BasicExpr start, BasicExpr stop, BasicExpr step, BasicStatement body) {
			super(file, line);
			this.variable = variable; this.start = start; this.stop = stop; this.step = step; this.body = body;
		}
		public void execute(BasicExecutionContext context) {
			int value = start.evaluate(context);
			int stopValue = stop.evaluate(context);
			int stepValue = step == null ? 1 : step.evaluate(context);
			while (stepValue > 0 ? value <= stopValue : value >= stopValue) {
				context.setVariable(variable, value);
				body.execute(context);
				value = context.getVariable(variable) + stepValue;
			}
		}
	}
}
