package view;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import comparator.SgCmpr;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import objects.Song;

public class ListController {
	@FXML         
	   ListView<Song> listView;  
	@FXML
	   Text songTitle, songArtist, songAlbum, songYear;

	   private ObservableList<Song> obsList;  
	  
	   public void start(Stage mainStage) {                
		  obsList = FXCollections.observableArrayList(readFromFile("src/songs.txt")); 
	      listView.setItems(obsList); 
	      
	      listView.setCellFactory(new Callback<ListView<Song>, ListCell<Song>>(){
	    	  
	            @Override
	            public ListCell<Song> call(ListView<Song> p) {	                 
	                ListCell<Song> cell = new ListCell<Song>(){
	                    @Override
	                    protected void updateItem(Song s, boolean bln) {
	                        super.updateItem(s, bln);
	                        if (s != null) {
	                            setText(s.getTitle());
	                        }
	                        else if (s == null)
	                        {
	                        	setText(null);
	                        }
	                    }
	 
	                };
	                 
	                return cell;
	            }
	        });
	      if (!obsList.isEmpty())
	    	  listView.getSelectionModel().select(0);
	      showSong();
	      listView
	      .getSelectionModel()
	      .selectedItemProperty()
	      .addListener(
	    		  (obs, oldVal, newVal) -> 
	    		  showSong());
	      
	      mainStage.setOnCloseRequest(event -> {
	    	  PrintWriter writer;
	    	  	try {
	    	  			File file = new File ("src/songs.txt");
	    	  			file.createNewFile();
	    	  			writer = new PrintWriter(file);
						for(Song s: obsList)
				    	  {
				    		  writer.println(s.getTitle());
				    		  writer.println(s.getArtist());
				    		  writer.println(s.getAlbum());
				    		  writer.println(s.getYear());
				    		  
				    	  }
				    	 writer.close(); 
			} catch (Exception e) {
				e.printStackTrace();
			}    	 
	      });
	   }
	   @FXML
	   private void handleEditButton(ActionEvent event) {
		   if (obsList.isEmpty()) {
			   showError("There is nothing to edit.");
			   return;
		   }
		   
		   int index = listView.getSelectionModel().getSelectedIndex();
		   Dialog<Boolean> dialog = new Dialog<>();
		   dialog.setTitle("Edit Song");
		   dialog.setHeaderText("Edit info and press Okay");
		   dialog.setResizable(true);
		   
		   Label titleLabel = new Label("Title: ");
		   Label artistLabel = new Label("Artist: ");
		   Label albumLabel = new Label("Album: ");
		   Label yearLabel = new Label("Year: ");
		   TextField titleTextField = new TextField(songTitle.getText());
		   TextField artistTextField = new TextField(songArtist.getText());
		   TextField albumTextField = new TextField(songAlbum.getText());
		   TextField yearTextField = new TextField(songYear.getText());

		   GridPane grid = new GridPane();
		   grid.add(titleLabel, 1, 1);
		   grid.add(titleTextField, 2, 1);
		   grid.add(artistLabel, 1, 2);
		   grid.add(artistTextField, 2, 2);
		   grid.add(albumLabel, 1, 3);
		   grid.add(albumTextField, 2, 3);
		   grid.add(yearLabel, 1, 4);
		   grid.add(yearTextField, 2, 4);
		   
		   dialog.getDialogPane().setContent(grid);
		   
		   ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		   dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		   
		   dialog.setResultConverter(new Callback<ButtonType, Boolean>() {
			   @Override
			   public Boolean call(ButtonType b) {
				   if (b == buttonTypeOk) {
					   String error = checkFields(titleTextField.getText(), artistTextField.getText(),
							   albumTextField.getText(), yearTextField.getText());
					   
					   if (error != null) {
						   if (error.equals("Title and Artist cannot already exist in library.") 
								   && titleTextField.getText().equals(listView.getSelectionModel().getSelectedItem().getTitle()) 
								   && artistTextField.getText().equals(listView.getSelectionModel().getSelectedItem().getArtist()))
								   {
							   			return true;
								   }
						   showError(error);
						   return null;
					   }
					   return true;
				   }
				   return null;
			   }
		   });
		   
		   Optional<Boolean> result = dialog.showAndWait();
		   
		   if (result.isPresent()) {
			   Song temp = new Song(titleTextField.getText(),artistTextField.getText(),albumTextField.getText(),yearTextField.getText());
			   obsList.set(index,temp);
			   listView.getSelectionModel().select(index);
			   showSong();
		   }
	   }
	   
