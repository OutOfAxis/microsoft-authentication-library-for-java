Building a project from source is only possible if the referenced project is a sbt project. sbt doesn't know of all the different build systems out there, so how is it supposed to know how to build a non sbt project?

It is possible to add support for other build systems through a sbt plugin but this may be a lot of work.

Your referenced project is a simple Maven project, which means that you can easily create a sbt project from it. Just fork the repo and create a build.sbt with the following content:

scalaVersion := "2.13.0"

projectDependencies += "junit" % "junit" % "3.8.1" % "test"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
