package edu.temple.grpr

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import org.json.JSONObject

class DashboardFragment : Fragment(){

    val grPrViewModel: GrPrViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java)
    }

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

        // Query the server for the current Group ID (if available)
        // and use it to close the group
        createFab.setOnLongClickListener {
            Helper.api.queryStatus(requireContext(), Helper.user.get(requireContext()), Helper.user.getSessionKey(requireContext())!!, object: Helper.api.Response {
                    override fun processResponse(response: JSONObject) {
                        Helper.api.closeGroup(requireContext(), Helper.user.get(requireContext()), Helper.user.getSessionKey(requireContext())!!, response.getString("group_id"), null)
                        Log.d("CloseGroup", "closed current group")
                    }
                })
            true
        }

        createFab.setOnClickListener{
            (activity as DashboardInterface).createGroup()
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if the user joined a group, an argument is passed back from groupFrag. Set to false if it was never passed.
        Log.d("status of joined", arguments?.getBoolean("joined", false).toString())

        //if group is active, change UI depending on that
        grPrViewModel.getGroupId().observe(requireActivity()) { groupId ->
            if(groupId.isNullOrEmpty()){
                setFabButton(getString(R.string.entrance_color), R.string.create, R.drawable.ic_baseline_group_add_24, { (activity as DashboardInterface).createGroup()})
            }else{
                setFabButton(getString(R.string.exit_color), R.string.end, android.R.drawable.ic_menu_close_clear_cancel, {(activity as DashboardInterface).endGroup()})
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

    private fun setFabButton(backgroundTint : String, text: Int, resourceId : Int, ocl : View.OnClickListener){
        createFab.backgroundTintList = ColorStateList.valueOf(Color.parseColor(backgroundTint))
        createFab.text = resources.getString(text)
        createFab.setIconResource(resourceId)
        createFab.setOnClickListener(ocl)
    }

    interface DashboardInterface {
        fun logout()
        fun createGroup()
        fun endGroup()
        fun leaveGroup()
        fun joinGroup()
    }

}