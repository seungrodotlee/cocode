package com.cocode.client.elements;

import com.cocode.client.components.SidebarIcon;
import com.cocode.client.utils.GlobalUtility;
import com.cocode.data.Unit;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class VoicePanel extends JPanel {
    GlobalUtility global;
    protected boolean running;
    ByteArrayOutputStream out;
    private Boolean micEnabled = false, speakerEnabled = true;


    public VoicePanel() {
        setLayout(new GridLayout(1, 2));

        final JButton micBtn = new JButton(new SidebarIcon("mic").imageIcon());
        final JButton speakerBtn = new JButton(new SidebarIcon("volume_up").imageIcon());

        global = GlobalUtility.getInstance();

        SourceDataLine line = global.getLine();

        ActionListener micListener =
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!micEnabled) {
                            captureAudio();
                            micBtn.setIcon(new SidebarIcon("mic_off").imageIcon());
                        } else {
                            running = false;
                            micBtn.setIcon(new SidebarIcon("mic").imageIcon());
                        }

                        micEnabled = !micEnabled;
                        micBtn.revalidate();
                    }
                };
        micBtn.addActionListener(micListener);

        ActionListener speakerListener =
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(speakerEnabled) {
                            speakerBtn.setIcon(new SidebarIcon("volume_off").imageIcon());
                        } else {
                            speakerBtn.setIcon(new SidebarIcon("volume_up").imageIcon());
                        }

                        speakerEnabled = !speakerEnabled;
                        global.setListening(speakerEnabled);
                    }
                };
        speakerBtn.addActionListener(speakerListener);

        add(micBtn);
        add(speakerBtn);
    }

    private void captureAudio() {
        try {
            final AudioFormat format = getFormat();
            DataLine.Info info = new DataLine.Info(
                    TargetDataLine.class, format);
            final TargetDataLine line = (TargetDataLine)
                    AudioSystem.getLine(info);

            line.open(format);
            line.start();

            Runnable runner = new Runnable() {
                int bufferSize = (int) (format.getSampleRate()
                        * format.getFrameSize());
                byte buffer[] = new byte[bufferSize];

                public void run() {
                    out = new ByteArrayOutputStream();
                    running = true;
                    try {
                        while (running) {
                            int count =
                                    line.read(buffer, 0, buffer.length);
                            if (count > 0) {
                                out.write(buffer, 0, count);
                                out.flush();

                                System.out.println("[VOICE] send");
                                global.sendMessage(new Unit(Unit.VOICE_DATA, null, null, buffer));
                            }
                        }
                        out.close();
                    } catch (IOException ex) {
                        System.err.println("I/O problems: " + ex);
                        System.exit(-1);
                    }
                }
            };
            Thread captureThread = new Thread(runner);
            captureThread.start();
        } catch (Exception e) {
            System.err.println("Line unavailable: " + e);
            //System.exit(-2);
        }
    }

    private AudioFormat getFormat() {
        float sampleRate = 8000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate,
                sampleSizeInBits, channels, signed, bigEndian);
    }
}
