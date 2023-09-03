package mogware

import com.github.kwhat.jnativehook.GlobalScreen
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener
import kotlinx.coroutines.*
import java.awt.Desktop
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


// Turn on / off printing of program output
const val printOutput: Boolean = true
// Turn on / off printing of logging prompt
const val printLoggingPrompt: Boolean = true
// Turn on / off printing of error messages on program exit
const val printErrorMessage: Boolean = true

// Important variable - First time setup prompt  (Enable or disable logging prompt) - true by default, false allows running program first time without UI     -     Default logging is turned ON without prompt!
const val allowPrompt: Boolean = false

// Allow program to prevent sleep mode
const val allowPreventSleep: Boolean = true
const val preventSleepTimer: Long = 30000L

// Enables logging into a file: (yes / no) - Default: YES (Change in config file)
var enableLogging: String /* yes / no */ = "yes"

//  Change mouse position X Y here (First number)                                                                                                                                     !!! Important value
const val positionHorizontal: Int = 400
const val positionVertical: Int = 400
const val checkForMouseMovementDelay: Long = 10L    // Delay for checking for mouse movement (optimization, limiting resource usage) in Milliseconds (Must be a Long value - include L behind value)<<<

// Exception
var exception: String = ""


var file: File = File("empty") // Logging enabled by default
val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
val createFile = File("${System.getProperty("user.home")}\\MouseMovementLog.txt")


fun main() {

    val checkConfig: String = checkConfig(createFile)


    if (allowPreventSleep && printOutput) println("Program is preventing sleep!")

    if (checkConfig == "null") {
        if (allowPrompt) {
            if (printLoggingPrompt) println("Do you want to log activity in user directory file MouseMovementLog.txt? (First time configuration - Configurable in log file) -  yes / no:")
            if (readlnOrNull() != null && readln().lowercase().contains("yes")) {
                enableLogging = "yes"
            }
        }
    }


    val date = Date()
    val startText = "\n\n\n---- Start of program  |  $date ---- Time: Hour.Minute.Second\n\n"

    val writer = FileWriter(createFile, true).use {writer ->
        when (checkConfig) {
            "null" -> {
                if (printOutput) println("Logging into a file is enabled ($createFile)")
                if (createFile.isFile && !createFile.exists()) {
                    try {
                        createFile.createNewFile()
                    }catch (e: Exception) {
                         exception = e.stackTraceToString()
                        if (printOutput) println("There was an error creating file \"$createFile\"! Close the program to print the full message.")
                    }
                }
                writer.write("Configuration (Allow / disallow logging movements into THIS file) -> allowLogging:yes$startText")
                file = createFile
                enableLogging = "yes"

            }

            "no" -> {
                if (printOutput) println("Logging is disabled! Enable in ${System.getProperty("user.home")}\\MouseMovementLog.txt")
                enableLogging = "no"

            }

            "yes" -> {
                file = createFile
                writer.write(startText)
                enableLogging = "yes"
                if (printOutput) println("Logging enabled (Change in log file: $createFile)")

            }
        }
    }


    runBlocking {
        mouselockRunner(positionHorizontal, positionVertical)
    }

}


// important variables
var status: String = "stay"
val robot = Robot()
var movements = 0
//


// function to lock the mouse if movement detected.
suspend fun mouselockRunner(posHor: Int, posVer: Int) = runBlocking{

    //println("Ctrl Q")
    GlobalScreen.registerNativeHook()

    GlobalScreen.addNativeKeyListener(GlobalKeyListener())

    robot.mouseMove(positionHorizontal, positionVertical) // Initial movement to prevent false detection

    val preventSleepFunction = GlobalScope.launch(Dispatchers.Default) {
        unsleepDevice()
    }


    // loop that checks for mouse position changes (and if prevent sleep is on)
    if (allowPreventSleep) {

        while (status == "stay") {
            val position: Point = MouseInfo.getPointerInfo().location
            if (position.x != positionHorizontal || position.y != positionVertical) {
                if (position.x != positionHorizontal + 1 || position.y != positionVertical + 1) {
                    centerMouseFunction(position.x, position.y)
                }

            }

            Thread.sleep(checkForMouseMovementDelay)
        }
    } else {
        while (status == "stay") {
            val position: Point = MouseInfo.getPointerInfo().location
            if (position.x != positionHorizontal || position.y != positionVertical) {
                centerMouseFunction(position.x, position.y)
            }
            Thread.sleep(checkForMouseMovementDelay)
        }
    }



}


