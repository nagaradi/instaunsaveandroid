# Insta Unsave (Android starter)

A dark-mode Android helper that opens Instagram and lets the user keep a manual count of posts they have unsaved.

## Important limitation
This starter does **not** log in to Instagram, read private Saved posts, or automatically unsave them. Instagram does not provide a general public API for a third-party app to bulk-manage a user's Saved posts. The user must unsave posts in Instagram and tap `+1 post unsaved` here to track progress.

## Build an APK using GitHub Actions
1. Create a GitHub repository and upload the files in this folder, keeping the folder structure.
2. Open **Actions** and select **Build Insta Unsave APK**.
3. Tap **Run workflow** (or push to `main`) and wait for the run to finish green.
4. Open the successful run, scroll to **Artifacts**, and download `InstaUnsave-debug-apk`.
5. Extract the downloaded artifact ZIP and install `app-debug.apk` on your Android phone.

Package: `com.example.instaunsave`; minimum Android: 8.0 (API 26).
