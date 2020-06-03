package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.RootRepository.isAuth
import ru.skillbranch.skillarticles.extensions.selectDestination //TODO Необходимо реализовать
import ru.skillbranch.skillarticles.ui.base.BaseActivity
import ru.skillbranch.skillarticles.viewmodels.RootViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class RootActivity : BaseActivity<RootViewModel>() {
    override val layout: Int = R.layout.activity_root
    public override val viewModel: RootViewModel by viewModels()
    override fun subscribeOnState(state: IViewModelState){
        println("state: $state")
    } // TODO добавил

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp()
        //return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //top level destination
        val appbarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_articles,
                R.id.nav_bookmarks,
                R.id.nav_transcriptions,
                R.id.nav_profile
            )
        )

        setupActionBarWithNavController(navController, appbarConfiguration)
        nav_view.setOnNavigationItemSelectedListener {
            //if click on bottom navigation item -> navigate to destination by item id
            viewModel.navigate(NavigationCommand.To(it.itemId))
            true
        }

        navController.addOnDestinationChangedListener{ controller, destination, arguments ->
            //if destination change set select bottom navigation item
            nav_view.selectDestination(destination)

            //if(destination.id == R.id.nav_auth) nav_view.selectItem(arguments?.get("private_destination") as Int?)

            if(viewModel.currentState.isAuth && destination.id == R.id.nav_auth) {
                controller.popBackStack()
                val private: Int? = arguments?.get("private_destination") as Int?
                if(private != null) controller.navigate(private)
            }

            /*
            //TODO if (destination.label == "Authorization" && уже прошли авторизацию) {destination = "Login"}
            // грязный хак ((
            println("currentState ${(viewModel as RootViewModel).currentState}")
            if(destination.label == "Authorization" && viewModel.currentState.isAuth  == true ){
                //controller.getBackStackEntry()
                //controller.navigateUp()
                //controller.
                controller.popBackStack() //удаление верхнего фрагмента из backstack
                viewModel.navigate(NavigationCommand.To(R.id.nav_profile))

            }*/

        }
    }

    override fun renderNotification(notify: Notify){
        val snackbar = Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG)

        if(bottombar!=null) snackbar.anchorView = bottombar
        else snackbar.anchorView = nav_view

        when(notify) {
            is Notify.ActionMessage -> {
                val(_,label, handler) = notify

                with(snackbar){
                    setActionTextColor(getColor(R.color.color_accent_dark))
                    setAction(label) { handler.invoke() }
                }
            }

            is Notify.ErrorMessage -> {
                val(_,label, handler) = notify

                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    handler ?: return@with
                    setAction(label) { handler.invoke() }
                }
            }
        }

        snackbar.show()
    }
}

