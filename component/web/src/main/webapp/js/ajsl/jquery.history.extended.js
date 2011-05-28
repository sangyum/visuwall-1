/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

(function($) {
	
	$.extend($.history, {
		parseRequest : function(request) {
			//TODO fill _ctrl cleanly
			this._queryBuilderObj._ctrl = this._options.ctrls;
			
			var result = {};
			if (request) {
				var controllers = request.split('|');
		       	for (var i = 0; i < controllers.length; i++) {
		       		var ctrl = this._queryBuilderObj._parseController(controllers[i]);
		       		result[ctrl.ctrl] = ctrl;
		       	}
			}
	       	return result;
		},
		
	    _queryBuilderObj : {
	    	
	    	_data : null,
	    	_ctrl : null,
	    	
			_parseController : function(url) {
				var result = {};
				var varsSepar = url.split('?');
				if (varsSepar.length > 2) {
					throw 'malform url with more than one "?" in controller' + controllers[0];
				}
				result.ctrlVars = varsSepar[0].split('/');
				result.ctrl = result.ctrlVars.shift();
				
				// check for specific method
				if (result.ctrlVars.length) {
					var ctrlWithMethod = result.ctrl + '/' + result.ctrlVars[0];
					if (this._ctrl[ctrlWithMethod] != undefined) {
						result.ctrlVars.shift();
						result.ctrl = ctrlWithMethod; 
					}
				}
				
				// vars
				if (varsSepar.length > 1) {
					result.vars = this._parseQueryVars(varsSepar[1]);
				} else {
					result.vars = {};
				}
				return result; 
			},

		    _parseQueryVars : function(varStr) {
		       	var res = {};
		       	var pairs = varStr.split('&');
		       	for (var i = 0; i < pairs.length; i++) {
		       	    var keyval = pairs[i].split('=');
		       	    res[keyval[0]] = keyval[1];
		       	}
		       	return res;
		    },

			_buildRequest : function() {
				var res = '';
				for (var ctrl in this._data) {
					if (res) {
						res += '|';
					}
					res += ctrl;
					var ctrlVars = this._data[ctrl].ctrlVars.join('/');
					if (ctrlVars) {
						res += '/' + ctrlVars;
					}
					
					var vars = this._data[ctrl].vars;
					if (Object.size(vars)) {
						res += '?';
					}
					var i = 0;
					for (var varName in vars) {
						if (i) {
							res += '&';
						}
 						res += varName + '=' + vars[varName];
						i++;
					}
				}
				return res;
			},

			addController : function(url) {
	    		var ctrl = this._parseController(url);
	    		this._data[ctrl.ctrl] = ctrl;
	    		return this;
	    	},

	    	removeController : function(ctrlName) {
	    		delete this._data[ctrlName];
	    		return this;
	    	},

			load : function() {
				$.history.load(this._buildRequest(this._data));
	    		return this;
			}
	    	
	    },

		queryBuilder : function() {
	    	var builder = $.extend({}, this._queryBuilderObj);
	    	builder._ctrl = this._options.ctrls;
	    	builder._data = this.parseRequest(this.location.get());
	    	return builder;
	    }
	    		
	});

})(jQuery);