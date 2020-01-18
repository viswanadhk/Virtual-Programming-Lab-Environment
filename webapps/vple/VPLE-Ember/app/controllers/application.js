import Controller from '@ember/controller';
import AjaxUtil from 'vple-ember/utils/ajax-util';//No I18N

export default Controller.extend({
    init: function()
    {
        var self = this;
        AjaxUtil.ajaxAsync('/vple/ApplicationClient?methodToCall=getTabs',{}).then(function(tabs){
            self.set('tabs', tabs);
        });
    },
    actions: {
        logout: function()
        {
            var self = this;
            AjaxUtil.ajaxAsync('/vple/ApplicationClient?methodToCall=Logout',{}).then(function(tabs){
                window.location.href = "http://localhost:8080/vple";
                window.location.reload();
            });
        }
    }
});
