# k-Nearest Neighbors (kNN) algorithm for Apache Flink

For my Insight Data Engineering project, I coordinated with the Flink community and worked on the k-nearest neighbors algorithm developed by Chiwan Park found here:

https://github.com/chiwanpark/flink/tree/FLINK-1745/flink-staging/flink-ml/src/main/scala/org/apache/flink/ml/nn

I incorporated a quadtree data structure to improve the efficiency of the kNN query.  I am currently in the progress of finalizing the data strucutre and algorithm to submit a pull request.

The brute-force approach to finding the k-nearest neighbors computes the pairwise distance for a given test point to all points in the training set

![](img/brute-force.png)

To reduce the number of distance computations needed for the kNN query, the training set is partioned using a quadtree data structure.  Once a box has more than some specified value `maxPerBox` of training set points, the box is partitioned into equal sub-boxes; and once each sub-box has more than `maxPerBox` training points, it is further partioned, as demnonstrated by the green sub-boxes in the diagram:

![](img/quadtree.png)

The intuitive idea of partitioning the training set into smaller sub-boxes is appealing, though there are some notable challenges in using the quadtree for the kNN query, namely some of the k-nearest neighbors may not be in the minimal bounding box of the gold star.  The following digram, for example, shows red points in the training set that are closest to the test point:

![](img/quadtree-challenge.png)

A clean efficient way to search both in a test point's minimal bounding box *and* surrounding area is needed.  Defining the "surounding area" of a test point is in fact the most delicate part.  The leaf-nodes of the quadtree are the only nodes containing a non-empty collection of objects in the training set, and the key to defining the "surrounding area" of a test point is to construct a min-heap on the leaf nodes to select boxes that are closest to the test point.  

In Scala, the min-heap is defined on tuples of doubles and nodes of the tree
```scala
  val nodeQueue = PriorityQueue[(Double, Node)]
```
The `Double` of the tuple is defined as a suitable notion of the distance from a test point to a node, namely a method in the Node class is 
``` scala
   minDist(obj: DenseVector)
```
where `DenseVector` is a Flink data type corresponding the the object type.  The defining property of `minDist` is that every point inside the rectangle has distance to `obj` greater than `minDist`.  

##Tests
Many tests and files used to bencmark the kNN algorithm can be found here:
https://github.com/danielblazevski/flink/tree/FLINK-1745-devel/flink-staging/flink-ml/src/test/scala/org/apache/flink/ml/nn

The benchmark files will be removed when the pull request is made to FLINK.

## Running the kNN algorithm; Building Flink
First Flink needs to be built from Source, as this is a development brach of the Flink repo.  See the README.md file in the root directory of this repo.  I personally found it easieist to add on top of Flink by using IntelliJ


## Link to presentation
Here is a link for a slideshare presentation about my project:
http://www.slideshare.net/danielblazevski/dan-blazevski-insightdemo
