package com.sid.nike.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty

data class Response(
	@field:JsonProperty("list")
	val definitions: List<Definition>? = null
)

@Entity(tableName = "definition")
data class Definition(

	@PrimaryKey
	@ColumnInfo(name = "def_id")
	@field:JsonProperty("defid")
	var defId: Int? = null,

	@Ignore
	@field:JsonProperty("sound_urls")
	var soundUrls: List<Any?>? = null,

	@ColumnInfo(name = "thumbs_down")
	@field:JsonProperty("thumbs_down")
	var thumbsDown: Int? = null,

	@ColumnInfo(name = "author")
	@field:JsonProperty("author")
	var author: String? = null,

	@ColumnInfo(name = "written_on")
	@field:JsonProperty("written_on")
	var writtenOn: String? = null,

	@ColumnInfo(name = "definition")
	@field:JsonProperty("definition")
	var definition: String? = null,

	@ColumnInfo(name = "permalink")
	@field:JsonProperty("permalink")
	var permalink: String? = null,

	@ColumnInfo(name = "thumbs_up")
	@field:JsonProperty("thumbs_up")
	var thumbsUp: Int? = null,

	@ColumnInfo(name = "word")
	@field:JsonProperty("word")
	var word: String? = null,

	@ColumnInfo(name = "current_vote")
	@field:JsonProperty("current_vote")
	var currentVote: String? = null,

	@ColumnInfo(name = "example")
	@field:JsonProperty("example")
	var example: String? = null
)