package org.nashvillecode.mbasic;

import java.io.*;
import java.util.ArrayList;

public class BasicLineReader {
	private File file;
	private int lineNumber;
	private BufferedReader reader;

	public BasicLineReader(File f) throws FileNotFoundException {
		file = f;
		lineNumber = 0;
		reader = new BufferedReader(
			new InputStreamReader(
				new FileInputStream(f)));
	}

	File getFile() { return file; }
	int getLineNumber() { return lineNumber; }

	public BasicException syntaxError() {
		return syntaxError("syntax error");
	}

	public BasicException syntaxError(String message) {
		return new BasicException(message, file, lineNumber);
	}

	public String[] readLine() throws IOException {
		String line = reader.readLine();
		if (line == null)
			return null;
		lineNumber++;

		ArrayList<String> tokens = new ArrayList<String>();
		int i = 0, len = line.length();
		while (i < len) {
			char c = line.charAt(i);
			if (Character.isSpaceChar(c)) {
				i++;
				continue;
			}

			if (c == '#')
				break;  // Treat the rest of the line as a comment: ignore it.

			int j = i + 1;
			if (Character.isJavaIdentifierPart(c)) {
				while (j < len && Character.isJavaIdentifierPart(line.charAt(j)))
					j++;
			}
			tokens.add(line.substring(i, j));
			i = j;
		}

		return tokens.toArray(new String[0]);
	}
}
