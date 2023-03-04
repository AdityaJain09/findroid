package dev.jdtech.jellyfin.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jdtech.jellyfin.models.FindroidMovieDto
import dev.jdtech.jellyfin.models.FindroidSourceDto
import dev.jdtech.jellyfin.models.Server
import dev.jdtech.jellyfin.models.ServerAddress
import dev.jdtech.jellyfin.models.User

@Database(entities = [Server::class, ServerAddress::class, User::class, FindroidMovieDto::class, FindroidSourceDto::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ServerDatabase : RoomDatabase() {
    abstract val serverDatabaseDao: ServerDatabaseDao
}
