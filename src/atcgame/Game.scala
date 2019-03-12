package atcgame

import scala.swing._

import scala.collection.mutable.Buffer


class Game {
  val planes = Buffer[Plane]()
  
  planes += new Plane(10, 10)
  planes += new Plane(50, 50)
  
  def step() = {
    planes.foreach(_.move())
  }
  
}