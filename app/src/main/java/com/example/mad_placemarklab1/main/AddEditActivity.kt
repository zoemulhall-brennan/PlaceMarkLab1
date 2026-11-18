package com.example.mad_placemarklab1


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mad_placemarklab1.models.PlacedMark
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Intent


class AddEditActivity : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var xInput: EditText
    private lateinit var yInput: EditText

    private lateinit var imagePreview: ImageView
    private var selectedImageUri: Uri? = null


    private var editingId: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_edit)

        titleInput =
            findViewById(R.id.titleEditText)

        descriptionInput =
            findViewById(R.id.descEditText)

        xInput =
            findViewById(R.id.xEditText)

        yInput =
            findViewById(R.id.yEditText)
        imagePreview =
            findViewById(R.id.imageViewAdd)


        val saveButton =
            findViewById<Button>(R.id.okButton)

        val cancelButton =
            findViewById<Button>(R.id.cancelButton)

        editingId =
            intent.getLongExtra("id", -1L)
                .takeIf { it != -1L }

        if (editingId != null) {

            loadExistingMark(editingId!!)
        }
        val imagePicker =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                if (uri != null) {
                    // Keep access to the document URI for later use, when supported.
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {
                        // Some providers may not grant persistable access.
                    }
                    selectedImageUri = uri
                    imagePreview.setImageURI(uri)
                }
            }
        findViewById<Button>(R.id.imagePickButton).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        val mapPicker =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data ?: return@registerForActivityResult
                    val selectedLatitude = data.getDoubleExtra("latitude", 0.0)
                    val selectedLongitude = data.getDoubleExtra("longitude", 0.0)

                    selectedLatitude?.let {
                        xInput.setText(it.toString())
                    }

                    selectedLongitude?.let {
                        yInput.setText(it.toString())
                    }
                }
            }
        findViewById<Button>(R.id.pickLocationButton).setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            mapPicker.launch(intent)
        }

        saveButton.setOnClickListener {
            saveMark()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }


    private fun loadExistingMark(id: Long) {

        val mark =
            AppData.placedMarks.findOne(id)

        if (mark == null) {

            Toast.makeText(
                this,
                "Mark not found",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        titleInput.setText(mark.title)
        descriptionInput.setText(mark.desc)
        xInput.setText(mark.x.toString())
        yInput.setText(mark.y.toString())
        selectedImageUri = mark.imageUri?.let { Uri.parse(it) }

        if (selectedImageUri != null) {
            imagePreview.setImageURI(selectedImageUri)
        } else {
            imagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
        }

    }

    private fun saveMark() {

        val title =
            titleInput.text.toString().trim()

        val description =
            descriptionInput.text.toString().trim()

        if (title.isEmpty()) {
            titleInput.error = "Title is required"
            return
        }

        val x =
            xInput.text.toString().toDoubleOrNull()

        if (x == null) {
            xInput.error = "Enter a valid number"
            return
        }

        val y =
            yInput.text.toString().toDoubleOrNull()

        if (y == null) {
            yInput.error = "Enter a valid number"
            return
        }

        if (editingId == null) {

            val mark = PlacedMark(
                title = title,
                desc = description,
                x = x,
                y = y,
                imageUri = selectedImageUri?.toString()
            )

            AppData.placedMarks.create(mark)

            Toast.makeText(
                this,
                "Mark created",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            val mark = PlacedMark(
                id = editingId!!,
                title = title,
                desc = description,
                x = x,
                y = y,
                imageUri = selectedImageUri?.toString()
            )

            AppData.placedMarks.update(mark)

            Toast.makeText(
                this,
                "Mark updated",
                Toast.LENGTH_SHORT
            ).show()
        }

        finish()
    }
}