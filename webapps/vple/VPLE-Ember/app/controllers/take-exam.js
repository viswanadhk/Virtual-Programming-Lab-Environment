import Controller from '@ember/controller';
import AjaxUtil from 'vple-ember/utils/ajax-util';//No I18N

export default Controller.extend({
    slideIndex: 1,

    init: function()
    {
        this.set("examsAvailable", false);
        this.set("examStarted", false);
        this.checkForExam();
    },
    checkForExam: function()
    {
        var self = this;
        AjaxUtil.ajaxAsync('/vple/ExamsManager?methodToCall=getAvailableExams',{}).then(function(resp){
            self.set("examsAvailable", resp.STATUS);
            self.set("EXAM_ID", resp.EXAM_ID);
        });
    },
    showDivs: function(n) {
        var i;
        var x = $(".questionSlide");
        if (n > x.length) {this.slideIndex = 1}    
        if (n < 1) {this.slideIndex = x.length}
        for (i = 0; i < x.length; i++) {
            $(x[i]).css('display', "none");  
        }
        $(x[this.slideIndex-1]).css('display', "block");  
    },
    actions: {
        startExam: function()
        {
            var self = this;
            AjaxUtil.ajaxAsync('/vple/ExamsManager?methodToCall=startExam',{EXAM_ID:self.get("EXAM_ID")}).then(function(resp){
                self.set("examsAvailable", false);
                self.set("examStarted", true);
                self.set("exam_questions", resp.RESULT);
                Ember.run.scheduleOnce("afterRender",function(){
                    self.showDivs(self.slideIndex);
                });
            });   
        },
        nextOrPrevQuestion: function(n) {
            this.showDivs(this.slideIndex += n);
        },
        runCode: function(questionId)
        {
            var self = this;
            var lines = $("#solution_"+questionId).val().split("\n");
            var inputCode = [];
            
            for(var i=0;i<lines.length;i++)
            {
                inputCode.push({CODE:encodeURIComponent(lines[i].replace(/\\/g, '\\\\').replace(/"/g, '\\"'))});
            }

            var params = {
                QUESTION_ID: questionId,
                LANGUAGE: parseInt($("#languages").find(":selected").attr("id")),
                CODE: inputCode
            };

            AjaxUtil.ajaxAsync('/vple/ExamsManager?methodToCall=runCode',params).then(function(resp){
                if(resp.STATUS)
                {
                    alert("Program run successful !!");
                    self.send("nextOrPrevQuestion", 1);
                }
                else {
                    $("#result_"+questionId).val(resp.RESULT);
                }
            });   
        },
        saveCode: function(question)
        {
            var self = this;
            var lines = $("#solution_"+question.QUESTION_ID).val().split("\n");
            var inputCode = [];            
            for(var i=0;i<lines.length;i++)
            {
                inputCode.push({CODE:encodeURIComponent(lines[i].replace(/\\/g, '\\\\').replace(/"/g, '\\"'))});
            }

            var params = {
                QUESTION_ID: question.QUESTION_ID,
                CODE: inputCode,
                EXAM_ID:self.get("EXAM_ID"),
                LANGUAGE: parseInt($("#languages").find(":selected").attr("id"))
            };

            AjaxUtil.ajaxAsync('/vple/ExamsManager?methodToCall=saveCode',params).then(function(resp){
                if(resp.STATUS)
                {
                    if(resp.EXAM_FINISHED)
                    {
                        alert("Exam finished..");
                        window.location.reload();
                    }
                    else {
                        question.ALREADY_SUBMITTED = true;
                        self.send("nextOrPrevQuestion", 1);
                    }
                }
                else {
                    $("#result_"+question.QUESTION_ID).val(resp.ERROR_MSG);
                }
            });   
        }
    }
});
