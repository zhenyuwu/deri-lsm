

/*
 * Usage example:
 * $("#twitterSearch").liveTwitter('bacon', {limit: 10, rate: 15000});
 */


(function ($) {

  // Extend jQuery with a reverse function if it isn't already defined
  if (!$.fn.reverse) {
    $.fn.reverse = function () {
      return this.pushStack(this.get().reverse(), arguments);
    };
  }

  $.fn.liveMashup = function (query, options, callback) {
    var domNode = this;
    $(this).each(function () {
      var settings = {};

      // Does this.twitter already exist? Let's just change the settings.
      if (this.mashup) {
        settings = $.extend(this.mashup.settings, options);
        this.mashup.settings = settings;
        if (query) {
          this.mashup.query = query;
        }
        if (this.mashup.interval) {
          this.mashup.refresh();
        }
        if (callback) {
          this.mashup.callback = callback;
        }

      // ..if not, let's initialize.
      } else {

        // These are the default settings.
        settings = $.extend({          
          rate:      15000,    // Refresh rate in ms
          limit:     10,       // Limit number of results          
          refresh:   true,
          city:'London',
          country:'England',
          retweets:  false,
          service:   false,
          
        }, options);
        // showAuthor should default to true unless mode is 'user_timeline'.
        if (typeof settings.showAuthor === "undefined") {
          settings.showAuthor = (settings.mode === 'user_timeline') ? false : true;
        }

        // Set up a dummy function for the Twitter API callback.
        if (!window.mashup_callback) {
          window.mashup_callback = function () {
            return true;
          };
        }

        this.mashup = {
          settings:      settings,
          query:         query,
          interval:      false,
          city:'London',
          country:'England',
          container:     this,
          lastTimeStamp: 0,
          callback:      callback,   

          // Update the relative timestamps
          twitter: function () {
        	  var twitterDiv = document.getElementById('twitterMashup');
        	  if(twitterDiv==null){
        		  twitterDiv = document.createElement("div");
        		  twitterDiv.setAttribute("id", "twitterMashup");   
        		  twitterDiv.style.overflow = 'auto';
        		  twitterDiv.style.maxHeight = '30%';
//        		  twitterDiv.style.height = "200px";
        		  $(this.container).append(twitterDiv);
        	  }
        	  $(twitterDiv).liveTwitter("london").each(function(){
        		  twitterDiv.twitter.refresh();
        		  twitterDiv.twitter.start();
			  });
        	
          },

          weather: function () {
        	  var mashup = this;
        	  var weatherDiv = document.createElement("div");
        	  weatherDiv.style.width = "15em";
        	  weatherDiv.style.height = "5em";
        	  
        	  $(this.container).append(weatherDiv);
        	  $.simpleWeather({
          		location: 'berlin' + "," + 'germany',
  				unit: 'c',
  				success: function(weather) {
  					html = '<h3>'+weather.city+', '+weather.region+' '+weather.country+'</h3>';
  					html += '<img style="float:left;" width="125px" src="'+weather.image+'">';
  					html += '<p>'+weather.temp+'&deg; '+weather.units.temp+'<br /><span>'+weather.currently+'</span></p>';
  					html += '<a href="'+weather.link+'">View Forecast &raquo;</a>';					
  					$(weatherDiv).html(html);
  				},
  				error: function(error) {
  					$(weatherDiv).html('<p>'+error+'</p>');
  				}
  			});        	  
          },
          

          // Handle reloading
          refresh: function (initialize) {
            var mashup = this;
            if (mashup.settings.refresh || initialize) {
            	mashup.twitter();
            	mashup.weather();
                  // Trigger event
                  $(domNode).trigger('tweets');
             }              
          },

          // Start refreshing
          start: function () {
            var mashup = this;
//            if (!this.interval) {
//              this.interval = setInterval(function () {
            	  mashup.refresh();
//              }, mashup.settings.rate);
//              this.refresh(true);
//            }
          },

          // Stop refreshing
          stop: function () {
            if (this.interval) {
              clearInterval(this.interval);
              this.interval = false;
            }
          },

          // Clear all tweets
          clear: function () {
            $(this.container).find('div.tweet').remove();
            this.lastTimeStamp = null;
          }
        };

        var mashup = this.mashup;

        

        this.mashup.start();
      }
    });
    return this;
  };
})(jQuery);
