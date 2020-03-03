package com.onedot

import com.onedot.parser.CSVParser
import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent.duration._

import scala.concurrent.Await

class CSVParserSpec extends AnyFlatSpec {

  "CSV Parser" should "read lines from the file" in {
    val result = Await.result(CSVParser.parse("/test.csv"), 1.minute)
    assert(result.size == 101) // Including the header
  }

  it should "be able to handle new line characters embedded in a quoted cell" in {
    val result = Await.result(CSVParser.parse("/new-line-in-quoted-cell.csv"), 1.minute)
    assert(result(0) == Vector("a", "\"a split cell\"", "b", "\"something else\""))
  }

  it should "be able to process field delimiters in quoted cells" in {
    val result = Await.result(CSVParser.parse("/field-delimiters-in-quoted-cells.csv"), 1.minute)
    assert(result(0) == Vector("a", "\"b,c,d\"", "e"))
  }

  /**
   * The convention here is that an absent element should be treated like an empty string when parsed
   */
  it should "be able to process absent elements" in {
    val result = Await.result(CSVParser.parse("/absent-element-at-the-end-of-the-line.csv"), 1.minute)
    assert(result(0) == Vector("", "a", "b", "", "", "", "c", ""))
  }

  it should "be able to parse a field which consists of a quoted cell" in {
    val result = Await.result(CSVParser.parse("/part-of-the-field-appears-as-quoted.csv"), 1.minute)
    assert(result(0) == Vector("\"abc,\"onetwo", "three", "doremi"))
    result(0).foreach(println(_))
    println(result(0).size)
  }

  it should "be able to parse the following combination: new line as quoted part of a cell" in {

  }

  it should "be able to process absent elements and new lines in quoted cells" in {

  }

  it should "be able to process large files" in {

  }

}
