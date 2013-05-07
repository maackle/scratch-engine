package scratch

import math._
import org.lwjgl.Sys

package object common {

  type Real = Float
  type Radian = Double

  def clamp(amt:Float)(lo:Float, hi:Float) = max(lo, min(hi, amt))

  object Op {
    def apply(fn: =>Any) = new Op(()=>{fn})
    lazy val NOOP = Op(())
  }

  class Op(val fn:()=>Any) {
    def apply() = fn()
  }

  val NOOP = () => {}
  def NOOP1[T] = (t:T) => {}
  def NOOP2[T, U] = (t:T, u:U) => {}

  private var __onetimer = collection.mutable.Set[Op]()

  private def _milliseconds = ((Sys.getTime * 1000) / Sys.getTimerResolution)

  private lazy val ms0 = System.currentTimeMillis()

  def milliseconds: Long = {
    (System.currentTimeMillis() - ms0)
  }

  def seconds: Float = milliseconds / 1000f

  object implicits {
    implicit def block2Fn(bloc: =>Any) = ()=>{bloc}
    implicit def fn2val[A](fn: ()=>A):A = fn()
    implicit def float2int(f:Float) = f.toInt
  }

  def once(bloc: =>Unit) = {
    val op = Op(bloc)
    if(! __onetimer.contains(op)) {
      op()
      __onetimer += op
    }
  }
}
