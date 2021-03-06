package at.technikum_wien.polzert.news.data

import androidx.annotation.NonNull
import androidx.room.*
import at.technikum_wien.polzert.news.data.db.InstantConverter
import at.technikum_wien.polzert.news.data.db.SetConverter
import kotlinx.datetime.Instant

@kotlinx.serialization.Serializable
@Entity(tableName="news_item", indices = [Index(value = ["identifier"], unique = true)])
@TypeConverters(InstantConverter::class, SetConverter::class)
data class NewsItem(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id : Long?,
    @ColumnInfo(name = "identifier")
    @NonNull
    var identifier : String,
    @ColumnInfo(name = "title")
    @NonNull
    var title : String,
    @ColumnInfo(name = "link")
    var link : String?,
    @ColumnInfo(name = "description")
    var description : String?,
    @ColumnInfo(name = "image_url")
    var imageUrl : String?,
    @ColumnInfo(name = "author")
    var author : String?,
    @ColumnInfo(name = "publication_date")
    @NonNull
    var publicationDate : Instant,
    @ColumnInfo(name = "keywords")
    @NonNull
    var keywords : Set<String>) {
    @Ignore
    constructor(identifier: String, title: String, link: String?, description: String?, imageUrl: String?, author: String?, publicationDate: Instant, keywords: Set<String>) : this(null, identifier, title, link, description, imageUrl, author, publicationDate, keywords)
}
