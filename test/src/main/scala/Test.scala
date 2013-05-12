package test

import skitch.core._
import skitch._
import skitch.vector.{vec2, vec}
import org.lwjgl.input.Keyboard
import scala.Some
import java.io.{FileInputStream, File}
import skitch.core.MouseDown
import skitch.core.KeyHold
import skitch.gfx
import scala.Some
import skitch.gl
import grizzled.slf4j.{Logger, Logging}
import skitch.gfx.Image

object Test extends SkitchApp { self =>

  val loader = new ResourceLoader(new File("/home/michael/code/skitch-engine/test/src/main/resources"))

  def initialize =  {
    loader.autoload()
    loader.watch()
  }

  def cleanup = ()

  val windowTitle = "Test test"
  val initialWindowSize = Some(600, 600)

  val startState = new TestState

  abstract class State extends StateBase {
    implicit val app = self
    val loader = self.loader
  }

  run()
}

class Image4(image:Resource[Image]) extends ResourceDependency with Logging {

	val resourceDependencies:Set[ResourceLike] = Set(image)

	def w = image.is.width
	def h = image.is.height

	def refresh(reso:ResourceLike) {
		info("refreshed %s due to %s" format (this, reso))
	}

	def render() {
		image.option.map({ im =>
			gl.matrix {
				for (t <- Seq(vec(0,0), vec(w, 0), vec(0, h), vec(-w, 0))) {
					gl.translate(t)
					im.render()
				}
			}
		})
	}
}

class TestState extends Test.State with LWJGLKeyboard with Logging {

	def onEnter = info("enter")
	def onExit  = info("exit")

	//  lazy val image = Image(common.getFile("img/snail3.png"))
	val image = loader.image("img/snail.png")

	val image4 = new Image4(image)

	//  val view = new View2D( Rect(100, 100, 200, 200) )
	val view = new View2D

	def update(dt:Float) {

	}

	def render() {
		debug("rendering")
		val (w, h) = view.bounds.dimensions
		def circles() = {
			gl.clear(Color(0x222222))
			gl.fill(false)
			Color.red.bind()
			for(i <- -w to w by 10) {
				if(i > 0) Color.blue.bind()
				gfx.circle(4, vec(i, 0))
			}
		}
		view.apply {
			circles()
			image.option.map( im => im.render() )
			gl.translate(vec(100, 100))
			image4.render()
		}
	}

	listen {
		case KeyHold(Keyboard.KEY_EQUALS) => view.camera.zoom += 0.1f
		case KeyHold(Keyboard.KEY_MINUS) => view.camera.zoom -= 0.1f

		case KeyHold(KEY_A) => view.camera.position.x -= 1f
		case KeyHold(KEY_D) => view.camera.position.x += 1f
		case KeyHold(KEY_S) => view.camera.position.y -= 1f
		case KeyHold(KEY_W) => view.camera.position.y += 1f
		case MouseDown(code, pos) => info("%s  %s" format (pos, view.toWorld(pos)))
	}

}
