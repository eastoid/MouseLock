# MouseLock for windows
gonna rewrite
Issue: Doesnt lock upon waking pc up (sometimes..?)
> Might work on Linux who knows
### = Locks cursor in place, logs mouse movements =
Build:
```gradle
gradle build ShadowJar                                                                                    
```

---

> (CPU 'optimized' version, all delays and important variables are highlited at the top of the file)
+ Default delay: Cursor pos check: 10ms, Loop when movement detected: 10,000 cycles                                                                                            
+ Writes to userprofile\MouseMovementLog.txt                                                                                             
+ Allows disabling logging into a file
+ Administrator window in focus blocks mouse robot. Run .jar file as administrator to prevent robot fails.
  
Default bind:                                                                                                                                                                                                                     
Close -> CTRL+Q                                                                                                                                                                                                                     
Open log -> CTRL+O                                                                                                                                                                       

