package com.googleudacity.andrewcunningham.quizapp;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QuizMaster quizMaster = new QuizMaster(this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.questions_recycler);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(new QuestionAdapter(this, quizMaster.getQuestions()));
    }

    public class QuestionAdapter extends RecyclerView.Adapter {

        private ArrayList<QuizMaster.Question> questions;
        private LayoutInflater layoutInflater;

        public QuestionAdapter(Context context, ArrayList<QuizMaster.Question> questions) {
            this.questions = questions;
            layoutInflater = LayoutInflater.from(context);
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
                    return new OptionQuestionViewHolder(choiceView);
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((OptionQuestionViewHolder) holder).bind(questions.get(position));
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }

        public class OptionQuestionViewHolder extends RecyclerView.ViewHolder{

            ViewSwitcher viewSwitcher;
            ImageView answerResult;
            TextView questionTextView, answer1, answer2, answer3;
            RadioButton[] radioButtons = new RadioButton[3];
            Button submit;
            QuizMaster.Question question;

            public OptionQuestionViewHolder(View itemView) {
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

            public void checkAnswer(){
                if(radioButtons[question.getCorrectAnswers()[0]].isChecked()){
                    answerResult.setImageResource(R.drawable.success);
                } else{
                    answerResult.setImageResource(R.drawable.error);
                }
                viewSwitcher.setDisplayedChild(1);
            }
        }
    }
}


