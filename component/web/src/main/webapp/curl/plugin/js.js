/** MIT License (c) copyright B Cavalier & J Hann */

/**
 * curl js! plugin
 *
 * Licensed under the MIT License at:
 * 		http://www.opensource.org/licenses/mit-license.php
 *
 */

/**
 * usage:
 *  require(['ModuleA', 'js!myNonAMDFile.js!order', 'js!anotherFile.js!order], function (ModuleA) {
 * 		var a = new ModuleA();
 * 		document.body.appendChild(a.domNode);
 * 	});
 *
 * Specify the !order suffix for files that must be evaluated in order.
 * Using the !order option and requiring js files more than once doesn't make
 * much sense since files are loaded exactly once.
 *
 * Specify the !exports=someGlobalVar option to return a global variable to
 * the module depending on the javascript file. Using this option also allows
 * positive error feedback to the loader since it can now detect if the
 * javascript file failed to load correctly.
 *
 * Async=false rules learned from @getify's LABjs!
 * http://wiki.whatwg.org/wiki/Dynamic_Script_Execution_Order
 *
 */
(function (global, doc) {
define(/*=='js',==*/ ['curl/_privileged'], function (priv) {
"use strict";
	var cache = {},
		queue = [],
		supportsAsyncFalse = doc && doc.createElement('script').async == true,
		waitForOrderedScript,
		undef;

	function nameWithExt (name, defaultExt) {
		return name.lastIndexOf('.') <= name.lastIndexOf('/') ?
			name + '.' + defaultExt : name;
	}

	function loadScript (def, success, failure) {
		// script processing rules learned from RequireJS

		var deadline, completed;

		// default deadline is very far in the future (5 min)
		// devs should set something reasonable if they want to use it
		deadline = new Date().valueOf() + (def.timeoutMsec || 300) * 1000;

		// initial script processing
		function process () {
			completed = true;
			if (def.exports) def.resolved = testGlobalVar(def.exports);
			if (!def.exports || def.resolved) {
				success();
			}
			else {
				failure();
			}
		}

		function fail (ex) {
			completed = true;
			failure(ex);
		}

		// some browsers (Opera and IE6-8) don't support onerror and don't fire
		// readystatechange if the script fails to load so we need to poll.
		// this poller only runs if def.exports is specified and failure callback
		// is defined (see below)
		function poller () {
			// if the script loaded
			if (!completed) {
				// if neither process or fail as run and our deadline is in the past
				if (deadline < new Date()) {
					failure();
				}
				else {
					setTimeout(poller, 10);
				}
			}
		}
		if (failure && def.exports) setTimeout(poller, 10);

		priv.core.loadScript(def, process, fail);

	}

	function fetch (def, promise) {

		loadScript(def,
			function () {
				// if there's another queued script
				var next = queue.shift();
				waitForOrderedScript = queue.length > 0;
				if (next) {
					// go get it (from cache hopefully)
					fetch.apply(null, next);
				}
				promise['resolve'](def.resolved || true);
			},
			function (ex) {
				promise['reject'](ex);
			}
		);

	}

	function testGlobalVar (varName) {
		try {
			return eval('global.' + varName);
		}
		catch (ex) {
			return undef;
		}
	}

	return {

		// the !options force us to cache ids in the plugin
		'dynamic': true,

		'load': function (name, require, callback, config) {

			var order, exportsPos, exports, prefetch, def, promise;

			order = name.indexOf('!order') > 0; // can't be zero
			exportsPos = name.indexOf('!exports=');
			exports = exportsPos > 0 && name.substr(exportsPos + 9); // must be last option!
			prefetch = 'prefetch' in config ? config['prefetch'] : true;
			name = order || exportsPos > 0 ? name.substr(0, name.indexOf('!')) : name;

			// if we've already fetched this resource, get it out of the cache
			if (name in cache) {
				callback(cache[name]);
			}
			else {
				cache[name] = undef;
				def = {
					name: name,
					url: require['toUrl'](nameWithExt(name, 'js')),
					order: order,
					exports: exports,
					timeoutMsec: config['timeout']
				};
				promise = {
					'resolve': function (o) {
						cache[name] = o;
						(callback['resolve'] || callback)(o);
					},
					'reject': callback['reject'] || function (ex) { throw ex; }
				};

				// if this script has to wait for another
				// or if we're loading, but not executing it
				if (order && !supportsAsyncFalse && waitForOrderedScript) {
					// push onto the stack of scripts that will be fetched
					// from cache. do this before fetch in case IE has file cached.
					queue.push([def, promise]);
					// if we're prefetching
					if (prefetch) {
						// go get the file under an unknown mime type
						def.mimetype = 'text/cache';
						loadScript(def,
							// remove the fake script when loaded
							function (el) { el.parentNode.removeChild(el); },
							function () {}
						);
						def.mimetype = '';
					}
				}
				// otherwise, just go get it
				else {
					waitForOrderedScript = waitForOrderedScript || order;
					fetch(def, promise);
				}
			}

		}

	};
});
}(this, this.document));