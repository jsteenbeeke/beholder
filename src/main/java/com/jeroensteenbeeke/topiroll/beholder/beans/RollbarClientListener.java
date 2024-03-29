/*
 * This file is part of Beholder
 * Copyright (C) 2016 - 2023 Jeroen Steenbeeke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jeroensteenbeeke.topiroll.beholder.beans;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.IHeaderContributor;

public class RollbarClientListener implements
		IHeaderContributor {
	private static final long serialVersionUID = 817261889164561772L;
	private final String environment;

	private final String clientKey;

	public RollbarClientListener(String environment, String clientKey) {
		this.environment = environment;
		this.clientKey = clientKey;
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		StringBuilder builder = new StringBuilder();
		builder.append("<script>");
		builder.append("var _rollbarConfig = {");
		builder.append("accessToken: \"").append(clientKey).append("\",\n");
		builder.append("captureUncaught: true,\n");
		builder.append("captureUnhandledRejections: true,\n");
		builder.append("\t\tpayload: {\n");
		builder.append("\t\tenvironment: \"").append(environment).append("\"");
		builder.append("\t}\n");
		builder.append("};\n");
		builder.append("// Rollbar Snippet\n");
		builder.append("!function(r)" +
				"{function o(e){if(n[e])return n[e].exports;var t=n[e]={exports:{},id:e,loaded:!1};return r[e].call(t" +
				".exports,t,t.exports,o),t.loaded=!0,t.exports}var n={};return o.m=r,o.c=n,o.p=\"\",o(0)}([function(r," +
				"o,n){\"use strict\";var e=n(1),t=n(4);_rollbarConfig=_rollbarConfig||{},_rollbarConfig" +
				".rollbarJsUrl=_rollbarConfig.rollbarJsUrl||\"https://cdnjs.cloudflare.com/ajax/libs/rollbar" +
				".js/2.0.4/rollbar.min.js\",_rollbarConfig.async=void 0===_rollbarConfig.async||_rollbarConfig.async;" +
				"var a=e.setupShim(window,_rollbarConfig),l=t(_rollbarConfig);window.rollbar=e.Rollbar,a.loadFull" +
				"(window,document,!_rollbarConfig.async,_rollbarConfig,l)},function(r,o,n){\"use strict\";function e" +
				"(r){return function(){try{return r.apply(this,arguments)}catch(r){try{console.error(\"[Rollbar]: " +
				"Internal error\",r)}catch(r){}}}}function t(r,o){this.options=r,this._rollbarOldOnError=null;var " +
				"n=s++;this.shimId=function(){return n},window&&window._rollbarShims&&(window" +
				"._rollbarShims[n]={handler:o,messages:[]})}function a(r,o){var n=o.globalAlias||\"Rollbar\";if" +
				"(\"object\"==typeof r[n])return r[n];r._rollbarShims={},r._rollbarWrappedError=null;var t=new d(o);" +
				"return e(function(){return o.captureUncaught&&(t._rollbarOldOnError=r.onerror,i" +
				".captureUncaughtExceptions(r,t,!0),i.wrapGlobals(r,t,!0)),o.captureUnhandledRejections&&i" +
				".captureUnhandledRejections(r,t,!0),r[n]=t,t})()}function l(r){return e(function(){var o=this,n=Array" +
				".prototype.slice.call(arguments,0),e={shim:o,method:r,args:n,ts:new Date};window._rollbarShims[this" +
				".shimId()].messages.push(e)})}var i=n(2),s=0,c=n(3),p=function(r,o){return new t(r,o)},d=c.bind(null," +
				"p);t.prototype.loadFull=function(r,o,n,t,a){var l=function(){var o;if(void 0===r._rollbarDidLoad)" +
				"{o=new Error(\"rollbar.js did not load\");for(var n,e,t,l,i=0;n=r._rollbarShims[i++];)for(n=n" +
				".messages||[];e=n.shift();)for(t=e.args||[],i=0;i<t.length;++i)if(l=t[i],\"function\"==typeof l){l(o)" +
				";break}}\"function\"==typeof a&&a(o)},i=!1,s=o.createElement(\"script\"),c=o.getElementsByTagName" +
				"(\"script\")[0],p=c.parentNode;s.crossOrigin=\"\",s.src=t.rollbarJsUrl,n||(s.async=!0),s.onload=s" +
				".onreadystatechange=e(function(){if(!(i||this.readyState&&\"loaded\"!==this" +
				".readyState&&\"complete\"!==this.readyState)){s.onload=s.onreadystatechange=null;try{p.removeChild(s)" +
				"}catch(r){}i=!0,l()}}),p.insertBefore(s,c)},t.prototype.wrap=function(r,o){try{var n;if" +
				"(n=\"function\"==typeof o?o:function(){return o||{}},\"function\"!=typeof r)return r;if(r._isWrap)" +
				"return r;if(!r._wrapped&&(r._wrapped=function(){try{return r.apply(this,arguments)}catch(e){var o=e;" +
				"throw\"string\"==typeof o&&(o=new String(o)),o._rollbarContext=n()||{},o._rollbarContext" +
				"._wrappedSource=r.toString(),window._rollbarWrappedError=o,o}},r._wrapped._isWrap=!0,r" +
				".hasOwnProperty))for(var e in r)r.hasOwnProperty(e)&&(r._wrapped[e]=r[e]);return r._wrapped}catch(o)" +
				"{return r}};for(var u=\"log,debug,info,warn,warning,error,critical,global,configure," +
				"handleUncaughtException,handleUnhandledRejection\".split(\",\"),f=0;f<u.length;++f)t" +
				".prototype[u[f]]=l(u[f]);r.exports={setupShim:a,Rollbar:d}},function(r,o){\"use strict\";function n" +
				"(r,o,n){if(r){var t;\"function\"==typeof o._rollbarOldOnError?t=o._rollbarOldOnError:r.onerror&&!r" +
				".onerror.belongsToShim&&(t=r.onerror,o._rollbarOldOnError=t);var a=function(){var n=Array.prototype" +
				".slice.call(arguments,0);e(r,o,t,n)};a.belongsToShim=n,r.onerror=a}}function e(r,o,n,e){r" +
				"._rollbarWrappedError&&(e[4]||(e[4]=r._rollbarWrappedError),e[5]||(e[5]=r._rollbarWrappedError" +
				"._rollbarContext),r._rollbarWrappedError=null),o.handleUncaughtException.apply(o,e),n&&n.apply(r,e)" +
				"}function t(r,o,n){if(r){\"function\"==typeof r._rollbarURH&&r._rollbarURH.belongsToShim&&r" +
				".removeEventListener(\"unhandledrejection\",r._rollbarURH);var e=function(r){var n=r.reason,e=r" +
				".promise,t=r.detail;!n&&t&&(n=t.reason,e=t.promise),o&&o.handleUnhandledRejection&&o" +
				".handleUnhandledRejection(n,e)};e.belongsToShim=n,r._rollbarURH=e,r.addEventListener" +
				"(\"unhandledrejection\",e)}}function a(r,o,n){if(r){var e,t,a=\"EventTarget,Window,Node," +
				"ApplicationCache,AudioTrackList,ChannelMergerNode,CryptoOperation,EventSource,FileReader," +
				"HTMLUnknownElement,IDBDatabase,IDBRequest,IDBTransaction,KeyOperation,MediaController,MessagePort," +
				"ModalWindow,Notification,SVGElementInstance,Screen,TextTrack,TextTrackCue,TextTrackList,WebSocket," +
				"WebSocketWorker,Worker,XMLHttpRequest,XMLHttpRequestEventTarget,XMLHttpRequestUpload\".split(\",\");" +
				"for(e=0;e<a.length;++e)t=a[e],r[t]&&r[t].prototype&&l(o,r[t].prototype,n)}}function l(r,o,n){if(o" +
				".hasOwnProperty&&o.hasOwnProperty(\"addEventListener\")){for(var e=o.addEventListener;e" +
				"._rollbarOldAdd&&e.belongsToShim;)e=e._rollbarOldAdd;var t=function(o,n,t){e.call(this,o,r.wrap(n),t)" +
				"};t._rollbarOldAdd=e,t.belongsToShim=n,o.addEventListener=t;for(var a=o.removeEventListener;a" +
				"._rollbarOldRemove&&a.belongsToShim;)a=a._rollbarOldRemove;var l=function(r,o,n){a.call(this,r,o&&o" +
				"._wrapped||o,n)};l._rollbarOldRemove=a,l.belongsToShim=n,o.removeEventListener=l}}r" +
				".exports={captureUncaughtExceptions:n,captureUnhandledRejections:t,wrapGlobals:a}},function(r,o)" +
				"{\"use strict\";function n(r,o){this.impl=r(o,this),this.options=o,e(n.prototype)}function e(r){for" +
				"(var o=function(r){return function(){var o=Array.prototype.slice.call(arguments,0);if(this.impl[r])" +
				"return this.impl[r].apply(this.impl,o)}},n=\"log,debug,info,warn,warning,error,critical,global," +
				"configure,handleUncaughtException,handleUnhandledRejection,_createItem,wrap,loadFull,shimId\".split" +
				"(\",\"),e=0;e<n.length;e++)r[n[e]]=o(n[e])}n.prototype._swapAndProcessMessages=function(r,o){this" +
				".impl=r(this.options);for(var n,e,t;n=o.shift();)e=n.method,t=n.args,this[e]&&\"function\"==typeof " +
				"this[e]&&this[e].apply(this,t);return this},r.exports=n},function(r,o){\"use strict\";r" +
				".exports=function(r){return function(o){if(!o&&!window._rollbarInitialized){r=r||{};for(var n,e,t=r" +
				".globalAlias||\"Rollbar\",a=window.rollbar,l=function(r){return new a(r)},i=0;n=window" +
				"._rollbarShims[i++];)e||(e=n.handler),n.handler._swapAndProcessMessages(l,n.messages);window[t]=e," +
				"window._rollbarInitialized=!0}}}}]);\\n");
		builder.append("// End Rollbar Snippet");
		builder.append("</script>\n");

		response.render(JavaScriptHeaderItem.forScript(builder, "rollbar-init"));
	}
}
