package edu.uci.ics.screencap;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
/**
 * This abstract class represents a single dump to a file. It is
 * left deliberately as a class because I want to leave room for 
 * future functionality:
 * <ul>
 * <li/>popping up a panel to select a filename for dumps
 * <li/>rotating dump names
 * <li/>possibly, animation dumps?
 * </ul>
 * 
 * At the very least, all that a dump needs, though, is a file
 * to dump to and a component to get the dump from. The dump calls
 * print() or paint() on the component, and therefore:
 * <ul>
 * <li/>The component must not mess around with the system state during
 * a dump. For example, triggering an animation cycle. (This will just
 * disappoint all parties.)
 * <li/>The component must not count on any particular state on the
 * Graphics context. (It will be disappointed).
 * </ul>
 * 
 * @author danyelf
 */
abstract public class Dump {
	public Dump() {
	}
	
	abstract public void dumpComponent(File filename, Component component)
			throws IOException;
}
