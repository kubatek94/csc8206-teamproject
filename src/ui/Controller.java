package ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import ui.utilities.*;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import ui.utilities.GridRectangle;

public class Controller implements Initializable {

    @FXML private Group grid;
    @FXML private GridPane palette;

    @FXML private List<UiBlock> blockList;

    @FXML private ScrollPane scrollPane;

    public static List<UiBlock> blocks;
    private GridRectangle[][] rectangles;
    public final int GRID_HEIGHT = 50;
    public final int GRID_WIDTH = 50;
    public static final int CELL_SIZE = 30;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*GridRectangle[][]*/ rectangles = new GridRectangle[GRID_WIDTH][GRID_HEIGHT];
        blocks = new ArrayList<UiBlock>();
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                GridRectangle r1 = new GridRectangle(i*CELL_SIZE, j*CELL_SIZE, CELL_SIZE, CELL_SIZE, grid, rectangles);
                rectangles[i][j] = r1;
                grid.getChildren().add(r1);
                //addGridListener(r1);
            }
        }

        System.out.println(grid.getChildren().size());
        palette.setVgap(5);

        palette.setPrefSize(60, 60);
        for (UiBlock b: blockList) {
            addPaletteListener(b);
        }
        scrollPane.setMaxSize(750, 750);
    }

    private void addPaletteListener(final ImageView source) {
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

    @FXML private void onDeleteAction(ActionEvent event) 
    {
        ArrayList<UiBlock> found = new ArrayList<UiBlock>();
        for (UiBlock b : blocks)
        {
            if (b.isSelected())
            {
                //System.out.println(rectangles[((int) b.getX() / Controller.CELL_SIZE)][((int)b.getY() / Controller.CELL_SIZE)].isUsed());
                rectangles[((int) b.getX() / Controller.CELL_SIZE)][((int)b.getY() / Controller.CELL_SIZE)].freeUpSpace(b.getClass().getSimpleName());
                grid.getChildren().remove(b);
                found.add(b);
            }
        }
        blocks.removeAll(found);
    }
}