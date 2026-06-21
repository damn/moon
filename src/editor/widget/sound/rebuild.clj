(ns editor.widget.sound.rebuild
  (:require [clojure.actor.get-user-object :refer [get-user-object]]
            [clojure.actor.remove :refer [remove!]]
            [clojure.actor.set-user-object :refer [set-user-object!]]
            [clojure.actor.find-ancestor :refer [find-ancestor]]
            [clojure.group.clear-children :refer [clear-children!]]
            [clojure.utils.layout.pack :refer [pack!]]
            [scene2d.actor.is-window :as window?]
            [clojure.ui.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor window?/f))
    (pack! (find-ancestor table window?/f))
    (let [[k _] (get-user-object table)]
      (set-user-object! table [k sound-name]))))
