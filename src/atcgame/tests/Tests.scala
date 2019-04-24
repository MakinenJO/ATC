package atcgame.tests

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatest.Assertions._

import atcgame._

@RunWith(classOf[JUnitRunner])
class ATCTest extends FlatSpec with Matchers {
  
  "Game checkCollisions" should "properly detect collisions" in {
    
    val g = new Game()
    
    val plane1 = new Plane(g, "")
    val plane2 = new Plane(g, "")
    val plane3 = new Plane(g, "")
    
    plane1.x = 10; plane1.y = 10
    plane2.x = 100; plane2.y = 100
    plane3.x = 200; plane3.y = 200
    
    g.planes ++= Vector(plane1, plane2, plane3)
    
    val collision = g.checkCollisions()
    assert(collision == false, "collisions reported although no planes overlap")
    
    plane2.x = 50; plane2.y = 50
    plane3.x = 80; plane3.y = 80
    
    val collision2 = g.checkCollisions()
    assert(collision2, "collision not detected although planes overlap")
    
  }
}