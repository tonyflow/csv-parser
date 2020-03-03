package com.onedot.parser

import com.onedot.helper.Settings
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.matching.Regex

/**
 *
 */
object CSVParser {

  /**
   * Main logger of the parser
   */
  private final val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  /**
   * This is the number of chunks a large file will be broken into to be processed. In other words, this is the number
   * of [[Future]]s or threads that we will create in order to process the file.
   */
  private val NUMBER_OF_CHUNKS = 15

  /**
   * Match empty fields at end of a line
   */
  private val MISSING_CELLS_AT_THE_END: Regex = ",$".r

  /**
   * The main API of the parser. Receives the name of the file - atm the file should be located under the main or test
   * resources of the [[CSVParser]] project - and returns a stream of the results to be consumed by the client. The
   * stream is represented by a LayList which will only be computed when actually needed.
   *
   * @param filename Name of the file to be processed
   * @return Returns a [[LazyList]] of [[Vector]]s. The [[Vector]]s represent the tuples the CSV fields have been mapped
   *         to in order to be processed by the API.
   */
  def parse(filename: String): Future[LazyList[Vector[String]]] = {
    val url = getClass.getResource(filename)
    val file = Source.fromURL(url)
    val length = file.getLines().size


    val batchSize = length / NUMBER_OF_CHUNKS
    batchSize match {
      case 0 => //No need to create ranges since file is less than 15 lines long
        Future {
          Source.fromURL(url).getLines().foldLeft(LazyList.empty[Vector[String]]) {
            (acc, line) => processLine(acc, line)
          }
        }
      case linesPerExecutor =>
        // Break down file in chunks and process them in parallel. This can be a bit dangerous since there might be cases
        // when single cells including new lines could be processed by different threads.
        val ranges = Range(start = 0, end = length + 1, step = linesPerExecutor).iterator
          .sliding(2)
          .toVector
          // For the last batch, let the executor process the remaining lines: end = length
          .map(twoElementList => (twoElementList.head, if (length - twoElementList.last < batchSize) length else twoElementList.last)) // max + 1 since it is exclusive

        val processedChunks = ranges.collect {
          case (start, end) =>
            Future {
              logger.info(s"Thread ${Thread.currentThread()} reading from $start to $end")
              Source.fromURL(url).getLines().to(LazyList)
                .drop(start)
                .take(end - start)
                .foldLeft(LazyList.empty[Vector[String]]) {
                  (acc, line) =>
                    // We are appending on the lazy list so in order to merge a previous line that ended with \n and
                    // a new one that started with \n the process line needs both
                    processLine(acc, line)
                }
            }
        }

        // Create one single lazy list/stream out of the processed chunks
        Future.foldLeft(processedChunks)(LazyList.empty[Vector[String]])(_ ++ _)
    }
  }

  private def processLine(accumulator: LazyList[Vector[String]],
                          line: String): LazyList[Vector[String]] = {
    // Start with the naive splitting - split at every delimiter
    val naiveSplit = line.split(",").toVector

    // Identify line with missing cells
    val potentiallyMissingElements = MISSING_CELLS_AT_THE_END.findFirstIn(line) match {
      case None => naiveSplit
      case Some(_) =>
        // Add one last element at the end of the list
        // split will create empty string for all the other missing elements
        // ,a,b,,,,c, = Vector("","a","b","","","","c") :+ ""
        naiveSplit :+ ""
    }

    // Process field delimiters inside quoted text
    val firstIndexOfDoubleQuote = potentiallyMissingElements.indexWhere(_.contains('\"'))
    val lastIndexOfDoubleQuote = potentiallyMissingElements.lastIndexWhere(_.contains('\"'))
    val tokens = if (firstIndexOfDoubleQuote != -1 && lastIndexOfDoubleQuote != -1 && areQuotesBalanced(line)) {
      val start = potentiallyMissingElements.slice(0, firstIndexOfDoubleQuote)
      val tail = potentiallyMissingElements.slice(lastIndexOfDoubleQuote + 1, naiveSplit.size)
      val mergedCells = potentiallyMissingElements.slice(firstIndexOfDoubleQuote, lastIndexOfDoubleQuote + 1).mkString(Settings.fieldDelimiter)
      (start :+ mergedCells) ++ tail
    } else {
      potentiallyMissingElements
    }

    // Process new line characters embedded in quoted cell
    val previous = accumulator.lastOption
    if (!areQuotesBalanced(tokens.head) && previous.isDefined) {
      // Build split element
      val updatedLastElement = s"${previous.get.last} ${tokens.head.replace("\n", "")}"
      // Drop last element from the previous vector
      val updatedLastCSVLine = (previous.get.dropRight(1) :+ updatedLastElement) ++ tokens.tail
      accumulator.dropRight(1) :+ updatedLastCSVLine
    } else {
      accumulator :+ tokens
    }
  }

  /**
   * Make sure the number of quotes in the given string is balanced. This can only be the case if we're talking about
   * an even number of quotes
   *
   * @param token The string under examination
   * @return true is the number of quotes is even. false otherwise.
   */
  private def areQuotesBalanced(token: String) = token.count(_ == '\"') % 2 == 0
}
