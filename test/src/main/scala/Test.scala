package test

import scratch.core._
import scratch.{LWJGLKeyboard, gfx, Color, gl}
import scratch.vector.{vec2, vec}
import org.lwjgl.input.Keyboard
import scala.Some

object Test extends ScratchApp {

  def initialize = ()
  def cleanup = ()

  val windowTitle = "Test test"
  val initialWindowSize = Some(600, 600)

  val startState = new TestState

  run()
}

class TestState extends ScratchState(Test) with LWJGLKeyboard {

  def onEnter = println("enter")
  def onExit  = println("exit")

//  val view = new View2D(Test)
  val view = new View2D(Rect(100, 200, 600, 600))(Test)
//  val view2 = new View2D(Some(Rect(100, 100, 200, 400)))(Test)

  def update(dt:Float) {

  }

  def render() {
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
    case MouseDown(code, pos) => println(pos)
  }

}