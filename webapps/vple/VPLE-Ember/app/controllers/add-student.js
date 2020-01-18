import Controller from '@ember/controller';
import AjaxUtil from 'vple-ember/utils/ajax-util';//No I18N

export default Controller.extend({
    init: function()
    {
        this.set('addStudentFormLoaded', false);
        this.getStudents();
    },
    getStudents: function()
    {
        var self = this;
        self.set('addStudentFormLoaded', false);
        AjaxUtil.ajaxAsync('/vple/StudentsManager?methodToCall=getStudents',{}).then(function(students){
            self.set('students', students);
        });
    },
    actions: {
        toggleStudentConfigForm: function()
        {
            this.set('addStudentFormLoaded', !this.get('addStudentFormLoaded'));
        },
        saveStudent: function()
        {
            var self = this;
            var data = {
                STUDENT_ID: $("#id_no").val().trim(),
                FIRST_NAME: $("#first_name").val().trim(),
                LAST_NAME: $("#last_name").val().trim()
            };
            AjaxUtil.ajaxAsync('/vple/StudentsManager?methodToCall=saveStudent',data).then(function(response){
                if(!response.STATUS)
                {
                    alert(response.ERROR_MSG);
                }
                else {
                    self.getStudents();
                }
            });
        }
    }
});
