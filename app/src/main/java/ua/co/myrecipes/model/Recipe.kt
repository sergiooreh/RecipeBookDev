package ua.co.myrecipes.model

class Recipe() {
    var name = ""
    var type: String = ""
    var durationPrepare = 0
    var ingredients = mapOf<String,String>()
    var directions = arrayOf<String>()
}