package com.onedot.parser

import java.io.{File, RandomAccessFile}

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

object CSVParser {

  private final val logger: Logger = Logger(LoggerFactory.getLogger(getClass))

  private val NUMBER_OF_CHUNKS = 10

  def parse(filename: String): Future[LazyList[Vector[String]]] = {
    val url = getClass.getResource(filename)
    val file = Source.fromURL(url)
    val length = file.getLines().size
    val linesPerExecutor = length / NUMBER_OF_CHUNKS

    val ranges = Range(start = 0, end = length + 1, step = linesPerExecutor).iterator
      .sliding(2)
      .toVector
      .map(twoElementList => (twoElementList.head, twoElementList.last)) // max + 1 since it is exclusive

    val processedChunks = ranges.collect {
      case (start, end) =>
        Future {
          logger.info(s"Thread ${Thread.currentThread()} reading from $start to $end")
          Source.fromURL(url).getLines().to(LazyList)
            .drop(start)
            .take(end - start)
            .foldLeft(LazyList.empty[Vector[String]]) {
              (acc, line) => acc :+ line.split(",").toVector
            }
        }
    }

    // Create one single lazy list/stream out of the processed chunks
    Future.foldLeft(processedChunks)(LazyList.empty[Vector[String]])(_ ++ _)


  }

  def byteParsing(filename: String) = {
    val defaultBlockSize = 1 * 1024 * 1024

    val url = getClass.getResource(filename)
    val file = new File(url.getPath)
    val randomAccessFile = new RandomAccessFile(file, "r")
    try {
      (randomAccessFile.length / defaultBlockSize).toInt
    } finally {
      randomAccessFile.close
    }

    // Log dropped frames
    logger.info("Could not process the following line of input")
  }
}
