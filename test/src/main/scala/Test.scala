package test

import scalene.gfx.{Image, Sprite}
import scratch.core._
import scratch._
import scratch.vector.{vec2, vec}
import org.lwjgl.input.Keyboard
import scala.Some
import java.io.{FileInputStream, File}
import scratch.core.MouseDown
import scratch.core.KeyHold
import scratch.gfx
import scala.Some
import scratch.gl
import grizzled.slf4j.{Logger, Logging}

object Test extends ScratchApp {

  def initialize =  {
    Resource.autoload()
    Resource.developmentMode("/home/michael/code/scratch-engine/test/src/main/resources")
  }

  def cleanup = ()

  val windowTitle = "Test test"
  val initialWindowSize = Some(600, 600)

  val startState = new TestState


  run()
}

class TestState extends ScratchState(Test) with LWJGLKeyboard with Logging {

  def onEnter = info("enter")
  def onExit  = info("exit")

//  lazy val image = Image(common.getFile("img/snail3.png"))
  val image = Resource("img/snail3.png")(Image.apply(_, null))

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
    }
//    view2.apply {
//      circles()
//    }
  }

  listen {
    case KeyHold(Keyboard.KEY_EQUALS) => view.camera.zoom += 0.1f
    case KeyHold(Keyboard.KEY_MINUS) => view.camera.zoom -= 0.1f

    case KeyHold(KEY_A) => view.camera.position.x -= 1f
    case KeyHold(KEY_D) => view.camera.position.x += 1f
    case KeyHold(KEY_S) => view.camera.position.y -= 1f
    case KeyHold(KEY_W) => view.camera.position.y += 1f
    case MouseDown(code, pos) => info(pos)
  }

}