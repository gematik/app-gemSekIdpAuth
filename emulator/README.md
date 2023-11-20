# Integration environment

Both scripts `createEmulator.sh` und `configureEmulator.sh` builds the integration environment 
that exists of an emulator on which gsia and a DiGA is pre-installed. With this emulator it is 
possible to observe the authentication process as defined in App-App-Flow via gemSekIdp.

## Requirements

In order to get the environment running it is mandatory to have
 - JDK-8
 - JDK-17
 - Android SDK

installed. Paths of Java versions needs to be entered in `createEmulator.sh` und `configureEmulator.sh`.
Path to Android SDK and location of your emulators need to be entered in `createEmulator.sh`

## Use of custom DiGA in integration environment

We provide the possibility to pre-install the DiGA like gsia. To make use of this feature you need to
put the directory and the filename of your already compiled DiGA into `configureEmulator.sh` in 
`GIGA_PATH` and `DIGA_NAME` respectively. Leave `INSTALL_CUSTOM_DIGA=1` to install your own DiGA. 

## Start of integration environment

By executing `createEmulator.sh` the emulator is created. It'll take 1-2 min until the emulator is 
ready to be used.

## Annotation

The integration environment is far from perfect and just serves the purpose to provide a quick
playground for testing the behavior of a DiGA and gsia.