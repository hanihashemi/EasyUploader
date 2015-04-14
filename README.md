# EasyUploader
Easy photo uploader with progress bar :D

I looked, but could not find simple library for android to upload file to Server, So we wrote this library with my friend.

&#x202b;# آپلود فایل آسان
&#x202b;تو اینترنت جستجو کردم اما نتونستم لایبرری خوبی برای آپلود عکس به همراه پراگرس برای اندروید پیدا کنم این شد که با یکی از دوستان اینو نوشتیم و اینجا گذاشتیم تا بقیه هم بتونن استفاده کنند.

## Server code

PHP:
```php
<?php
	$body = @file_get_contents('php://input');
	$file = 'uploaded_file';
	file_put_contents($file, $body);

	echo "file uploaded, url is: http://127.0.0.1/uploaded_file";
?>
```

## Screenshot

![Easy Uploader](https://github.com/hanihashemi/EasyUploader/blob/master/images/screenshot.png)
