package mogware

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess




/*

    Gonna refactor code eventually
    for now enjoy true italian spaghetti




*/







// Important variable - First time setup prompt  (Enable or disable logging prompt) - true by default, false allows running program first time without UI     -     Default logging is turned ON without prompt!
val allowPrompt = false

// Enables logging into a file: (yes / no) - Default: YES (Change in config file)
var enableLogging = "yes"

// Change mouse position X Y here: >>
val positionHorizontal = 400
val positionVertical = 400
val checkForMouseMovementDelay = 10L    // Delay for checking for mouse movement (optimization, limiting resource usage) in Milliseconds (Must be a Long value - include L behind value)<<<
//                                 <<

var file = File("empty") // Logging enabled by default
val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss");




fun main() {

    val createFile = File("${System.getProperty("user.home")}\\MouseMovementLog.txt")
    val checkConfig = checkConfig(createFile)


    if (checkConfig == "null") {
        if (allowPrompt) {
            println("Do you want to log activity in user directory file MouseMovementLog.txt? (First time configuration - Configurable in log file) -  yes / no:")
            if (readLine() != null && readLine()!!.lowercase().contains("yes")) {
                enableLogging = "yes"
            }
        }
    }



    val date: Date = Date()
    val startText = "\n\n\n---- Start of program  |  $date ---- Time: Hour.Minute.Second\n\n"
    val writer = FileWriter(createFile, true)

    when (checkConfig) {
        "null" -> {
            println("Logging into a file is enabled ($createFile)")
            if (createFile.isFile && !createFile.exists()) { createFile.createNewFile() }
            writer.write("Configuration (Allow / disallow logging movements into THIS file) -> allowLogging:yes$startText")
            file = createFile
            enableLogging = "yes"

        }
        "no" -> {
            println("Logging is disabled! Enable in ${System.getProperty("user.home")}\\MouseMovementLog.txt")
            enableLogging = "no"

        }
        "yes" -> {
            file = createFile
            writer.write(startText)
            enableLogging = "yes"
            println("Logging enabled (Change in log file: $createFile)")

        }
    }

    writer.close()

    useless(positionHorizontal, positionVertical)

}


var status: String = "stay"
val robot = Robot()
var movements = 0

fun useless(posHor: Int, posVer: Int) {
    //println("Ctrl Q")
    GlobalScreen.registerNativeHook()

    GlobalScreen.addNativeKeyListener(GlobalKeyListener())

    robot.mouseMove(positionHorizontal, positionVertical) // Initial movement to prevent false detection


    // >> Get screen size (not utilized)
//    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
//    val width = screenSize.getWidth()
//    val height = screenSize.getHeight()
    // << Can use for different purpose


    /** Delayed loop to check for mouse movement */
    while (status == "stay") {
        val position: Point = MouseInfo.getPointerInfo().location
        if (position.x != positionHorizontal || position.y != positionVertical) {

            centerMouseFunction(position.x, position.y)
        }

        Thread.sleep(checkForMouseMovementDelay)
    }


}

/** Function to handle mouse movement */
fun centerMouseFunction(posHor: Int, posVer: Int) {
    val date = "   ${Date()}   "

    val warningText = "===== $movements ===    ${java.time.LocalTime.now().hour}.${java.time.LocalTime.now().minute}.${java.time.LocalTime.now().second}    ====="
    if (posHor < 10 || posVer < 10 || posHor > 800 || posVer > 800) {
        robot.mouseMove(positionHorizontal, positionVertical)
        val warningOne = "Device is in sleep mode:  $date  -   positions: $posHor, $posVer"
        if (enableLogging == "yes") {
            CoroutineScope(Dispatchers.IO).launch {
                println("Logging...")
                val writer = FileWriter(file, true)
                writer.write("$warningText\n$warningOne\n\n")
                writer.close()
            }
            println("$warningText\n$warningOne")
        }

        return
    }

    movements++
    val warningOne = "Movement detected at: $date"
    val warningTwo = "Positions out of order: X: $posHor, Y: $posVer"
    val warning = "\n$warningText\n$warningOne\n$warningTwo"

    if (enableLogging == "yes") {
        CoroutineScope(Dispatchers.IO).launch {
            println("Logging...")
            val writer = FileWriter(file, true)
            writer.write("$warning\n\n")
            writer.close()
        }
    }

    println(warning)

    for (i in 1..10000) {
        //println("Testing purpose: i: '$i' - After movement loop (10,000 passes)")   << Print every movement pass after mouse detection is triggered - 10,000 passes by default
        robot.mouseMove(positionHorizontal, positionVertical)
    }
    println("- Movement timeout - ")


}




/** Function for global key listening to exit program (default CTRL+Q) */
class GlobalKeyListener : NativeKeyListener {
    override fun nativeKeyPressed(event: NativeKeyEvent) {
        // Exit if user presses Ctrl + Q
        if (event.keyCode == NativeKeyEvent.VC_Q && event.modifiers and NativeKeyEvent.CTRL_MASK != 0) {
            status = "stop"
            GlobalScreen.unregisterNativeHook()
            val writer = FileWriter(file, true)
            writer.write("\n\n -- PROGRAM CLOSED -- ${Date()}\n//////\n")
            writer.close()
            exitProcess(69)
        }
        if (event.keyCode == NativeKeyEvent.VC_O && event.modifiers and NativeKeyEvent.CTRL_MASK != 0) {

            val desktop = Desktop.getDesktop()
            desktop.open(file)


        }
    }
}


fun checkConfig(file: File): String {
    if (!file.isFile && !file.exists()) {
        return "null"
    }

    val scanner = Scanner(file)
//    val list = mutableListOf<String>()
    while (scanner.hasNext()) {
        val line = scanner.nextLine()
        return if (line.contains("yes")) {
            scanner.close()
            "yes"
        } else if (line.contains("no")) {
            scanner.close()
            "no"
        } else continue
    }

    return "null"

}

















