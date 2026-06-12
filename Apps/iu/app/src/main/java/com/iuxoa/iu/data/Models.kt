package com.iuxoa.iu.data

import com.google.firebase.Timestamp

// ─────────────────────────────────────────────────────────────────────────────
// DATA MODELS  (mirror Firestore document structures from ANDROID_APP_GUIDE.md)
// ─────────────────────────────────────────────────────────────────────────────

data class Project(
    val docId:  String  = "",
    val order:  Int     = 0,
    val id:     String  = "",
    val name:   String  = "",
    val cat:    String  = "",   // App | Web | Data | Game
    val year:   String  = "",
    val tags:   List<String> = emptyList(),
    val accent: String  = "#e85533",
    val img:    String  = "",
    val desc:   String  = ""
)

data class Patent(
    val docId:  String  = "",
    val order:  Int     = 0,
    val id:     String  = "",
    val title:  String  = "",
    val type:   String  = "Patent",
    val year:   String  = "",
    val link:   String? = null
)

data class ResearchPaper(
    val docId:  String  = "",
    val order:  Int     = 0,
    val id:     String  = "",
    val title:  String  = "",
    val type:   String  = "Research Paper",
    val year:   String  = "",
    val link:   String? = null
)

data class BookChapter(
    val docId:  String  = "",
    val order:  Int     = 0,
    val id:     String  = "",
    val title:  String  = "",
    val type:   String  = "Book Chapter",
    val year:   String  = "",
    val link:   String? = null
)

data class OtherPub(
    val docId:  String  = "",
    val order:  Int     = 0,
    val id:     String  = "",
    val title:  String  = "",
    val type:   String  = "Journal",
    val year:   String  = "",
    val link:   String? = null
)

data class GuestbookEntry(
    val docId:    String    = "",
    val name:     String    = "",
    val message:  String    = "",
    val emoji:    String    = "✨",
    val approved: Boolean   = false,
    val createdAt: Timestamp? = null
)

data class BucketItem(
    val docId:    String  = "",
    val order:    Int     = 0,
    val title:    String  = "",
    val category: String  = "",
    val done:     Boolean = false,
    val year:     String  = ""
)

data class Contact(
    val docId:    String    = "",
    val name:     String    = "",
    val email:    String    = "",
    val message:  String    = "",
    val read:     Boolean   = false,
    val createdAt: Timestamp? = null
)

data class HeroStat(
    val value: String = "",
    val label: String = ""
)

data class Settings(
    val heroStats: List<HeroStat> = emptyList()
)
