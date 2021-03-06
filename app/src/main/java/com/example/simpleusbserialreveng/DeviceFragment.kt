package com.example.simpleusbserialreveng

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber


class DeviceFragment: Fragment() {
    private lateinit var refreshButton: Button
    private lateinit var connectButton: Button
    private lateinit var connectMessage: TextView


    private var device: UsbDevice? = null
    private var port: UsbSerialPort? = null
    private var driver: UsbSerialDriver? = null
    private val baudRate = 9600

    //interface to be implemented by MainActivity "parent activity"
    interface onTerminalFragmentStarted {
        fun addTerminalFragmentToMenu(fragment: Fragment)
    }

    //callback variable that get context of the main activity to call a function in the main activity
    private var callback: onTerminalFragmentStarted? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as? onTerminalFragmentStarted
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device, container,false)
        refreshButton = view.findViewById(R.id.refreshButton)
        connectButton = view.findViewById(R.id.connectButton)
        connectMessage = view.findViewById(R.id.connect_text)
        return view
    }

    override fun onStart() {
        super.onStart()
        refreshButton.setOnClickListener { refresh()
            if (driver != null) {
                connectButton.isEnabled = true
                connectMessage.text = getString(R.string.connected_message)
            }
        }
        connectButton.setOnClickListener {
            if (driver != null)
            {
                val args = Bundle()
                args.putInt("device", driver!!.device.deviceId)
                args.putInt("port", 0)
                args.putInt("baud", baudRate)
                val fragment: Fragment = TerminalFragment()
                fragment.arguments = args
                //fragmentManager!!.beginTransaction().replace(R.id.fragmentContainer, fragment, "terminal")
                   // .addToBackStack(null).commit()
                //change activeFragment to terminal fragment in mainactivity
                callback?.addTerminalFragmentToMenu(fragment)
            }
        }
    }


    private fun refresh() {
        val usbManager = requireActivity().getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)
        if (availableDrivers.isEmpty()) {
//            deviceName.text = "Not device found"
//            productId.text = "NA"
//            vendorId.text = "NA"
            device = null
            port= null
            connectMessage.text = getString(R.string.diconnected_message)
            return;
        }

        //get the first driver
        driver = availableDrivers[0]
        //get the first port
        port = driver!!.ports[0]
//        deviceName.text = driver!!.javaClass.simpleName
//        productId.text = driver!!.device.deviceId.toString()
//        vendorId.text = driver!!.device.vendorId.toString()
    }
}