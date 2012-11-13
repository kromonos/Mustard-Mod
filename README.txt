Mustard MOD
 * 0.4.0
 ** RC3
 *** Some graphical enhancements


Mustard

 * 0.1.8.4
    i. Added a workaround to bypass duplicate notices in timelines
 * 0.1.8.3
    i. Fixed a BUG in alter table OAuth if upgrading from older version
 * 0.1.8.2
    i. Added OAuth Settings form, to manage custom Consumer Keys
    i. Fixed a bug in Mention service
 * 0.1.8.1
    i. Fixed strings in fr and nb translations
 * 0.1.8.0
    i. OAuth Support
       (You don't need to delete the old account, use "switch account" function and it will update the old one)
    i. Now supports statusnet private site
 * 0.1.7.7
    i. New languages added: pt,nb,zh,nl
    i. Added Block user function
    i. Review context menu items order
    i. Added ACTION_SEND for plain/text mime (to share URL)
    i. Added join/leave group function
    i. Added personal timeline auto refresh (Enable it in settings)
    i. Added refresh after post (Enable it in settings)
 * 0.1.7.6
    i. Repeat now can use the dedicated API. (Enable it in settings)
 * 0.1.7.5
    i. Fixed "Login button not visible in Landscape mode" #LP 498512
    i. Fixed "Typo in first Settings option, missing 'b'" #LP 498514
    i. Added new Bookmark functions
 * 0.1.7.4
    i. Fixed an error in User Personal Timeline
    i. Added a service to check statusnet sites version's upgrade 
    i. Added an option to send an anonymous snapshot of number of accounts active
    i. Added a checkbox in create account form to force use of SSL/TLS
    i. Removed sound in new replies notification
 * 0.1.7.3
    i. Rewrite Image downloader, now is Threaded thanks to KWY (from #android-dev)
    i. Fixed search function, now trim search string
    i. Fixed search layout: in landscape "Search" button was hidden
    i. Added avatar upload function. Image is cropped into a square then resized to 500x500
    i. Added Settings. Now you can set how many notices to retrieve
    i. Added flag to disable GeoLocation globally
    i. Added an option to approximate geolocation 
    i. Now when an error occurs while posting notice will be notified with NotificationManager
    i. Disabled sub/unsub context menu item on your notice
    i. s/status/notice
    i. German translation by @eike
    i. French translation by @stemp
    i. Added auto load more notices function (just arrive at the end of the list)
 * 0.1.7.2
    i. Adding icon on notice with geo information and showing it (using geonames.org)
    i. Added User Location in User timeline
 * 0.1.7.1
    i. Fixing typo in parameter name
 * 0.1.7.0
    i. Staring Geo Location support
 * 0.1.6.0
    i. Increasing SOCKET_TIMEOUT in http connection when uploading file
    i. Users personal timeline, replies and favorites 
 * 0.1.5.2
    i. Fixed issue when rotating
    i. Refactor of upload function 
 * 0.1.5.1
    i. Fixed DB create statement
 * 0.1.5.0
    i. Added search tag function
    i. Added sub/unsub - ATTENTION: there is a bug in StatusNet 0.8.2 API returns always NOT FOLLOWING 
    i. Initial view attachment function
    i. Added favorites function
 * 0.1.4.1
	i. Fixed Date parse error. thanks to @jpec, @gdaniele
 * 0.1.4
	i. Added "favorite" menu item in context menu
	i. Fixed bug in search list, username was missing
	i. Added support to attachment
	i. Changed link color to white
	i. Fixed text in About window
	i. Added chars counter in Update Status window
	i. Fixed Login window
 * 0.1.3
	i. Added Search functions
	i. Added About window
