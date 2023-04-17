## Mapbox SDK fix for Android 14


Currently there is a crash in Android 14, when the map comes from background to the foreground the location, in the `LatLngEvaluator.java`  when the `evaluate` function is called, sometimes the fraction variable is null, then internally the computation with the current lat/lng will be a NaN and Mapbox will throw an exception.
This repo fix the issue when the `fraction` is NaN then set the fraction to 0,  this is a workarond until mapbox fix the issue, however since this is happening for Maps version 9.x.x they might not fix the issue because the repo is in maintenance mode. (in favor to Mapbox maps v10).

this is a fork from https://github.com/mapbox/mapbox-gl-native-android and I removed the test (to not use jetifier due roboelectric), and removed some code that might not be needed. I updated some mapbox core services and telemetry.
There is a dummy apk key, this key will not probably work since was just for the jitpack.io build, and I did not want to integrate the CI for a easy fix.
Original issue is here: https://github.com/mapbox/mapbox-maps-android/issues/2059
I also created a PR to solve the issue but it might take months/years/never to be merged by the mapbox team
PR. https://github.com/mapbox/mapbox-gl-native-android/pull/747

stacktrace of the error
```
java.lang.IllegalArgumentException: latitude must not be NaN
	at com.mapbox.mapboxsdk.geometry.LatLng.setLatitude(LatLng.java:129)
	at com.mapbox.mapboxsdk.location.LatLngEvaluator.evaluate(LatLngEvaluator.java:15)
	at com.mapbox.mapboxsdk.location.LatLngEvaluator.evaluate(LatLngEvaluator.java:8)
	at android.animation.KeyframeSet.getValue(KeyframeSet.java:202)
	at android.animation.PropertyValuesHolder.calculateValue(PropertyValuesHolder.java:1017)
	at android.animation.ValueAnimator.animateValue(ValueAnimator.java:1698)
	at android.animation.ValueAnimator.animateValuesInRange(ValueAnimator.java:1512)
	at android.animation.AnimatorSet.animateValuesInRange(AnimatorSet.java:1025)
	at android.animation.AnimatorSet.animateBasedOnPlayTime(AnimatorSet.java:883)
	at android.animation.AnimatorSet.initChildren(AnimatorSet.java:1164)
	at android.animation.AnimatorSet.startAnimation(AnimatorSet.java:1384)
	at android.animation.AnimatorSet.start(AnimatorSet.java:741)
	at android.animation.AnimatorSet.start(AnimatorSet.java:696)
	at com.mapbox.mapboxsdk.location.MapboxAnimatorSetProvider.startAnimation(MapboxAnimatorSetProvider.java:31)
	at com.mapbox.mapboxsdk.location.LocationAnimatorCoordinator.playAnimators(LocationAnimatorCoordinator.java:383)
	at com.mapbox.mapboxsdk.location.LocationAnimatorCoordinator.feedNewLocation(LocationAnimatorCoordinator.java:140)
	at com.mapbox.mapboxsdk.location.LocationComponent.updateLocation(LocationComponent.java:1622)
	at com.mapbox.mapboxsdk.location.LocationComponent.updateLocation(LocationComponent.java:1593)
	at com.mapbox.mapboxsdk.location.LocationComponent.setLastLocation(LocationComponent.java:1663)
	at com.mapbox.mapboxsdk.location.LocationComponent.onLocationLayerStart(LocationComponent.java:1451)
	at com.mapbox.mapboxsdk.location.LocationComponent.onStart(LocationComponent.java:1377)
	at com.mapbox.mapboxsdk.maps.MapboxMap.onStart(MapboxMap.java:149)
	at com.mapbox.mapboxsdk.maps.MapView.onStart(MapView.java:397)
	at app.ui.main.maps.mapsv3.MapboxMapFragment.onStart(MapboxMapFragment.kt:224)
	at androidx.fragment.app.Fragment.performStart(Fragment.java:3162)
	at androidx.fragment.app.FragmentStateManager.start(FragmentStateManager.java:588)
	at androidx.fragment.app.FragmentStateManager.moveToExpectedState(FragmentStateManager.java:279)
	at androidx.fragment.app.FragmentStore.moveToExpectedState(FragmentStore.java:113)
	at androidx.fragment.app.FragmentManager.moveToState(FragmentManager.java:1435)
	at androidx.fragment.app.FragmentManager.dispatchStateChange(FragmentManager.java:2979)
	at androidx.fragment.app.FragmentManager.dispatchStart(FragmentManager.java:2904)
	at androidx.fragment.app.FragmentController.dispatchStart(FragmentController.java:274)
	at androidx.fragment.app.FragmentActivity.onStart(FragmentActivity.java:359)
	at androidx.appcompat.app.AppCompatActivity.onStart(AppCompatActivity.java:251)
	at app.ui.main.MainActivity.onStart(MainActivity.kt:273)
	at android.app.Instrumentation.callActivityOnStart(Instrumentation.java:1581)
	at android.app.Activity.performStart(Activity.java:8628)
	at android.app.ActivityThread.handleStartActivity(ActivityThread.java:3798)
	at android.app.servertransaction.TransactionExecutor.performLifecycleSequence(TransactionExecutor.java:225)
	at android.app.servertransaction.TransactionExecutor.cycleToPath(TransactionExecutor.java:205)
	at android.app.servertransaction.TransactionExecutor.executeLifecycleState(TransactionExecutor.java:177)
	at android.app.servertransaction.TransactionExecutor.execute(TransactionExecutor.java:98)
	at android.app.ActivityThread$H.handleMessage(ActivityThread.java:2436)
	at android.os.Handler.dispatchMessage(Handler.java:106)
	at android.os.Looper.loopOnce(Looper.java:205)
	at android.os.Looper.loop(Looper.java:294)
	at android.app.ActivityThread.main(ActivityThread.java:8180)
	at java.lang.reflect.Method.invoke(Native Method)
2023-04-14 21:04:16.173 15810-15810 AndroidRuntime          com.mytestapp.debug            E  	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:552)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:946)
```
