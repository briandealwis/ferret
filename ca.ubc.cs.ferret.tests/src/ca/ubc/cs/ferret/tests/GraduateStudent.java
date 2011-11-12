/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.tests;


public class GraduateStudent {
    public String name;
    public String supervisor;
    public String degree;
    public String origin;
    
    public GraduateStudent(String name, String supervisor, String degree, String origin) {
        this.name = name;
        this.supervisor = supervisor;
        this.degree = degree;
        this.origin = origin;
    }
    
    public String toString() {
        return name + "(" + degree + "), " + supervisor + ", " + origin;
    }

    public int hashCode() {
        return name.hashCode();
    }
    
    public boolean equals(Object o) {
        if(!(o instanceof GraduateStudent)) { return false; }
        GraduateStudent other = (GraduateStudent)o;
        return name.equals(other.name) && supervisor.equals(other.supervisor)
            && degree.equals(other.degree) && origin.equals(other.origin);
    }
}
