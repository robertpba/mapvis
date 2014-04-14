package mapvis.preprocessing.wiki

import mapvis.utility.CSVData
import scalax.collection.GraphEdge.DiEdge
import scalax.collection.Graph
import scalax.collection.GraphPredef._

class GraphTest {
  def main(args: Array[String]) {

    //var g = Graph[String, DiEdge]();
    var edges = List[DiEdge[String]]()
    var nodes = Set[String]()

    var csv = CSVData.open("category.txt", separator = '\t', skipFirst = true)
    for (values <- csv){
      edges = (values(0) ~> values(3)) :: edges
      nodes = nodes + values(0)
      nodes = nodes + values(3)
      //println (values.length)
      //print (values)
      //for (value <- values){
      //  print(value)
      //  print(" ")
      //}
      //println
    }
    println (nodes)


    var g = Graph.from(nodes, edges)


    print(g.findCycle)

  }
}
