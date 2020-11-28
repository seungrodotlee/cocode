package com.seungro.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.seungro.client.elements.ChatLogPanel;
import com.seungro.client.elements.Sidebar;
import com.seungro.client.elements.UserListPanel;
import com.seungro.client.utils.ColorPack;
import com.seungro.client.utils.GlobalUtility;
import com.seungro.client.utils.KeyUtil;
import com.seungro.client.utils.User;
import com.seungro.data.Unit;
import org.fife.ui.rtextarea.RTextScrollPane;

public class Client extends ClientFrame {
    private GlobalUtility global;

    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    Future<?> future;
    Boolean sharing = false;

    private User me;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Boolean connected = false;

    private JPanel mainPane;
    private JSplitPane editorPane;
    private JSplitPane centerPane;
    private JSplitPane wholePane;
    private JTabbedPane mainTab;
    private JPanel tabWrap;
    private ChatLogPanel chatLogPanel;
    private JPanel rightPanel;
    private JPanel listWrap;
    private UserListPanel listPanel;
    private Sidebar side;
    private JPanel buttonWrap;
    private JButton shareButton;

    private String name;
    private String room;
    private Login login;

    public Client(String name, String room, Login login) {
        this.name = name;
        this.room = room;
        this.login = login;

        global = GlobalUtility.getInstance();
        global.setUserName(name);

        side = new Sidebar();
        mainPane = new JPanel(new BorderLayout());
        tabWrap = new JPanel();
        mainTab = new JTabbedPane();
        chatLogPanel = new ChatLogPanel();
        rightPanel = new JPanel();
        listWrap = new JPanel();
        listPanel = new UserListPanel();
        editorPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, side, tabWrap);
        centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPane, chatLogPanel);
        wholePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPane, rightPanel);
        buttonWrap = new JPanel();
        shareButton = new JButton("공유");

        global.setMainTabPane(mainTab);
        global.setUserListPanel(listPanel);
        buttonWrap.setLayout(new BorderLayout());
        buttonWrap.add(shareButton, BorderLayout.CENTER);
        buttonWrap.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, ColorPack.BG_DARK));
        buttonWrap.setPreferredSize(new Dimension(1, 36));
        tabWrap.setLayout(new BorderLayout());
        tabWrap.add(mainTab, BorderLayout.CENTER);
        tabWrap.setBackground(ColorPack.BG);
        listWrap.setLayout(new BorderLayout());
        listWrap.add(listPanel, BorderLayout.PAGE_START);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(buttonWrap, BorderLayout.PAGE_START);
        rightPanel.add(listWrap, BorderLayout.CENTER);
        centerPane.setResizeWeight(1);
        centerPane.setDividerSize(5);
        wholePane.setDividerSize(5);
        wholePane.setResizeWeight(1);
        shareButton.setBackground(ColorPack.FADED_GREEN);
        shareButton.setForeground(Color.WHITE);
        shareButton.setBorder(null);
        shareButton.addMouseListener(new ShareBtnListener());
        mainPane.add(wholePane);

        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setContentPane(mainPane);
        setTitle("Text Editor Demo");
        ready();
        setVisible(true);
        revalidate();
        repaint();
        connectToServer();

        TimerTask r = new TimerTask() {
            @Override
            public void run() {
                centerPane.setDividerLocation(centerPane.getWidth() - 320);
            }
        };

        Timer t = new Timer();
        t.schedule(r, 100);
    }

    private void connectToServer() {
        try {
            me = new User(name);
            String ip = KeyUtil.decrypt(room);
            System.out.println("room ip = " + ip);

            socket = new Socket(ip, 10001);

            System.out.println("my name is " + name);

            global.setSocket(socket);

            output = new ObjectOutputStream(socket.getOutputStream());

            Unit loginUnit = new Unit(Unit.ENTER_DATA, name, null, login.getRoomType() + "/" + room);
            output.writeObject(loginUnit);
            output.flush();

            input = new ObjectInputStream(socket.getInputStream());
            Unit receive = (Unit) input.readObject();


            if(receive.getType() == Unit.LOG_DATA) {
                System.out.println("log = " + receive.getTitle() + ", key = " + room);

                String log = (String) receive.getTitle();
                if(log.equals("success") || log.equals(room)) {
                    UserListPanel userListPanel = global.getUserListPanel();

                    if(login.getRoomType().equals("new")) {
                        global.setAuth(true);
                        me.setAuth(true);
                        userListPanel.addUser(me);
                    } else {
                        ArrayList<String> members = (ArrayList<String>) receive.getValue();

                        System.out.println("auth = " + receive.getUserName());
                        for(String member : members) {
                            User u = new User(member);
                            if(member.equals(receive.getUserName())) {
                                u.setAuth(true);
                            }
                            userListPanel.addUser(u);
                            userListPanel.revalidate();
                        }
                    }

                    login.dispose();
                    System.out.println(log);

                    ClientThread thread = new ClientThread(this, socket, input);
                    thread.start();
                }
            }

            connected = true;
        } catch (Exception e) {
            e.printStackTrace();

            login.setErrorMsg("코드를 다시 확인해주세요");
        }
    }

    public void share() {
        Runnable share = new Runnable() {
            @Override
            public void run() {
                try {
                    String value = ((RTextScrollPane) mainTab.getSelectedComponent()).getTextArea().getText();

                    Unit u = new Unit(Unit.FILE_DATA, me.getName(), mainTab.getTitleAt(mainTab.getSelectedIndex()), value);

                    output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject(u);
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        future = service.scheduleAtFixedRate(share, 0, 300, TimeUnit.MILLISECONDS);
        global.setCurrentEditor(me.getName());
        shareButton.setText("공유 종료");
        shareButton.setBackground(ColorPack.RED_ORANGE);
        sharing = !sharing;
    }

    public void stop() {
        future.cancel(true);

        Unit u = new Unit(Unit.LOG_DATA, me.getName(), "finish_share", null);

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(u);
            output.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        global.setCurrentEditor(null);
        shareButton.setText("공유");
        shareButton.setBackground(ColorPack.FADED_GREEN);
        sharing = !sharing;
    }

    private class ShareBtnListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if(sharing) {
                stop();
                return;
            }

            if(!sharing) {
                if(mainTab.getSelectedIndex() == -1) {
                    return;
                }

                if(!connected) {
                    return;
                }

                try {
                    if(global.amIAuth()) {
                        Unit u = new Unit(Unit.LOG_DATA, me.getName(), "accept_share", null);

                        output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeObject(u);
                        output.flush();
                    } else {
                        Unit u = new Unit(Unit.LOG_DATA, me.getName(), "request_share", null);

                        output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeObject(u);
                        output.flush();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}