
/*oncapturecamera

        1-)

        String path = Environment.getExternalStorageDirectory()+File.separator+"image.jpg";
        File imgFile = new File(path);
        if(imgFile.exists())
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
            mAttacher.update();
        }

        2-)

        try {
            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

            Bitmap bitmap;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
            imageView.setImageBitmap(bitmap);
            mAttacher.update();
            String path = android.os.Environment.getExternalStorageDirectory() + File.separator + "Phoenix" + File.separator + "default";

            f.delete();

            OutputStream outFile = null;

            File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

            try {

                outFile = new FileOutputStream(file);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);

                outFile.flush();

                outFile.close();

            } catch (FileNotFoundException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

 */

/*
Cameraintent

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, REQUEST_CAMERA);
 */
