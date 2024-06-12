package com.example.thyroiddisease

import android.content.res.AssetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimodelActivity : AppCompatActivity() {
    private lateinit var interpreter: Interpreter
    private val mModelPath = "thyroid.tflite"

    private lateinit var resultText: TextView
    private lateinit var edtsh: EditText
    private lateinit var edt3: EditText
    private lateinit var edfti: EditText
    private lateinit var edtt4: EditText
    private lateinit var edquery: EditText
    private lateinit var edtumor: EditText
    private lateinit var edtt4mes: EditText

    private lateinit var checkButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simodel)

        resultText = findViewById(R.id.txtResult)
        edtsh = findViewById(R.id.edtsh)
        edfti = findViewById(R.id.edfti)
        edt3 = findViewById(R.id.edt3)
        edtt4 = findViewById(R.id.edtt4)
        edquery = findViewById(R.id.edquery)
        edtumor = findViewById(R.id.edtumor)
        edtt4mes = findViewById(R.id.edtt4mes)
        checkButton = findViewById(R.id.btnCheck)
        checkButton.setOnClickListener {
            var result = doInference(
                edtsh.text.toString(),
                edfti.text.toString(),
                edt3.text.toString(),
                edtt4.text.toString(),
                edquery.text.toString(),
                edtumor.text.toString(),
                edtt4mes.text.toString()
            )
            runOnUiThread {
                if (result == 0) {
                    resultText.text = "Negative"
                } else if (result == 1) {
                    resultText.text = "Hypothyroid"
                } else if (result == 2){
                    resultText.text = "Hyperthyroid"
                }
            }
        }
        initInterpreter()
    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(5)
        //options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(assets, mModelPath),options)
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffSet = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffSet, declaredLength)
    }

    private fun doInference(input1: String, input2: String, input3: String, input4: String, input5: String, input6: String, input7: String): Int {
        val inputVal = FloatArray(7)
        inputVal[0] = input1.toFloat()
        inputVal[1] = input2.toFloat()
        inputVal[2] = input3.toFloat()
        inputVal[3] = input4.toFloat()
        inputVal[4] = input5.toFloat()
        inputVal[5] = input6.toFloat()
        inputVal[6] = input7.toFloat()
        val output = Array(1) { FloatArray(3) }
        interpreter.run(inputVal, output)

        Log.e("result", (output[0].toList() + " ").toString())

        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }
}