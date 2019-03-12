//package atcgame
//
//import scala.swing._
//import java.awt.Color
//import javax.swing.JInternalFrame
//import javax.swing.JDesktopPane
//import javax.swing.JFrame
//import java.awt.Window.Type
//import javax.swing.WindowConstants
//import javax.swing.UIManager
//import javax.imageio.ImageIO
//import java.io.File
//import java.awt.geom.AffineTransform
//import java.awt.image.AffineTransformOp
//import atcgame.ui.AirFieldView
//
//class TestUI extends SimpleSwingApplication {
//  
//  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)
//
//	def top = new MainFrame {
//		//visible = false
//		title = "Air Traffic Contol"
//		iconify()
//		//dispose()
//	}
//	
//  val window1 = new Frame {
//	  this.peer.setType(Type.UTILITY) //disables window showing on taskbar
//	  this.peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
//    title = "Arrivals"
//    size = new Dimension(400, 500)
//    visible = true
//    
//    override def closeOperation() = {
//	    end()
//	  }
//  }
//  
//  
//  val window2 = new Frame {
//    this.peer.setType(Type.UTILITY)
//    this.peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
//    //this.peer.setLocation(0, 450)
//    this.peer.setLocation(0, window1.peer.getBounds.getHeight.toInt)
//    title = "Departures"
//    size = new Dimension(400, 500)
//    visible = true
//    override def closeOperation() = {
//	    end()
//	  }
//  }
//  
//  
//  
//  val window3 = new Frame {
//    this.peer.setTitle("Airport")
//    
//    size = new Dimension(900, 900)
//    preferredSize = size
//    
//    this.peer.setLocation(400, 50)
//    
//    visible = true
//    
//    val fieldView = new Component {
//      val planeImage = ImageIO.read(new File("img/plane2.png"))
//      override def paintComponent(g: Graphics2D) = {
//        val at = new AffineTransform()
//        at.rotate(2, planeImage.getWidth/2, planeImage.getHeight/2)
//        val op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR)
//        g.drawImage(planeImage, 50, 50, null)
//        val image = op.filter(planeImage, null)
//        g.drawImage(image, 200, 200, null)
//        at.rotate(2, planeImage.getWidth / 2, planeImage.getHeight / 2)
//        g.drawImage(planeImage, op, 300, 300)
//      }
//    }
//    
//    contents = fieldView
//    
//    this.peer.getContentPane().setBackground(Color.green)
//    
//    
//    override def closeOperation() = {
//      end()
//    }
//  }
//  
//  
//  
//  val window4 = new Frame {
//    this.peer.setTitle("Game Info")
//    
//    size = new Dimension(400, 200)
//    
//    this.peer.setLocation(800, 200)
//    
//    visible = true
//  }
//  
//  val window5 = new AirFieldView(this, "Testi", 300, 300, 300, 300)
//  
//  
//  
//  def end() = {
//    val result = Dialog.showConfirmation(null, "Do you want to quit?", "Quit", Dialog.Options.YesNo)
//    if(result == Dialog.Result.Ok) sys.exit()
//  }
//
//}
//
//
////object Main extends GameUI {
////  
////}