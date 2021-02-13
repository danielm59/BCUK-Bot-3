package com.expiredminotaur.bcukbot.fun.trivia.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Questions
{

    @SerializedName("response_code")
    @Expose
    private Integer responseCode;
    @SerializedName("results")
    @Expose
    private List<Question> Questions = null;

    public Integer getResponseCode()
    {
        return responseCode;
    }

    public List<Question> getQuestions()
    {
        return Questions;
    }


    public static class Question
    {

        @SerializedName("category")
        @Expose
        private String category;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("difficulty")
        @Expose
        private String difficulty;
        @SerializedName("question")
        @Expose
        private String question;
        @SerializedName("correct_answer")
        @Expose
        private String correctAnswer;
        @SerializedName("incorrect_answers")
        @Expose
        private List<String> incorrectAnswers = null;

        public String getCategory()
        {
            return category;
        }

        public String getType()
        {
            return type;
        }

        public String getDifficulty()
        {
            return difficulty;
        }

        public String getQuestion()
        {
            return question;
        }

        public String getCorrectAnswer()
        {
            return correctAnswer;
        }

        public List<String> getIncorrectAnswers()
        {
            return incorrectAnswers;
        }
    }
}
