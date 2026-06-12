package com.iuxoa.iu.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iuxoa.iu.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// No authentication required — personal admin app with open Firestore rules.
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = FirestoreRepository()

    val projects       = repo.projectsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val patents        = repo.patentsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val papers         = repo.papersFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bookChapters   = repo.bookChaptersFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val otherPubs      = repo.otherPubsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val guestbook      = repo.guestbookFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val bucketList     = repo.bucketListFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val contacts       = repo.allContactsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val unreadContacts = repo.unreadContactsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val settings       = repo.settingsFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Settings())

    val unreadCount = unreadContacts.map { it.size }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // ── Guestbook ─────────────────────────────────────────────────────────────
    fun deleteGuestbook(docId: String) = viewModelScope.launch {
        runCatching { repo.deleteGuestbookEntry(docId) }
            .onFailure { Log.e("VM", "deleteGuestbook failed", it) }
    }

    // ── Contacts ──────────────────────────────────────────────────────────────
    fun markContactRead(docId: String) = viewModelScope.launch {
        runCatching { repo.markContactRead(docId) }
            .onFailure { Log.e("VM", "markContactRead failed", it) }
    }

    fun deleteContact(docId: String) = viewModelScope.launch {
        runCatching { repo.deleteContact(docId) }
            .onFailure { Log.e("VM", "deleteContact failed", it) }
    }

    // ── Projects ──────────────────────────────────────────────────────────────
    fun deleteProject(docId: String) = viewModelScope.launch {
        runCatching { repo.deleteProject(docId) }
            .onFailure { Log.e("VM", "deleteProject failed", it) }
    }

    fun addProject(project: Project, onComplete: () -> Unit = {}) = viewModelScope.launch {
        runCatching { repo.addProject(project) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "addProject failed", it) }
    }

    fun updateProject(project: Project, onComplete: () -> Unit = {}) = viewModelScope.launch {
        runCatching { repo.updateProject(project) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "updateProject failed", it) }
    }

    // ── Bucket List ───────────────────────────────────────────────────────────
    fun toggleBucketItem(docId: String, done: Boolean) = viewModelScope.launch {
        runCatching { repo.toggleBucketItem(docId, done) }
            .onFailure { Log.e("VM", "toggleBucketItem failed", it) }
    }

    fun addBucketItem(item: BucketItem, onComplete: () -> Unit = {}) = viewModelScope.launch {
        runCatching { repo.addBucketItem(item) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "addBucketItem failed", it) }
    }

    fun updateBucketItem(item: BucketItem, onComplete: () -> Unit = {}) = viewModelScope.launch {
        runCatching { repo.updateBucketItem(item) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "updateBucketItem failed", it) }
    }

    fun deleteBucketItem(docId: String) = viewModelScope.launch {
        runCatching { repo.deleteBucketItem(docId) }
            .onFailure { Log.e("VM", "deleteBucketItem failed", it) }
    }

    // ── Research ──────────────────────────────────────────────────────────────
    fun addResearchItem(
        col: String, id: String, title: String, type: String,
        year: String, link: String?, order: Int, onComplete: () -> Unit = {}
    ) = viewModelScope.launch {
        runCatching { repo.addResearchItem(col, id, title, type, year, link, order) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "addResearch failed", it) }
    }

    fun updateResearchItem(
        col: String, docId: String, id: String, title: String,
        type: String, year: String, link: String?, order: Int, onComplete: () -> Unit = {}
    ) = viewModelScope.launch {
        runCatching { repo.updateResearchItem(col, docId, id, title, type, year, link, order) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "updateResearch failed", it) }
    }

    fun deleteResearchItem(col: String, docId: String) = viewModelScope.launch {
        runCatching { repo.deleteResearchItem(col, docId) }
            .onFailure { Log.e("VM", "deleteResearch failed", it) }
    }

    fun updateHeroStats(stats: List<HeroStat>, onComplete: () -> Unit = {}) = viewModelScope.launch {
        runCatching { repo.updateHeroStats(stats) }
            .onSuccess { onComplete() }
            .onFailure { Log.e("VM", "updateHeroStats failed", it) }
    }
}
