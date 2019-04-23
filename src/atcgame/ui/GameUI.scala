package atcgame.ui

import atcgame._

import scala.swing._
import java.awt.Color
import javax.swing.JInternalFrame
import javax.swing.JDesktopPane
import javax.swing.JFrame
import java.awt.Window.Type
import javax.swing.WindowConstants
import javax.swing.UIManager
import javax.imageio.ImageIO
import java.io.File
import java.awt.event.ActionListener
import javax.swing.Timer

class GameUI extends SimpleSwingApplication {
  
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)

	var game = new Game(this)
	
  val arrivalsView = new FlightListView(this, "Arrivals", 400, 500, 10, 10)
  val departuresView = new FlightListView(this, "Departures", 400, 500, 10, 510)
  
  val airfieldView = new AirFieldView(this, "Airport", 1100, 1000, 400, 10)
  
  val gameInfoView = new GameInfoView(this, "Air Traffic Control", 400, 400, 1500, 200) {
    
    val windows = Vector(arrivalsView, departuresView, airfieldView)
		
		reactions += {
      case w: event.WindowDeiconified => {
        windows.foreach(_.peer.setState(java.awt.Frame.NORMAL))
      }
      case v: event.WindowIconified => {
        //TODO: pause game
        windows.foreach(_.peer.setState(java.awt.Frame.ICONIFIED))
      }
      case x: event.WindowActivated => {
        windows.foreach(_.peer.toFront())
        println("activated!")
      }
    }
  }
  
  game.start()
  
  def top = gameInfoView
  
  val listener = new ActionListener() {
    def actionPerformed(e: java.awt.event.ActionEvent) = {
      game.step()
    }
  }
  
  val timer = new Timer(4, listener)
  timer.start()
  
  //listenTo(arrivalsView.button)
  listenTo(gameInfoView.button)

  reactions += {
    case event.ButtonClicked(b: Button) => {
      b match {
        //case arrivalsView.button => game.planes.foreach(_.descend())
        case gameInfoView.button => restart()
      }
    }
  }
  
  def updateViews = {
    
  }
  
  def addArrivingPlane(p: Plane) = {
    arrivalsView.addPlane(p)
  }
  
  def end() = {
    val result = Dialog.showConfirmation(null, "Do you want to quit?", "Quit", Dialog.Options.YesNo)
    if(result == Dialog.Result.Ok) sys.exit()
  }
  
  def restart() = {
    timer.stop()
    airfieldView.timer.stop()
    game = new Game(this)
    timer.start()
    airfieldView.timer.start()
  }

}