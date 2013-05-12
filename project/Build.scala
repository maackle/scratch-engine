import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName         = "skitch-dev"
  val appVersion      = "0.1a"

  val appDependencies = Seq(

  )

  val hello = TaskKey[Unit]("hello", "Prints 'Hello World'")

  val helloTask = hello := {
    println("oh hello!!!")
  }

  val skitch = Project("skitch", file("skitch"), settings = Project.defaultSettings ++ Seq(
    //    scalaSource in (Compile) <<= baseDirectory / "src"
  ))

  val test = Project("test", file("test"), settings = Project.defaultSettings ++ Seq(
    //    scalaSource in (Compile) <<= baseDirectory / "src"
  )) dependsOn(skitch) aggregate(skitch)


}
