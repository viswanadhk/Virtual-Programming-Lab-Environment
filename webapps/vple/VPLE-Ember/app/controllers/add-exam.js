import Controller from '@ember/controller';
import AjaxUtil from 'vple-ember/utils/ajax-util';//No I18N

export default Controller.extend({

    currentTab: 0,
    students: [],
    questions: [],
    showForm: true,
    addExamFormLoaded: false,

    init: function()
    {
        var self = this;
        self.fillStudents();
        self.fillExams();
    },
    fillExams: function()
    {
        var self = this;
        AjaxUtil.ajaxAsync('/vple/ExamsManager?methodToCall=getExams',{}).then(function(exams){
            self.set('exams', exams);
        });
    },
    fillStudents: function()
    {
        var self = this;
        AjaxUtil.ajaxAsync('/vple/StudentsManager?methodToCall=getStudents',{}).then(function(students){
            self.set('students', students);
        });
    },
    saveExam: function()
    {
        var self = this;
        var students = [];
        $("input:checked").each(function() 
        { 
            students.push($(this).attr("id"))
        });
        self.questions.push(
            {
                DESCRIPTION: $("#description").val(),
                TEST_CASES: $("#test_cases").val(),
                EXPECTED_OUTPUT: $("#expected_output").val(),
                SCORE: $("#score").val()
            }
        );
        var params = {
            QUESTIONS: self.questions,
            EXAM_ID: $("#exam_id").val(),
            EXAM_NAME: $("#exam_name").val(),
            DURATION: $("#duration").val(),
            NO_OF_QUESTIONS: $("#no_of_questions").val(),
            STUDENTS: students
        };
        AjaxUtil.ajaxAsync('/vple/ExamsManager?methodToCall=saveExam',params).then(function(response){
            if(response.STATUS)
            {
                alert("Data added successfully !!");
            }
            self.resetExamParams();
        });
    },
    resetExamParams: function()
    {
        var self = this;
        $("#exam_name").val("");
        $("#exam_id").val("");
        $("#score").val("");
        $("#duration").val("");   
        $("#no_of_questions").val("");   
        $("#description").val("");   
        $("#test_cases").val("");   
        $("#expected_output").val("");   
        $("#expected_output").val("");   
        $("#expected_output").val("");   

        self.questions = [];

        var x = $(".tab");
        $(x[self.currentTab]).addClass("hide");
        self.currentTab = 0;
        self.send("showTab", self.currentTab);

        self.send("toggleExamForm");
    },
    actions: {
        toggleExamForm: function()
        {
            var self = this;
            this.set('addExamFormLoaded', !this.get('addExamFormLoaded'))
            
            if(this.get('addExamFormLoaded'))
            {
                Ember.run.scheduleOnce("afterRender",function(){
                    self.send("showTab", self.currentTab);
                });
            }
        },
        saveAndAdd: function()
        {
            var self = this;
            self.questions.push(
                {
                    DESCRIPTION: $("#description").val(),
                    TEST_CASES: $("#test_cases").val(),
                    EXPECTED_OUTPUT: $("#expected_output").val(),
                    SCORE: $("#score").val()
                }
            );
            $("#description").val("");
            $("#test_cases").val("");
            $("#expected_output").val("");
            $("#score").val("");
        },
        showTab : function(n) {
            var x = $(".tab");
            console.log(n);
            $(x[n]).removeClass("hide");
            if (n == 0) {
              $("#prevBtn").addClass("hide");
            } else {
              $("#prevBtn").removeClass("hide");
            }
            if (n == (x.length - 1)) {
              $("#nextBtn").innerHTML = "Submit";
            } else {
              $("#nextBtn").innerHTML = "Next";
            }
          },
          nextPrev: function(n) {
            var self = this;
            var x = $(".tab");
            if ((self.currentTab + n) >= x.length) {
                self.saveExam();
                return false;
            }
            $(x[self.currentTab]).addClass("hide");
            self.currentTab = self.currentTab + n;
            self.send("showTab", self.currentTab);
          }          
    }

});
