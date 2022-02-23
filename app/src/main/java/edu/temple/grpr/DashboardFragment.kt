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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject

class DashboardFragment : Fragment(){

    val grPrViewModel: GrPrViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GrPrViewModel::class.java)
    }

    lateinit var createFab : ExtendedFloatingActionButton
    lateinit var joinFab : FloatingActionButton
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
        joinFab = layout.findViewById(R.id.joinFab)

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

        joinFab.setOnClickListener{
            Navigation.findNavController(layout)
                .navigate(R.id.action_dashboardFragment_to_groupFragment)
        }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if group is active, change UI depending on that
        grPrViewModel.getGroupId().observe(requireActivity()) { groupId ->
            if(groupId.isNullOrEmpty()) {
                setExtendedFabButton(
                    getString(R.string.entrance_color),
                    R.string.create,
                    R.drawable.ic_baseline_group_add_24,
                    { (activity as DashboardInterface).createGroup() })
            }else {
//                if(Helper.user.getCreatorStatus(requireContext()) == false)
                    setExtendedFabButton(
                        getString(R.string.exit_color),
                        R.string.end,
                        android.R.drawable.ic_menu_close_clear_cancel,
                        { (activity as DashboardInterface).endGroup() })
            }
        }

//        grPrViewModel.getCreatorStatus().observe(requireActivity()) { status ->
//            if(status == true){
//                setFabButton(getString(R.string.exit_color), R.drawable.ic_baseline_clear_24, {(activity as DashboardInterface).leaveGroup()})
//            }
//            else{
//                setFabButton(getString(R.string.entrance_color), R.drawable.ic_baseline_group_add_24, {Navigation.findNavController(layout)
//                    .navigate(R.id.action_dashboardFragment_to_groupFragment)})
//            }
//        }
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

        return false
    }

    private fun setExtendedFabButton(backgroundTint : String, text: Int, resourceId : Int, ocl : View.OnClickListener){
        createFab.backgroundTintList = ColorStateList.valueOf(Color.parseColor(backgroundTint))
        createFab.text = resources.getString(text)
        createFab.setIconResource(resourceId)
        createFab.setOnClickListener(ocl)
    }

    private fun setFabButton(backgroundTint: String, resourceId: Int, ocl : View.OnClickListener){
        joinFab.backgroundTintList  = ColorStateList.valueOf(Color.parseColor(backgroundTint))
        joinFab.setImageResource(resourceId)
        joinFab.setOnClickListener(ocl)
    }

    interface DashboardInterface {
        fun logout()
        fun createGroup()
        fun endGroup()
        fun leaveGroup()
    }

}