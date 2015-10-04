/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.ml.nn

import org.apache.flink.api.scala._
import org.apache.flink.ml.classification.Classification
import org.apache.flink.ml.math.DenseVector
import org.apache.flink.ml.metrics.distances.SquaredEuclideanDistanceMetric
import org.apache.flink.test.util.FlinkTestBase
import org.scalatest.{FlatSpec, Matchers}

// used only to setup and time the KNN algorithm

class KNNBenchmark_2Guass extends FlatSpec {

    val env = ExecutionEnvironment.getExecutionEnvironment

    //// Generate trainingSet
    val r = scala.util.Random
    val nFill = 20000

  val rSeq01 = Seq.fill(nFill)(DenseVector(r.nextGaussian, r.nextGaussian))
  val rSeq02 = Seq.fill(nFill)(DenseVector(r.nextGaussian + 5.0, r.nextGaussian + 2.0))

  val rSeq1 = rSeq01 ++ rSeq02

  /// GENERATE RANDOM SET OF POINTS IN [0,1]x[0,1]
  val trainingSet = env.fromCollection(rSeq1)

  var rSeqTest = Seq.fill(2*nFill)(DenseVector(r.nextGaussian, r.nextGaussian))
  val testingSet = env.fromCollection(rSeqTest)

///////////////////////////////////////////////////
  val rSeq12 = Seq.fill(nFill)(DenseVector(r.nextGaussian, r.nextGaussian))
  val rSeq22 = Seq.fill(nFill)(DenseVector(r.nextGaussian + 5.0, r.nextGaussian + 2.0))
  val rSeq2 = rSeq12 ++ rSeq22

  val trainingSet2 = env.fromCollection(rSeq2)

  var rSeqTest2 = Seq.fill(2*nFill)(DenseVector(r.nextGaussian, r.nextGaussian))
  val testingSet2 = env.fromCollection(rSeqTest2)


  ////////////////////////
  val rSeq13= Seq.fill(nFill)(DenseVector(r.nextGaussian, r.nextGaussian))
  val rSeq23 = Seq.fill(nFill)(DenseVector(0.5*r.nextGaussian + 50.0, 0.5*r.nextGaussian + 20.0))
  val rSeq3 = rSeq13 ++ rSeq23

  val trainingSet3 = env.fromCollection(rSeq3)

  var rSeqTest3 = Seq.fill(2*nFill)(DenseVector(1*r.nextGaussian, 1*r.nextGaussian))
  val testingSet3 = env.fromCollection(rSeqTest3)

/////////////////////////////////////////
 val rSeq14= Seq.fill(nFill)(DenseVector(r.nextGaussian, r.nextGaussian))
  val rSeq24 = Seq.fill(nFill)(DenseVector(0.5*r.nextGaussian + 500.0, 0.5*r.nextGaussian + 200.0))
  val rSeq4 = rSeq14 ++ rSeq24

  val trainingSet4 = env.fromCollection(rSeq4)

  var rSeqTest4 = Seq.fill(2*nFill)(DenseVector(2*r.nextGaussian, 2*r.nextGaussian))
  val testingSet4 = env.fromCollection(rSeqTest4)

  /////////////////////////////////////////1

  var t0 = System.nanoTime()
  val knn = KNN()
    .setK(3)
    .setBlocks(4)
    .setDistanceMetric(SquaredEuclideanDistanceMetric())

  knn.fit(trainingSet)
  val result = knn.predict(testingSet).collect()

  var tf = System.nanoTime()


  /////////////////////////////////////////2

  var t02 = System.nanoTime()
  val knn2 = KNN()
    .setK(3)
    .setBlocks(4)
    .setDistanceMetric(SquaredEuclideanDistanceMetric())

  knn2.fit(trainingSet2)
  val result2 = knn2.predict(testingSet2).collect()

  var tf2 = System.nanoTime()

  //////////////////////////////////////////3
  var t03 = System.nanoTime()
  val knn3 = KNN()
    .setK(3)
    .setBlocks(4)
    .setDistanceMetric(SquaredEuclideanDistanceMetric())

  knn3.fit(trainingSet3)
  val result3 = knn3.predict(testingSet3).collect()

  var tf3 = System.nanoTime()

  //////////////////////////////////////////4
  var t04 = System.nanoTime()
  val knn4 = KNN()
    .setK(3)
    .setBlocks(4)
    .setDistanceMetric(SquaredEuclideanDistanceMetric())

  knn4.fit(trainingSet4)
  val result4 = knn4.predict(testingSet4).collect()

  var tf4 = System.nanoTime()

  println("Elapsed time 1 =       : " + (tf - t0)/1000000000 + "s")
  println("Elapsed time 2 =       : " + (tf2 - t02)/1000000000 + "s")
  println("Elapsed time 3 =       : " + (tf3 - t03)/1000000000 + "s")
  println("Elapsed time 4 =       : " + (tf4 - t04)/1000000000 + "s")

  println("")
}
