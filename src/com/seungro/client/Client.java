package com.seungro.client;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Boolean connected = false;

    private JLayeredPane layeredPane;
    private JPanel mainPane;
    private JSplitPane editorPane;
    private JSplitPane centerPane;
    private JSplitPane wholePane;
    private JTabbedPane mainTab;
    private Sidebar side;
    private JPanel bottomBar;
    private JButton shareButton;
    private String name = null;

    public Client(String name, String room, Login login) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension((int) screenSize.getWidth(), (int) screenSize.getHeight()));

        global = GlobalUtility.getInstance();
        global.setUserName(name);

        try {
            KeyUtil keyUtil = new KeyUtil();
            String ip = keyUtil.decrypt(room);
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
                System.out.println("log = " + receive.getValue() + ", key = " + room);

                String log = (String) receive.getValue();
                if(log.equals("success") || log.equals(room)) {
                    login.dispose();
                    System.out.println(log);

                    ClientThread thread = new ClientThread(socket, input);
                    thread.start();
                }
            }

            connected = true;
        } catch (Exception e) {
            e.printStackTrace();

            login.setErrorMsg("코드를 다시 확인해주세요");
        }

        System.out.println("client on");

        JPanel welcomePanel = new JPanel();
        JPanel tabWrap = new JPanel();
        welcomePanel.add(new JLabel("welcome"));

        mainPane = new JPanel(new BorderLayout());

        mainTab = new JTabbedPane();
        tabWrap.add(mainTab);
        tabWrap.setBackground(ColorPack.BG);
        global.setMainTabPane(mainTab);

        side = new Sidebar();

        ChatLogPanel chatLogPanel = new ChatLogPanel();
        JPanel listWrap = new JPanel();
        UserListPanel listPanel = new UserListPanel();

        listPanel.addUser(new User("이승로"));
        listPanel.addUser(new User("김승로"));
        listPanel.addUser(new User("박승로"));

        listWrap.setLayout(new BorderLayout());
        listWrap.add(listPanel, BorderLayout.PAGE_START);
        listWrap.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, ColorPack.BG_DARK));

        GlobalUtility.getInstance().setCurrentEditor("박승로");

        editorPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, side, tabWrap);
        centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPane, chatLogPanel);
        wholePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPane, listWrap);
        centerPane.setResizeWeight(1);
        wholePane.setResizeWeight(1);

        BasicSplitPaneDivider divider = (BasicSplitPaneDivider) wholePane.getComponent(2);
        divider.setBackground(ColorPack.BG_DARK);
        //divider.setBorder(null);

        bottomBar = new JPanel(new FlowLayout());
        shareButton = new JButton("공유");
        shareButton.addMouseListener(new ShareBtnListener());
        bottomBar.add(shareButton);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ColorPack.BG_DARK));

        mainPane.add(wholePane);
        mainPane.add(bottomBar, BorderLayout.PAGE_END);

        setContentPane(mainPane);
        setTitle("Text Editor Demo");
        ready();
        setVisible(true);
    }

    private class ShareBtnListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if(sharing) {
                System.out.println("stop");
                future.cancel(true);
            }

            if(!sharing) {
                if(mainTab.getSelectedIndex() == -1) {
                    return;
                }

                if(!connected) {
                    return;
                }

                System.out.println("start");
                Runnable share = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String value = ((RTextScrollPane) mainTab.getSelectedComponent()).getTextArea().getText();

                            Unit u = new Unit(Unit.FILE_DATA, name, mainTab.getTitleAt(mainTab.getSelectedIndex()), value);

                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeObject(u);
                            output.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                future = service.scheduleAtFixedRate(share, 0, 300, TimeUnit.MILLISECONDS);
            }

            sharing = !sharing;
        }
    }
}