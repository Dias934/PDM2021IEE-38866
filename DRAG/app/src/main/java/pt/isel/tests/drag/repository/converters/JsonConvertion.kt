package pt.isel.tests.drag.repository.converters

import com.google.gson.*
import java.lang.reflect.Type

class JsonConvertion<T: Any> : JsonSerializer<T>, JsonDeserializer<T> {
    private val CLASSNAME = "CLASSNAME"
    private val DATA = "DATA"


    override fun serialize(src: T, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject= JsonObject()
        jsonObject.add(CLASSNAME, context?.serialize(src.javaClass.name))
        jsonObject.add(DATA, context?.serialize(src))
        return jsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): T? {
        val jsonObject= json?.asJsonObject
        val klass= Class.forName(context?.deserialize(jsonObject?.get(CLASSNAME), String::class.java))
        return context?.deserialize(jsonObject?.get(DATA), klass)
    }
}