// function that handles mouse movement
fun centerMouseFunction(posHor: Int, posVer: Int) {
    val date = "   ${Date()}   "

    val warningText = "===== $movements ===    ${java.time.LocalTime.now().hour}.${java.time.LocalTime.now().minute}.${java.time.LocalTime.now().second}    ====="
    if (posHor < 10 || posVer < 10 || posHor > 800 || posVer > 800) {
        robot.mouseMove(positionHorizontal, positionVertical)
        val warningOne = "Device is in sleep mode:  $date  -   positions: $posHor, $posVer"
        if (enableLogging == "yes") {
            CoroutineScope(Dispatchers.IO).launch {

                try {
                    FileWriter(file, true).use {writer ->
                        writer.write("$warningText\n$warningOne\n\n")
                    }
                    println("Logging...")
                } catch (e: Exception) {
                    val message: String = if (!exception.isBlank()) "This is probably because there was an error creating the logfile." else ""
                    if (printErrorMessage && printOutput) println("There was en error writing output into the log file. $message")
                }
            }
            if (printOutput) println("$warningText\n$warningOne")
        }

        return
    }

    movements++
    val warningOne = "Movement detected at: $date"
    val warningTwo = "Positions out of order: X: $posHor, Y: $posVer"
    val warning = "\n$warningText\n$warningOne\n$warningTwo"

    if (enableLogging == "yes") {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                FileWriter(file, true).use {writer ->
                    writer.write("$warning\n\n")
                }
                if (printOutput) println("Logging...")
            } catch (e: Exception) {
                val message: String = if (!exception.isBlank()) "This is probably because there was an error creating the logfile." else ""
                if (printErrorMessage && printOutput) println("There was en error writing output into the log file. $message")
            }
        }
    }

    if (printOutput) println(warning)



    for (i in 1..1000) {
        robot.mouseMove(positionHorizontal, positionVertical)
    }


}



// function to prevent device from sleeping
suspend fun unsleepDevice() {
    if (!allowPreventSleep) return


    while (allowPreventSleep) {
        delay(preventSleepTimer)
        robot.mouseMove(positionHorizontal + 1, positionVertical + 1)
        robot.mouseMove(positionHorizontal, positionVertical)
        if (status != "stay") exitProcess(500)
    }

}



// set global key listener
class GlobalKeyListener : NativeKeyListener {
    override fun nativeKeyPressed(event: NativeKeyEvent) {

        // Exit if user presses Ctrl + Q
        if (event.keyCode == NativeKeyEvent.VC_Q && event.modifiers and NativeKeyEvent.CTRL_MASK != 0) {
            status = "stop"

            GlobalScreen.unregisterNativeHook()

            if (enableLogging.contains("y")) {
                try {
                    FileWriter(file, true).use { writer ->
                        writer.write("\n\n -- PROGRAM CLOSED -- ${Date()}\n//////\n")
                    }
                } catch (e: Exception) {
                    val message: String = if (!exception.isBlank()) "This is probably because there was an error creating the logfile." else ""
                    if (printErrorMessage && printOutput) println("There was en error writing output into the log file. $message")
                }
            }

            if (printOutput && printErrorMessage) println(exception)

            exitProcess(418)
        }
        if (event.keyCode == NativeKeyEvent.VC_O && event.modifiers and NativeKeyEvent.CTRL_MASK != 0) {

            val desktop = Desktop.getDesktop()
            desktop.open(file)


        }
    }
}


// function to init read the config
fun checkConfig(file: File): String {
    if (!file.isFile && !file.exists()) {
        return "null"
    }

    val scanner = Scanner(file)
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

