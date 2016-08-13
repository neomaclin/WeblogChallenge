name := "WebBlogChallenge"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq("org.apache.spark" %% "spark-core" % "2.0.0",
                            "com.github.nscala-time" %% "nscala-time" % "2.12.0",
                            "org.specs2" %% "specs2-core" % "3.8.4" % "test")