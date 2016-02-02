/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableModel;

import com.jtattoo.plaf.aero.AeroLookAndFeel;
import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import com.jtattoo.plaf.luna.LunaLookAndFeel;
import com.jtattoo.plaf.mcwin.McWinLookAndFeel;
import com.jtattoo.plaf.smart.SmartLookAndFeel;
import com.jtattoo.plaf.texture.TextureLookAndFeel;

import utils.FileUtils;
import utils.MP3PlayerFileFilter;
import utils.SkinUtils;

import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.border.TitledBorder;

import java.awt.Color;
import java.awt.Font;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.border.EtchedBorder;
import javax.swing.JList;
import javax.swing.AbstractListModel;

import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.naming.Binding;
import javax.swing.JFileChooser;

import mp3.BasicMP3Player;
import mp3.MP3;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;

import java.awt.FlowLayout;
import javax.swing.ScrollPaneConstants;



public class MP3Player extends javax.swing.JFrame implements BasicPlayerListener {

	private static final String MP3_FILE_EXTENSION = "mp3";
    private static final String MP3_FILE_DESCRIPTION = "files mp3";
    private static final String PLAYLIST_FILE_EXTENSION = "pls";
    private static final String PLAYLIST_FILE_DESCRIPTION = "files playlist";
    private static final String EMPTY_STRING = "";
    private static final String INPUT_SONG_NAME = "enter song name";
    
    private DefaultListModel mp3ListModel = new DefaultListModel();
    private FileFilter mp3FileFilter = new MP3PlayerFileFilter(MP3_FILE_EXTENSION, MP3_FILE_DESCRIPTION);
    private FileFilter playlistFileFilter = new MP3PlayerFileFilter(PLAYLIST_FILE_EXTENSION, PLAYLIST_FILE_DESCRIPTION);
    private BasicMP3Player player = new BasicMP3Player(this);
    
    private long secondsAmount; 
    private long duration; 
    private int bytesLen; 
    private double posValue = 0.0; 
    private int bitrate;
    private int frequency;
    
    private boolean movingFromJump = false;
    private boolean moveAutomatic = false;
    
    private long sec;
    private long time;
    private long min;
    private long second;
    
