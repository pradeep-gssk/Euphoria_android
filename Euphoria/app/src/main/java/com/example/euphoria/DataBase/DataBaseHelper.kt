package com.example.euphoria.DataBase

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.euphoria.DataBase.Questionnaires.*
import com.example.euphoria.DataBase.Timer.*
import com.example.euphoria.Extensions.intValue
import com.example.euphoria.Extensions.stringValue
import com.example.euphoria.Models.EUSession
import com.example.euphoria.Models.EUStop
import org.json.JSONArray
import org.json.JSONObject

@Database(entities = [Questionnaires::class,
                    Questionnaire::class,
                    Option:: class,
                    Video:: class,
                    Session:: class,
                    Stop:: class,
                    Sound:: class,
                    Diet:: class,
                    Exercises:: class,
                    History:: class],
                    version = 1)

abstract class DataBaseHelper: RoomDatabase() {
    abstract fun questionnairesDao(): QuestionnairesDao
    abstract fun questionnaireDao(): QuestionnaireDao
    abstract fun optionDao(): OptionDao
    abstract fun videoDao(): VideoDao
    abstract fun sessionDao(): SessionDao
    abstract fun stopDao(): StopDao
    abstract fun soundDao(): SoundDao
    abstract fun dietDao(): DietDao
    abstract fun exercisesDao(): ExercisesDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: DataBaseHelper? = null

        fun getDatabase(context: Context): DataBaseHelper {
            val tempInstance = INSTANCE
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBaseHelper::class.java,
                    "Euphoria"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    fun addQuestionnaires(jsonObject: JSONObject, total: Int) {
        val questionnaires =
            Questionnaires(jsonObject.stringValue("title"), 0, total)
        val questionnairesId = questionnairesDao().insert(questionnaires)
        val array = jsonObject.getJSONArray("questionnaire")
        addQuestionnaire(array, questionnairesId)
    }

    fun addQuestionnaire(jsonArray: JSONArray, questionnairesId: Long) {
        for (i in 0..(jsonArray.length() - 1)) {
            val questionnaireObject = jsonArray.getJSONObject(i)
            val questionnaire = Questionnaire(
                null,
                null,
                questionnaireObject.stringValue("element"),
                questionnaireObject.stringValue("question"),
                questionnaireObject.intValue("optionType"),
                questionnaireObject.intValue("subOptionType"),
                questionnairesId
            )
            val questionnaireId = questionnaireDao().insert(questionnaire)
            val array = questionnaireObject.getJSONArray("options")
            addOption(array, questionnaireId)
        }
    }

    fun addOption(jsonArray: JSONArray, questionnaireId: Long) {
        val options = ArrayList<Option>()
        for (i in 0..(jsonArray.length() - 1)) {
            val optionsObject = jsonArray.getJSONObject(i)
            val option = Option(
                optionsObject["option"].stringValue(),
                questionnaireId
            )
            options.add(option)
        }
        optionDao().insertAll(options)
    }

    fun addExercises(jsonArray: JSONArray) {
        val exercises = ArrayList<Exercises>()
        for (i in 0..(jsonArray.length() - 1)) {
            val exerciseObject = jsonArray.getJSONObject(i)
            val exercise = Exercises(
                exerciseObject.stringValue("video_name"),
                exerciseObject.stringValue("thumbnail"),
                exerciseObject.stringValue("video_description"),
                exerciseObject.stringValue("video_path"),
                exerciseObject.stringValue("element"),
                exerciseObject.stringValue("exercise")
            )
            exercises.add(exercise)
        }
        exercisesDao().insertAll(exercises)
    }

    fun addVideos(jsonArray: JSONArray) {
        val videosList = ArrayList<Video>()
        for (i in 0..(jsonArray.length() - 1)) {
            val videosObject = jsonArray.getJSONObject(i)
            val video = Video(
                videosObject.stringValue("thumbnail"),
                videosObject.stringValue("title"),
                videosObject.stringValue("video_description"),
                videosObject.stringValue("video_name"),
                videosObject.stringValue("video_path")
            )
            videosList.add(video)
        }
        videoDao().insertAll(videosList)
    }

    fun addSounds(jsonArray: JSONArray) {
        val sounds = ArrayList<Sound>()
        for (i in 0..(jsonArray.length() - 1)) {
            val soundObject = jsonArray.getJSONObject(i)
            val sound = Sound(
                soundObject.stringValue("name"),
                soundObject.stringValue("resource"),
                soundObject.stringValue("type")
            )
            sounds.add(sound)
        }
        soundDao().insertAll(sounds)
    }

    fun addDiet(jsonArray: JSONArray) {
        val diets = ArrayList<Diet>()
        for (i in 0..(jsonArray.length() - 1)) {
            val dietObject = jsonArray.getJSONObject(i)
            val diet = Diet(
                dietObject.stringValue("channels"),
                dietObject.stringValue("diet"),
                dietObject.stringValue("effect"),
                dietObject.stringValue("flavour"),
                dietObject.stringValue("name"),
                dietObject.stringValue("nature"),
                dietObject.stringValue("organ"),
                dietObject.stringValue("element")
            )
            diets.add(diet)
        }
        dietDao().insertAll(diets)
    }

    fun saveSession(session: EUSession) {

        val entity = Session(session.name, session.timeInterval)
        val entityId = sessionDao().insert(entity)
        saveStop(session.stops, entityId)
    }

    fun saveStop(stops: ArrayList<EUStop>, entityId: Long) {

        val stopEntities = ArrayList<Stop>()

        for (stop in stops) {
            val sound = stop.sound
            val stopEntity = Stop(
                sound?.resource.stringValue(),
                sound?.name.stringValue(),
                stop.timeInterval,
                sound?.type.toString(),
                entityId)
            stopEntities.add(stopEntity)
        }

        stopDao().insertAll(stopEntities)
    }
}