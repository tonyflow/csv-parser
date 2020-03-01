package com.onedot

import com.onedot.parser.CSVParser
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.duration._

import scala.concurrent.Await

class CSVParserSpec extends AnyFlatSpec {

  "CSV Parser" should "be able to read bytes from the file" in {
    CSVParser.byteParsing("/test.csv")
  }

  it should "read lines from the file" in {
    Await.result(CSVParser.parse("/test.csv"), 1.minute).foreach(println(_))
  }

}
