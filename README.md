# EasyUploader
Easy photo uploader with progress bar :D

I looked, but could not find simple library for android to upload file to Server, So we wrote this library with my friend.

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

## Screenshot: 

![Easy Uploader](https://github.com/hanihashemi/EasyUploader/blob/master/images/screenshot.png)
