package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.skillbranch.skillarticles.viewmodels.IArticleViewModel

class RootActivity : AppCompatActivity() {

    private lateinit var ViewModel: IArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupToolbar()
        setupBottomBar()
        setupSubMenu()


    }
}
