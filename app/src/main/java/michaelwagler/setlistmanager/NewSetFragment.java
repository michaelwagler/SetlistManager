package michaelwagler.setlistmanager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import michaelwagler.setlistmanager.db.DBHelper;
import michaelwagler.setlistmanager.model.Band;
import michaelwagler.setlistmanager.model.Set;
import michaelwagler.setlistmanager.model.Venue;


public class NewSetFragment extends Fragment implements View.OnClickListener{
    private String LOG = "NewSetFragment";
    DBHelper helper;
    Set set;
    final DateTimeFormatter fmtTime = DateTimeFormat.forPattern("h:mmaa");

    TextView currentBand;
    Button changeBand;
    TextView currentVenue;
    Button changeVenue;

    int venueId;
    int bandId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_set, container, false);
        EditText editName = (EditText) v.findViewById(R.id.setNameEditText);
        Button done = (Button) v.findViewById(R.id.doneButton);
        Button cancel = (Button) v.findViewById(R.id.cancelButton);
        Button time = (Button) v.findViewById(R.id.openTimePickerButton);
        TextView setTime = (TextView) v.findViewById(R.id.currentTime);
        DatePicker dPicker = (DatePicker) v.findViewById(R.id.datePicker);

        DateTime now = new DateTime();
        setTime.setText(now.toString(fmtTime));
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        time.setOnClickListener(this);

        currentBand = (TextView) v.findViewById(R.id.currentBand);
        changeBand = (Button) v.findViewById(R.id.changeBandButton);
        currentVenue = (TextView) v.findViewById(R.id.currentVenue);
        changeVenue = (Button) v.findViewById(R.id.changeVenueButton);

        changeBand.setOnClickListener(this);
        changeVenue.setOnClickListener(this);

        currentBand.setText("none");
        currentVenue.setText("none");

        helper = new DBHelper(NewSetFragment.super.getActivity());


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String id_str = bundle.getString("set_id");
            if (id_str != null) {
                // This means we are editing an existing set.
                int setId = Integer.parseInt(id_str);
                done.setText("Update");
                set = helper.getSetById(setId);
                editName.setText(set.getName());
                DateTime setDT = set.getDateTime();
                if (setDT != null) {
                    setTime.setText(setDT.toString(fmtTime));
                    dPicker.updateDate(setDT.getYear(), setDT.getMonthOfYear() - 1, setDT.getDayOfMonth());
                }
                Band band = helper.getBandById(set.getBandId());
                if (band != null) {
                    currentBand.setText(band.getName());
                    bandId = band.getId();
                }
                Venue venue = helper.getVenueById(set.getVenueId());
                if (venue != null) {
                    currentVenue.setText(venue.getName());
                    venueId = venue.getId();
                }
            }
        }


        if (set == null) {
            ActionBar aB = NewSetFragment.super.getActivity().getActionBar();
            aB.setTitle("New Set");
        }


        return v;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(final View v) {

        switch (v.getId()) {
            case R.id.openTimePickerButton:
                TextView text_init = (TextView) ((View) v.getParent()).findViewById(R.id.currentTime);
                DateTime dt_init = DateTime.parse(text_init.getText().toString(), fmtTime);

                TimePickerDialog timeDialog = new TimePickerDialog(this.getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView text = (TextView) ((View) v.getParent()).findViewById(R.id.currentTime);

                        DateTime dt = new DateTime().withHourOfDay(hourOfDay).withMinuteOfHour(minute);
                        text.setText(dt.toString(fmtTime));

                    }
                }, dt_init.getHourOfDay(), dt_init.getMinuteOfHour(), false);
                timeDialog.show();

                break;

            case R.id.changeBandButton:
                AlertDialog.Builder otherBuilder = new AlertDialog.Builder(
                        NewSetFragment.super.getActivity());
                otherBuilder.setTitle("Associate with band");

                final List<Band> bands = helper.getAllBands();
                String[] bandsArray = new String[bands.size()];
                for (int k = 0; k < bands.size(); k++) {
                    bandsArray[k] = bands.get(k).toString();
                }

                otherBuilder.setItems(bandsArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Band b = bands.get(which);
                        currentBand.setText(b.getName());
                        bandId = b.getId();
                    }
                });
                otherBuilder.create().show();
                break;

            case R.id.changeVenueButton:
                // Associate with Venue
                AlertDialog.Builder venueBuilder = new AlertDialog.Builder(
                        NewSetFragment.super.getActivity());
                venueBuilder.setTitle("Associate with venue");

                final List<Venue> venues = helper.getAllVenues();
                String[] venuesArray = new String[venues.size()];
                for (int k = 0; k < venues.size(); k++) {
                    venuesArray[k] = venues.get(k).toString();
                }

                venueBuilder.setItems(venuesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Venue v = venues.get(which);
                        currentVenue.setText(v.getName());
                        venueId = v.getId();
                    }
                });

                venueBuilder.create().show();
                break;

            case R.id.cancelButton:
                getFragmentManager().popBackStack();
                break;
            case R.id.doneButton:
                View parent = (View) v.getParent().getParent();
                String setName = ((TextView) parent.
                        findViewById(R.id.setNameEditText)).getText().toString();
                DateTime setDate;

                String timeString = ((TextView) parent.findViewById(R.id.currentTime)).getText().toString();
                DatePicker dp = ((DatePicker) parent.findViewById(R.id.datePicker));

                setDate = fmtTime.parseDateTime(timeString);

                setDate = setDate.withDate(dp.getYear(), (dp.getMonth() + 1), dp.getDayOfMonth());


                Set testDuplicate = helper.getSetByName(setName);
                if (testDuplicate != null) {
                    // if there is a set with this name already exists and it is not this current set, show warning.
                    if (set == null || testDuplicate.getId() != set.getId()) {
                        // Show dialog if set with this name already exists
                        AlertDialog.Builder warning = new AlertDialog.Builder(
                                NewSetFragment.super.getActivity());
                        warning.setMessage("a set with this name already exists, please choose a different name.");
                        warning.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        warning.create().show();
                        break;
                    }
                }

                if (set != null) {
                    // we are editing an existing set

                    set.setName(setName);
                    set.setDateTime(setDate);
                    set.setVenueId(venueId);
                    set.setBandId(bandId);
                    helper.updateSet(set);
                }
                else {
                    // brand new set
                    Set set = new Set(setName);
                    set.setDateTime(setDate);
                    set.setVenueId(venueId);
                    set.setBandId(bandId);
                    helper.createSet(set);
                }
                // the following code hides the virtual keyboard on "Create";
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                getFragmentManager().popBackStack();
                break;

            default:
                break;
        }

    }
}
