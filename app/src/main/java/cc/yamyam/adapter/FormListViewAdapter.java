package cc.yamyam.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cc.yamyam.FormActivity;
import cc.yamyam.R;
import cc.yamyam.model.FormElement;
import cc.yamyam.model.FormSelection;

/**
 * Created by siyuan on 10.08.15.
 */
public class FormListViewAdapter extends ArrayAdapter<FormElement> {

    private final FormActivity context;
    private final ArrayList<FormElement> values;

    public FormListViewAdapter(FormActivity context, ArrayList<FormElement> values) {
        super(context, R.layout.form_list_item_checkbox, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        final FormElement fe = values.get(position);

        final View rowView;

        if(fe.getHidden()) {
            rowView = inflater.inflate(R.layout.form_list_item_hidden, parent, false);
            rowView.setVisibility(View.GONE);
        } else if(fe.getType() == FormElement.FORM_ELEMENT_CHECKBOX) {
            rowView = inflater.inflate(R.layout.form_list_item_checkbox, parent, false);
        } else if(fe.getType() == FormElement.FORM_ELEMENT_TEXTFIELD) {
            rowView = inflater.inflate(R.layout.form_list_item_textfield, parent, false);
        } else if(fe.getType() == FormElement.FORM_ELEMENT_SELECT) {
            rowView = inflater.inflate(R.layout.form_list_item_select, parent, false);
        } else if(fe.getType() == FormElement.FORM_ELEMENT_IMAGE) {
            rowView = inflater.inflate(R.layout.form_list_item_image, parent, false);
        } else {
            rowView = inflater.inflate(R.layout.form_list_item, parent, false);
        }


        rowView.findViewById(R.id.value).setTag(fe.getId());
        ((TextView)rowView.findViewById(R.id.label)).setText(fe.getLabel());

        if(fe.getDescription() != null) {
            TextView descriptionView = (TextView)rowView.findViewById(R.id.description);
            descriptionView.setText(fe.getDescription());


            if(fe.getId().equals("accept")) {

                descriptionView.setClickable(true);
                //descriptionView.setTextColor(context.getResources().getColor(R.color.votinion_blue));
                descriptionView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        /*
                        String language = null;
                        for(FormElement f : values) {
                            if(f.getId().equals("language")) {
                                FormSelection s = (FormSelection)f.getValue();
                                if(s != null) {
                                    language = s.getKey();
                                }
                                break;
                            }
                        }

                        Intent i = new Intent(context, StaticContentActivity.class);
                        i.putExtra("content","agb");
                        i.putExtra("language",language);
                        context.startActivity(i);
                        */
                    }

                });
            }

        } else {
            rowView.findViewById(R.id.description).setVisibility(View.GONE);
        }


        if(fe.getType() == FormElement.FORM_ELEMENT_CHECKBOX) {

            //Utils.log("value: "+fe.getValue());

            Integer defaultInt = Integer.valueOf(0);
            if(fe.getValue() != null) {
                defaultInt = (Integer)fe.getValue();
            }

            fe.setValue(defaultInt);

            CheckBox cb = (CheckBox)rowView.findViewById(R.id.value);

            //Utils.log("default value "+defaultInt);

            if(defaultInt == 0) {
                cb.setChecked(false);
            } else {
                cb.setChecked(true);
            }

            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    //fe.setValue((isChecked)?Integer.valueOf(1):Integer.valueOf(0));
                    //Utils.log("set value: "+fe.getValue());
                }
            });
        } else if(fe.getType() == FormElement.FORM_ELEMENT_TEXTFIELD) {
            final EditText et = (EditText)rowView.findViewById(R.id.value);

            if(fe.getSpecificType() == FormElement.FORM_ELEMENT_PASSWORDFIELD) {
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }

            if(fe.getSpecificType() == FormElement.FORM_ELEMENT_TEXTAREA) {
                et.setSingleLine(false);
            }

            if(fe.getValue() != null) {
                String defaultString = (String)fe.getValue();
                et.setText(defaultString);
                fe.setValue(defaultString);
            }

            et.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {}

                @Override
                public void beforeTextChanged(CharSequence s, int start,int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    fe.setValue(s.toString());
                }

            });

        } else if(fe.getSpecificType() == FormElement.FORM_ELEMENT_SELECT) {

            rowView.setClickable(true);
            rowView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    /*
                    FragmentManager fm = context.getSupportFragmentManager();
                    FormSelectionFragment dialog = new FormSelectionFragment();
                    dialog.setElement(fe);
                    dialog.setListener(new FormSelectionListener() {
                        @Override
                        public void onSelected(FormSelection selection) {
                            fe.setValue(selection);
                            ((TextView)rowView.findViewById(R.id.value)).setText(selection.getValue());

                        }
                    });
                    dialog.show(fm, "dialog_selection");*/

                }

            });

            ((TextView)rowView.findViewById(R.id.value)).setText(context.getString(R.string.select));

            if(fe.getValue() != null) {
                FormSelection defaultSelection = (FormSelection)fe.getValue();
                fe.setValue(defaultSelection);
                ((TextView)rowView.findViewById(R.id.value)).setText(defaultSelection.getValue());
            }

        } else if(fe.getSpecificType() == FormElement.FORM_ELEMENT_SELECT_CATEGORY) {
            /*
            rowView.setClickable(true);
            rowView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    CategorySelectionFragment fragment = new CategorySelectionFragment();
                    FragmentManager fm = context.getSupportFragmentManager();

                    ArrayList<Category> categories = Utils.getCategoryList(context);
                    fragment.setCategories(categories);

                    fragment.setListener(new CategorySelectionListener() {
                        @Override
                        public void onSelected(Category category) {
                            fe.setValue(category);

                            String label = category.getLabel();
                            if(category.getParent() != null) {
                                label = category.getParent().getLabel()+" > "+label;
                            }
                            ((TextView)rowView.findViewById(R.id.value)).setText(label);
                        }
                    });
                    fragment.show(fm, "categories");

                }

            });

            ((TextView)rowView.findViewById(R.id.value)).setText(context.getString(R.string.select));

            if(fe.getValue() != null) {
                Category category = (Category)fe.getValue();
                String label = category.getLabel();
                if(category.getParent() != null) {
                    label = category.getParent().getLabel()+" > "+label;
                }
                ((TextView)rowView.findViewById(R.id.value)).setText(label);
            }

            if(fe.getInitSelection()) {
                fe.setInitSelection(false);
                rowView.performClick();
            }
            */
        } else if(fe.getType() == FormElement.FORM_ELEMENT_IMAGE) {

            if(fe.getValue() != null) {
                Bitmap image = (Bitmap)fe.getValue();
                ((ImageView)rowView.findViewById(R.id.image)).setImageBitmap(image);
            } else if(fe.getDefaultValue() != null) {
                Bitmap defaultImage = (Bitmap)fe.getDefaultValue();
                ((ImageView)rowView.findViewById(R.id.image)).setImageBitmap(defaultImage);
            }

            rowView.setClickable(true);

            /*
            rowView.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {

                    final CharSequence[] items = { context.getString(R.string.image_pick), context.getString(R.string.image_camera) };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.setting_picture));
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (which == 0) {
                                Intent pick = new Intent(Intent.ACTION_PICK);
                                pick.setType("image/*");
                                context.startActivityForResult(pick, REQUEST_PHOTO_FROM_GALERIE+position);

                            } else {
                                File photo;
                                try {
                                    photo = Utils.createTemporaryFile(IMAGE_TMP_NAME);
                                } catch (Exception e) {
                                    Utils.logError(e);
                                    return;
                                }

                                Intent cam = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                Uri file = Uri.fromFile(photo);
                                cam.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, file);
                                context.startActivityForResult(cam, REQUEST_PHOTO_FROM_CAMERA+position);
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }



            });
            */

        }

        return rowView;
    }



}
