package com.livehomeradio.models

data class DashBoardModel(
    val audio_info: String="",
    val bitrate: Int=0,
    val channels: Int=0,
    val genre: String="",
    val listener_peak: Int=0,
    val listeners: Int=0,
    val listenurl: String="",
    val samplerate: Int=0,
    val server_description: String="",
    val server_name: String="",
    val server_type: String="",
    val stream_start: String="",
    val stream_start_iso8601: String="",
    val title: String="Loading.."
)