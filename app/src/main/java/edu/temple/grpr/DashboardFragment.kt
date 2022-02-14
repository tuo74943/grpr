package edu.temple.grpr

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardFragment : Fragment(){

    lateinit var createFab : ExtendedFloatingActionButton

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
        val layout =  inflater.inflate(R.layout.fragment_dashboard, container, false)
        createFab = layout.findViewById(R.id.createFab)

        createFab.setOnClickListener{
            (activity as DashboardInterface).createGroup()
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    interface DashboardInterface {
        fun logout()
        fun createGroup()
        fun endGroup()
    }

}