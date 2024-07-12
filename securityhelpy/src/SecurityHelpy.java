import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;

public class SecurityHelpy extends JFrame {
    private static final String version_app = "1.1.0";
    private static final String creditos = "\nGracias por usar Security Helpy!\nDesarrollado por: TripleAn\nGitHub: https://github.com/triplean\n";
    private JTextField searchField;
    private JButton searchButton, homeButton, speedButton;
    private JList<String> postsList;
    private JEditorPane postsText;

    public SecurityHelpy() {
        checkUpdates();
        createWidgets();
        loadPosts();
    }

    private void checkUpdates() {
        try {
            URL url = new URL("https://triplean.github.io/projects/SecurityHelpy/resources/ver_java.txt");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Scanner scanner = new Scanner(conn.getInputStream());
            if (scanner.hasNextLine()) {
                String act_ver_on = scanner.nextLine().trim();
                if (!act_ver_on.equals(version_app)) {
                    JOptionPane.showMessageDialog(this, "Parece que hay una nueva versión de Security Helpy. Dirígete a https://triplean.github.io/sh/ para descargarla!", "Gestor de actualizaciones", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            scanner.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No pudimos conectarnos al servidor para buscar actualizaciones.", "Gestor de actualizaciones", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createWidgets() {
        setTitle("Security Helpy");
        String iconPath = "D:/Security Helpy/img/logo_new.png";
        File icon = new File(iconPath);
        ImageIcon imgicon = new ImageIcon(iconPath);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(imgicon.getImage());

        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel buscarLabel = new JLabel("Búsqueda:");
        searchField = new JTextField(20);
        searchButton = new JButton("Buscar");
        homeButton = new JButton("Regresar a inicio");
        speedButton = new JButton("Speedruns");
        JLabel ver_lbl = new JLabel("Versión: " + version_app);

        topPanel.add(buscarLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(homeButton);
        topPanel.add(speedButton);
        topPanel.add(ver_lbl);
        add(topPanel, BorderLayout.NORTH);

        postsList = new JList<>();
        postsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        postsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    showPost();
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(postsList);

        postsText = new JEditorPane();
        postsText.setEditable(false);
        postsText.setContentType("text/html");
        JScrollPane textScrollPane = new JScrollPane(postsText);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollPane, textScrollPane);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchHelp();
            }
        });

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHighlightedPosts();
            }
        });

        speedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSpeedrunVersion();
            }
        });
    }

    private void loadPosts() {
        try {
            String dataUrl = "https://triplean.github.io/projects/SecurityHelpy/resources/posts_es.json";
            String jsonText = readUrl(dataUrl);
            JSONArray posts = new JSONArray(jsonText);
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                listModel.addElement(post.getString("title"));
            }
            postsList.setModel(listModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los posts: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHighlightedPosts() {
        try {
            String dataUrl = "https://triplean.github.io/projects/SecurityHelpy/resources/posts_es.json";
            String jsonText = readUrl(dataUrl);
            JSONArray posts = new JSONArray(jsonText);
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                if (post.getBoolean("highlighted")) {
                    listModel.addElement(post.getString("title"));
                }
            }
            postsList.setModel(listModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los posts destacados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchHelp() {
        String searchTerm = searchField.getText().toLowerCase();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa un término de búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String dataUrl = "https://triplean.github.io/projects/SecurityHelpy/resources/posts_es.json";
            String jsonText = readUrl(dataUrl);
            JSONArray posts = new JSONArray(jsonText);
            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.getJSONObject(i);
                String title = post.getString("title").toLowerCase();
                String content = post.getString("html").toLowerCase();
                if (title.contains(searchTerm) || content.contains(searchTerm)) {
                    listModel.addElement(post.getString("title"));
                }
            }
            postsList.setModel(listModel);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al buscar ayuda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPost() {
        String selectedPost = postsList.getSelectedValue();
        if (selectedPost != null) {
            try {
                String dataUrl = "https://triplean.github.io/projects/SecurityHelpy/resources/posts.json";
                String jsonText = readUrl(dataUrl);
                JSONArray posts = new JSONArray(jsonText);
                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.getJSONObject(i);
                    if (post.getString("title").equals(selectedPost)) {
                        postsText.setText(post.getString("html"));
                        break;
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar el contenido de la guía: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openSpeedrunVersion() {
        JOptionPane.showMessageDialog(this, "El modo speedrunner sigue bajo desarrollo.", "Próximamente", JOptionPane.INFORMATION_MESSAGE);
    }

    private String readUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.toString());
        scanner.useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SecurityHelpy app = new SecurityHelpy();
                app.setVisible(true);
            }
        });
    }
}
