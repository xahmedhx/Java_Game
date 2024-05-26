package views;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import model.characters.*;
import model.characters.Character;
import engine.Game;
import exceptions.InvalidTargetException;
import exceptions.MovementException;
import exceptions.NoAvailableResourcesException;
import exceptions.NotEnoughActionsException;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.collectibles.Collectible;
import model.collectibles.Supply;
import model.collectibles.Vaccine;
import model.world.Cell;
import model.world.CharacterCell;
import model.world.CollectibleCell;
import model.world.TrapCell;
import javafx.scene.control.ButtonBar;


public class MainApplicaton extends Application implements EventHandler<ActionEvent> {

    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 700;
    
    private Button startButton;
    ///////////////////Game Scene//////
    private VBox p;
    private Button attack;
    private Button cure;
    private Hero selectedHero;
    //////////////////Selection Hero Scene//////////    
    private VBox p1;
    private Stage primaryStage;
    private Scene startScene;
    private Scene gameScene;
    private Scene heroSelectionScene;
    private GridPane mapGridPane = new GridPane() ;

    private VBox info=new VBox();
    Image Scene1 = new Image("Scene1 3.jpg");
    Image Scene2_3 = new Image ("Scene1 2.jpg");
    

    @Override
    public void start(Stage primaryStage)  {
        this.primaryStage = primaryStage;
        
        try {
            Game.loadHeroes("heroes.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        createStartScene();
        createHeroSelectionScene();
        
        primaryStage.setTitle("THE LAST OF US: LEGACY");
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setScene(startScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    @Override
	public void handle(ActionEvent event) {
		if(event.getSource()==startButton) {
	         primaryStage.setScene(heroSelectionScene);
		}
    }
  

    
    public void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void showAlertClose(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setOnCloseRequest(event -> {
            primaryStage.close();
        });
        alert.showAndWait();
    }
    
    private void createStartScene() {
        startButton = new Button("Start");
        startButton.setOnAction(this);
        startButton.setStyle("-fx-font-size: 60px; " +
                "-fx-text-fill: transparent; " +
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-padding: 20px 20px; " +
                "-fx-font-family: 'JMH HORROR 3D';");

        StackPane vbox = new StackPane(); // Spacing between buttons
        vbox.getChildren().addAll(new ImageView(Scene1),startButton);
        vbox.setAlignment(Pos.CENTER);

        startScene = new Scene(vbox, WINDOW_WIDTH, WINDOW_HEIGHT,Color.BLACK);
    }
    
    public void createHeroSelectionScene() {
        TilePane heroes = new TilePane();
        heroes.setPrefSize(1000, 600);
        heroes.setHgap(10);
        heroes.setVgap(10);
        heroes.setAlignment(Pos.CENTER);

        for (Hero h : Game.availableHeroes) {
            Button btn2 = new Button("Type: " + h.getClass().getSimpleName() + "\n" +
                    "Name: " + h.getName() + "\n" +
                    "Max HP: " + h.getMaxHp() + "\n" +
                    "Attack Damage: " + h.getAttackDmg() + "\n" +
                    "Max Actions: " + h.getMaxActions());
            btn2.setMinWidth(200);
            btn2.setMinHeight(200);
            btn2.getStyleClass().add("horror-button"); // Add custom CSS class for horror design
            heroes.getChildren().add(btn2);

            btn2.setOnAction(e -> {
            	selectedHero=h;
                Game.startGame(h);
                primaryStage.setScene(createGameScene());
            });
        }

        StackPane stackPane = new StackPane();
       stackPane.getChildren().addAll(new ImageView( Scene2_3),heroes);
        stackPane.setPrefSize(1000, 700);

        heroSelectionScene = new Scene(stackPane);
        heroSelectionScene.getStylesheets().add(getClass().getResource("horror-styles.css").toExternalForm()); // Load custom CSS file
    }
    public Scene createGameScene() {
    	//Game.startGame(selectedHero);
        p = new VBox(10);
        p.setPadding(new Insets(10));

        p1 = new VBox(10);
        p1.setPadding(new Insets(10));
        

        attack = new Button("Attack");
        attack.setOnAction(this);
        attack.setFocusTraversable(false);

        cure = new Button("Cure");
        cure.setOnAction(this);
        cure.setFocusTraversable(false);

        showOnMap(mapGridPane);
        
        HBox root1 = new HBox(30);
        root1.setPadding(new Insets(10));
        root1.getChildren().addAll(information(info),showOnMap(mapGridPane));
        root1.setSpacing(200);
        
        StackPane root =new StackPane();
        root.getChildren().addAll(new ImageView (Scene2_3),root1);
        

        gameScene= new Scene(root, WINDOW_WIDTH,WINDOW_HEIGHT);
        gameScene.getStylesheets().add(getClass().getResource("horror-styles.css").toExternalForm());
        gameScene.setOnKeyPressed(event -> {
    	    KeyCode keyCode = event.getCode();
    	    try {
    	        if (keyCode == KeyCode.RIGHT) {
    	            selectedHero.move(Direction.UP);
    	            showOnMap(mapGridPane);
    	            information(info);
    	        } else if (keyCode == KeyCode.LEFT) {
    	            selectedHero.move(Direction.DOWN);
    	            showOnMap(mapGridPane);
    	            information(info);
    	        } else if (keyCode == KeyCode.UP) {
    	            selectedHero.move(Direction.RIGHT);
    	            showOnMap(mapGridPane);
    	            information(info);
    	        } else if (keyCode == KeyCode.DOWN) {
    	            selectedHero.move(Direction.LEFT);
    	            showOnMap(mapGridPane);
    	            information(info);
    	        }else if (keyCode == KeyCode.E) {
    	            Game.endTurn();
    	            showOnMap(mapGridPane);
    	            information(info);
    	        } else if (keyCode == KeyCode.S) {
    	            selectedHero.useSpecial();
    	            showOnMap(mapGridPane);
    	            information(info);
    	        }
    	        else if (keyCode == KeyCode.A) {
    	            selectedHero.attack();
    	            showOnMap(mapGridPane);
    	            information(info);
    	        }
    	        else if (keyCode == KeyCode.C) {
    	            selectedHero.cure();
    	            showOnMap(mapGridPane);
    	            information(info);
    	        }
    	    } catch (MovementException | NotEnoughActionsException e1) {
    	        showAlert("Move Error", "Unable to move: " + e1.getMessage());
    	    } catch (NoAvailableResourcesException | InvalidTargetException e2) {
    	        showAlert("Special Ability Error", "Unable to use special ability: " + e2.getMessage());
    	    }
    	});
        return gameScene;
        
    }
    
    public GridPane showOnMap(GridPane group) {
        group.getChildren().clear();
        group.setHgap(1);
        group.setVgap(2);
        
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Button rect = new Button();
                rect.setMaxWidth(38);
                rect.setMaxHeight(38);
                rect.setMinHeight(38);
                rect.setMinWidth(38);
                rect.setFocusTraversable(false);
                rect.setStyle("-fx-background-color:#ffffff;");
                
                if (Game.map[i][14 - j].isVisible()) {
                    rect.setStyle("-fx-background-color:#0000ff;");
                    
                    if (Game.map[i][14 - j] instanceof CharacterCell) {
                        CharacterCell characterCell = (CharacterCell) Game.map[i][14 - j];
                        if (characterCell.getCharacter() != null) {
                            Character character = characterCell.getCharacter();
                            rect.setOnAction(e -> {
                                selectedHero.setTarget(character);
                                if (character instanceof Hero) {
                                    selectedHero = (Hero) character;
                                }
                            });
                        }
                        
                        if (characterCell.getCharacter() instanceof Zombie) {
                        	 int zombie = characterCell.getCharacter().getMaxHp();
                        	    Image zombieImage = new Image("z.png", 38, 38, false, false);
                        	    ImageView zombieView = new ImageView(zombieImage);
                        	    rect.setGraphic(zombieView);
                        	    Tooltip tooltip = new Tooltip("Health: "+ zombie );
                        	    Tooltip.install(rect, tooltip);	
                        } else if (characterCell.getCharacter() instanceof Hero) {
                            Hero hero = (Hero) characterCell.getCharacter();
                            rect.setStyle("-fx-background-color:#ffffff;");
                            
                            if (hero instanceof Medic) {
                                Image medicImage = new Image("m.png", 38, 38, false, false);
                                ImageView medicView = new ImageView(medicImage);
                                rect.setGraphic(medicView);
                            } else if (hero instanceof Explorer) {
                                Image explorerImage = new Image("e.png", 38, 38, false, false);
                                ImageView explorerView = new ImageView(explorerImage);
                                rect.setGraphic(explorerView);
                            } else if (hero instanceof Fighter) {
                                Image fighterImage = new Image("f.png", 38, 38, false, false);
                                ImageView fighterView = new ImageView(fighterImage);
                                rect.setGraphic(fighterView);
                            }
                        }
                    } else if (Game.map[i][14 - j] instanceof CollectibleCell) {
                        CollectibleCell collectibleCell = (CollectibleCell) Game.map[i][14 - j];
                        if (collectibleCell.getCollectible() instanceof Supply) {
                            Image supplyImage = new Image("s.png", 38, 38, false, false);
                            ImageView supplyView = new ImageView(supplyImage);
                            rect.setGraphic(supplyView);
                        } else if (collectibleCell.getCollectible() instanceof Vaccine) {
                            Image vaccineImage = new Image("v.png", 38, 38, false, false);
                            ImageView vaccineView = new ImageView(vaccineImage);
                            rect.setGraphic(vaccineView);
                        }
                        
                    }
                } else {
                    rect.setStyle("-fx-background-color:#808080;");
                }
                
                group.add(rect, i, j);
            }
        }
        if(Game.checkGameOver()) {
        	showAlertClose("Game Over !", "Try Again Later !");
        }
        if(Game.checkWin()) {
        	showAlertClose("You Win !", "Congratulations !");
        }
        
        return group;
    }


    private VBox information(VBox info) {
        info.getChildren().clear();
        info.setAlignment(Pos.CENTER);
        for (int i = 0; i < Game.heroes.size(); i++) {
            Button btn = new Button("Type: " + Game.heroes.get(i).getClass().getSimpleName() + "\n" +
                                    "Name: " + Game.heroes.get(i).getName() + "\n" +
                                    "HP: " + Game.heroes.get(i).getCurrentHp() + "\n" +
                                    "AttackDamage: " + Game.heroes.get(i).getAttackDmg() + "\n" +
                                    "ActionsAvailable: " + Game.heroes.get(i).getActionsAvailable() + "\n" +
                                    "Supplies: " + Game.heroes.get(i).getSupplyInventory().size() + "\n" +
                                    "Vaccines: " + Game.heroes.get(i).getVaccineInventory().size());
            btn.setId(String.valueOf(i));
            btn.setFocusTraversable(false);
            btn.setMinHeight(150);
            btn.setMinWidth(150);
            btn.getStyleClass().add("horror-button");
            info.getChildren().add(btn);
            btn.setOnAction(e -> {
                selectedHero = Game.heroes.get(Integer.parseInt(btn.getId()));
            });
        }
        return info;
    }



    public static void main(String[] args) {
        launch(args);
    }
	

}
