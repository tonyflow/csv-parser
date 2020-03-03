package com.onedot.helper

import com.onedot.helper.SettingConstants._
import com.typesafe.config.ConfigFactory

/**
 * Configuration properties
 */
object Settings {

  private val config = ConfigFactory.load("parser")
  val quotingString = if (config.getString(QUOTING_STRING).isEmpty) '\"' else config.getString(QUOTING_STRING).charAt(0)
  val lineSeparator = if (config.getString(LINE_SEPARATOR).isEmpty) "\n" else config.getString(LINE_SEPARATOR)
  val fieldDelimiter = if (config.getString(FIELD_DELIMITER).isEmpty) "," else config.getString(FIELD_DELIMITER)
  val hasHeader = config.getBoolean(HAS_HEADER)
}

/**
 * Define configuration paths for [[CSVParser]] properties
 */
object SettingConstants {
  final val QUOTING_STRING = "csv.quoting-string"
  final val LINE_SEPARATOR = "csv.line-separator"
  final val FIELD_DELIMITER = "csv.field-delimiter"
  final val HAS_HEADER = "csv.has-header"
}