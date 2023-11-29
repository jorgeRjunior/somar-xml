package LeitorXML;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.net.URI;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XsdToClassConverter {

  private JFrame frame;
  private JTextField xsdFolderField;
  private JButton xsdBrowseButton;
  private JButton convertButton;
  private JButton githuButton;
  private JTextField packagePathField; 

  public static void main(String[] args) {
    new XsdToClassConverter();
  }

  public XsdToClassConverter() {
    initializeGui();
  }

  private void initializeGui() {
    frame = new JFrame("Totalizador XML");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(550, 250);

    JPanel panel = new JPanel();
    frame.add(panel);
    placeComponents(panel);

    frame.setVisible(true);
  }

  private void placeComponents(JPanel panel) {

    panel.setLayout(null);

    JLabel xsdLabel = new JLabel("XML Folder:");
    xsdLabel.setBounds(10, 50, 80, 25);
    panel.add(xsdLabel);

    xsdFolderField = new JTextField(20);
    xsdFolderField.setBounds(100, 50, 300, 25);
    xsdFolderField.setText(" Pasta com arquivos XML");
    panel.add(xsdFolderField);

    xsdBrowseButton = new JButton("Browse");
    xsdBrowseButton.setBounds(410, 50, 80, 25);
    xsdBrowseButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        browseXsdFolder();
      }
    });
    panel.add(xsdBrowseButton);
    
    JLabel packageLabel = new JLabel("Tag para Somar:");
    packageLabel.setBounds(10, 80, 150, 25);
    panel.add(packageLabel);
    packagePathField = new JTextField(20);
    packagePathField.setBounds(120, 80, 280, 25);
    packagePathField.setText(" Exemplo: <vOrig> então, coloque    vOrig");
    panel.add(packagePathField); 

    convertButton = new JButton("Somar");
    convertButton.setBounds(150, 150, 200, 25);
    convertButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        convertXsds();
      }
    });
    panel.add(convertButton);
    
    githuButton = new JButton("GitHub");
    githuButton.setText("GitHub");
    githuButton.setBounds(400, 150, 100, 25);
    githuButton.addMouseListener(new MouseAdapter() {
    	  public void mouseClicked(MouseEvent e) {
    	    openLink();
    	  }
    	});
    
    panel.add(githuButton);
  }

  private void openLink() {
	  try {
	    Desktop.getDesktop().browse(new URI("https://github.com/jorgeRjunior/jorgeRjunior")); 
	  } catch (Exception ex) {
	    ex.printStackTrace();
	  }
	}
  
    private void browseXsdFolder() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Selecione a pasta com XMLs");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int userSelection = fileChooser.showOpenDialog(frame);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File folder = fileChooser.getSelectedFile();
      xsdFolderField.setText(folder.getAbsolutePath());
    }
  }

  private void convertXsds() {
	  
	  int contArquivos = 0;
	  Double totalSomatorio = 0d;
	  
	  String xsdFolder = xsdFolderField.getText();

	  File folder = new File(xsdFolder);
	  File[] xsdFiles = folder.listFiles(new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	      return name.toLowerCase().endsWith(".xml");
	    }
	  });

	  String tag = packagePathField.getText();
	  
	  for(File xsd : xsdFiles) {
		  
		  contArquivos++;
	    
	    String fileName = xsd.getName();

	    try {
            // Faz o parsing do arquivo XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xsd);

            // Normaliza o documento para garantir que a estrutura seja consistente
            doc.getDocumentElement().normalize();

            // Obtém todos os elementos com a tag <vOrig>
            NodeList listaVOrig = doc.getElementsByTagName(tag);

            // Itera sobre os elementos encontrados
            for (int i = 0; i < listaVOrig.getLength(); i++) {
                Element elemento = (Element) listaVOrig.item(i);
                String valorOrigem = elemento.getTextContent();
                
                totalSomatorio += Double.parseDouble(valorOrigem); 
                // Faça o que precisar com o valor encontrado (valorOrigem)
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    try {

	    } catch (Exception ex) {
	      JOptionPane.showMessageDialog(frame, "Erro ao converter " + fileName);
	    }
	  }
      NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

	  JOptionPane pane = new JOptionPane("Somatória realizada com sucesso! \nArquivos: "+contArquivos+"\nValor Total Somado: "+formatoMoeda.format(totalSomatorio));
	  JDialog dialog = pane.createDialog(null, "Resultado");
	  dialog.setSize(300, 200); // Defina as dimensões desejadas (largura x altura)
	  dialog.setVisible(true);


	}

}