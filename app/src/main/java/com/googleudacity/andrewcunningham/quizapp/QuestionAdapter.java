package com.googleudacity.andrewcunningham.quizapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter {

    private Context context;
    private int correctAnswers = 0;
    private int questionsAnswered = 0;
    private ArrayList<QuizMaster.Question> questions;
    private LayoutInflater layoutInflater;

    public QuestionAdapter(Context context, ArrayList<QuizMaster.Question> questions) {
        this.context = context;
        this.questions = questions;
        layoutInflater = LayoutInflater.from(context);
    }

    private void updateScore(boolean correct) {
        questionsAnswered++;
        if (correct) correctAnswers++;
        if (questionsAnswered == questions.size()) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.quiz_over)
                    .setMessage(context.getString(R.string.score, correctAnswers))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(true)
                    .show();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return questions.get(position).getQuestionType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case QuizMaster.Question.CHOICE:
                View choiceView = layoutInflater.inflate(R.layout.card_question_options, parent, false);
                return new QuestionAdapter.OptionQuestionViewHolder(choiceView);
            case QuizMaster.Question.TEXT:
                View textView = layoutInflater.inflate(R.layout.card_question_submit, parent, false);
                return new QuestionAdapter.TextQuestionViewHolder(textView);
            case QuizMaster.Question.MULTIPLE:
                View multipleView = layoutInflater.inflate(R.layout.card_question_multiple, parent, false);
                return new QuestionAdapter.MultipleQuestionViewHolder(multipleView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof QuestionAdapter.OptionQuestionViewHolder) {
            ((QuestionAdapter.OptionQuestionViewHolder) holder).bind(questions.get(position));
        } else if (holder instanceof QuestionAdapter.TextQuestionViewHolder) {
            ((QuestionAdapter.TextQuestionViewHolder) holder).bind(questions.get(position));
        } else if (holder instanceof QuestionAdapter.MultipleQuestionViewHolder) {
            ((QuestionAdapter.MultipleQuestionViewHolder) holder).bind(questions.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    private class OptionQuestionViewHolder extends RecyclerView.ViewHolder {

        ViewSwitcher viewSwitcher;
        ImageView answerResult;
        TextView questionTextView, answer1, answer2, answer3;
        RadioButton[] radioButtons = new RadioButton[3];
        Button submit;
        QuizMaster.Question question;

        OptionQuestionViewHolder(View itemView) {
            super(itemView);
            questionTextView = (TextView) itemView.findViewById(R.id.question);
            answer1 = (TextView) itemView.findViewById(R.id.answer1);
            answer2 = (TextView) itemView.findViewById(R.id.answer2);
            answer3 = (TextView) itemView.findViewById(R.id.answer3);

            radioButtons[0] = (RadioButton) itemView.findViewById(R.id.option1);
            radioButtons[1] = (RadioButton) itemView.findViewById(R.id.option2);
            radioButtons[2] = (RadioButton) itemView.findViewById(R.id.option3);

            submit = (Button) itemView.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAnswer();
                }
            });

            answerResult = (ImageView) itemView.findViewById(R.id.answer_result);
            viewSwitcher = (ViewSwitcher) itemView.findViewById(R.id.view_switcher);
        }

        public void bind(QuizMaster.Question question) {
            this.question = question;
            questionTextView.setText(question.getQuestion());
            answer1.setText("A: " + question.getAnswers().get(0));
            answer2.setText("B: " + question.getAnswers().get(1));
            answer3.setText("C: " + question.getAnswers().get(2));
        }

        public void checkAnswer() {
            if (radioButtons[question.getCorrectAnswersInts()[0]].isChecked()) {
                answerResult.setImageResource(R.drawable.success);
                updateScore(true);
            } else {
                answerResult.setImageResource(R.drawable.error);
                updateScore(false);
            }
            viewSwitcher.setDisplayedChild(1);
        }
    }

    private class TextQuestionViewHolder extends QuestionAdapter.OptionQuestionViewHolder {

        EditText answerEntry;

        TextQuestionViewHolder(View itemView) {
            super(itemView);
            answerEntry = (EditText) itemView.findViewById(R.id.answer_entry);
        }

        @Override
        public void bind(QuizMaster.Question question) {
            this.question = question;
            questionTextView.setText(question.getQuestion());
        }

        @Override
        public void checkAnswer() {
            if (answerEntry.getText().toString().toLowerCase().trim().equals(question.getCorrectAnswerString().toLowerCase().trim())) {
                answerResult.setImageResource(R.drawable.success);
                updateScore(true);
            } else {
                answerResult.setImageResource(R.drawable.error);
                updateScore(false);
            }
            viewSwitcher.setDisplayedChild(1);
        }
    }

    private class MultipleQuestionViewHolder extends QuestionAdapter.OptionQuestionViewHolder {

        CheckBox option1, option2, option3;
        CheckBox[] checkBoxes;

        MultipleQuestionViewHolder(View itemView) {
            super(itemView);
            option1 = (CheckBox) itemView.findViewById(R.id.choice1);
            option2 = (CheckBox) itemView.findViewById(R.id.choice2);
            option3 = (CheckBox) itemView.findViewById(R.id.choice3);
            checkBoxes = new CheckBox[]{option1, option2, option3};
        }

        @Override
        public void checkAnswer() {
            int correct = R.drawable.error;
            int[] correctAnswers = question.getCorrectAnswersInts();
            ArrayList<Integer> correctAnswersArray = new ArrayList<>();
            for (int correctAnswer : correctAnswers) {
                correctAnswersArray.add(correctAnswer);
            }

            ArrayList<Integer> checkedBoxes = new ArrayList<>();
            for (int y = 0; y < checkBoxes.length; y++) {
                if (checkBoxes[y].isChecked()) {
                    checkedBoxes.add(y);
                }
            }

            if (correctAnswersArray.equals(checkedBoxes)) {
                correct = R.drawable.success;
                updateScore(true);
            } else {
                updateScore(false);
            }

            answerResult.setImageResource(correct);
            viewSwitcher.setDisplayedChild(1);
        }
    }

}

