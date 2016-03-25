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
describe('XWiki Filter Plugin for CKEditor', function() {
  var editor;

  beforeEach(function(done) {
    editor = testUtils.createEditor(done, {'extraPlugins': 'xwiki-filter'});
  });

  it('converts empty lines to empty paragraphs', function(done) {
    testUtils.assertSnapshot(editor, '<p>1</p><div class="wikimodel-emptyline"></div><p>2</p>',
      '<p>1</p><p><br></p><p>2</p>').then(done);
  });

  it('converts empty lines to empty paragraphs and back', function(done) {
    testUtils.assertNoChangeAfterDataRoundTrip(editor,
      '<p>before</p><div class="wikimodel-emptyline"></div><p>after</p>').then(done);
  });
});
