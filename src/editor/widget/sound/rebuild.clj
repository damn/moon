(ns editor.widget.sound.rebuild
  (:require [scene2d.actor.get-user-object :refer [get-user-object]]
            [scene2d.actor.remove :refer [remove!]]
            [scene2d.actor.set-user-object :refer [set-user-object!]]
            [scene2d.actor.find-ancestor :refer [find-ancestor]]
            [scene2d.group.clear-children :refer [clear-children!]]
            [gdl.pack :refer [pack!]]
            [gdl.is-window :as window?]
            [ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor window?/f))
    (pack! (find-ancestor table window?/f))
    (let [[k _] (get-user-object table)]
      (set-user-object! table [k sound-name]))))
