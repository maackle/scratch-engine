package scalene.gfx

import java.io.{FileInputStream, File}
import scratch.core.{Resource, Tex, Render}


trait ImageLike extends SubTexture with Render{
  def clip:ClipRect
  def width:Int
  def height:Int
}

case class Image(val tex:Tex, cliprect:ClipRect) extends ImageLike {

  lazy val clip = if(cliprect==null) ClipRect(0,0,texWidth,texHeight) else cliprect
  val (width, height) = (clip.w, clip.h)
  def render() { blit() }
  override def toString = "Image(tex=%s, clip=%s)".format(tex, clip)
}

object Image {

  def apply(file:File, clip:ClipRect = null) = {
    new Image(Tex.load(file), clip)
  }

}

