package com.learn.flashLearnTagalog

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.other.Constants.KEY_ENG_FIRST
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Card(word:Word) : Fragment() {

    @Inject
    lateinit var sharedPref : SharedPreferences

    private val viewModel: MainViewModel by viewModels()

    val currentWord = word
    var shownWord : String = ""
    private lateinit var tvCurrWord : TextView
    private lateinit var card : ImageView
    var front = true
    private var engFirst : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_card, container, false)

        engFirst = sharedPref.getBoolean(KEY_ENG_FIRST, false)

        val wordType : TextView = view.findViewById(R.id.tvType)

        val words = if(engFirst)
            currentWord.english
        else
            currentWord.tagalog

        val numberOfEnglishWords = words.split("\\s+".toRegex()).size

        card = view.findViewById(R.id.card)
        card.setImageResource(R.drawable.card_front_new)
        tvCurrWord = view.findViewById(R.id.tvCurrWord)

        wordType.text = when(currentWord.type){
            "n" -> {
                "Noun"
            }
            "comp" -> {
                "Compound Noun"
            }
            "v" -> {
                "Verb"
            }
            "adj" -> {
                "Adjective"
            }
            "adv" -> {
                "adverb"
            }
            "inf" -> {
                "infinitive"
            }
            "intrj"->{
                "interjection"
            }
            "prep"->{
                "preposition"
            }
            else -> {
                currentWord.type
            }
        }


        if(engFirst){
            shownWord = currentWord.english
            if(numberOfEnglishWords > 1)
                tvCurrWord.maxLines = 2
            else
                tvCurrWord.maxLines = 1
        }else{
            shownWord = currentWord.tagalog
            tvCurrWord.maxLines = 1
        }

        tvCurrWord.text = shownWord

        val imFlipCard : ImageButton = view.findViewById(R.id.imFlipCard)
        imFlipCard.setOnClickListener{
            currentWord.id?.let { it1 -> viewModel.flipWord(it1) }
            if(shownWord == currentWord.english){
                shownWord = currentWord.tagalog
                tvCurrWord.maxLines = 1
            }
            else{
                shownWord = currentWord.english
                if(numberOfEnglishWords > 1)
                    tvCurrWord.maxLines = 2
                else
                    tvCurrWord.maxLines = 1
            }
            tvCurrWord.text = shownWord
            front = if(front){
                card.setImageResource(R.drawable.card_back)
                false
            }else{
                card.setImageResource(R.drawable.card_front_new)
                true
            }
        }

        return view
    }
}