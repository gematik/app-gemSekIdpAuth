# Release 2.1.22
- clean up repository

# Release 2.1.21
- add binary of GSIA from appcenter to GitHub

# Release 2.1.19
- improved UI and Error Handling
- bug fixed: saved kvnr was not loaded correctly

# Release 2.1.18
- trim X-Auth Header when set

# Release 2.1.17
- improve UI
- save kvnr persistently in shared preferences
- error handling now includes Toasts
- stricter exception handling for http requests
- gesundheitsID Icon now in GSIA

# Release 2.1.16
- add iOS support

# Release 2.1.14
- add support for local development setup

# Release 2.1.11
- add layout for manual start of GSIA
- X-Auth Key can be set if GSIA is started manually

# Release 2.1.10
- better user feedback

# Release 2.1.8
- new signing key -> update required; older version no longer working

# Release 2.1.6
- crashes that occur after authentication are fixed
- select claims works again
- in case no claim is selected / kvnr is changed gemSekIdp won't send AuthCode to gemSekIdpAuth this will be shown in the logs

# Release 2.1.5
- fixes nullpointerexception on empty intent

# Release 2.1.4
- GSIA can handle request_uri + user_id

# Release 2.1.2
- signing of GSIA fixed. Older Version cannot be updated by new versions. That's fixed for GSIA >= 2.1.2

# Release 2.1.1
- top and bottom UI elements have constant size, list of claims fills the rest
- list of claims is scrollable

# Release 2.1.0
- add list of KVNR that are known to sekIDP
- X-Authorization key has to be entered inside of GSIA
- after entry X-Authorization key is saved locally on device
- display list of claims received by sekIDP
- claim can be selected individually

# Release 2.0.3
- iOS Support
- multiple test identities
- better logging

# Release 1.0.7

- GSIA GitHub Release