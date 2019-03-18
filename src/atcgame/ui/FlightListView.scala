package atcgame.ui

import java.awt.Window.Type
import java.awt.GridLayout
import scala.swing._
import javax.swing.BoxLayout
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.Timer


class FlightListView
(parent: GameUI,
    title: String,
    width: Int, height: Int,
    offsetX: Int, offsetY: Int) 
    
extends ATCWindow(parent, title, width, height, offsetX, offsetY, Type.UTILITY) {
  val layout = new BoxPanel(Orientation.Vertical)
  val button = new Button("Descend")
  layout.contents += button
  
  
  
  val textView = new Label() {
    this.peer.setPreferredSize(new Dimension(300, 30))
    this.peer.setMinimumSize(new Dimension(300, 30))
    this.peer.setMaximumSize(new Dimension(300, 30))
    var textPos = 300
    override def paintComponent(g: Graphics2D) = {
      g.setColor(Color.BLACK)
      g.fillRect(0,0,300, 50)
      g.setColor(Color.YELLOW)
      g.drawString("Well hello there!", textPos, 20)
      
      if(textPos < -300) {timer.stop(); println("Timer stopped")}
    }
    
    val listener = new ActionListener() {
      def actionPerformed(e: java.awt.event.ActionEvent) = {
        textPos -= 1
        repaint()
      }
    }
  
    val timer = new Timer(10, listener)
    timer.start()
    
    }
  
  
  layout.contents += textView
  
  
  contents = layout
}