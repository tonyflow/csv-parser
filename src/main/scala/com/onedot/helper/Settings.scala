package com.onedot.helper

import com.onedot.helper.SettingConstants._
import com.typesafe.config.ConfigFactory

object Settings {

  private val config = ConfigFactory.load("parser")
  val quotingString = config.getString(QUOTING_STRING)
  val lineSeparator = config.getString(LINE_SEPARATOR)
  val fieldDelimiter = config.getString(FIELD_DELIMITER)

}


object SettingConstants {
  final val QUOTING_STRING = "quoting-string"
  final val LINE_SEPARATOR = "line-separator"
  final val FIELD_DELIMITER = "field-delimiter"
}