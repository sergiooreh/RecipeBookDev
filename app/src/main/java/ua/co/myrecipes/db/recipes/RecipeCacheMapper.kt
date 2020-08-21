package ua.co.myrecipes.db.recipes

import ua.co.myrecipes.model.Recipe
import ua.co.myrecipes.util.EntityMapper
import javax.inject.Inject

class RecipeCacheMapper
@Inject
constructor(): EntityMapper<RecipeCacheEntity, Recipe> {
    override fun mapFromEntity(entity: RecipeCacheEntity): Recipe {
        return Recipe().apply {
            id = entity.id
            name = entity.name
            type = entity.type
            durationPrepare = entity.durationPrepare
//            ingredients = entity.ingredients
            directions = entity.directions
            img = entity.img
        }
    }

    override fun mapToEntity(domainModel: Recipe): RecipeCacheEntity =
        RecipeCacheEntity(
            id = domainModel.id,
            name = domainModel.name,
            type = domainModel.type,
            durationPrepare = domainModel.durationPrepare,
//            ingredients = domainModel.ingredients,
            directions = domainModel.directions,
            img = domainModel.img
        )


    fun mapFromEntityList(entities: List<RecipeCacheEntity>): List<Recipe> =
        entities.map { mapFromEntity(it) }
}