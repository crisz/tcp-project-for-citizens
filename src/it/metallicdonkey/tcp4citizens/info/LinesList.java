package it.metallicdonkey.tcp4citizens.info;

import java.util.LinkedList;

import it.metallicdonkey.tcp.models.Line;

public class LinesList {
	private static LinkedList<Line> lines;
	public static boolean addLine(Line l) {
		return lines.add(l);
	}
}
