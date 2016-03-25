/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
var testUtils = (function() {
  return {
    createEditor: function(done, config) {
      var editor = CKEDITOR.appendTo(document.body, CKEDITOR.tools.extend({
        allowedContent: true,
        customConfig: '',
        // Basic plugins, as per http://docs.ckeditor.com/#!/guide/dev_tests-section-test-requirements .
        plugins: 'wysiwygarea,toolbar,undo,basicstyles',
        skin: 'moono'
      }, config, true));
      editor.on('instanceReady', done);
      return editor;
    },

    assertNoChangeAfterDataRoundTrip: function(editor, inputData) {
      var deferred = jQuery.Deferred();
      editor.setData(inputData, {
        callback: function() {
          editor.config.fullData = true;
          editor.setData(editor.getData(), {
            callback: function() {
              editor.config.fullData = false;
              expect(editor.getData()).toBe(inputData);
              deferred.resolve();
            }
          });
        }
      });
      return deferred.promise();
    },

    assertSnapshot: function(editor, inputData, expectedSnapshot) {
      var deferred = jQuery.Deferred();
      editor.setData(inputData, {
        callback: function() {
          expect(editor.getSnapshot()).toBe(expectedSnapshot);
          deferred.resolve();
        }
      });
      return deferred.promise();
    }
  };
})();
