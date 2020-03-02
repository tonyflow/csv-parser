package com.onedot

import com.onedot.parser.CSVParser
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.duration._

import scala.concurrent.Await

class CSVParserSpec extends AnyFlatSpec {

  "CSV Parser"  should "read lines from the file" in {
    Await.result(CSVParser.parse("/test.csv"), 1.minute).foreach(println(_))
  }

  it should "be able to handle new line characters embedded in a quoted cell" in {
    Await.result(CSVParser.parse("/new-line-in-quoted-cell.csv"), 1.minute).foreach(println(_))
  }

  it should "be able to process field delimiters in quoted cells" in {
    Await.result(CSVParser.parse("/field-delimiters-in-quoted-cells.csv"), 1.minute).foreach(println(_))
  }

  /**
   * The convention here is that an absent element should be treated like an empty string when parsed
   */
  it should "be able to process absent elements" in {
    Await.result(CSVParser.parse("/absent-element-at-the-end-of-the-line.csv"), 1.minute).foreach(println(_))
  }

}
