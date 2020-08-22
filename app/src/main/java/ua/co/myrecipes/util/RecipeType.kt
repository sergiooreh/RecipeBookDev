package ua.co.myrecipes.util

import ua.co.myrecipes.R

enum class RecipeType(val img: Int) {
    COOKIES(img = R.drawable.cookies),
    TRADITIONAL(img = R.drawable.traditional),
    SANDWICH(img = R.drawable.sandwich),
    PIZZA(img = R.drawable.pizza),
    COCKTAILS(img = R.drawable.coctails),
    DESSERTS(img = R.drawable.desserts),
    SALADS(img = R.drawable.salads)
}