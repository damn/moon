(ns editor.widget.sound.rebuild
  (:require [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.utils.layout.pack :refer [pack!]]
            [scene2d.ui.table.add-rows :refer [add-rows!]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Group)
           (com.badlogic.gdx.scenes.scene2d.ui Window)))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (Group/.clearChildren table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (Actor/.remove (find-ancestor actor #(instance? Window %)))
    (pack! (find-ancestor table #(instance? Window %)))
    (let [[k _] (Actor/.getUserObject table)]
      (Actor/.setUserObject table [k sound-name]))))
