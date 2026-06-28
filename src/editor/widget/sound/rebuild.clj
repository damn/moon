(ns editor.widget.sound.rebuild
  (:require [scene2d.utils.layout.pack :refer [pack!]]
            [scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)
           (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn- find-window-ancestor [actor]
  (loop [a actor]
    (if-let [p (Actor/.getParent a)]
      (if (instance? Window p)
        p
        (recur p))
      (throw (Error. (str "Actor has no parent window " actor))))))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (Group/.clearChildren table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (Actor/.remove (find-window-ancestor actor))
    (pack! (find-window-ancestor table))
    (let [[k _] (Actor/.getUserObject table)]
      (Actor/.setUserObject table [k sound-name]))))
