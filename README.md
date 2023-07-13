# MouseLock for windows
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

  
Default bind:                                                                                                                                                                                                                     
Close -> CTRL+Q                                                                                                                                                                                                                     
Open log -> CTRL+O                                                                                                                                                                       