    @Override
    public void opened(Object o, Map map) {
        duration = (long) Math.round((((Long) map.get("duration")).longValue()) / 1000000);
        bytesLen = (int) Math.round(((Integer) map.get("mp3.length.bytes")).intValue());
        bitrate = (int) Math.round((((Integer) map.get("bitrate")).intValue()) / 1000);
        frequency = (int) Math.round((((Integer) map.get("mp3.frequency.hz")).intValue()) / 1000);
        
        String albumName = map.get("album") !=null ? map.get("album").toString() : FileUtils.getFileNameWithoutExtension(new File(o.toString()).getName());
        String songName = map.get("title") != null ? map.get("title").toString() : FileUtils.getFileNameWithoutExtension(new File(o.toString()).getName());
        String authorName = map.get("author") != null ? map.get("author").toString() : FileUtils.getFileNameWithoutExtension(new File(o.toString()).getName());
        
        System.out.println(bitrate);
        System.out.println(map);
        System.out.println(albumName);
        
        if (songName.length() > 60) {
            songName = songName.substring(0, 60) + "...";
        }
        sec = 60;
        time = duration;
        labelSongName.setText(authorName + " - " + songName);
        min = time/sec;
        second = time%sec;
        
        labelInfo.setText((bitrate) + " KBPS    " + (frequency) + " KHz                                     " + (min) + ":" + (second));
        System.out.println(labelSongName);
        System.out.print(authorName +" - " + songName);
        System.out.println("  " + (time/sec) + ":" + (time%sec));
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {

        float progress = -1.0f;

        if ((bytesread > 0) && ((duration > 0))) {
            progress = bytesread * 1.0f / bytesLen * 1.0f;
        }
        secondsAmount = (long) (duration * progress);

        if (duration != 0) {
            if (movingFromJump == false) {
                slideProgress.setValue(((int) Math.round(secondsAmount * 1000 / duration)));

            }
        }
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
        int state = bpe.getCode();
        if (state == BasicPlayerEvent.PLAYING) {
            movingFromJump = false;
        } else if (state == BasicPlayerEvent.SEEKING) {
            movingFromJump = true;
        } else if (state == BasicPlayerEvent.EOM) {
            if (selectNextSong()) {
                playFile();
            }
        }
    }

    @Override
    public void setController(BasicController bc) {
    }
    /**
     * Creates new form MP3PlayerGui
     */
    public MP3Player() {
    	fileChooser.setMultiSelectionEnabled(true);
    	fileChooser.setAcceptAllFileFilterUsed(false);
    	setIconImage(Toolkit.getDefaultToolkit().getImage(MP3Player.class.getResource("/images/ps_classe_disc_player_2752.png")));
    	setFont(new Font("Harlow Solid Italic", Font.PLAIN, 14));
        initComponents();
    }
    
    private void playFile() {
        int[] indexPlayList = listPlayList.getSelectedIndices();
        if (indexPlayList.length > 0) {
            MP3 mp3 = (MP3) mp3ListModel.getElementAt(indexPlayList[0]);
            player.play(mp3.getPath());
            player.setVolume(slideVolume.getValue(), slideVolume.getMaximum());
        }
    }
        
    private boolean selectNextSong() {
        int nextIndex = listPlayList.getSelectedIndex() + 1;
        if (nextIndex <= listPlayList.getModel().getSize() - 1) {
        	listPlayList.setSelectedIndex(nextIndex);
            return true;
        }
        return false;
    }
    
    private boolean selectPrevSong() {
        int nextIndex = listPlayList.getSelectedIndex() - 1;
        if (nextIndex >= 0) {
        	listPlayList.setSelectedIndex(nextIndex);
            return true;
        }
       return false;
    }
    
    private void searchSong() {
        String searchStr = txtSearch.getText();       
        if (searchStr == null || searchStr.trim().equals(EMPTY_STRING)) {
            return;
        }
        ArrayList<Integer> mp3FindedIndexes = new ArrayList<Integer>();        
        for (int i = 0; i < mp3ListModel.size(); i++) {
            MP3 mp3 = (MP3) mp3ListModel.getElementAt(i);
            
            if (mp3.getName().toUpperCase().contains(searchStr.toUpperCase())) {
                mp3FindedIndexes.add(i);
            }
        }        
        int[] selectIndexes = new int[mp3FindedIndexes.size()];

        if (selectIndexes.length == 0) {
            JOptionPane.showMessageDialog(this, "Search string \'" + searchStr + "\' returned no results");
            txtSearch.requestFocus();
            txtSearch.selectAll();
            return;
        }

        for (int i = 0; i < selectIndexes.length; i++) {
            selectIndexes[i] = mp3FindedIndexes.get(i).intValue();
        }
        listPlayList.setSelectedIndices(selectIndexes);
    }
 
    
    private void processSeek(double bytes) {
        try {
            long skipBytes = (long) Math.round(((Integer) bytesLen).intValue() * bytes);
            player.jump(skipBytes);
        } catch (Exception e) {
            e.printStackTrace();
            movingFromJump = false;
        }

    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    
    private void initComponents() {
    	jSeparator1 = new javax.swing.JPopupMenu.Separator();
    	popupPlaylist = new javax.swing.JPopupMenu();
    	popmenuAddSong = new javax.swing.JMenuItem();
    	popmenuAddSong.setFont(new Font ("Harlow Solid Italic", Font.PLAIN, 12));
    	popmenuDeleteSong = new javax.swing.JMenuItem();
    	popmenuDeleteSong.setFont(new Font ("Harlow Solid Italic", Font.PLAIN, 12));
    	popmenuPlay = new javax.swing.JMenuItem();
    	popmenuPlay.setFont(new Font ("Harlow Solid Italic", Font.PLAIN, 12));
        popmenuStop = new javax.swing.JMenuItem();
        popmenuStop.setFont(new Font ("Harlow Solid Italic", Font.PLAIN, 12));
        popmenuPause = new javax.swing.JMenuItem();
        popmenuPause.setFont(new Font ("Harlow Solid Italic", Font.PLAIN, 12));
    	slideVolume = new javax.swing.JSlider();
    	slideProgress = new javax.swing.JSlider();
    	slideProgress.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slideProgressStateChanged(evt);
    		}
    	});
    	slideProgress.setValue(0);
    	slideProgress.setSnapToTicks(true);
    	slideProgress.setMaximum(1000);
    	labelSongName = new javax.swing.JLabel("...");
    	labelSongName.setFont(new Font("Times New Roman", Font.PLAIN, 11));
    	panelProgress = new javax.swing.JPanel();
    	panelProgress.setBounds(10, 35, 335, 62);
        panelVolume = new javax.swing.JPanel();
        panelVolume.setBounds(10, 5, 335, 30);
        listPlayList = new javax.swing.JList();
        listPlayList.setBorder(null);
        MenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuFile.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuOpenPlaylist = new javax.swing.JMenuItem();
        menuOpenPlaylist.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		menuOpenPlaylistActionPerformed(evt);
        	}
        });
        menuOpenPlaylist.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuSavePlaylist = new javax.swing.JMenuItem();
        menuSavePlaylist.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		menuSavePlaylistActionPerformed(evt);
        	}
        });
        menuSavePlaylist.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuSeparator = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuExit.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		menuExitActionPerformed(evt);
        	}
        });
        menuExit.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuPrefs = new javax.swing.JMenu();
        menuPrefs.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuChangeSkin = new javax.swing.JMenu();
        menuChangeSkin.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuSkin1 = new javax.swing.JMenuItem();
        menuSkin1.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuSkin2 = new javax.swing.JMenuItem();
        menuSkin2.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MP3Player of Yuk");
        setResizable(false);

        panelVolume.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        tglbtnVolume = new javax.swing.JToggleButton();
        tglbtnVolume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglbutVolumeActionPerformed(evt);
        	}
        });
        tglbtnVolume.setBounds(11, 1, 25, 25);
        
                tglbtnVolume.setIcon(new ImageIcon(MP3Player.class.getResource("/images/speaker.png"))); // NOI18N
                tglbtnVolume.setToolTipText("Mute");
                tglbtnVolume.setSelectedIcon(new ImageIcon(MP3Player.class.getResource("/images/mute.png")));
        

        slideVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slideVolumeStateChanged(evt);
        	}
        });
        slideVolume.setSnapToTicks(true);
        slideVolume.setMinorTickSpacing(5);
        slideVolume.setBounds(67, 7, 258, 15);
        slideVolume.setToolTipText("Change volume");
        slideVolume.setMaximum(200);

        menuFile.setText("File");

        menuOpenPlaylist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open-icon.png"))); // NOI18N
        menuOpenPlaylist.setText("Open playlist");
        menuOpenPlaylist.setName("");
        menuFile.add(menuOpenPlaylist);

        menuSavePlaylist.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save_16.png"))); // NOI18N
        menuSavePlaylist.setText("Save playlist");
        menuFile.add(menuSavePlaylist);
        menuFile.add(menuSeparator);

        menuExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/exit.png"))); // NOI18N
        menuExit.setText("Exit");
        menuFile.add(menuExit);

        MenuBar.add(menuFile);

        menuPrefs.setText("Service");

        menuChangeSkin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/gear_16.png"))); // NOI18N
        menuChangeSkin.setText("ChangeSkin");

        menuSkin1.setText("Skin1");
        menuSkin1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSkin1ActionPerformed(evt);
            }
        });
        menuChangeSkin.add(menuSkin1);

        menuSkin2.setText("Skin2");
        menuSkin2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSkin2ActionPerformed(evt);
            }
        });
        menuChangeSkin.add(menuSkin2);

        menuPrefs.add(menuChangeSkin);
        
        menuSkin3 = new JMenuItem("Skin3");
        menuSkin3.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 12));
        menuSkin3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSkin3ActionPerformed(evt);
        	}
        });
        menuChangeSkin.add(menuSkin3);

        MenuBar.add(menuPrefs);

        setJMenuBar(MenuBar);
        
        panelSearch = new JPanel();
        panelSearch.setBounds(10, 345, 335, 60);
        panelSearch.setBorder(null);
        
        panelPlay = new JPanel();
        panelPlay.setBounds(10, 100, 48, 240);
        panelPlay.setBackground(UIManager.getColor("Panel.background"));
        panelPlay.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        butPlay = new JButton("");
        butPlay.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		butPlaySongActionPerformed(evt);
        	}
        });
        butPlay.setToolTipText("Play");
        butPlay.setBackground(new Color(0, 0, 0));
        butPlay.setIcon(new ImageIcon(MP3Player.class.getResource("/images/player_play.png")));
        butPlay.setBounds(0, 0, 48, 48);
        butPlay.setHorizontalTextPosition(SwingConstants.LEFT);
        butPlay.setAlignmentY(0.0f);
        butPlay.setAlignmentX(0.5f);
        
        butPause = new JButton("");
        butPause.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		butPauseSongActionPerformed(evt);
        	}
        });
        butPause.setToolTipText("Pause");
        butPause.setBackground(new Color(0, 0, 0));
        butPause.setIcon(new ImageIcon(MP3Player.class.getResource("/images/player_pause_8205.png")));
        butPause.setBounds(0, 48, 48, 48);
        butPause.setHorizontalTextPosition(SwingConstants.LEFT);
        butPause.setAlignmentY(0.0f);
        butPause.setAlignmentX(0.5f);
     
        panelProgress.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panelProgress.setLayout(null);
        
        slideProgress.setBounds(11, 32, 315, 15);
        panelProgress.add(slideProgress);
        
        labelSongName.setBounds(14, 10, 298, 16);
        panelProgress.add(labelSongName);
        panelVolume.setLayout(null);
        panelVolume.add(tglbtnVolume);
        panelVolume.add(slideVolume);
        panelPlay.setLayout(null);
        panelPlay.add(butPlay);
        panelPlay.add(butPause);
        
        butStop = new JButton("");
        butStop.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		butStopSongActionPerformed(evt);
        	}
        });
        butStop.setToolTipText("Stop");
        butStop.setBackground(new Color(0, 0, 0));
        butStop.setIcon(new ImageIcon(MP3Player.class.getResource("/images/player_stop.png")));
        butStop.setHorizontalTextPosition(SwingConstants.LEFT);
        butStop.setAlignmentY(0.0f);
        butStop.setAlignmentX(0.5f);
        butStop.setBounds(0, 96, 48, 48);
        panelPlay.add(butStop);
        
        butNextSong = new JButton("");
        butNextSong.setToolTipText("Next song");
        butNextSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNextSongActionPerformed(evt);
        	}
        });
        butNextSong.setBackground(new Color(0, 0, 0));
        butNextSong.setIcon(new ImageIcon(MP3Player.class.getResource("/images/player_end.png")));
        butNextSong.setHorizontalTextPosition(SwingConstants.LEFT);
        butNextSong.setAlignmentY(0.0f);
        butNextSong.setAlignmentX(0.5f);
        butNextSong.setBounds(0, 144, 48, 48);
        panelPlay.add(butNextSong);
        
        butPrevSong = new JButton("");
        butPrevSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrevSongActionPerformed(evt);
        	}
        });
        butPrevSong.setToolTipText("Prev song");
        butPrevSong.setBackground(new Color(0, 0, 0));
        butPrevSong.setIcon(new ImageIcon(MP3Player.class.getResource("/images/player_start.png")));
        butPrevSong.setHorizontalTextPosition(SwingConstants.LEFT);
        butPrevSong.setAlignmentY(0.0f);
        butPrevSong.setAlignmentX(0.5f);
        butPrevSong.setBounds(0, 192, 48, 48);
        panelPlay.add(butPrevSong);
        butSearch = new javax.swing.JButton();
        butSearch.setBackground(new Color(64, 224, 208));
        butSearch.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		butSearchActionPerformed(evt);
        	}
        });
        butSearch.setFont(new Font("Harlow Solid Italic", Font.PLAIN, 11));
        butSearch.setBounds(0, 30, 84, 25);
        butSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search_16.png"))); 
        butSearch.setText("Search");
        butSearch.setToolTipText("Search song");
        butSearch.setActionCommand("search");
        butSearch.setName("btnSearch");
        txtSearch = new javax.swing.JTextField();
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchKeyPressed(evt);
        	}
        });
        txtSearch.setToolTipText("searching song");
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
        	@Override
        	public void focusGained(java.awt.event.FocusEvent evt) {
        		txtSearchFocusGained(evt);
        	}
        	@Override
        	public void focusLost(java.awt.event.FocusEvent evt) {
        		txtSearchFocusLost(evt);
        	}
        });
        txtSearch.setBounds(95, 30, 228, 25);
        
                txtSearch.setFont(new java.awt.Font("Tahoma", 2, 11)); 
                txtSearch.setForeground(new java.awt.Color(153, 153, 153));
        butAddSong = new javax.swing.JButton();
        butAddSong.setBackground(new Color(64, 224, 208));
        butAddSong.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		btnAddSongActionPerformed(evt);
        	}
        });
       
        butAddSong.setBounds(0, 0, 84, 25);
        
                butAddSong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus_16.png"))); 
                butAddSong.setToolTipText("Add song");
                butAddSong.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                butAddSong.setName("");
        butDeleteSong = new javax.swing.JButton();
        butDeleteSong.setBackground(new Color(64, 224, 208));
        butDeleteSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteSongActionPerformed(evt);
            }
        });      
        butDeleteSong.setBounds(85, 0, 84, 25);
        
                butDeleteSong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove_icon.png"))); 
                butDeleteSong.setToolTipText("Delete song");
                butDeleteSong.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                butDeleteSong.setName("");
        butSelectNext = new javax.swing.JButton();
        butSelectNext.setBackground(new Color(64, 224, 208));
        butSelectNext.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		butSelectNextActionPerformed(evt);
        	}
        });
        butSelectNext.setBounds(170, 0, 84, 25);
        
                butSelectNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/arrow-down-icon.png"))); 
                butSelectNext.setToolTipText("Highlight next song");
                butSelectNext.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                butSelectNext.setName("btnAddSong");
        butSelectPrev = new javax.swing.JButton();
        butSelectPrev.setBackground(new Color(64, 224, 208));
        butSelectPrev.addActionListener(new java.awt.event.ActionListener () {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		butSelectPrevActionPerformed(evt);
        	}
        });
        butSelectPrev.setBounds(255, 0, 81, 25);
        
                butSelectPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/arrow-up-icon.png"))); 
                butSelectPrev.setToolTipText("Highlight prev song");
                butSelectPrev.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                butSelectPrev.setName("btnAddSong");
                butSelectPrev.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        panelSearch.setLayout(null);
        panelSearch.add(butSearch);
        panelSearch.add(txtSearch);
        panelSearch.add(butAddSong);
        panelSearch.add(butDeleteSong);
        panelSearch.add(butSelectNext);
        panelSearch.add(butSelectPrev);
        getContentPane().setLayout(null);
        getContentPane().add(panelProgress);
        getContentPane().add(panelSearch);
        getContentPane().add(panelPlay);
        getContentPane().add(panelVolume);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        scrollPane.setBounds(68, 117, 274, 222);
        getContentPane().add(scrollPane);
        
        listPlayList.setModel(mp3ListModel);
        listPlayList.setToolTipText("Playlist");
        listPlayList.setComponentPopupMenu(popupPlaylist);
        listPlayList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstPlayListMouseClicked(evt);
            }
        });
        listPlayList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstPlayListKeyPressed(evt);
            }
        });
        
        scrollPane.setViewportView(listPlayList);
        
        labelInfo = new JLabel("...");
        labelInfo.setBounds(68, 100, 274, 14);
        getContentPane().add(labelInfo);
        labelInfo.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-344)/2, (screenSize.height-594)/2, 353, 460);
        
        popmenuAddSong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/plus_16.png")));
        popmenuAddSong.setText("Add song");
        popmenuAddSong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popmenuAddSongActionPerformed(evt);
            }
        });
        popupPlaylist.add(popmenuAddSong);      
    
    popmenuDeleteSong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/remove_icon.png"))); // NOI18N
    popmenuDeleteSong.setText("Remove song");
    popmenuDeleteSong.setToolTipText("");
    popmenuDeleteSong.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            popmenuDeleteSongActionPerformed(evt);
        }
    });
    popupPlaylist.add(popmenuDeleteSong);
    popupPlaylist.add(jSeparator1);
    
    popmenuPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Play.png"))); // NOI18N
    popmenuPlay.setText("Play");
    popmenuPlay.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            popmenuPlayActionPerformed(evt);
        }
    });
    popupPlaylist.add(popmenuPlay);

    popmenuStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop-red-icon.png"))); // NOI18N
    popmenuStop.setText("Stop");
    popmenuStop.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            popmenuStopActionPerformed(evt);
        }
    });
    popupPlaylist.add(popmenuStop);

    popmenuPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Pause-icon.png"))); // NOI18N
    popmenuPause.setText("Pause");
    popmenuPause.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            popmenuPauseActionPerformed(evt);
        }
    });
    popupPlaylist.add(popmenuPause);
}
    
   // create methods
    private void menuSkin1ActionPerformed(java.awt.event.ActionEvent evt) {
        SkinUtils.changeSkin(this, UIManager.getSystemLookAndFeelClassName());
    }
    
    private void menuSkin2ActionPerformed(java.awt.event.ActionEvent evt) {
        SkinUtils.changeSkin(this, new TextureLookAndFeel());
    }
    
    private void menuSkin3ActionPerformed(java.awt.event.ActionEvent evt) {
        SkinUtils.changeSkin(this, new HiFiLookAndFeel());
    }
    
   
    private void btnAddSongActionPerformed(java.awt.event.ActionEvent evt) {
        FileUtils.addFileFilter(fileChooser, mp3FileFilter);
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                MP3 mp3 = new MP3(file.getName(), file.getPath());
                if (!mp3ListModel.contains(mp3)) {
                    mp3ListModel.addElement(mp3);
                }
            }
        }
    }
    
    
    private void butDeleteSongActionPerformed (java.awt.event.ActionEvent evt){
    	int[] indexPlaylist = listPlayList.getSelectedIndices();
    	if (indexPlaylist.length > 0){
    		ArrayList<MP3> mp3ListForRemove = new ArrayList<MP3>();
    		for (int i = 0; i < indexPlaylist.length; i++){
    			MP3 mp3 = (MP3)mp3ListModel.getElementAt(indexPlaylist[i]);
    			mp3ListForRemove.add(mp3);
    		}
    		for (MP3 mp3 : mp3ListForRemove){
    			mp3ListModel.removeElement(mp3);
    		}
    	}
    }
    
    private void butSelectPrevActionPerformed (java.awt.event.ActionEvent evt){
    	selectPrevSong();
    }
    
    private void butSelectNextActionPerformed (java.awt.event.ActionEvent evt){
    	selectNextSong();
    }
    
    private void butPlaySongActionPerformed (java.awt.event.ActionEvent evt){
    	playFile();
    }

    private void menuSavePlaylistActionPerformed (java.awt.event.ActionEvent evt){
    	FileUtils.addFileFilter(fileChooser, playlistFileFilter);
    	int result = fileChooser.showSaveDialog(this);
    	if (result == JFileChooser.APPROVE_OPTION){
    		File selectedFile = fileChooser.getSelectedFile();
    		if(selectedFile.exists()){
    			int resultOverride = JOptionPane.showConfirmDialog(this, "File exists!", "Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION);
    			if (resultOverride == JOptionPane.NO_OPTION){
    				menuSavePlaylistActionPerformed(evt);
    			} else if (resultOverride == JOptionPane.CANCEL_OPTION){
    				fileChooser.cancelSelection();
    			} else {
    				fileChooser.approveSelection();
    			}
    		}
    		String fileExtension = FileUtils.getFileExtension(selectedFile);
    		String fileNameForSave = (fileExtension !=null && fileExtension.equals(PLAYLIST_FILE_EXTENSION)) ? selectedFile.getPath() : selectedFile.getPath() + "." + PLAYLIST_FILE_EXTENSION;
    		FileUtils.serialize(mp3ListModel, fileNameForSave);
    	}
    }
    
    private void menuOpenPlaylistActionPerformed (java.awt.event.ActionEvent evt){
    	FileUtils.addFileFilter(fileChooser, playlistFileFilter);
    	int result = fileChooser.showOpenDialog(this);
    	
    	if (result == JFileChooser.APPROVE_OPTION){
    		File selectedFile = fileChooser.getSelectedFile();
    		DefaultListModel mp3ListModel = (DefaultListModel) FileUtils.deserialize(selectedFile.getPath());
    		this.mp3ListModel = mp3ListModel;
    		listPlayList.setModel(mp3ListModel);
    	}
    }
    
    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {
       searchSong();
    }
    
    private void txtSearchFocusGained(java.awt.event.FocusEvent evt) {
        if (txtSearch.getText().equals(INPUT_SONG_NAME)) {
            txtSearch.setText(EMPTY_STRING);
        }
    }
    
    private void txtSearchFocusLost(java.awt.event.FocusEvent evt) {
        if (txtSearch.getText().trim().equals(EMPTY_STRING)) {
            txtSearch.setText(INPUT_SONG_NAME);
        }
    }
    
    private void butStopSongActionPerformed(java.awt.event.ActionEvent evt) {
        player.stop();
    }

    private void butPauseSongActionPerformed(java.awt.event.ActionEvent evt) {
        player.pause();
    }

    private void slideVolumeStateChanged(javax.swing.event.ChangeEvent evt) {
    	player.setVolume(slideVolume.getValue(),slideVolume.getMaximum());
        if (slideVolume.getValue()==0){
            tglbtnVolume.setSelected(true);
        }else {
            tglbtnVolume.setSelected(false);
        }
    }
    
    private int currentVolumeValue;
    private void tglbutVolumeActionPerformed(java.awt.event.ActionEvent evt) {
        if (tglbtnVolume.isSelected()){
            currentVolumeValue = slideVolume.getValue();
            slideVolume.setValue(0);
        }else{
            slideVolume.setValue(currentVolumeValue);
        }
    }
    private void butNextSongActionPerformed(java.awt.event.ActionEvent evt) {
    	if (selectNextSong()) {
            playFile();
        }
    }

    private void butPrevSongActionPerformed(java.awt.event.ActionEvent evt) {
    	if (selectPrevSong()) {
            playFile();
        }
    }
    
    private void menuExitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }
    
    private void slideProgressStateChanged(javax.swing.event.ChangeEvent evt) {

        if (slideProgress.getValueIsAdjusting() == false) {
            if (moveAutomatic == true) {
                moveAutomatic = false;
                posValue = slideProgress.getValue() * 1.0 / 1000;
                processSeek(posValue);
            }
        } else {
            moveAutomatic = true;
            movingFromJump = true;
        }
    }
    
    private void lstPlayListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getModifiers() == InputEvent.BUTTON1_MASK && evt.getClickCount() == 2) {
            playFile();
        }
    }
    
    private void lstPlayListKeyPressed(java.awt.event.KeyEvent evt) {
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            playFile();
        }
    }
   
    private void txtSearchKeyPressed(java.awt.event.KeyEvent evt) {
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            searchSong();
        }
    }
    
    private void popmenuAddSongActionPerformed(java.awt.event.ActionEvent evt) {
        btnAddSongActionPerformed(evt);
    }
 
    private void popmenuDeleteSongActionPerformed(java.awt.event.ActionEvent evt) {
        butDeleteSongActionPerformed(evt);
    }
    
    private void popmenuPlayActionPerformed(java.awt.event.ActionEvent evt) {
        butPlaySongActionPerformed(evt);
    }

    private void popmenuStopActionPerformed(java.awt.event.ActionEvent evt) {
        butStopSongActionPerformed(evt);
    }

    private void popmenuPauseActionPerformed(java.awt.event.ActionEvent evt) {
        butPauseSongActionPerformed(evt);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MP3Player.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MP3Player.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MP3Player.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MP3Player.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MP3Player().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify
    
    private javax.swing.JButton butAddSong;
    private javax.swing.JButton butDeleteSong;
    private javax.swing.JButton butSearch;
    private javax.swing.JButton butSelectPrev;
    private javax.swing.JButton butSelectNext;
    private javax.swing.JMenuBar MenuBar;
    private javax.swing.JMenu menuChangeSkin;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenuItem menuOpenPlaylist;
    private javax.swing.JMenu menuPrefs;
    private javax.swing.JMenuItem menuSavePlaylist;
    private javax.swing.JPopupMenu.Separator menuSeparator;
    private javax.swing.JMenuItem menuSkin1;
    private javax.swing.JMenuItem menuSkin2;
    private javax.swing.JPanel panelVolume;
    private javax.swing.JToggleButton tglbtnVolume;
    private javax.swing.JTextField txtSearch;
    private JMenuItem menuSkin3;
    private JPanel panelSearch;
    private JPanel panelPlay;
    private JButton butPlay;
    private JButton butPause;
    private JButton butStop;
    private JButton butNextSong;
    private JButton butPrevSong;
    private javax.swing.JSlider slideProgress;
    private javax.swing.JSlider slideVolume;
    private javax.swing.JLabel labelSongName;
    private javax.swing.JPanel panelProgress;
    private javax.swing.JList listPlayList;
    private javax.swing.JMenuItem popmenuAddSong;
    private javax.swing.JMenuItem popmenuDeleteSong;
    private javax.swing.JPopupMenu popupPlaylist;
    private final JFileChooser fileChooser = new JFileChooser();
    private JLabel labelInfo;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem popmenuPause;
    private javax.swing.JMenuItem popmenuPlay;
    private javax.swing.JMenuItem popmenuStop;
}
