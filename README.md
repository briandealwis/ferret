# Ferret: A Software Exploration Tool

![A little ferret](site/src/main/resources/XenoFerret.jpg)

Ferret is a software exploration tool.  Boild down to its essence,
Ferrit is Eclipse Search on steroids.

![Ferret in action](site/src/main/resources/doc/ferret-demo.png)

Ferret is an Eclipse view that provides a structured display of
information describing how one or more program elements are situated
in the context of the system.

This context is computed automatically as the program elements are
selected in the IDE, and is structured as answers to a set of
_conceptual queries_ about those elements.  These conceptual
queries are intended to closely match the types of questions
developers actually want to answer.

Ferret's conceptual queries include and build-on the standard
queries supported by Eclipse. 
These conceptual queries include queries such as:

  - Where is this method called?
  - What methods return objects of this type?
  - Where are objects of this type instantiated?
  - What interfaces define (or specify) this method?
  - What implementations are there of this interface method?
  - What are the other alternative implementations of this method in
    the hierarchy?

More notable is that Ferret's conceptual
queries can make use of different sources of program information,
such as the static relations embedded in the source code, the
dynamic runtime information recorded in a TPTP trace, and
plugin details as defined in `plugin.xml` and `MANIFEST.MF`.
Thus dynamic runtime information can be used to replace
some of the statically-derived relations, transforming the questions
above to "Where are objects of this type _actually_ instantiated?"
or "Where is this method _actually_ called?"

The conceptual queries are categorized by whether they involve
_declarations_, are about _inter-class_ or _intra-class_
relations, or are _hierarchical_ in nature.

The results of queries may be clustered by their different
attributes, as selected by the programmer.  For example: the references
to a method, which are generally themselves methods, may be clustered
into different groups identified by attributes such as their access
protection (public, private, package, or default), their containing
type, their package, or boolean attributes such as static vs
non-static.

The Ferret documentation ships with the plugin, and is also available 
[online](http://manumitting.com/tools/eclipse/ferret/stable/doc/) too.

For more background on Ferret, see the _Publications_
below.

## Notes

Kenyon was a research project to index Subversion repositories.  It died
a long time ago and the Kenyon binding has long been disabled.  It's
entirely possible that we could rewrite some of its support for JGit.

The Eclipse Testing and Profiling tools project (TPTP) was archived in
May 2016 and the TPTP binding has long been disabled.

## Installation

Ferret is an Eclipse plug-in and requires Eclipse 4.6 (Neon) or
later.  To install, use the following update site:

    http://manumitting.com/tools/eclipse/ferret/stable

To live on the bleeding edge, with the latest commits to `master`,
use the following update site:

    http://manumitting.com/tools/eclipse/ferret/LATEST

## License

Ferret is made available under the <a
href="https://www.eclipse.org/legal/epl-v10.html">Eclipse Public License
v1.0</a>.

## Publications

B de Alwis, GC Murphy (2008).  [Answering Conceptual Queries with
Ferret](https://scholar.google.com/citations?view_op=view_citation&hl=en&user=Kk_J-4MAAAAJ&citation_for_view=Kk_J-4MAAAAJ:u-x6o8ySG0sC). 
In the Proceedings of the International Conference on Software Engineering
(ICSE). Leipzig, Germany.
[doi:10.1145/1368088.1368092](http://dx.doi.org/10.1145/1368088.1368092)

B de Alwis, GC Murphy (2006).  [Using Visual Momentum to Explain
Disorientation in the Eclipse
IDE](https://scholar.google.com/citations?view_op=view_citation&hl=en&user=Kk_J-4MAAAAJ&citation_for_view=Kk_J-4MAAAAJ:9yKSN-GCB0IC).
In the Proceedings of the International Conference
on Visual Languages and Human-Centric Computing. 
[doi:10.1109/VLHCC.2006.49](http://dx.doi.org/10.1109/VLHCC.2006.49)
