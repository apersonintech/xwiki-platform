package com.xpn.xwiki.wysiwyg.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

/**
 * This {@link Constants} interface is used to make user interface strings internationalizable.
 */
public interface Strings extends Constants
{
    Strings INSTANCE = (Strings) GWT.create(Strings.class);

    String apply();

    String attachment();

    String backColor();

    String bold();

    String cancel();

    String charmap();

    String close();

    String code();

    String colorPicker();

    String copy();

    String cut();

    String deleteCol();

    String deleteRow();

    String font();

    String fontSize();

    String foreColor();

    String format();

    String h1();

    String h2();

    String h3();

    String h4();

    String h5();

    String hr();

    String image();

    String indent();

    String insertColAfter();

    String insertColBefore();

    String insertRowAfter();

    String insertRowBefore();

    String insertTable();

    String italic();

    String justifyCenter();

    String justifyFull();

    String justifyLeft();

    String justifyRight();

    String link();

    String macro();

    String normal();

    String ol();

    String outdent();

    String paste();

    String redo();

    String removeFormat();

    String strikeThrough();

    String subscript();

    String superscript();

    String sync();

    String teletype();

    String ul();

    String underline();

    String undo();

    String unlink();
}
