/*
 * Copyright 2005 by X.
 * @author bsd
 */
package ca.ubc.cs.ferret.tests;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ca.ubc.cs.clustering.Cluster;
import ca.ubc.cs.clustering.Clustering;
import ca.ubc.cs.clustering.attrs.AttributeClusteringsFactory;
import ca.ubc.cs.clustering.attrs.ClusterableCollection;

public class CategorizationTest {
        
    public static Set<GraduateStudent> getPeople() {
        Set<GraduateStudent> people = new HashSet<GraduateStudent>();
        people.add(new GraduateStudent("Brian", "Gail", "PhD", "Canada"));
        people.add(new GraduateStudent("Chris", "Gregor", "PhD", "Canada"));
        people.add(new GraduateStudent("Brett", "Eric", "PhD", "USA"));
        people.add(new GraduateStudent("Jonathan", "Kris", "PhD", "Canada"));
        people.add(new GraduateStudent("Thomas", "Gail", "PhD", "Germany"));
        people.add(new GraduateStudent("Ed", "Kris", "MSc", "USA"));
        people.add(new GraduateStudent("Clint", "Eric", "MSc", "USA"));
        people.add(new GraduateStudent("Terry", "Gregor", "MSc", "Canada"));
        people.add(new GraduateStudent("Sara", "Eric", "MSc", "Iran"));
        people.add(new GraduateStudent("Maria", "Gregor", "MSc", "Canada"));
        people.add(new GraduateStudent("Jan", "Gail", "PhD", "Germany"));
        people.add(new GraduateStudent("John", "Gail", "PhD", "Canada"));
        people.add(new GraduateStudent("Sean", "Gail", "MSc", "Canada"));
        people.add(new GraduateStudent("Mik", "Gregor", "PhD", "Poland"));
        people.add(new GraduateStudent("Lyndon", "Gail", "MSc", "Canada"));
        return people;
    }

    @Test
    public void testSimpleCase() {
        AttributeClusteringsFactory<GraduateStudent> cat = new AttributeClusteringsFactory<GraduateStudent> ();
        cat.build(new ClusterableCollection<GraduateStudent>(getPeople()));
        for(Clustering<GraduateStudent> cl : cat.getAllClusterings()) {
        	if(cl.getName().equals("supervisor")) {
                assertEquals(4, cl.numberClusters());
                int count = 0;
                for(Cluster<GraduateStudent> cluster : cl.getClusters().values()) {
                	count += cluster.size();
                }
                assertEquals(getPeople().size(), count);
        	}
        }
    }
    
//    public void testSqueezer() {
//        ClusterableCollection<GraduateStudent> cc = new ClusterableCollection<GraduateStudent>();
//        cc.addAll(getPeople());
//        SqueezerClusterer<GraduateStudent> sc = new SqueezerClusterer<GraduateStudent>(cc, 2f);
//        for(List<GraduateStudent> group : sc.getClusters()) {
//            System.out.println("Cluster:");
//            for(GraduateStudent student : group) {
//                System.out.println("   " + student);
//            }
//        }
//        assertEquals(7, sc.getClusters().size());
//    }
}
