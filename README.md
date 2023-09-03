# MouseLock for windows  (might work on linux to some extent)                                                                                                                                                                                                                                                    
#### Locks cursor in place, logs mouse movements and prevents device sleep (default actions, changable easily with top level values - must rebuild with command below)

Also knows when device goes to sleep mode and prevents movement (only if prevent sleep mode is off)

<br />

Keybinds:
- **Exit program:** ㅤ*CTRL + Q*
- **Open Log file:** ㅤ*CTRL + O*
<br />
How to customize: <br /> -> Edit top level values and build with the following command ():

```gradle
gradle build ShadowJar
```
<br />

Editable values (All ON by default):
- Printing of program output (Movement logging in console, etc.)
- Printing of prompt for logging (Asking whether you want to log outputs into log file  -  default logging is turned on) - not recommended to turn off (runs only once if you create logfile as well)
- Printing of error message when program turns off <br />
- Allow Prompt: ON by default, asks (first time running) if user wants to log into log file
- Allow preventing device sleep (automatic micro mouse movements)
- Prevent sleep Timer: How often to prevent sleep (unimportant value, default: 30,000 MS) <br />
- Enable Logging: Main value to enable or disable logging to logfile
- Log file location - default value: users/yourUsername/MouseMovementLog.txt <br />
- Position (horizontal, vertical): Pixel position to lock mouse at
- Check for mouse movement delay: Default 10ms to check for mouse movement
