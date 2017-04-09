package com.googleudacity.andrewcunningham.quizapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * The QuizMaster class is responsible for taking the questions out of the the question.json file
 * and passing them back to whatever requests it.
 */

public class QuizMaster {

    private Context context;
    private ArrayList<Question> questionsArray = new ArrayList<Question>();

    public class Question {

        // Finals
        public final static int CHOICE = 0;
        public final static int TEXT = 1;
        public final static int MULTIPLE = 2;

        // Fields
        private String question;
        private ArrayList<String> answers;
        private int[] correctAnswersInts;
        private String correctAnswerString;
        private int questionType;

        public Question(String question, ArrayList<String> answers, int[] correctAnswersInts, int questionType) {
            this.question = question;
            this.answers = answers;
            this.correctAnswersInts = correctAnswersInts;
            this.questionType = questionType;
        }

        public Question(String question, String correctAnswerString, int questionType) {
            this.question = question;
            this.correctAnswerString = correctAnswerString;
            this.questionType = questionType;
        }


        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Question: ");
            sb.append(question);
            sb.append("\n");
            sb.append("Answers: ");
            if (answers != null) {
                sb.append(answers.toArray().toString());
            }
            sb.append("\n");
            return sb.toString();
        }

        public int getQuestionType() {
            return questionType;
        }

        public String getQuestion() {
            return question;
        }

        public ArrayList<String> getAnswers() {
            return answers;
        }

        public int[] getCorrectAnswersInts() {
            return correctAnswersInts;
        }

        public String getCorrectAnswerString() {
            return correctAnswerString;
        }
    }

    public QuizMaster(Context context) {
        this.context = context;
        loadQuestions();
    }

    /**
     * Loads the JSON String from the questions.json file
     *
     * @param context
     * @return
     */
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.questions);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Splits the JSON String up in order to create Question objects.
     */
    public void loadQuestions() {
        questionsArray.clear();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray questionsJSONArray = obj.getJSONArray("questions");

            for (int i = 0; i < questionsJSONArray.length(); i++) {
                JSONObject jo_inside = questionsJSONArray.getJSONObject(i);

                // Type
                int type = -1;
                if (jo_inside.getString("type").equals("choice")) {
                    type = Question.CHOICE;
                } else if (jo_inside.getString("type").equals("text")) {
                    type = Question.TEXT;
                } else if (jo_inside.getString("type").equals("multiple")) {
                    type = Question.MULTIPLE;
                }

                // Question
                String questionString = jo_inside.getString("question");

                if (type == Question.CHOICE || type == Question.MULTIPLE) {

                    // Correct Answer(s)
                    int[] correctAnswers;
                    if(type == Question.CHOICE) {
                        correctAnswers = new int[1];
                        correctAnswers[0] = (int) Integer.valueOf(jo_inside.getString("correct"));
                    } else {
                        JSONArray jsonCorrectAnswers = jo_inside.getJSONArray("correct");
                        correctAnswers = new int[jsonCorrectAnswers.length()];
                        for(int j = 0; j < jsonCorrectAnswers.length(); j++){
                            correctAnswers[j] = (int) jsonCorrectAnswers.get(j);
                        }
                    }

                    // Answers
                    ArrayList<String> answers = new ArrayList<>();
                    JSONArray answersJSON = jo_inside.getJSONArray("answers");
                    Log.d("wah", "AAC --> answersJSON: " + answersJSON);
                    for (int j = 0; j < answersJSON.length(); j++) {
                        JSONObject singleJSON = answersJSON.getJSONObject(j);
                        String answerText = singleJSON.getString("" + j);
                        answers.add(answerText);
                    }

                    Question question = new Question(
                            questionString,
                            answers,
                            correctAnswers,
                            type);

                    Log.d("WAH", "AAC --> " + question.toString());
                    questionsArray.add(question);
                }

                if (type == Question.TEXT) {
                    // Correct Answer
                    String correctAnswer = jo_inside.getString("correct");
                    Question question = new Question(questionString, correctAnswer, type);

                    Log.d("WAH", "AAC --> " + question.toString());
                    questionsArray.add(question);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Question> getQuestions() {
        return questionsArray;
    }
}
