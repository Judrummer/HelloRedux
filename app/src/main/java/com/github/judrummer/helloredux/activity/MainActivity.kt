package com.github.judrummer.helloredux.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.github.judrummer.helloredux.R
import com.github.judrummer.helloredux.fragment.TodoListFragment
import com.github.judrummer.helloredux.fragment.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TodoListFragment.OnListFragmentInteractionListener {

    override fun onListFragmentInteraction(item: DummyContent.DummyItem) {
        Snackbar.make(fab, "Click Item ${item.content}", Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show() }
        supportFragmentManager.beginTransaction().add(R.id.containerList, TodoListFragment.newInstance(1)).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
