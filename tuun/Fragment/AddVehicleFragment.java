package com.penguinsonabeach.tuun.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.penguinsonabeach.tuun.Object.Car;
import com.penguinsonabeach.tuun.R;

/**
 * Created by Phoenix on 3/25/2018.
 */

public class AddVehicleFragment extends Fragment {
    Spinner yearList;
    Spinner makeList;
    Spinner modelList;
    Spinner trimList;
    ArrayAdapter<CharSequence> yearAdapter;
    ArrayAdapter<CharSequence> makeAdapter;
    ArrayAdapter<CharSequence> modelAdapter;
    ArrayAdapter<CharSequence> trimAdapter;
    ImageView makeImageView;
    Button addVehicleButton;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef;
    FirebaseUser gUser;

    public static AddVehicleFragment newInstance(Bundle arguments) {
        AddVehicleFragment garageTab2 = new AddVehicleFragment();
        if (arguments != null) {
            garageTab2.setArguments(arguments);
        }
        return garageTab2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_addvehicle,container,false);
        gUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference("users").child(gUser.getUid()).child("vehicles");

        makeImageView = rootView.findViewById(R.id.addCarImageView);

        yearList = rootView.findViewById(R.id.yearSpinner);
        yearAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.year, R.layout.spinner_item);
        yearAdapter.setDropDownViewResource(R.layout.spinner_item);
        yearList.setAdapter(yearAdapter);

        makeList = rootView.findViewById(R.id.makeSpinner);
        makeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.make, R.layout.spinner_item);
        makeAdapter.setDropDownViewResource(R.layout.spinner_item);
        makeList.setAdapter(makeAdapter);
        makeList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String makeValue = makeList.getSelectedItem().toString();
                modelList = rootView.findViewById(R.id.modelSpinner);
                createModelAdapter(makeValue);
                modelAdapter.setDropDownViewResource(R.layout.spinner_item);
                modelList.setAdapter(modelAdapter);
                modelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String modelValue = modelList.getSelectedItem().toString();
                        trimList = rootView.findViewById(R.id.trimSpinner);
                        createTrimAdapter(modelValue);
                        trimAdapter.setDropDownViewResource(R.layout.spinner_item);
                        trimList.setAdapter(trimAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addVehicleButton = rootView.findViewById(R.id.addCarButton);
        addVehicleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleAddAlert();
            }
        });

        return rootView;
    }


    protected void vehicleAddAlert(){
        final Car lCar = new Car(yearList.getSelectedItem().toString(),makeList.getSelectedItem().toString(),modelList.getSelectedItem().toString());
        checkTrim(lCar);
        String lMessage = getResources().getString(R.string.add_vehicle_message);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.add_vehicle_title);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(lMessage+" "+lCar.toString());
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String lVehicleID = userRef.push().getKey();
                        lCar.setId(lVehicleID);
                        userRef.child(lVehicleID).setValue(lCar);
                        Toast.makeText(getActivity(),lCar.toString()+" added to your garage.",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void createModelAdapter(String make){

        switch(make){
            case "ACURA":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ACURA, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.acura);
                break;
            case "AUDI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.AUDI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.audi);
                break;
            case "BENTLEY":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.BENTLEY, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.bentley);
                break;
            case "BMW":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.BMW, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.bmw);
                break;
            case "BUGATTI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.BUGATTI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.bugatti);
                break;
            case "BUICK":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.BUICK, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.buick);
                break;
            case "CADILLAC":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.CADILLAC, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.cadillac);
                break;
            case "CHEVROLET":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.CHEVROLET, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.chevrolet);
                break;
            case "CHRYSLER":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.CHRYSLER, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.chrysler);
                break;
            case "DATSUN":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.DATSUN, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.datsun);
                break;
            case "DODGE":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.DODGE, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.dodge);
                break;
            case "FERRARI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.FERRARI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.ferrari);
                break;
            case "FIAT":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.FIAT, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.fiat);
                break;
            case "FORD":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.FORD, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.ford);
                break;
            case "HONDA":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.HONDA, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.honda);
                break;
            case "HUMMER":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.HUMMER, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.hummer);
                break;
            case "HYUNDAI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.HYUNDAI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.hyundai);
                break;
            case "INFINITI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.INFINITI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.infiniti);
                break;
            case "JAGUAR":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.JAGUAR, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.jaguar);
                break;
            case "JEEP":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.JEEP, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.jeep);
                break;
            case "KIA":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.KIA, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.kia);
                break;
            case "LAMBORGHINI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.LAMBORGHINI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.lamborghini);
                break;
            case "LAND ROVER":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.LANDROVER, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.landrover);
                break;
            case "LEXUS":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.LEXUS, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.lexus);
                break;
            case "LOTUS":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.LOTUS, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.lotus);
                break;
            case "MASERATI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MASERATI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.maserati);
                break;
            case "MAZDA":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MAZDA, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.mazda);
                break;
            case "MCLAREN":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MCLAREN, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.mclaren);
                break;
            case "MERCEDES-BENZ":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MERCEDESBENZ, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.mercedes);
                break;
            case "MINI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MINI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.mini);
                break;
            case "MITSUBISHI":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MITSUBISHI, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.mitsubishi);
                break;
            case "NISSAN":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.NISSAN, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.nissan);
                break;
            case "PONTIAC":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.PONTIAC, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.pontiac);
                break;
            case "PORSCHE":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.PORSCHE, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.porsche);
                break;
            case "ROLLS-ROYCE":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.ROLLSROYCE, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.rollsroyce);
                break;
            case "SCION":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.SCION, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.scion);
                break;
            case "SUBARU":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.SUBARU, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.subaru);
                break;
            case "TESLA":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.TESLA, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.tesla);
                break;
            case "TOYOTA":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.TOYOTA, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.toyota);
                break;
            case "VOLKSWAGEN":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.VOLKSWAGEN, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.volkswagen);
                break;
            case "VOLVO":
                modelAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.VOLVO, R.layout.spinner_item);
                makeImageView.setImageResource(R.drawable.volvo);
                break;
            default:
                break;



        }

    }

    protected void createTrimAdapter(String model){
        switch(model){
            case "3000 GT":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Mit3000GT, R.layout.spinner_item);
                break;
            case "C30":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.C30, R.layout.spinner_item);
                break;
            case "Cherokee":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cherokee, R.layout.spinner_item);
                break;
            case "300":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cherokee, R.layout.spinner_item);
                break;
            case "Charger":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Charger, R.layout.spinner_item);
                break;
            case "Challenger":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Challenger, R.layout.spinner_item);
                break;
            case "Celica":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Celica, R.layout.spinner_item);
                break;
            case "Civic":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Civic, R.layout.spinner_item);
                break;
            case "CLK-Class":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.CLK, R.layout.spinner_item);
                break;
            case "Camaro":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Camaro, R.layout.spinner_item);
                break;
            case "Cobalt":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cobalt, R.layout.spinner_item);
                break;
            case "Cooper":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Cooper, R.layout.spinner_item);
                break;
            case "Corolla":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Corolla, R.layout.spinner_item);
                break;
            case "CRX":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.CRX, R.layout.spinner_item);
                break;
            case "CTS":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.CTS, R.layout.spinner_item);
                break;
            case "Dart":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Dart, R.layout.spinner_item);
                break;
            case "Del Sol":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.DelSol, R.layout.spinner_item);
                break;
            case "Durango":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Durango, R.layout.spinner_item);
                break;
            case "F-150":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.F150, R.layout.spinner_item);
                break;
            case "Fiesta":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Fiesta, R.layout.spinner_item);
                break;
            case "Focus":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Focus, R.layout.spinner_item);
                break;
            case "500":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Fiat500, R.layout.spinner_item);
                break;
            case "Galant":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Galant, R.layout.spinner_item);
                break;
            case "Genesis Coupe":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Genesis, R.layout.spinner_item);
                break;
            case "Grand Cherokee":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.GrandCherokee, R.layout.spinner_item);
                break;
            case "GS":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.GS, R.layout.spinner_item);
                break;
            case "GT-R":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.GTR, R.layout.spinner_item);
                break;
            case "Impreza":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Impreza, R.layout.spinner_item);
                break;
            case "Integra":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Integra, R.layout.spinner_item);
                break;
            case "IS":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.IS, R.layout.spinner_item);
                break;
            case "Juke":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Juke, R.layout.spinner_item);
                break;
            case "Lancer":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Lancer, R.layout.spinner_item);
                break;
            case "LC":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.LC, R.layout.spinner_item);
                break;
            case "Mazda3":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Mazda3, R.layout.spinner_item);
                break;
            case "Mazda6":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Mazda6, R.layout.spinner_item);
                break;
            case "MR2":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MR2, R.layout.spinner_item);
                break;
            case "MX-5":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.MX5, R.layout.spinner_item);
                break;
            case "Neon":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Neon, R.layout.spinner_item);
                break;
            case "300zx":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Nis300zx, R.layout.spinner_item);
                break;
            case "350z":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Nis350z, R.layout.spinner_item);
                break;
            case "370z":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Nis370z, R.layout.spinner_item);
                break;
            case "911":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Porsche911, R.layout.spinner_item);
                break;
            case "Prelude":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Prelude, R.layout.spinner_item);
                break;
            case "Range Rover":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.RangeRover, R.layout.spinner_item);
                break;
            case "RC":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.RC, R.layout.spinner_item);
                break;
            case "RSX":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.RSX, R.layout.spinner_item);
                break;
            case "S40":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.S40, R.layout.spinner_item);
                break;
            case "S60":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.S60, R.layout.spinner_item);
                break;
            case "SC":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.SC, R.layout.spinner_item);
                break;
            case "SL-Class":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.SL, R.layout.spinner_item);
                break;
            case "Stealth":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Stealth, R.layout.spinner_item);
                break;
            case "Supra":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Supra, R.layout.spinner_item);
                break;
            case "Veloster":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Veloster, R.layout.spinner_item);
                break;
            case "Viper":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Viper, R.layout.spinner_item);
                break;
            case "WRX":
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Impreza, R.layout.spinner_item);
                break;
            default:
                trimAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.Base, R.layout.spinner_item);
                break;



        }

    }

    protected void checkTrim(Car car){
        if(trimList.getSelectedItem() != null && (!trimList.getSelectedItem().equals("Base"))){car.setTrim(trimList.getSelectedItem().toString()); }
        else{car.setTrim("");}
    }
}

