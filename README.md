Branchster-Android-Demo-Application
==================
This is an example Application demonstrating Android mobile deep linking / deeplinking using Branch SDK (Branch Metrics). Use this App to create and share Monsters.

## Installing and running App directly
You can run the App directly by downloading and installing the app-release.apk file following the steps below:

1. Download **app-release.apk** file which exists in the *app/release* folder.
2. Install using the below adb command
adb install -r app-release.apk

## Configuring Keys and Secrets
This repository does not contain API keys so you need to define your own in order for the connected APIs to function. With the exception of the *Crashlytics ApiKey* (see the note below) the keys are defined as XML string resources and referenced at build-time. If you build the project as-is, you will get something like the following error:

```
Error: .. No resource found that matches the given name (at 'value' with value '@string/..').
```

To set up your own API keys and get rid of this error:

1. Open up **api_keys.xml** which exists in the */res/values* folder.
2. Insert your Branch App Key, Facebook ID and Twitter key/secret in this file.
3. Clean/Rebuild your project.

```XML
<?xml version="1.0" encoding="utf-8"?>
<resources>

<!--
Your Branch App Key Goes Here
If you don't have one, see the Branch Android Quick-Start for how to get one:
https://github.com/BranchMetrics/Branch-Integration-Guides/blob/master/android-quick-start.md
-->
<string name="bnc_app_key">YOUR BRANCH APP KEY</string>

<!--
Your Your Facebook App ID Goes Here
If you don't have one, see the Facebook SDK for Android documentation:
https://developers.facebook.com/docs/android
-->
<string name="facebook_app_id">YOUR FACEBOOK APP ID</string>

<!--
Your Twitter Key and Secret Goes Here
If you don't have these, see the Twitter Kit for Android documentation:
https://dev.twitter.com/twitter-kit/android
-->
<string name="twitter_key">YOUR TWITTER APP KEY</string>
<string name="twitter_secret">YOUR TWITTER APP SECRET</string>

</resources>
```

