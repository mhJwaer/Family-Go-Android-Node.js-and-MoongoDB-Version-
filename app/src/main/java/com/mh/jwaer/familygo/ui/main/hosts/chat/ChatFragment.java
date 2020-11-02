package com.mh.jwaer.familygo.ui.main.hosts.chat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mh.jwaer.familygo.R;
import com.mh.jwaer.familygo.adapters.ReceiveMessageItem;
import com.mh.jwaer.familygo.adapters.SendMessageItem;
import com.mh.jwaer.familygo.data.models.CircleMember;
import com.mh.jwaer.familygo.data.models.MessageModel;
import com.mh.jwaer.familygo.data.network.RetrofitClient;
import com.mh.jwaer.familygo.databinding.FragmentChatBinding;
import com.mh.jwaer.familygo.util.UserClient;
import com.xwray.groupie.GroupAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.mh.jwaer.familygo.util.CONSTANTS.NULL_CIRCLE;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private static final String TAG = "ChatFragment";
    private Gson gson = new Gson();
    private RecyclerView recyclerView;
    private GroupAdapter adapter = new GroupAdapter();
    private List<CircleMember> members = new ArrayList<>();
    private MessageModel receivedMessage ;
    private List<MessageModel> messages = new ArrayList<>();


    private Socket socket;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //init sockets
        socket = SocketClient.getInstance();
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SocketIOService: ", "socket connected");
            }
        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d("SocketIOService: ", "socket connection error!");
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SocketIOService: ", "socket disconnected!");
            }
        });
        socket.connect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //setting up views
        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        getLifecycle().addObserver(viewModel);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        joinSocketRoom();
        getCircleMembers();
        enableReceiveMessageListener();


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.sendBtn.setSpeed(2);
                binding.sendBtn.playAnimation();
                if (UserClient.getUser().getCircle().equals(NULL_CIRCLE)){
                    Toast.makeText(getActivity(), "Join Circle First TO Start Send Messages", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String message = binding.senderEditText.getText().toString().trim();
                if (message.equals("")) return;
                long ts = System.currentTimeMillis()/1000L;
                final SendMessageItem item = new SendMessageItem(message, ts);
                adapter.add(item);
                MessageModel messageModel = new MessageModel(UserClient.getUser().getUid(), message, UserClient.getUser().getCircle(), String.valueOf(ts));
                sendMessage(messageModel);
                binding.senderEditText.getText().clear();
                recyclerView.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }

    private void joinSocketRoom() {
        socket.emit("JoinRoom", UserClient.getUser().getUid());
    }

    private void getCircleMembers() {

        socket.emit("retrieveCircleMembers", UserClient.getUser().getCircle());
        socket.on("circleMembers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SocketIOService", "call: SocketMembersRetrieved!!");
                JSONArray obj =(JSONArray) args[0];
                Type collectionType = new TypeToken<List<CircleMember>>(){}.getType();
                members = gson.fromJson(obj.toString(), collectionType);
                getAllOldMessages();

            }
        });
    }

    private void getAllOldMessages() {
        socket.emit("retrieveOldMessages", UserClient.getUser().getCircle());
        socket.on("oldMessages", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONArray obj =(JSONArray) args[0];
                Log.d("SocketIOService", "call: Old messages retrieved !! size = "+ obj.length());

                Type collectionType = new TypeToken<List<MessageModel>>(){}.getType();
                List<MessageModel> messagesModels = gson.fromJson(obj.toString(), collectionType);
                addMessagesToAdapter(messagesModels);
            }
        });
    }

    private void enableReceiveMessageListener() {
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                receivedMessage = gson.fromJson(obj.toString(), MessageModel.class);
                setNewMessage(receivedMessage);
            }
        });
    }

    private void sendMessage(MessageModel message) {
        try{
            JSONObject object = new JSONObject(gson.toJson(message));
            socket.emit("sendMsg", object);
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    private void setNewMessage(MessageModel message) {
        if (message.getSenderId().equals(UserClient.getUser().getUid())) return;
        for (CircleMember member : members) {
            if (member.getUid().equals(message.getSenderId())) {

                String modelPhotoUrl = member.getPhotoUrl().replace("\\", "/");
                String photoUrl = RetrofitClient.BASE_URL + modelPhotoUrl;

                final ReceiveMessageItem item = new ReceiveMessageItem(message.getContent(),
                        member.getName(),
                        photoUrl,
                        1,
                        requireContext());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.add(item);
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                });

            }
        }
    }

    private void addMessagesToAdapter(List<MessageModel> messageModels) {
//        for (int i = 0; i<messageModels.size(); i++){
//            Log.d(TAG, "call: "+ messageModels.get(i).getContent());
//        }
        for (MessageModel message : messageModels) {
            if (message.getSenderId().equals(UserClient.getUser().getUid())) {
                SendMessageItem item = new SendMessageItem(message.getContent(),  Long.parseLong(message.getCreatedAt()));
                adapter.add(item);
                continue;
            }
            for (CircleMember member : members) {
                if (member.getUid().equals(UserClient.getUser().getUid())) continue;
                if (member.getUid().equals(message.getSenderId())) {
                    ReceiveMessageItem item = new ReceiveMessageItem(message.getContent(),
                            member.getName(),
                            member.getPhotoUrl(),
                            Long.parseLong(message.getCreatedAt()),
                            getContext());
                    adapter.add(item);
                }
            }
        }
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        socket.disconnect();
    }
}