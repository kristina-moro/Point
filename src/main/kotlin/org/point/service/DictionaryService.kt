package org.point.service

import io.github.reactivecircus.cache4k.Cache
import org.point.domain.Category
import org.point.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.minutes

@Service
class DictionaryService(private val repository: CategoryRepository) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val cacheCategory = Cache.Builder<Int, List<Category>>()
        .expireAfterWrite(120.minutes)
        .build()

    fun getCatalog(
        categoryId: Int?
    ): List<Category> {
        log.info("Categories requested by categoryId=$categoryId")
        val cacheKey = categoryId ?: 0
        var categoryList = cacheCategory.get(cacheKey)
        if (categoryList.isNullOrEmpty()) {
            categoryList = repository.getCatalogByParentId(categoryId)
                .also { cacheCategory.put(cacheKey, it) }
        }
        return categoryList
    }

}