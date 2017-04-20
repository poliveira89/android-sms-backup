SMSBackup
=========

Simple tool to extract my received/sent SMS from an Android device.

## Why build it? Motivation...
Because the most known option is open-source but delivers only to E-mail something that I do not wish.
Even being a open source c`ontains a lots of extras instead of forking I developed a simple solution to save these information into the storage of the device.
And then I decide how to share it...

## Permissions

 * `READ_SMS` for the obvious reason to gain acess into the inbox
 * `WRITE_EXTERNAL_STORAGE` / `READ_EXTERNAL_STORAGE` to gain acesso to SD Card and write the export file


## General Details

### Export

The exported file it will be saved on the *Documents* folder of the internal storage

### Format

The format of the file it's CSV to be easily imported on most visualization softwares.
And it's easily readable with a Text editor.

### Emulator

If you are trying this code on a emulator you can extract the file with the following command:
```bash
adb pull /storage/emulated/0/Documents/sms_backup.csv
```