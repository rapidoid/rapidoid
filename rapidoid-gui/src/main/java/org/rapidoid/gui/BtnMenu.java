package org.rapidoid.gui;


import org.rapidoid.html.Tag;
import org.rapidoid.u.U;

import java.util.*;

/**
 * Created by milievski on 12/14/2015.
 */
public class BtnMenu {
    private String title;
    private List<Map<Object,String>> menuItems;

    public BtnMenu() {
        menuItems = U.list();
        menuItems.add(new LinkedHashMap<Object, String>());
    }

    public void addMenuItem(String text,String url){
        menuItems.get(menuItems.size()-1).put(text,url);
    }

    public void addSeparator(){
        menuItems.add(new LinkedHashMap<Object, String>());
    }

    private Tag generateButtonHtmlContent() {
        Tag span = GUI.span().class_("caret");
        return GUI.button().type("button").class_("btn btn-default dropdown-toggle")
                .attr("data-toggle","dropdown")
                .attr("aria-haspopup","true")
                .attr("aria-expanded","false").contents(title+" ",span);
    }


    private Optional<Tag> generateMenuHtmlContent(){

        if(!menuItems.get(0).isEmpty()) {
            List<Tag> content = U.list();

            for (Iterator<Map<Object, String>> iterator = menuItems.iterator(); iterator.hasNext(); ) {

                generateMenuItems(content, iterator);

                if (iterator.hasNext()) {
                    content.add(GUI.li().role("separator").class_("divider"));
                }
            }

            return Optional.of(GUI.ul().class_("dropdown-menu").contents(content));
        }
        return Optional.empty();
    }

    private void generateMenuItems(List<Tag> content, Iterator<Map<Object, String>> subMenuIterator) {
        for (Map.Entry<Object, String> menuItem : subMenuIterator.next().entrySet()) {
            Tag a = GUI.a().href(menuItem.getValue()).contents(menuItem.getKey());
            Tag li = GUI.li().contents(a);
            content.add(li);
        }
    }

    @Override
    public String toString() {
        return render();
    }

    public String render(){
        List<Tag> content = U.list();
        content.add(generateButtonHtmlContent());
        Optional<Tag> menuContent = generateMenuHtmlContent();
        if(menuContent.isPresent()) {
            content.add(menuContent.get());
        }
        return GUI.div().class_("btn-group").contents(content).toString();
    }

    public String title() {
        return title;
    }

    public BtnMenu title(String title) {
        this.title = title;
        return this;
    }

    public List<Map<Object, String>> items() {
        return menuItems;
    }

    public BtnMenu items(Map<Object, String> items) {
        this.menuItems.add(items);
        return this;
    }

}
