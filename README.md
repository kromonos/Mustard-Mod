# Mustard Mod #
- 2012-12-08
	* Simple toastie fix
	* Finished todo in MustardBaseActivity, for highlighting dents where username is called
	* Added translation option for spam report.
	* Fixed a bug in spam reporting option
	* Small "fix" in strings
	* Added option to include/exclude user id of spammer in spamreport
- 2012-12-07
	* Implemented "Reply All" option
	* Changed font from Roboto-Regular to Roboto-Light
	* Changed default font size to normal instead of small
	* Fixed german translation
	* Option to "disable" URL shortener
- 2012-12-06
	* New font sizes
- 2012-12-04
	* New options: Ask for block a user, ask for repeat a dent
- 2012-12-02
	* New spam report options
	* New option: Repeat + Fav will Repeat and Fav the dent with only one click
- 2012-11-30
	* graphical adjustments
	* options for own consumer key, consumer secret, instance (experimental)
	* show/hide source on the bottom of dents option 
	  (Settings -> Global Settings -> "Display source in dents")
- 2012-11-26
	* Fixed missing string in english language. Thanks to @macno, 
	  @thelovebug and all other tester with such great patience
- 2012-11-25
	* replies to account will now be marked
	* more graphical adjustments
	* fixed crash in conversation view
	* More translations
- 2012-11-24
	* additional german translations
	* some graphical adjustments
	* changed layout of dents
- 2012-11-23
	* Some graphical adjustments
- 2012-11-22
	* Some graphical enhancements
	* Some tries to fix themeing
- 0.4.0-RC4 2012-11-17
	* Rename App Namespace to make it play store ready
	* Changed visibility of send button from IfRoom to always
- 0.4.0-RC4 2012-11-16
	* Some graphical enhancements. 
	* New notification icon, with correct dimensions for l/m/h/xh/xxhdpi resolutions
- 0.4.0-RC3 Some graphical enhancements


# Mustard #

 * 0.1.8.4 Added a workaround to bypass duplicate notices in timelines
 * 0.1.8.3 Fixed a BUG in alter table OAuth if upgrading from older version
 * 0.1.8.2
   * Added OAuth Settings form, to manage custom Consumer Keys
   * Fixed a bug in Mention service
 * 0.1.8.1 Fixed strings in fr and nb translations
 * 0.1.8.0
   * OAuth Support
       (You don't need to delete the old account, use "switch account" function and it will update the old one)
   * Now supports statusnet private site
 * 0.1.7.7
    * New languages added: pt,nb,zh,nl
    * Added Block user function
    * Review context menu items order
    * Added ACTION_SEND for plain/text mime (to share URL)
    * Added join/leave group function
    * Added personal timeline auto refresh (Enable it in settings)
    * Added refresh after post (Enable it in settings)
 * 0.1.7.6 Repeat now can use the dedicated API. (Enable it in settings)
 * 0.1.7.5
    * Fixed "Login button not visible in Landscape mode" #LP 498512
    * Fixed "Typo in first Settings option, missing 'b'" #LP 498514
    * Added new Bookmark functions
 * 0.1.7.4
    * Fixed an error in User Personal Timeline
    * Added a service to check statusnet sites version's upgrade 
    * Added an option to send an anonymous snapshot of number of accounts active
    * Added a checkbox in create account form to force use of SSL/TLS
    * Removed sound in new replies notification
 * 0.1.7.3
    * Rewrite Image downloader, now is Threaded thanks to KWY (from #android-dev)
    * Fixed search function, now trim search string
    * Fixed search layout: in landscape "Search" button was hidden
    * Added avatar upload function. Image is cropped into a square then resized to 500x500
    * Added Settings. Now you can set how many notices to retrieve
    * Added flag to disable GeoLocation globally
    * Added an option to approximate geolocation 
    * Now when an error occurs while posting notice will be notified with NotificationManager
    * Disabled sub/unsub context menu item on your notice
    * s/status/notice
    * German translation by @eike
    * French translation by @stemp
    * Added auto load more notices function (just arrive at the end of the list)
 * 0.1.7.2
    * Adding icon on notice with geo information and showing it (using geonames.org)
    * Added User Location in User timeline
 * 0.1.7.1 Fixing typo in parameter name
 * 0.1.7.0 Staring Geo Location support
 * 0.1.6.0
    * Increasing SOCKET_TIMEOUT in http connection when uploading file
    * Users personal timeline, replies and favorites 
 * 0.1.5.2
    * Fixed issue when rotating
    * Refactor of upload function 
 * 0.1.5.1 Fixed DB create statement
 * 0.1.5.0
    * Added search tag function
    * Added sub/unsub - ATTENTION: there is a bug in StatusNet 0.8.2 API returns always NOT FOLLOWING 
    * Initial view attachment function
    * Added favorites function
 * 0.1.4.1 Fixed Date parse error. thanks to @jpec, @gdaniele
 * 0.1.4
	* Added "favorite" menu item in context menu
	* Fixed bug in search list, username was missing
	* Added support to attachment
	* Changed link color to white
	* Fixed text in About window
	* Added chars counter in Update Status window
	* Fixed Login window
 * 0.1.3
	* Added Search functions
	* Added About window
