package atcgame

import java.awt.Graphics2D
import javax.imageio.ImageIO
import java.io.File
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.Color
import java.awt.Font

class Runway(val exit1: Exit, val exit2: Exit) {
  
  val rotation = {
    val x = math.abs(exit1.x - exit2.x)
    val y = exit2.y - exit1.y
    math.atan(y.toDouble / x) + math.Pi / 2
  }
  
  val length =  {
    math.sqrt(math.pow((exit1.x - exit2.x), 2) + math.pow((exit1.y - exit2.y), 2))
  }
  
  def landingAngle(exit: Exit) = {
    if (exit == exit1) (rotation + math.Pi / 2) % (math.Pi * 2)
    else (rotation + math.Pi * 3 / 2) % (math.Pi * 2)
  }

  
  def draw(g: Graphics2D) {
    val at = new AffineTransform()
    at.translate(-Runway.dX, -Runway.dY)
    at.rotate(rotation, Runway.dX, Runway.dY)
    val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
    var x = exit1.x
    var y = exit1.y
    for(i <- 0 to length.toInt / 32 + 1) {
    	g.drawImage(Runway.runwayImage, op, x, y)
    	x += (32 * math.sin(rotation)).toInt
    	y -= (32 * math.cos(rotation)).toInt
    }
    
    val normalFont = g.getFont()
    
    if(exit1.selected) {g.setColor(Color.YELLOW); g.setFont(Runway.highlightFont) }
    else {g.setColor(Color.WHITE); g.setFont(normalFont)}
    g.drawString(exit1.name, exit1.x - 10, exit1.y)
    
    if(exit2.selected) {g.setColor(Color.YELLOW); g.setFont(Runway.highlightFont)}
    else {g.setColor(Color.WHITE); g.setFont(normalFont)}
    g.drawString(exit2.name, exit2.x - 10, exit2.y)
    
    g.setColor(Color.BLACK)
    g.setFont(normalFont)
  }
}

case class Exit(x: Int, y: Int, name: String, var selected: Boolean = false)

object Runway {
  private val runwayImage = ImageIO.read(new File("img/runway.png"))
  private val dX = runwayImage.getWidth / 2
  private val dY = runwayImage.getHeight / 2
  private val highlightFont = new Font("default", Font.BOLD, 18)
}