package com.example.texteditor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView fileNameDisplay;
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Text Editor/", s, s1;
    File homeDir = new File(path);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextTextMultiLine);
        fileNameDisplay = findViewById(R.id.fileName);
        checkPermission();
        if(!homeDir.exists()) {
            if(homeDir.mkdir())
                Toast.makeText(this, "Text Editor is ready to use", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT>=25) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.create) {
            newFile();
            return true;
        } else if(id == R.id.save) {
            saveFile();
            return true;
        } else if(id == R.id.saveas) {
            saveAsFile();
            return true;
        } else if(id == R.id.open) {
            openFile();
            return true;
        } else if(id == R.id.rename) {
            rename();
            return true;
        } else if(id == R.id.stats) {
            statisticsCount();
            return true;
        } else if(id == R.id.files) {
            loadFileList();
            return true;
        } else if(id == R.id.delete) {
            delete();
            return true;
        } else if(id == R.id.close) {
            close();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    public void newFile() {
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        fileName.setHint("Enter the filename");
        layout.addView(fileName);
        dialog_box.setView(layout);
        dialog_box.setTitle("Create File");
        dialog_box.setPositiveButton("Create", (dialog, which) -> {
            String textFile = fileName.getText().toString();
            if(!textFile.endsWith(".txt")) textFile += ".txt";
            File file = new File(homeDir, textFile);
            s = textFile.substring(0, textFile.length()-4);
            if (file.exists()) {
                Toast.makeText(this, s + " already exists! Type another name", Toast.LENGTH_LONG).show();
            } else {
                writeFile(textFile, editText.getText().toString());
                Toast.makeText(this, s + " created successfully", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel",(dialog,which) -> dialog.cancel()).show();
    }

    private void saveFile() {
        String textFile = fileNameDisplay.getText().toString();
        File file = new File(homeDir, textFile);
        s = textFile.substring(0, textFile.length()-4);
        if (file.exists()) {
            writeFile(fileNameDisplay.getText().toString(), editText.getText().toString());
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, s + " does not exists, please create a new file", Toast.LENGTH_LONG).show();
        }
    }

    private void saveAsFile() {
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        fileName.setHint("Enter new filename");
        layout.addView(fileName);
        dialog_box.setView(layout);
        dialog_box.setTitle("Save as File");
        dialog_box.setPositiveButton("Save as", (dialog, which) -> {
            String textFile = fileName.getText().toString();
            if(!textFile.endsWith(".txt")) textFile += ".txt";
            File file = new File(homeDir, textFile);
            s = textFile.substring(0, textFile.length()-4);
            if (file.exists()) {
                Toast.makeText(this, s +" already exists! Type another name", Toast.LENGTH_LONG).show();
            } else {
                writeFile(textFile, editText.getText().toString());
                Toast.makeText(this, "Created a new File "+ s +" and saved successfully", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Cancel",(dialog,which) -> dialog.cancel()).show();
    }

    private void writeFile(String fileName, String fileContent) {
        File file = new File(homeDir, fileName);
        try {
            FileOutputStream fs = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fs);
            pw.println(fileContent);
            fileNameDisplay.setText(fileName);
            pw.flush();
            pw.close();
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openFile() {
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        fileName.setHint("Enter the filename");
        layout.addView(fileName);
        dialog_box.setView(layout);
        dialog_box.setTitle("Open File");
        dialog_box.setPositiveButton("Open", (dialog, which) -> {
            String textFile = fileName.getText().toString();
            if(!textFile.endsWith(".txt")) textFile += ".txt";
            File file = new File(homeDir, textFile);
            s = textFile.substring(0, textFile.length()-4);
            if (file.exists()) {
                readFile(textFile);
                Toast.makeText(this, s + " opened successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, s + " does not exists, please create a new file", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("Cancel",(dialog,which) -> dialog.cancel()).show();
    }

    private void readFile(String fileName) {
        File file = new File(homeDir, fileName);
        if(file.isFile()) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                }
                editText.setText(stringBuilder);
                fileNameDisplay.setText(fileName);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void rename() {
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText oldName = new EditText(this);
        oldName.setHint("Enter the filename");
        String old_Name = fileNameDisplay.getText().toString();
        s1 = old_Name = old_Name.substring(0, old_Name.length()-4);
        if(!old_Name.equals("untitled")) oldName.setText(old_Name);
        EditText newName = new EditText(this);
        newName.setHint("Enter new filename");
        layout.addView(oldName);
        layout.addView(newName);
        dialog_box.setView(layout);
        dialog_box.setTitle("Rename a File");
        dialog_box.setPositiveButton("Rename", (dialog, which) -> {
            String oldFileName = oldName.getText().toString();
            if (!oldFileName.endsWith(".txt")) oldFileName += ".txt";
            File oldFile = new File(homeDir, oldFileName);

            String newFileName = newName.getText().toString();
            if (!newFileName.endsWith(".txt")) newFileName += ".txt";
            File newFile = new File(homeDir, newFileName);
            s = newFileName.substring(0, newFileName.length()-4);
            if (newFile.exists()) {
                Toast.makeText(this, s + " already exists!", Toast.LENGTH_SHORT).show();
            } else if (oldFile.exists()) {
                if (oldFile.renameTo(newFile)) {
                    Toast.makeText(this, s1 + " renamed to " + s, Toast.LENGTH_LONG).show();
                    if (fileNameDisplay.getText().toString().equals(oldFileName)) {
                        fileNameDisplay.setText(newFileName);
                    }
                } else
                    Toast.makeText(this, "Error! File cannot be renamed", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, s1 + " does not exists!", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("Close", (dialog, which) -> dialog.cancel()).show();
    }

    private void loadFileList() {
        LayoutInflater inflater= LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.alert_box_scroll, null);
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        TextView files = view.findViewById(R.id.fileNames);
        StringBuilder displayText = new StringBuilder();

        dialog_box.setView(view);
        dialog_box.setTitle("Created Files");
        for (File obj: Objects.requireNonNull(homeDir.listFiles())) {
            displayText.append("\n").append(obj.getName()).append("\n");
        }
        files.setText(displayText);
        files.setTextSize(20);
        files.setPadding(100, 0,0,0);
        dialog_box.setPositiveButton("OK", null).show();
    }

    public void delete() {
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        String curFile = fileNameDisplay.getText().toString();
        curFile = curFile.substring(0, curFile.length()-4);
        if(!curFile.equals("untitled")) fileName.setText(curFile);
        fileName.setHint("Enter the filename");
        layout.addView(fileName);
        dialog_box.setView(layout);
        dialog_box.setTitle("Delete a File");
        dialog_box.setPositiveButton("Delete", (dialog, which)-> {
            String textFile = fileName.getText().toString();
            if(!textFile.endsWith(".txt")) textFile += ".txt";
            File file = new File(homeDir, textFile);
            s = textFile.substring(0, textFile.length()-4);
            if(file.exists()){
                if(file.delete()) {
                    Toast.makeText(this, s + " deleted successfully", Toast.LENGTH_SHORT).show();
                    if(fileNameDisplay.getText().toString().equals(textFile)) {
                        editText.setText("");
                        fileNameDisplay.setText(R.string.fileName);
                    }
                } else {
                    Toast.makeText(this, "Error! File cannot be deleted", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, s + " does not exists!", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("Close", (dialog, which) -> dialog.cancel()).show();
    }

    public void close() {
        editText.setText("");
        fileNameDisplay.setText(R.string.fileName);
    }

    private void statisticsCount() {
        int wordCount, charCount;
        String displayText = editText.getText().toString();
        AlertDialog.Builder dialog_box = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView fileName = new TextView(this);
        String[] words = displayText.split("\\s+");
        if(displayText.equals("")) wordCount = 0;
        else wordCount = words.length;
        charCount = displayText.length();
        layout.addView(fileName);
        dialog_box.setView(layout);
        dialog_box.setTitle("Statistics");
        fileName.setText(Html.fromHtml(
                "<br>Word count<br>&ensp<font color=black>" + wordCount +" </font><br>" +
                        "<br>Character count<br>&ensp<font color=black>" + charCount +"</font>"
        ));
        fileName.setTextSize(20);
        fileName.setPadding(100, 0,0,0);
        dialog_box.setPositiveButton("OK", null).show();
    }
}