package cc.yamyam;

public class FormActivity  extends BaseActivity {

    /*
    protected ListView list;
    protected FormListViewAdapter listAdapter;
    protected ArrayList<FormElement> formElements = new ArrayList<FormElement>();

    protected ArrayList<MenuItem> actionButtons = new ArrayList<MenuItem>();

    private String formAction = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        list = (ListView)findViewById(R.id.list);
        listAdapter = new FormListViewAdapter(this, formElements);
        list.setAdapter(listAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:	finish(); return true;
            case R.id.action_save: saveForm(); break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.form, menu);

        actionButtons.add(menu.findItem(R.id.action_save));

        return super.onCreateOptionsMenu(menu);
    }

    private void showActionButtons(Boolean show) {
        for(MenuItem mi : actionButtons) {
            mi.setVisible(show);
        }
    }

    protected void saveForm() {

        setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
        showActionButtons(false);

        HTTPRequest request = new HTTPRequest(formAction);

        for(FormElement fe : formElements) {

            try {


                if(fe.getValue() instanceof Bitmap) {
                    request.addImageValue("ff_"+fe.getId(), (Bitmap)fe.getValue());
                } else if(fe.getValue() instanceof String) {
                    String value = (fe.getValue()==null)?"":fe.getValue().toString();
                    request.addPostValue("ff_"+fe.getId(), value);
                    Utils.log("ff_"+fe.getId()+" "+value);
                } else if(fe.getValue() instanceof Category) {
                    Category c = (Category)fe.getValue();
                    if(fe.getSimple()) {
                        request.addPostValue("ff_"+fe.getId(), c.getKey());
                        Utils.log("ff_"+fe.getId()+" "+c.getKey());
                    } else {
                        if(c.getParent() == null) {
                            request.addPostValue("ff_category", c.getKey());
                            request.addPostValue("ff_subcategory", "");
                        } else {
                            request.addPostValue("ff_category", c.getParent().getKey());
                            request.addPostValue("ff_subcategory", c.getKey());
                        }
                    }
                } else if(fe.getValue() instanceof Integer) {
                    String value = fe.getValue().toString();
                    request.addPostValue("ff_"+fe.getId(), value);
                    Utils.log("ff_"+fe.getId()+" "+value);
                } else if(fe.getValue() instanceof FormSelection) {
                    FormSelection value = (FormSelection) fe.getValue();
                    request.addPostValue("ff_"+fe.getId(), value.getKey());
                }



            } catch(Exception e) {
                // only valid pairs
            }
        }
        request.setListener(new HTTPRequestListener() {
            @Override
            public void onHTTPRequestFinished(JSONObject response, int tag) {

                try {

                    Utils.log(response.toString());

                    if(response.getString("code").equals("OK")) {

                        if(response.has("content")) {
                            if(response.getJSONObject("content").has("formerror")) {
                                Object errorObject = response.getJSONObject("content").get("formerror");
                                if(errorObject instanceof JSONArray) {
                                    JSONArray errorArray = (JSONArray)errorObject;
                                    if(errorArray.length() == 0) {
                                        success();
                                        return;
                                    }
                                }
                                fail(response);
                                return;
                            }
                        }
                        success();

                    } else {
                        fail(response);
                    }

                } catch(Exception e) {
                    fail(null);
                }

            }

            @Override
            public void onHTTPRequestFailed(int tag) {
                fail(null);
            }
        });
        request.start();
    }

    protected void success() {
        setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        showActionButtons(true);
        onSuccess();
        setResult(RESULT_SETTINGS_OK);
        finish();
    }

    protected void onSuccess() {
        Toast.makeText(this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
    }

    protected void fail(JSONObject res) {
        setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        showActionButtons(true);

        onFail(res);

    }

    protected void onFail(JSONObject res) {
        Toast.makeText(this, getString(R.string.save_error), Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int req, int res, Intent data) {

        if (req >= REQUEST_PHOTO_FROM_CAMERA && res == RESULT_OK) {

            try {

                Uri file = Uri.fromFile(Utils.getTemporaryFile(IMAGE_TMP_NAME));

                getContentResolver().notifyChange(file, null);
                ContentResolver cr = getContentResolver();
                Bitmap image = android.provider.MediaStore.Images.Media.getBitmap(cr, file);

                File f = new File(file.getPath());
                f.delete();

                setImage(image, req-REQUEST_PHOTO_FROM_CAMERA);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (req >= REQUEST_PHOTO_FROM_GALERIE && res == RESULT_OK && data != null) {
            try {
                Uri selectedImage = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTempStorage = new byte[16 * 1024];
                Bitmap image = BitmapFactory.decodeStream(imageStream);

                setImage(image, req-REQUEST_PHOTO_FROM_GALERIE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setImage(Bitmap image, int position) {

        image = Utils.scaleImage(image);
        formElements.get(position).setValue(image);

        listAdapter.notifyDataSetChanged();

    }

    public String getFormAction() {
        return formAction;
    }

    public void setFormAction(String formAction) {
        this.formAction = formAction;
    }


*/

}

