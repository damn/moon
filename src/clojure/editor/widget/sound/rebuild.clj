(ns clojure.editor.widget.sound.rebuild
  (:require [gdl.actor.get-user-object :refer [get-user-object]]
            [gdl.actor.remove :refer [remove!]]
            [gdl.actor.set-user-object :refer [set-user-object!]]
            [gdl.actor.find-ancestor :refer [find-ancestor]]
            [gdl.group.clear-children :refer [clear-children!]]
            [gdl.layout.pack :refer [pack!]]
            [gdl.actor.is-window :as window?]
            [gdl.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor window?/f))
    (pack! (find-ancestor table window?/f))
    (let [[k _] (get-user-object table)]
      (set-user-object! table [k sound-name]))))
