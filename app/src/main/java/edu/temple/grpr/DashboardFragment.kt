package edu.temple.grpr

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class DashboardFragment : Fragment(){

    lateinit var createFab : ExtendedFloatingActionButton
    lateinit var layout : View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Lets the system know that this fragment will contribute to the app menu!
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layout =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        createFab = layout.findViewById(R.id.createFab)

        createFab.setOnClickListener{
            (activity as DashboardInterface).createGroup()
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //if group is active, change UI depending on that
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java).getGroupId().observe(requireActivity()) {
            if(it.isNullOrEmpty()){
                createFab.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#03DAC5"))
                createFab.text = resources.getString(R.string.create)
                createFab.setIconResource(R.drawable.ic_baseline_group_add_24)
                createFab.setOnClickListener{(activity as DashboardInterface).createGroup()}
            }else{
                createFab.backgroundTintList  = ColorStateList.valueOf(Color.parseColor("#e91e63"))
                createFab.setIconResource(android.R.drawable.ic_menu_close_clear_cancel)
                createFab.text = resources.getString(R.string.leave)
                createFab.setOnClickListener {(activity as DashboardInterface).endGroup()}
            }
        }
    }

    // This fragment places a menu item in the app bar
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_logout) {
            (activity as DashboardInterface).logout()
            return true
        }

        if(item.itemId == R.id.action_join_group) {
            Navigation.findNavController(layout)
                .navigate(R.id.action_dashboardFragment_to_groupFragment)
            return true
        }

        return false
    }

    interface DashboardInterface {
        fun logout()
        fun createGroup()
        fun endGroup()
    }

}