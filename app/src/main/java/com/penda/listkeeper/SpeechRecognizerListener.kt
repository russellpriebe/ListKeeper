package com.penda.listkeeper

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.penda.listkeeper.datamodel.ListElement


class SpeechRecognizerListener(private val recognizer: SpeechRecognizer): RecognitionListener {
    var mText: MutableLiveData<String> = MutableLiveData()

    override fun onReadyForSpeech(params: Bundle?) {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onEndOfSpeech() {
        recognizer.stopListening()
    }

    override fun onError(error: Int) {
        recognizer.stopListening()
        mText.postValue("error")
    }

    override fun onResults(results: Bundle?) {
        results?.let {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if(matches.size == 0){
                mText.postValue("null")
            } else {
                matches?.let {
                    mText.postValue(matches[0])
                }
            }
        }

    }
}