package ui;

import backend.NetworkDeserializationException;
import backend.NetworkSerializationException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import ui.utilities.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import ui.utilities.GridRectangle;

public class Controller implements Initializable {
    public static final int GRID_HEIGHT = 50;
    public static final int GRID_WIDTH = 50;
    public static final int CELL_SIZE = 30;

    @FXML private Group grid;
    @FXML private GridPane palette;
    @FXML private List<UiBlock> blockList; //list of blocks in the Pallet
    @FXML private ScrollPane scrollPane;

    private GridRectangle[][] rectangles = new GridRectangle[GRID_WIDTH][GRID_HEIGHT];
    private UiNetwork uiNetwork;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
		ObservableList<Node> children = grid.getChildren();
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                GridRectangle r1 = new GridRectangle(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE, this);
                rectangles[i][j] = r1;
                children.add(r1);
            }
        }
        
        
        uiNetwork = new UiNetwork(this);

        palette.setVgap(5);
        palette.setPrefSize(60, 60);
        for (UiBlock b: blockList) {
            addPaletteListener(b);
        }
        scrollPane.setMaxSize(750, 750);
    }
    
    /**
     * Method to access the controller components.
     * @return the ui grid component.
     */
    public Group getGrid() {
    	return grid;
    }
    
    public GridRectangle[][] getRectangles() {
    	return rectangles;
    }
    
    /**
     * Method to access the controller components.
     * @return the uiNetwork component.
     */
    public UiNetwork getUiNetwork() {
    	return uiNetwork;
    }

    private void addPaletteListener(final Canvas source) {
        source.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                /* drag was detected, start drag-and-drop gesture*/
                System.out.println("onDragDetected");

                /* allow any transfer mode */
                Dragboard db = source.startDragAndDrop(TransferMode.ANY);
                /* put a string on dragboard */
                ClipboardContent content = new ClipboardContent();
                String type = source.getClass().getSimpleName();
                System.out.println(type);
                content.putString(type);
                db.setContent(content);

                event.consume();
            }
        });

        source.setOnDragDone(new EventHandler <DragEvent>() {
            public void handle(DragEvent event) {
                /* the drag-and-drop gesture ended */
                System.out.println("onDragDone");
                /* if the data was successfully moved, clear it */
                if (event.getTransferMode() == TransferMode.MOVE) {
//                    source.setText("");
                }

                event.consume();
            }
        });
    }

    @FXML private void onDeleteAction(ActionEvent event) {
    	uiNetwork.deleteUiBlocks(true); //true - delete selected elements only
    }

    @FXML private void onLoadAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Network File");
        File networkFile = fileChooser.showOpenDialog(grid.getScene().getWindow());

        if (networkFile != null) {
            try {
            	FileInputStream inputStream = new FileInputStream(networkFile);
            	
    	        // Must delete currently active network from the frontend grid
    	    	uiNetwork.deleteUiBlocks(false);
            	
    	    	// and override with new network
    			uiNetwork = UiNetwork.load(inputStream);
    			uiNetwork.setController(this);
    			
    			//redraw network elements
    			uiNetwork.refreshUi();
    		} catch (FileNotFoundException | NetworkDeserializationException e) {
    			System.out.println(e.getMessage()); //TODO: show an error message to the user
    		}
        }
    }

    @FXML private void onSaveAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Save File");
        File networkFile = fileChooser.showSaveDialog(grid.getScene().getWindow());
        
        if (networkFile != null) {
            try {
            	FileOutputStream outputStream = new FileOutputStream(networkFile);
    			uiNetwork.save(outputStream);
    		} catch (FileNotFoundException | NetworkSerializationException e) {
    			System.out.println(e.getMessage()); //TODO: show an error message to the user
    		}	
        }
    }
    
    @FXML private void onValidateNetworkAction(ActionEvent event) {    	
    	System.out.println("Network is " + (uiNetwork.getNetwork().isValid() ? "valid" : "invalid"));
    }
    
    @FXML private void onDumpAction(ActionEvent event) {
    	System.out.println("UiNetwork:");
    	System.out.println(uiNetwork);
    	
    	System.out.println("\nNetwork: ");
    	System.out.println(uiNetwork.getNetwork());
    }
}
