package com.googleudacity.andrewcunningham.quizapp;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andrewcunningham on 02/04/2017.
 */

public class QuizMaster {

    Context context;

    public class Question {

        // Finals
        public final static int CHOICE = 0;
        public final static int TEXT = 1;

        // Fields
        private String question;
        private ArrayList<String> answers;
        private int[] correctAnswers;
        private int questionType;

        public Question(String question, ArrayList<String> answers, int[] correctAnswers, int questionType) {
            this.question = question;
            this.answers = answers;
            this.correctAnswers = correctAnswers;
            this.questionType = questionType;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Question: ");
            sb.append(question);
            sb.append("\n");
            sb.append("Answers: ");
            sb.append(answers.toArray().toString());
            sb.append("\n");
            return sb.toString();
        }
    }

    public QuizMaster(Context context) {
        this.context = context;
        test();
    }

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

    public void test() {
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray questionsJSONArray = obj.getJSONArray("questions");
            ArrayList<Question> questionsArray = new ArrayList<Question>();

            for (int i = 0; i < questionsJSONArray.length(); i++) {
                JSONObject jo_inside = questionsJSONArray.getJSONObject(i);

                // Type
                int type;
                if (jo_inside.getString("type").equals("choice")) {
                    type = 0;
                } else {
                    type = 1;
                }

                // Question
                String questionString = jo_inside.getString("question");

                // Answers
                ArrayList<String> answers = new ArrayList<>();
                JSONArray answersJSON = jo_inside.getJSONArray("answers");
                for (int j = 0; j < answersJSON.length(); j++) {
                    String answer = answersJSON.getString(i);
                    answers.add(answer);
                }
                // Correct Answer
                int[] correctAnswers = new int[1];
                correctAnswers[0] = (int) Integer.valueOf(jo_inside.getString("correct"));

                Question question = new Question(
                        questionString,
                        answers,
                        correctAnswers,
                        type);

                Log.d("WAH", "AAC --> " + question.toString());
                questionsArray.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