	   @FXML
	   private void handleAddButton(ActionEvent event) {
		   int index = listView.getSelectionModel().getSelectedIndex();
		   Dialog<Song> dialog = new Dialog<>();
		   dialog.setTitle("Add Song");
		   dialog.setHeaderText("Add a New Song!");
		   dialog.setResizable(true);
		   
		   Label titleLabel = new Label("Title: ");
		   Label artistLabel = new Label("Artist: ");
		   Label albumLabel = new Label("Album: ");
		   Label yearLabel = new Label("Year: ");
		   TextField titleTextField = new TextField();
		   TextField artistTextField = new TextField();
		   TextField albumTextField = new TextField();
		   TextField yearTextField = new TextField();
		   
		   GridPane grid = new GridPane();
		   grid.add(titleLabel, 1, 1);
		   grid.add(titleTextField, 2, 1);
		   grid.add(artistLabel, 1, 2);
		   grid.add(artistTextField, 2, 2);
		   grid.add(albumLabel, 1, 3);
		   grid.add(albumTextField, 2, 3);
		   grid.add(yearLabel, 1, 4);
		   grid.add(yearTextField, 2, 4);
		   
		   dialog.getDialogPane().setContent(grid);
		   
		   ButtonType buttonTypeOk = new ButtonType("Add", ButtonData.OK_DONE);
		   dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		   
		   dialog.setResultConverter(new Callback<ButtonType, Song>() {
			   @Override
			   public Song call(ButtonType b) {
				   if (b == buttonTypeOk) {
					   
					   String error = checkFields(titleTextField.getText(), artistTextField.getText(),
							   albumTextField.getText(), yearTextField.getText());
					   
					   if (error != null) {
						   showError(error);
						   return null;
					   }
						
					   if (yearTextField.getText().trim().isEmpty())
						   return new Song(titleTextField.getText().trim(),
								   artistTextField.getText().trim(),
								   albumTextField.getText().trim(),
								   "");
						   
					   return new Song(titleTextField.getText().trim(),
							   artistTextField.getText().trim(),
							   albumTextField.getText().trim(),
							   yearTextField.getText().trim());
				   }
				   return null;
			   }
		   });
		   Optional<Song> result = dialog.showAndWait();
		   if (result.isPresent()) {
			   Song tempSong = (Song) result.get();
			   obsList.add(tempSong);
			   FXCollections.sort(obsList, new SgCmpr());
			   if (obsList.size() == 1) {
				   listView.getSelectionModel().select(0);
			   }
			   else
			   {
				   index = 0;
				   for(Song s: obsList)
				   {
					   
					   if(s == tempSong)
					   {
						  listView.getSelectionModel().select(index);
						  break;
					   }
					   
					   index++;
				   }
			   }
				   
		   }
	   }
	   @FXML
	   private void handleDeleteButton(ActionEvent event) {
		   if (obsList.isEmpty()) {
			   showError("There is nothing to delete.");
			   return;
		   }
		   
		   Song item = listView.getSelectionModel().getSelectedItem();
		   int index = listView.getSelectionModel().getSelectedIndex();
		   
		   Alert alert = 
				   new Alert(AlertType.INFORMATION);
		   alert.setTitle("Delete Item");
		   alert.setHeaderText(
				   "There's no going back!");

		   String content = "Are you sure you want to delete " + item.getTitle() + "?";

		   alert.setContentText(content);

		   Optional<ButtonType> result = alert.showAndWait();
		   if (result.isPresent()) {
			   obsList.remove(item);
			   if (obsList.isEmpty()) {
				   songTitle.setText("");
				   songArtist.setText("");
				   songAlbum.setText("");
				   songYear.setText("");
			   }
			  else if(index == obsList.size()-1)
			   {
				   listView.getSelectionModel().select(index--);
			   }
			   else
			   {
				   listView.getSelectionModel().select(index++);
			   }
			   showSong();
		   }
		   
		   
			
	   }
	  private void showSong() {
		   if (listView.getSelectionModel().getSelectedIndex() < 0)
			   return;
		   Song s = listView.getSelectionModel().getSelectedItem();
		   songTitle.setText(s.getTitle());
		   songArtist.setText(s.getArtist());
		   songAlbum.setText(s.getAlbum());
		   songYear.setText(s.getYear());
	   }
	  @FXML
	   private void listKeyPress(KeyEvent event) {
		   if(event.getCode() == KeyCode.UP ||
				   event.getCode() == KeyCode.DOWN)
			   showSong();
	   }
	  private ArrayList<Song> readFromFile(String filePathName)
	   {
		   ArrayList <Song> songs = new ArrayList<Song>();
		   BufferedReader br;
		   Path filePath = Paths.get(filePathName);
		   try {

				if (!new File(filePathName).exists())
				{
				   return songs;
				}
			   br = Files.newBufferedReader(filePath);
			   String line = br.readLine();
				
			   while (line != null) { 
		              
				   String title = line;
		               
				   line = br.readLine();
				   String artist = line;
		               
				   line = br.readLine();
				   String album = line;
		               
				   line = br.readLine();
				   String year = line;
		               
				   Song temp = new Song(title, artist, album, year);
				   songs.add(temp);
		               
				   line = br.readLine(); 
			   }  
				
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
		   Collections.sort(songs, new SgCmpr());
		   return songs;
	   }
	   private void showError(String error) {
		   Alert alert = 
				   new Alert(AlertType.INFORMATION);
		   alert.setTitle("Error");
		   alert.setHeaderText("Error");
		   String content = error;
		   alert.setContentText(content);
		   alert.showAndWait();
	   }
	   private String checkFields(String title, String artist, String album, String year) {
		   if (title.trim().isEmpty())
			   return "Title cannot be empty.";
		   else if (artist.trim().isEmpty())
			   return "Artist cannot be empty.";
		   else if (!isUniqueSong(title, artist))
			   return "Title and Artist cannot already exist in library.";
		   
		   else if (!year.trim().isEmpty()) {
			   if (!year.trim().matches("[0-9]+"))
				   return "Year must only contain numbers.";
			   else if (year.trim().length() != 4)
				   return "Year must be 4 digits long.";
		   }
		   
		   return null;
	   }
	   private boolean isUniqueSong(String title, String artist) {
		   for (Song s : obsList) {
			   				   
			   if (s.getTitle().toLowerCase().equals(title.trim().toLowerCase()) &&
					   s.getArtist().toLowerCase().equals(artist.trim().toLowerCase()))
			   {
				   return false;
			   }
				   
		   }
		   return true;
	   }
	
}