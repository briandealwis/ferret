package ca.ubc.cs.ferret.kenyon;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KenyonEclipseProjectMapper {
	protected Map<String,String> eclipseToKenyonProjectMapping = new HashMap<String, String>();
	
	public void associateEclipseToKenyon(String eclipseProjectName,
			String kenyonProjectName) {
		eclipseToKenyonProjectMapping.put(eclipseProjectName, kenyonProjectName);
	}
	
	public String rewriteJDTHandleIdentifier(String hi) {
		// handles are of form "=projectname/pkgroot<pkg[type..."
		if(hi.length() < 1 || hi.charAt(0) != '=') { return hi; }
		int sepIndex = hi.indexOf('/');
		if(sepIndex <= 0) { return hi; }
		String pname = hi.substring(1, sepIndex);
		if(!eclipseToKenyonProjectMapping.containsKey(pname)) {
			return hi;
		}
		return "=" + eclipseToKenyonProjectMapping.get(pname) 
			+ hi.substring(sepIndex);
	}

	
	public Collection<String> rewriteKenyonHandle(String id) {
		// handles are of the form "type:...."
		Set<String> rewrites = new HashSet<String>();
		int colonIndex = id.indexOf(':');
		if(colonIndex <= 0) { return rewrites; }
		if(id.charAt(colonIndex + 1) == '=') { colonIndex++; }
		int slashIndex = id.indexOf('/', colonIndex);
		if(slashIndex <= 0) { slashIndex = id.length(); }
		String kName = id.substring(colonIndex , slashIndex);
		if(kName.length() == 0) { return rewrites; }
		
		// Try the Kenyon <--> JDT project mapping
		for(String ep : eclipseToKenyonProjectMapping.keySet()) {
			String kp = eclipseToKenyonProjectMapping.get(ep);
			if(kp.equals(kName)) {
				rewrites.add(id.substring(0, colonIndex) + ep +
						id.substring(slashIndex));
			}
		}
		return rewrites;
	}

	public String rewriteObjectHandle(String hdl) {
		// handles are of form "desc:=projectname/pkgroot<pkg[type..."
		int pStartIndex = hdl.indexOf(':');
		pStartIndex = pStartIndex < 0 ? 0 : pStartIndex + 1;
		if(hdl.charAt(pStartIndex) == '=') { pStartIndex++; }
		int sepIndex = hdl.indexOf('/');
		if(sepIndex <= 0) { return hdl; }
		String pname = hdl.substring(pStartIndex, sepIndex);
		if(!eclipseToKenyonProjectMapping.containsKey(pname)) {
			return hdl;
		}
		return hdl.substring(0, pStartIndex)
			+ eclipseToKenyonProjectMapping.get(pname) 
			+ hdl.substring(sepIndex);
	}

}
