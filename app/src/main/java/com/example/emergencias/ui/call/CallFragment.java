package com.example.emergencias.ui.call;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.emergencias.R;
import com.example.emergencias.model.Contact;
import com.example.emergencias.model.User;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CallFragment extends Fragment implements CallPermission.NoticeDialogListener {

    ListView lv;
    User user;
    String numeroCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getView()!=null) {
            lv = getView().findViewById(R.id.callList);
            //Dados da Intent Anterior
            Intent quemChamou = requireActivity().getIntent();
            if (quemChamou != null) {
                Bundle params = quemChamou.getExtras();
                if (params != null) {
                    //Recuperando o Usuario
                    Toast.makeText(requireActivity(), params.getString("usuario"), Toast.LENGTH_LONG);
                    user = (User) params.getSerializable("usuario");
                    if (user != null) {
                        requireActivity().setTitle("Contatos de Emergência de " + user.getName());
                        preencherListView(user);
                        preencherListViewImagens(user);
                    }
                }
            }
        }
    }


    protected void atualizarListaDeContatos(User user){
        SharedPreferences recuperarContatos = requireActivity().getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = recuperarContatos.getInt("numContatos", 0);
        ArrayList<Contact> contatos = new ArrayList<Contact>();

        Contact contato;

        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis =
                            new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream oos = new ObjectInputStream(bis);
                    contato = (Contact) oos.readObject();

                    if (contato != null) {
                        contatos.add(contato);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        user.setContatos(contatos);
    }

    protected  void preencherListViewImagens(User user){

        final ArrayList<Contact> contatos = user.getContatos();
        Collections.sort(contatos);

        if (contatos.size()!=0) {
            String[] contatosNomes, contatosAbrevs;
            contatosNomes = new String[contatos.size()];
            contatosAbrevs= new String[contatos.size()];
            for (int j = 0; j < contatos.size(); j++) {
                contatosAbrevs[j] =contatos.get(j).getName().substring(0, 1);
                contatosNomes[j] =contatos.get(j).getName();
            }
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();

            for(int i =0; i < contatos.size(); i++) {
                Map<String,Object> listItemMap = new HashMap<String,Object>();
                listItemMap.put("imageId", R.drawable.ic_call_black_24dp);
                listItemMap.put("contato", contatosNomes[i]);
                listItemMap.put("abrevs",contatosAbrevs[i]);
                itemDataList.add(listItemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    requireActivity(),itemDataList,R.layout.list_imagem_item,
                    new String[]{"imageId","contato","abrevs"},
                    new int[]{R.id.userImage, R.id.userTitle,R.id.userAbrev});
            lv.setAdapter(simpleAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    chamar(contatos.get(i));
                }
            });

            // Função de remover com click longo na lista de contatos
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                    // Atualizando lista de contatos
                    contatos.remove(i);
                    user.setContatos(contatos);
                    return false;
                }
            });
        }
    }

    protected void preencherListView(User user) {

        final ArrayList<Contact> contatos = user.getContatos();

        if (contatos != null) {
            final String[] nomesSP;
            nomesSP = new String[contatos.size()];
            for (int j = 0; j < contatos.size(); j++) {
                nomesSP[j] = contatos.get(j).getName();
            }

            ArrayAdapter<String> adaptador;

            adaptador = new ArrayAdapter<String>(requireActivity(), R.layout.list_item, nomesSP);
            lv.setAdapter(adaptador);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    chamar(contatos.get(i));
                }
            });
        }
    }

    protected void chamar(Contact contato) {
        String number = contato.getNumber();
        Uri uri = Uri.parse(number);
        if (checarPermissaoPhone_SMD(number)) {
            Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
            startActivity(itLigar);
        } else {
            Intent itRedirect = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(itRedirect);
        }
    }

    protected boolean checarPermissaoPhone_SMD(String numero){

        numeroCall=numero;
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {

            if ( shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){

                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas";
                int codigo =1;
                CallPermission mensagemPermissao = new CallPermission(mensagem,titulo, codigo);

                mensagemPermissao.onAttach ((Context)requireActivity());
                mensagemPermissao.show(requireActivity().getSupportFragmentManager(), "primeiravez2");

            }else{
                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas II";
                int codigo =1;

                CallPermission mensagemPermissao = new CallPermission(mensagem,titulo, codigo);
                mensagemPermissao.onAttach ((Context)requireActivity());
                mensagemPermissao.show(requireActivity().getSupportFragmentManager(), "segundavez2");

            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 2222) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Uri uri = Uri.parse(numeroCall);
                Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                startActivity(itLigar);

            } else {
                String mensagem = "Seu aplicativo pode ligar diretamente, mas sem permissão não funciona. Se você marcou não perguntar mais, você deve ir na tela de configurações para mudar a instalação ou reinstalar o aplicativo  ";
                String titulo = "Porque precisamos telefonar?";
                CallPermission mensagemPermisso = new CallPermission(mensagem, titulo, 2);
                mensagemPermisso.onAttach((Context) requireActivity());
                mensagemPermisso.show(requireActivity().getSupportFragmentManager(), "segundavez");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Caso seja um Voltar ou Sucesso selecionar o item Ligar

        if (requestCode == 1111) {//Retorno de Mudar Perfil
            user=atualizarUser();
            requireActivity().setTitle("Contatos de Emergência de "+user.getName());
            atualizarListaDeContatos(user);
            preencherListViewImagens(user);
            preencherListView(user); //Montagem do ListView
        }

        if (requestCode == 1112) {//Retorno de Mudar Contatos
            atualizarListaDeContatos(user);
            preencherListViewImagens(user);
            preencherListView(user); //Montagem do ListView
        }

    }

    private User atualizarUser() {
        User user;
        SharedPreferences temUser= requireActivity().getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String senhaSalva = temUser.getString("senha","");
        String nomeSalvo = temUser.getString("nome","");
        boolean manterLogado=temUser.getBoolean("manterLogado",false);

        user=new User(nomeSalvo,senhaSalva,manterLogado);
        return user;
    }

    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo==1){
            String[] permissions ={Manifest.permission.CALL_PHONE};
            requestPermissions(permissions, 2222);

        }
    }

}