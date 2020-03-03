package com.onedot

import com.onedot.helper.Settings
import com.onedot.parser.CSVParser

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Sample execution of the [[CSVParser]]. Read a sample file and print results in the console. For more special cases
 * please see the tests folder.
 */
object ParserApp extends App {

  Await.result(CSVParser.parse("/sample.csv"), 1.minute).foreach {
    parsedLine =>
      println(parsedLine.mkString(Settings.fieldDelimiter))
  }
}
