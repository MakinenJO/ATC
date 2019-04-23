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

abstract class FlightListItem(val g: Game, val plane: Plane) extends BoxPanel(Orientation.Vertical) {
  this.peer.setPreferredSize(new Dimension(350, 70))
  this.peer.setMinimumSize(new Dimension(350, 70))
  this.peer.setMaximumSize(new Dimension(350, 70))
  //peer.setBounds(0, 0, 400, 60)
  border = new LineBorder(Color.DARK_GRAY)
  
  val planeInfo = new Label(plane.name)
  val descriptionLabel = new Label(plane.stateDescription) {
    this.peer.setPreferredSize(new Dimension(80, 30))
    this.peer.setMinimumSize(new Dimension(80, 30))
    this.peer.setMaximumSize(new Dimension(80, 30))
  }
  
  def updateContents(): Unit
  
  val runwayOptions = new PopupMenu {
    contents += new MenuItem("Select runway...")
    
    
    def addMenuItem(exit: Exit, runway: Runway, msg: String, f: (Exit, Runway) => Unit) = {
      val item = new MenuItem(Action(exit.name)(f(exit, runway))) {
        listenTo(mouse.clicks)
        listenTo(mouse.moves)
        
        reactions += {
          case event.MousePressed(_,_,_,_,_) => {
            textView.displayMessage(msg + " on runway via exit " + exit.name)
            exit.selected = false
            plane.selected = false
          }
          case event.MouseEntered(_,_,_) => {
            plane.selected = true
            exit.selected = true
          }
          case event.MouseExited(_,_,_) => {
            plane.selected = false
            exit.selected = false
          }
        }
      }
      contents += item
    }
  }
  
  
  val textView = new Label() {
    this.peer.setPreferredSize(new Dimension(250, 30))
    this.peer.setMinimumSize(new Dimension(250, 30))
    this.peer.setMaximumSize(new Dimension(250, 30))
    var message = ""
    var textPos = 250
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
    
    def displayMessage(msg: String) = {
      message = msg
      textPos = 300
      timer.start()
    }
    
    }
  
  
  listenTo(mouse.moves)
  
  reactions += {
    
    case event.MouseEntered(_,_,_) => {
      plane.selected = true
    }
    
    case event.MouseExited(_,_,_) => {
      plane.selected = false
    }
  }
  
  val bottomRow = new FlowPanel() {
    contents += descriptionLabel
    contents += textView
    println(contents.size)
  }
}


object FlightListItem {
  def arrival(g:Game, p: Plane) = new ArrivalItem(g, p)
  def departure(g:Game, p: Plane) = new DepartureItem(g, p)
}



class ArrivalItem(g:Game, plane: Plane) extends FlightListItem(g, plane) {
  
  textView.message = "Flight " + plane.name + " approaching airfield"
  
  val descendButton = new Button("Dsc") {
    reactions += {
      case event.ButtonClicked(b: Button) => {
        plane.descend()
      }
    }
  }
  
  g.runways.foreach(runway => {
    runwayOptions.addMenuItem(runway.exit1, runway, "Landing", plane.land)
    runwayOptions.addMenuItem(runway.exit2, runway, "Landing", plane.land)
  })
  
  val landButton = new Button("Lnd") {
    reactions += {
      case event.ButtonClicked(b: Button) => {
        runwayOptions.show(b, 0, 0)
      }
    }
  }
  

  val gateOptions = new PopupMenu {
    contents += new MenuItem("Select gate...")
    g.gates.foreach(gate => {
      addMenuItem(gate)
    })
    
    def addMenuItem(gate: Gate) = {
      val item = new MenuItem(Action(gate.name)(plane.assignGate(gate))) {
        listenTo(mouse.clicks)
        listenTo(mouse.moves)
        
        reactions += {
          case event.MousePressed(_,_,_,_,_) => {
            textView.displayMessage("Moving to gate " + gate.name)
            gate.selected = false
            plane.selected = false
          }
          case event.MouseEntered(_,_,_) => {
            plane.selected = true
            gate.selected = true
          }
          case event.MouseExited(_,_,_) => {
            plane.selected = false
            gate.selected = false
          }
        }
      }
      contents += item
    }
  }
  
  
  val gateButton = new Button("Gat") {
    reactions += {
      case event.ButtonClicked(b: Button) => {
        gateOptions.show(b, 0, 0)
      }
    }
  }

  val topRow = new FlowPanel() {
    contents += planeInfo
    contents += descendButton
    contents += landButton
    contents += gateButton
  }
  
  val mainLayout = new BoxPanel(Orientation.Vertical) {
    contents += topRow
    contents += bottomRow
  }
  
  contents += mainLayout
  listenTo(descendButton.mouse.moves)
  listenTo(landButton.mouse.moves)
  listenTo(gateButton.mouse.moves)
  
  override def updateContents() = {
    descriptionLabel.text = plane.stateDescription
    if(plane.readyToLand) {
      landButton.enabled = true
      descendButton.enabled = false
      gateButton.enabled = false
    }
    else if(plane.hasLanded) {
      landButton.enabled = false
      descendButton.enabled = false
      gateButton.enabled = true
    }
    else if(plane.readyToDescend) {
      landButton.enabled = true
      descendButton.enabled = true
      gateButton.enabled = false
    }
    else {
      landButton.enabled = false
      descendButton.enabled = false
      gateButton.enabled = false
    }
    revalidate()
    repaint()
  }
}




class DepartureItem(g: Game, plane: Plane) extends FlightListItem(g, plane) {
  textView.message = "Flight " + plane.name + " now boarding"
  
  g.runways.foreach(runway => {
    runwayOptions.addMenuItem(runway.exit1, runway, "Taking off", plane.takeoff)
    runwayOptions.addMenuItem(runway.exit2, runway, "Taking off", plane.takeoff)
  })
  
  val takeoffButton = new Button("Takeoff") {
    reactions += {
      case event.ButtonClicked(b: Button) => {
        runwayOptions.show(b, 0, 0)
      }
    }
  }
  
  val topRow = new FlowPanel() {
    contents += planeInfo
    contents += takeoffButton
  }
  
  val mainLayout = new BoxPanel(Orientation.Vertical) {
    contents += topRow
    contents += bottomRow
  }
  
  contents += mainLayout
  
  listenTo(takeoffButton.mouse.moves)
  
  override def updateContents() = {
    descriptionLabel.text = plane.stateDescription
    if(plane.readyToDepart) takeoffButton.enabled = true
    else takeoffButton.enabled = false
  }
}