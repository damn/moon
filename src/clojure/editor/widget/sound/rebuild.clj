(ns clojure.editor.widget.sound.rebuild
  (:require [gdl.get-user-object :refer [get-user-object]]
            [gdl.remove :refer [remove!]]
            [gdl.set-user-object :refer [set-user-object!]]
            [gdl.find-ancestor :refer [find-ancestor]]
            [gdl.clear-children :refer [clear-children!]]
            [gdl.pack :refer [pack!]]
            [gdl.is-window :as window?]
            [gdl.table.add-rows :refer [add-rows!]]))

(defn rebuild-sound-widget! [table sound-name ->sound-columns]
  (fn [actor {:keys [ctx/skin]}]
    (clear-children! table)
    (add-rows! table [(->sound-columns skin table sound-name)])
    (remove! (find-ancestor actor window?/f))
    (pack! (find-ancestor table window?/f))
    (let [[k _] (get-user-object table)]
      (set-user-object! table [k sound-name]))))
