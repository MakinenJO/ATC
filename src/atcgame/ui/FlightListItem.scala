package atcgame.ui

import atcgame._

import scala.swing._
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.Timer
import javax.swing.BoxLayout
import javax.swing.border.LineBorder
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class FlightListItem(val g: Game, val plane: Plane) extends BoxPanel(Orientation.Horizontal) {
  this.peer.setPreferredSize(new Dimension(300, 60))
  this.peer.setMinimumSize(new Dimension(300, 60))
  this.peer.setMaximumSize(new Dimension(300, 60))
  //peer.setBounds(0, 0, 400, 60)
  border = new LineBorder(Color.DARK_GRAY)
  
  val planeInfo = new Label(plane.name)
  
  val descendButton = new Button("Dsc") {
    reactions += {
      case event.ButtonClicked(b: Button) => {
        plane.descend()
      }
    }
  }
  
  val landButton = new Button("Lnd") {
    reactions += {
      case event.ButtonClicked(b: Button) => {
        plane.land((250, 250), g.runways(0))
      }
    }
  }
  
  
  val topRow = new FlowPanel() {
    contents += planeInfo
    contents += descendButton
    contents += landButton
  }
  
  
  val textView = new Label() {
    this.peer.setPreferredSize(new Dimension(300, 30))
    this.peer.setMinimumSize(new Dimension(300, 30))
    this.peer.setMaximumSize(new Dimension(300, 30))
    var message = "Good day and welcome to Air Traffic Control simulator!!!"
    var textPos = 300
    override def paintComponent(g: Graphics2D) = {
      g.setColor(Color.BLACK)
      g.fillRect(0,0,300, 50)
      g.setColor(Color.YELLOW)
      g.drawString(message, textPos, 20)
      
      if(textPos < -g.getFontMetrics.stringWidth(message)) {timer.stop()}
    }
    
    //maybe move updating together with gameUI drawing
    //might be smarter to keep it here, think about stopping
    val listener = new ActionListener() {
      def actionPerformed(e: java.awt.event.ActionEvent) = {
        textPos -= 1
        repaint()
      }
    }
  
    val timer = new Timer(10, listener)
    timer.start()
    
    }
  
  
  
  val mainLayout = new BoxPanel(Orientation.Vertical) {
    contents += topRow
    contents += textView
  }
  
  contents += mainLayout
  //listenTo(button)
  listenTo(descendButton.mouse.moves)
  listenTo(landButton.mouse.moves)
  listenTo(mouse.moves)
  
  reactions += {
    
    case event.MouseEntered(_,_,_) => {
      plane.selected = true
    }
    
    case event.MouseExited(_,_,_) => {
      plane.selected = false
    }
  }
  
  
}