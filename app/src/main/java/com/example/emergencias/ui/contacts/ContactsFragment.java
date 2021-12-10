package com.example.emergencias.ui.contacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.emergencias.R;
import com.example.emergencias.model.Contact;
import com.example.emergencias.model.User;
import com.example.emergencias.ui.call.CallFragment;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class ContactsFragment extends Fragment {

    Button searchBtn;
    EditText searchContact;
    ListView listview;
    User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperando tela anterior
        Intent quemChamou= requireActivity().getIntent();
        if (quemChamou!=null) {
            Bundle params = quemChamou.getExtras();
            if (params!=null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                requireActivity().setTitle("Alterar Contatos de Emergência");
            }
        }

        searchBtn = requireActivity().findViewById(R.id.searchContactBtn);
        searchContact = requireActivity().findViewById(R.id.contactSearching);
        listview = requireActivity().findViewById(R.id.listContacts);

        searchBtn.setOnClickListener(onClickSearch());
    }

    // Função para salvar um novo contato
    public void salvarContato (Contact newContact){
        SharedPreferences salvaContatos = requireActivity().getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = salvaContatos.getInt("numContatos", 0); //checando quantos contatos já tem
        SharedPreferences.Editor editor = salvaContatos.edit();
        try {
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(newContact);
            String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato"+(num+1), contatoSerializado);
            editor.putInt("numContatos",num+1);

        }catch(Exception e){
            e.printStackTrace();
        }
        editor.commit();
        user.getContatos().add(newContact);
    }


    // Função chamada no momento da busca
    public View.OnClickListener onClickSearch(){
        // Verificando permissão de acessos aos contatos
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED){
            // Pedindo permissão
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            return null;
        }

        // Permissão já está garantida, buscando lista de contatos
        ContentResolver cr = requireActivity().getContentResolver();
        String consulta = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
        String [] argumentosConsulta = {"%"+searchContact.getText()+"%"};
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, consulta,argumentosConsulta, null);
        final String[] nomesContatos = new String[cursor.getCount()];
        final String[] telefonesContatos = new String[cursor.getCount()];

        int i=0;
        while (cursor.moveToNext()) {
            int indiceNome = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            String contatoNome = cursor.getString(indiceNome);
            nomesContatos[i]= contatoNome;
            int indiceContatoID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            String contactID = cursor.getString(indiceContatoID);
            String consultaPhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, consultaPhone, null, null);

            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //Salvando só último telefone
                telefonesContatos[i] = number;
            }
            i++;
        }

        if (nomesContatos !=null) {
            // Percorrendo lista de resultado e criando função de click
            for(int j=0; j<=nomesContatos.length; j++) {
                ArrayAdapter<String> adaptador;
                adaptador = new ArrayAdapter<String>(requireActivity(), R.layout.fragment_contacts, nomesContatos);
                listview.setAdapter(adaptador);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Contact c= new Contact();
                        c.setName(nomesContatos[i]);
                        c.setNumber("tel:+"+telefonesContatos[i]);
                        salvarContato(c);
                        Intent intent = new Intent(requireActivity().getApplicationContext(), CallFragment.class);
                        intent.putExtra("usuario",user);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                });
            }
        }
        return null;
    }

}
