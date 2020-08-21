package ua.co.myrecipes.ui

/*
import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.co.myrecipes.repository.RecipeRepository

class RecipeViewModel @ViewModelInject constructor(
    val app: Application,
    */
/*private val recipeRepository: RecipeRepository*//*

): AndroidViewModel(app) {


    fun setStateEvent(mainStateEvent: MainStateEvent){
        viewModelScope.launch {
            when(mainStateEvent){
                */
/*is MainStateEvent.GetAllRecipesEvent -> {
                    recipeRepository.getAllComplexes(hasInternetConnection())
                        .onEach {dataState -> _allComplexState.value = dataState }
                        .launchIn(viewModelScope)
                }*//*

                MainStateEvent.None -> {
                    // who cares
                }
            }
        }
    }
}

sealed class MainStateEvent{
    object GetAllRecipesEvent: MainStateEvent()
    object None: MainStateEvent()

}*/
