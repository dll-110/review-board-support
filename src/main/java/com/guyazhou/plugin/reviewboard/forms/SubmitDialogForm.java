package com.guyazhou.plugin.reviewboard.forms;

import com.guyazhou.plugin.reviewboard.exceptions.InvalidArgumentException;
import com.guyazhou.plugin.reviewboard.model.repository.Repository;
import com.guyazhou.plugin.reviewboard.setting.ReviewBoardSetting;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.TextFieldWithStoredHistory;
import com.intellij.util.ui.ComboBoxWithHistory;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Submit form
 *
 * @author YaZhou.Gu 2017/1/5
 */
public class SubmitDialogForm extends DialogWrapper {

    private JPanel submitPanel;
    private JComboBox repositoryBox;
    private JRadioButton newRequestRadioButton;
    private JRadioButton existingRequestRadioButton;
    private JTextField existReviewIdField;
    private JButton loadReviewInfoButton;
    private JButton showDiffButton;
    private TextFieldWithStoredHistory summaryField;
    private JTextField branchField;
    private JTextField bugfield;
    private TextFieldWithStoredHistory groupsFiled;
    private TextFieldWithStoredHistory peopleField;
    private ComboBoxWithHistory descriptionHistory;
    private JTextArea descriptionArea;
    private JCheckBox chxDraft;

    private void createUIComponents() {
        summaryField = new TextFieldWithStoredHistory("reviewboard.summary");
        groupsFiled = new TextFieldWithStoredHistory("reviewboard.groups");
        peopleField = new TextFieldWithStoredHistory("reviewboard.people");
        // TODO
        descriptionHistory = new ComboBoxWithHistory("reviewboard.description");
    }

    /**
     * Inner class of item to the combobox for repositories
     */
    public static class RepositoryComboBoxItem {
        private final Repository repository;

        private RepositoryComboBoxItem(Repository repository) {
            this.repository = repository;
        }

        public Repository getRepository() {
            return this.repository;
        }

        @Override
        public String toString() {
            return String.format("%s : %s", repository.getId(), repository.getName());
        }
    }

    protected SubmitDialogForm(@Nullable Project project, Repository[] repositories, int possibleRepositoryIndex) {
        super(project);
        System.out.println(descriptionHistory.getSelectedIndex());
        // initialize
        this.setTitle("Submit Review Request");
        for (Repository repository : repositories) {
            //noinspection unchecked
            repositoryBox.addItem(new RepositoryComboBoxItem(repository));
        }
        if (possibleRepositoryIndex > -1) {
            repositoryBox.setSelectedIndex(possibleRepositoryIndex);
        }

        String hisRepo = ReviewBoardSetting.getInstance().getState().getSelectedrepository();
        if(hisRepo!=null && !hisRepo.equals("")){
            for (int i = 0; i < repositoryBox.getItemCount(); i++) {
                RepositoryComboBoxItem item = (RepositoryComboBoxItem)repositoryBox.getItemAt(i);
                if(item.getRepository().getName().equals(hisRepo)){
                    repositoryBox.setSelectedIndex(i);
                    continue;
                }
            }
        }
        newRequestRadioButton.setSelected(true);
        newRequestButtonSelected();

        this.loadPresetAttributes();

        newRequestRadioButton.addActionListener(e -> newRequestButtonSelected());
        existingRequestRadioButton.addActionListener(e -> {
            newRequestRadioButton.setSelected(false);
            existReviewIdField.setEnabled(true);
            loadReviewInfoButton.setEnabled(true);
        });

        super.setOKButtonText("Submit");
        super.init();

        // Show Diff Button
        showDiffButton.addActionListener(e -> {
            // TODO
        });
        // Load Review Info Button
        loadReviewInfoButton.addActionListener(e -> {
            // TODO
        });
    }

    /**
     * Load preset attributes, like people, groups
     */
    private void loadPresetAttributes() {
        ReviewBoardSetting.State persisentState = ReviewBoardSetting.getInstance().getState();
        if (null == persisentState) {
            return;
        }
        groupsFiled.setText(persisentState.getGroups());
        peopleField.setText(persisentState.getPeople());
    }

    /**
     * New Request Button selected
     * Set existReviewField disabled, set loadReviewInfo button disabled.
     */
    private void newRequestButtonSelected() {
        existingRequestRadioButton.setSelected(false);
        existReviewIdField.setEnabled(false);
        loadReviewInfoButton.setEnabled(false);
    }

    /************ Implement from DialogWrapper ************/
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return submitPanel;
    }

    @Override
    public boolean isOKActionEnabled() {
        if (existReviewIdField.isEnabled()) {
            String existReviewId = existReviewIdField.getText();
            if (null == existReviewId || "".equals(existReviewId)) {
                existReviewIdField.grabFocus();
                throw new InvalidArgumentException("Exist reviewId is empty");
            }
        }
        if (null == getSummary() || "".equals(getSummary())) {
            summaryField.grabFocus();
            throw new InvalidArgumentException("Summary can not be empty");
        }
        // verify people
        if (null == getPeople() || "".equals(getPeople())) {
            peopleField.grabFocus();
            throw new InvalidArgumentException("People can not be empty");
        }
        // verfy description
        if (null == getDescription() || "".equals(getDescription())) {
            descriptionArea.grabFocus();
            throw new InvalidArgumentException("Description can not be empty");
        }
        return true;
    }

    /**
     * Add some field text to history
     */
    public void addTextToHistory() {
        summaryField.addCurrentTextToHistory();
        groupsFiled.addCurrentTextToHistory();
        peopleField.addCurrentTextToHistory();
    }

    /**
     * Is new request
     *
     * @return true if is new request, otherwise false
     */
    public boolean isNewRequest() {
        return !existReviewIdField.isEnabled();
    }

    public String getExistReviewId() {
        return existReviewIdField.getText().trim();
    }

    public String getSummary() {
        return summaryField.getText().trim();
    }

    public String getBranch() {
        return branchField.getText().trim();
    }

    public String getBug() {
        return bugfield.getText().trim();
    }

    public String getGroups() {
        return groupsFiled.getText().trim();
    }

    public String getPeople() {
        return peopleField.getText().trim();
    }

    public String getDescription() {
        return descriptionArea.getText().trim();
    }

    public boolean isDraft() {
        return chxDraft.isSelected();
    }

    public int getSelectedRepositoryId() {
        if (repositoryBox.getSelectedItem() != null) {
            return ((RepositoryComboBoxItem) repositoryBox.getSelectedItem()).getRepository().getId();
        }
        return -1;
    }

}
