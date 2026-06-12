package com.iuxoa.iu.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await

// No auth — open Firestore rules (personal admin app).
private fun <T> Flow<List<T>>.orEmptyOnError(tag: String): Flow<List<T>> =
    catch { e ->
        Log.w("Firestore", "$tag error: ${e.message}")
        emit(emptyList())
    }

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // ── Projects ──────────────────────────────────────────────────────────────
    fun projectsFlow(): Flow<List<Project>> = callbackFlow {
        val sub = db.collection("projects").orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    Project(
                        docId  = doc.id,
                        order  = (doc.getLong("order") ?: 0).toInt(),
                        id     = doc.getString("id") ?: "",
                        name   = doc.getString("name") ?: "",
                        cat    = doc.getString("cat") ?: "",
                        year   = doc.getString("year") ?: "",
                        tags   = (doc.get("tags") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                        accent = doc.getString("accent") ?: "#e85533",
                        img    = doc.getString("img") ?: "",
                        desc   = doc.getString("desc") ?: ""
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("projects")

    suspend fun addProject(p: Project) {
        db.collection("projects").add(mapOf(
            "order" to p.order, "id" to p.id, "name" to p.name, "cat" to p.cat,
            "year" to p.year, "tags" to p.tags, "accent" to p.accent,
            "img" to p.img, "desc" to p.desc
        )).await()
    }

    suspend fun deleteProject(docId: String) {
        db.collection("projects").document(docId).delete().await()
    }

    suspend fun updateProject(p: Project) {
        db.collection("projects").document(p.docId).set(mapOf(
            "order" to p.order, "id" to p.id, "name" to p.name, "cat" to p.cat,
            "year" to p.year, "tags" to p.tags, "accent" to p.accent,
            "img" to p.img, "desc" to p.desc
        )).await()
    }

    // ── Research ──────────────────────────────────────────────────────────────
    fun patentsFlow(): Flow<List<Patent>> = callbackFlow {
        val sub = db.collection("patents").orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    Patent(
                        docId = doc.id,
                        order = (doc.getLong("order") ?: 0).toInt(),
                        id    = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        type  = doc.getString("type") ?: "Patent",
                        year  = doc.getString("year") ?: "",
                        link  = doc.getString("link")
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("patents")

    fun papersFlow(): Flow<List<ResearchPaper>> = callbackFlow {
        val sub = db.collection("papers").orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    ResearchPaper(
                        docId = doc.id,
                        order = (doc.getLong("order") ?: 0).toInt(),
                        id    = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        type  = doc.getString("type") ?: "Research Paper",
                        year  = doc.getString("year") ?: "",
                        link  = doc.getString("link")
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("papers")

    fun bookChaptersFlow(): Flow<List<BookChapter>> = callbackFlow {
        val sub = db.collection("bookChapters").orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    BookChapter(
                        docId = doc.id,
                        order = (doc.getLong("order") ?: 0).toInt(),
                        id    = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        type  = doc.getString("type") ?: "Book Chapter",
                        year  = doc.getString("year") ?: "",
                        link  = doc.getString("link")
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("bookChapters")

    fun otherPubsFlow(): Flow<List<OtherPub>> = callbackFlow {
        val sub = db.collection("otherPubs").orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    OtherPub(
                        docId = doc.id,
                        order = (doc.getLong("order") ?: 0).toInt(),
                        id    = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        type  = doc.getString("type") ?: "Journal",
                        year  = doc.getString("year") ?: "",
                        link  = doc.getString("link")
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("otherPubs")

    // ── Guestbook ─────────────────────────────────────────────────────────────
    // Reads ALL entries (admin view). Tries multiple common field name variants
    // so entries from different website form versions all show correctly.
    fun guestbookFlow(): Flow<List<GuestbookEntry>> = callbackFlow {
        val sub = db.collection("guestbook")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    // Try multiple field name variants for name (website forms vary)
                    val name = doc.getString("name")
                        ?: doc.getString("visitorName")
                        ?: doc.getString("fullName")
                        ?: doc.getString("userName")
                        ?: ""
                    // Also try to get email as fallback for display
                    val email = doc.getString("email") ?: ""
                    // Use name if not blank, else try email prefix, else "Anonymous"
                    val displayName = when {
                        name.isNotBlank() -> name
                        email.isNotBlank() -> email.substringBefore("@").replaceFirstChar { it.uppercase() }
                        else -> ""
                    }
                    // Try multiple field name variants for message
                    val message = doc.getString("message")
                        ?: doc.getString("content")
                        ?: doc.getString("text")
                        ?: ""
                    GuestbookEntry(
                        docId     = doc.id,
                        name      = displayName,
                        message   = message,
                        emoji     = doc.getString("emoji") ?: "✨",
                        approved  = doc.getBoolean("approved") ?: false,
                        createdAt = doc.getTimestamp("createdAt")
                            ?: doc.getTimestamp("timestamp")
                            ?: doc.getTimestamp("date")
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("guestbook")

    suspend fun approveGuestbookEntry(docId: String) {
        db.collection("guestbook").document(docId).update("approved", true).await()
    }

    suspend fun deleteGuestbookEntry(docId: String) {
        db.collection("guestbook").document(docId).delete().await()
    }

    // ── Bucket List ───────────────────────────────────────────────────────────
    fun bucketListFlow(): Flow<List<BucketItem>> = callbackFlow {
        val sub = db.collection("bucketList").orderBy("order")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    BucketItem(
                        docId    = doc.id,
                        order    = (doc.getLong("order") ?: 0).toInt(),
                        title    = doc.getString("title") ?: "",
                        category = doc.getString("category") ?: "",
                        done     = doc.getBoolean("done") ?: false,
                        year     = doc.getString("year") ?: ""
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("bucketList")

    suspend fun toggleBucketItem(docId: String, done: Boolean) {
        db.collection("bucketList").document(docId).update("done", done).await()
    }

    suspend fun addBucketItem(item: BucketItem) {
        db.collection("bucketList").add(mapOf(
            "order"    to item.order,
            "title"    to item.title,
            "category" to item.category,
            "done"     to item.done,
            "year"     to item.year
        )).await()
    }

    suspend fun updateBucketItem(item: BucketItem) {
        db.collection("bucketList").document(item.docId).set(mapOf(
            "order"    to item.order,
            "title"    to item.title,
            "category" to item.category,
            "done"     to item.done,
            "year"     to item.year
        )).await()
    }

    suspend fun deleteBucketItem(docId: String) {
        db.collection("bucketList").document(docId).delete().await()
    }

    // ── Research generic CRUD ─────────────────────────────────────────────────
    private fun researchMap(id: String, title: String, type: String, year: String, link: String?, order: Int) =
        mapOf("order" to order, "id" to id, "title" to title, "type" to type, "year" to year, "link" to (link ?: ""))

    suspend fun addResearchItem(collection: String, id: String, title: String, type: String, year: String, link: String?, order: Int) {
        db.collection(collection).add(researchMap(id, title, type, year, link, order)).await()
    }

    suspend fun updateResearchItem(collection: String, docId: String, id: String, title: String, type: String, year: String, link: String?, order: Int) {
        db.collection(collection).document(docId).set(researchMap(id, title, type, year, link, order)).await()
    }

    suspend fun deleteResearchItem(collection: String, docId: String) {
        db.collection(collection).document(docId).delete().await()
    }

    // ── Contacts ──────────────────────────────────────────────────────────────
    fun unreadContactsFlow(): Flow<List<Contact>> = callbackFlow {
        val sub = db.collection("contacts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    Contact(
                        docId     = doc.id,
                        name      = doc.getString("name") ?: "",
                        email     = doc.getString("email") ?: "",
                        message   = doc.getString("message") ?: "",
                        read      = doc.getBoolean("read") ?: false,
                        createdAt = doc.getTimestamp("createdAt")
                    )
                }?.filter { !it.read } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("unreadContacts")

    fun allContactsFlow(): Flow<List<Contact>> = callbackFlow {
        val sub = db.collection("contacts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                trySend(snap?.documents?.map { doc ->
                    Contact(
                        docId     = doc.id,
                        name      = doc.getString("name") ?: "",
                        email     = doc.getString("email") ?: "",
                        message   = doc.getString("message") ?: "",
                        read      = doc.getBoolean("read") ?: false,
                        createdAt = doc.getTimestamp("createdAt")
                    )
                } ?: emptyList())
            }
        awaitClose { sub.remove() }
    }.orEmptyOnError("allContacts")

    suspend fun markContactRead(docId: String) {
        db.collection("contacts").document(docId).update("read", true).await()
    }

    suspend fun deleteContact(docId: String) {
        db.collection("contacts").document(docId).delete().await()
    }

    // ── Settings ──────────────────────────────────────────────────────────────
    fun settingsFlow(): Flow<Settings> = callbackFlow {
        val sub = db.collection("settings").document("main")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val rawStats = (snap?.get("heroStats") as? List<*>) ?: emptyList<Any>()
                val stats = rawStats.filterIsInstance<Map<*, *>>().map { m ->
                    HeroStat(value = m["value"] as? String ?: "", label = m["label"] as? String ?: "")
                }
                trySend(Settings(heroStats = stats))
            }
        awaitClose { sub.remove() }
    }.catch { e ->
        Log.w("Firestore", "settings error: ${e.message}")
        emit(Settings())
    }

    suspend fun updateHeroStats(stats: List<HeroStat>) {
        val raw = stats.map { mapOf("value" to it.value, "label" to it.label) }
        db.collection("settings").document("main").update("heroStats", raw).await()
    }
}
