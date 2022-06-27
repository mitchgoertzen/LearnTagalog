package com.learn.flashLearnTagalog.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learn.flashLearnTagalog.DictionaryAdapter
import com.learn.flashLearnTagalog.R
import com.learn.flashLearnTagalog.db.Word
import com.learn.flashLearnTagalog.ui.LearningActivity
import com.learn.flashLearnTagalog.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DictionaryFragment : Fragment() {

    private lateinit var dictionaryAdapter: DictionaryAdapter

    private val viewModel: MainViewModel by viewModels()
    private var wordList : MutableList<Word> = mutableListOf()
    private var wordsPerPage = 200
    private var numPages = 1
    private var currentPage = 1

    @Inject
    lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dictionaryAdapter = DictionaryAdapter(mutableListOf())

        val view = inflater.inflate(R.layout.fragment_dictionary, container, false)
        val rvDictionary : RecyclerView = view.findViewById(R.id.rvDictionary)

        val currPage : TextView = view.findViewById(R.id.tvCurrPage)
        val totalPages : TextView = view.findViewById(R.id.tvTotalPages)

        val firstPage : ImageButton = view.findViewById(R.id.ibFirstPage)
        val lastPage : ImageButton = view.findViewById(R.id.ibLastPage)
        val nextPage : ImageButton = view.findViewById(R.id.ibNextPage)
        val prevPage : ImageButton = view.findViewById(R.id.ibPrevPage)

        numPages = viewModel.getSize()/wordsPerPage

        if(viewModel.getSize()%wordsPerPage > 0)
            numPages++

        totalPages.text = numPages.toString()

        deactivateSwitch(firstPage)
        deactivateSwitch(prevPage)

        firstPage.setOnClickListener{
            deactivateSwitch(firstPage)
            deactivateSwitch(prevPage)
            activateSwitch(nextPage)
            activateSwitch(lastPage)
            currentPage = 1
            currPage.text = "1"
            gatherWords()
        }

        lastPage.setOnClickListener{
            deactivateSwitch(nextPage)
            deactivateSwitch(lastPage)
            activateSwitch(firstPage)
            activateSwitch(prevPage)
            currentPage = numPages
            currPage.text = "$numPages"
            gatherWords()
        }

        nextPage.setOnClickListener{
            if(!prevPage.isEnabled){
                activateSwitch(prevPage)
                activateSwitch(firstPage)
            }
            currentPage++
            if(currentPage == numPages){
                deactivateSwitch(nextPage)
                deactivateSwitch(lastPage)
            }
            currPage.text = currentPage.toString()
            gatherWords()
        }

        prevPage.setOnClickListener{
            if(!nextPage.isEnabled){
                activateSwitch(nextPage)
                activateSwitch(lastPage)
            }
            currentPage--
            if(currentPage == 1){
                deactivateSwitch(prevPage)
                deactivateSwitch(firstPage)
            }
            currPage.text = currentPage.toString()
            gatherWords()
        }

        rvDictionary.adapter = dictionaryAdapter
        rvDictionary.layoutManager = LinearLayoutManager((activity as LearningActivity?))

        gatherWords()

        return view
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun gatherWords() {
        dictionaryAdapter.deleteToDos()

        GlobalScope.launch(Dispatchers.Main) {
            suspend {
                viewModel.getDictionaryWords(((currentPage - 1) * wordsPerPage),wordsPerPage).observe(viewLifecycleOwner) {
                    wordList = it.toMutableList()
                    for(i in 1..wordList.size){
                        dictionaryAdapter.addToDo(wordList[i-1])
                    }
                }
                Handler(Looper.getMainLooper()).postDelayed({
                }, 200)
            }.invoke()
        }
    }

    private fun activateSwitch(switch: ImageButton) {
        switch.scaleX = 1f
        switch.scaleY = 1f
        switch.isEnabled = true
        switch.alpha = 1f
    }

    private fun deactivateSwitch(switch: ImageButton) {
        switch.scaleX = .8f
        switch.scaleY = .8f
        switch.isEnabled = false
        switch.alpha = .8f
    }

}