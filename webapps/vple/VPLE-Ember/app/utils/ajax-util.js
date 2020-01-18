//ignorei18n_start
/* jshint ignore:start */
export default Ember.Object.create({
  ajaxSync:function(url,data,success){
      return this.ajax(url,data,success,true,false,false);
  },
  ajaxAsync:function(url,data,success){
      return this.ajax(url,data,success,true,true,false);
  },
  ajaxPostAsync:function(url,data,success){
      return this.ajax(url,data,success,true,true,true);
  },
  ajaxAsyncNoStringify:function(url,data,success){
      return this.ajax(url,data,success,false,true,false);
  },
  ajax:function(url, data, success, stringify,isAsync,isPost){
    return new Ember.RSVP.Promise(function (resolve) {
    $.ajax({
      type: isPost? "POST":"GET",
          async: isAsync,
          contentType: stringify ? "application/json; charset=utf-8" : "text/html; charset=utf-8",
          dataType: "json",
          url: url,
          data: stringify ? "req=" + encodeURIComponent(JSON.stringify(data)) : data
    })
    
        .done(function(json, textStatus, jqXHR){
            
          resolve(json);
        })
        .fail(function(jqXHR, textStatus, errorThrown){
        //alert('fail');
      });
    });
  },
  ajaxPool:function(url, data, success, controller, timeout) {
      timeout = (typeof timeout === "undefined") ? 5000 : timeout;
      var self = this;
      $.ajax({
          type: "GET",
          contentType: "application/json; charset=utf-8",
          dataType: "json",
          url: url,
          data: "req=" +  encodeURIComponent(JSON.stringify(data)),
          success: function(response, status) {
              //console.log(response,status);
              if (response==null || response.d == 0){
                  _.delay(function() {
                      self.ajaxPool(url, data, success, controller);
                  }, timeout);
              }else{
                  success.call(window, response, controller);
              }
          },
          error: function(xhr, ajaxOptions, thrownError) {
              //console.log(xhr, ajaxOptions, thrownError);
          }
      });
  }
  
  });
  /* jshint ignore:end */
  //ignorei18n_end