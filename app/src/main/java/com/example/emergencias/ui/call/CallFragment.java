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
import com.example.emergencias.model.User;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// TODO: criar modelo Contatos e layouts (R.layout e R.id's) para listagem
public class CallFragment extends Fragment implements CallPermission.NoticeDialogListener {

    ListView lv;
    User user;
    String numeroCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = getView().findViewById(R.id.callList);

        //Dados da Intent Anterior
        Intent quemChamou = getActivity().getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                if (user != null) {
                    getActivity().setTitle("Contatos de Emergência de "+user.getNome());
                    preencherListView(user);
                    preencherListViewImagens(user);
                }
            }
        }
    }

    protected void atualizarListaDeContatos(User user){
        SharedPreferences recuperarContatos = getActivity().getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = recuperarContatos.getInt("numContatos", 0);
        ArrayList<Contato> contatos = new ArrayList<Contato>();

        Contato contato;

        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis =
                            new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream oos = new ObjectInputStream(bis);
                    contato = (Contato) oos.readObject();

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

        final ArrayList<Contato> contatos = user.getContatos();
        Collections.sort(contatos);

        if (contatos != null) {
            String[] contatosNomes, contatosAbrevs;
            contatosNomes = new String[contatos.size()];
            contatosAbrevs= new String[contatos.size()];
            Contato c;
            for (int j = 0; j < contatos.size(); j++) {
                contatosAbrevs[j] =contatos.get(j).getNome().substring(0, 1);
                contatosNomes[j] =contatos.get(j).getNome();
            }
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

            for(int i =0; i < contatos.size(); i++) {
                Map<String,Object> listItemMap = new HashMap<String,Object>();
                listItemMap.put("imageId", R.drawable.ic_call_black_24dp);
                listItemMap.put("contato", contatosNomes[i]);
                listItemMap.put("abrevs",contatosAbrevs[i]);
                itemDataList.add(listItemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),itemDataList,R.layout.list_view_layout_imagem,
                    new String[]{"imageId","contato","abrevs"},
                    new int[]{R.id.userImage, R.id.userTitle,R.id.userAbrev});
            lv.setAdapter(simpleAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (checarPermissaoPhone_SMD(contatos.get(i).getNumero())) {

                        Uri uri = Uri.parse(contatos.get(i).getNumero());
                        //  Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(itLigar);
                    }
                }
            });
        }
    }

    protected void preencherListView(User user) {

        final ArrayList<Contato> contatos = user.getContatos();

        if (contatos != null) {
            final String[] nomesSP;
            nomesSP = new String[contatos.size()];
            Contato c;
            for (int j = 0; j < contatos.size(); j++) {
                nomesSP[j] = contatos.get(j).getNome();
            }

            ArrayAdapter<String> adaptador;

            adaptador = new ArrayAdapter<String>(getActivity(), R.layout.list_view_layout, nomesSP);
            lv.setAdapter(adaptador);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (checarPermissaoPhone_SMD(contatos.get(i).getNumero())) {

                        Uri uri = Uri.parse(contatos.get(i).getNumero());
                        // TODO: implementar ligar pelo aplicativo de chamadas (DIAL) caso o
                        //      ligar direto (CALL) falhe
                        //   Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(itLigar);
                    }


                }
            });
        }
    }

    protected boolean checarPermissaoPhone_SMD(String numero){

        numeroCall=numero;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {

            if ( shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){

                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas";
                int codigo =1;
                CallPermission mensagemPermissao = new CallPermission(mensagem,titulo, codigo);

                mensagemPermissao.onAttach ((Context)getActivity());
                mensagemPermissao.show(getActivity().getSupportFragmentManager(), "primeiravez2");

            }else{
                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas II";
                int codigo =1;

                CallPermission mensagemPermissao = new CallPermission(mensagem,titulo, codigo);
                mensagemPermissao.onAttach ((Context)getActivity());
                mensagemPermissao.show(getActivity().getSupportFragmentManager(), "segundavez2");

            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 2222) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "VALEU", Toast.LENGTH_LONG).show();
                Uri uri = Uri.parse(numeroCall);
                //   Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                startActivity(itLigar);

            } else {
                Toast.makeText(getActivity(), "SEU FELA!", Toast.LENGTH_LONG).show();

                String mensagem = "Seu aplicativo pode ligar diretamente, mas sem permissão não funciona. Se você marcou não perguntar mais, você deve ir na tela de configurações para mudar a instalação ou reinstalar o aplicativo  ";
                String titulo = "Porque precisamos telefonar?";
                CallPermission mensagemPermisso = new CallPermission(mensagem, titulo, 2);
                mensagemPermisso.onAttach((Context) getActivity());
                mensagemPermisso.show(getActivity().getSupportFragmentManager(), "segundavez");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Caso seja um Voltar ou Sucesso selecionar o item Ligar

        if (requestCode == 1111) {//Retorno de Mudar Perfil
            user=atualizarUser();
            getActivity().setTitle("Contatos de Emergência de "+user.getNome());
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
        User user = null;
        SharedPreferences temUser= getActivity().getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String loginSalvo = temUser.getString("login","");
        String senhaSalva = temUser.getString("senha","");
        String nomeSalvo = temUser.getString("nome","");
        String emailSalvo = temUser.getString("email","");
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